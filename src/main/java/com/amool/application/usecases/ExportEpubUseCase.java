package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.HttpDownloadPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.port.out.SubscriptionQueryPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.domain.Resource;
import io.documentnode.epub4j.epub.EpubWriter;
import io.documentnode.epub4j.domain.Author;

public class ExportEpubUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final AwsS3Port awsS3Port;
    private final HttpDownloadPort httpDownloadPort;
    private final WorkPort workPort;
    private final SubscriptionQueryPort subscriptionQueryPort;
    private static final String EPUBPATH = "works/{workId}/epub";

    public ExportEpubUseCase(ObtainWorkByIdPort obtainWorkByIdPort,
                             LoadChapterContentPort loadChapterContentPort,
                             AwsS3Port awsS3Port,
                             HttpDownloadPort httpDownloadPort,
                             WorkPort workPort,
                             SubscriptionQueryPort subscriptionQueryPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.awsS3Port = awsS3Port;
        this.httpDownloadPort = httpDownloadPort;
        this.workPort = workPort;
        this.subscriptionQueryPort = subscriptionQueryPort;
    }

    // Se agrega userId para validar acceso.
    public String execute(Long workId, Long userId) {
        Optional<Work> workOpt = obtainWorkByIdPort.obtainWorkById(workId);
        if (workOpt.isEmpty()) {
            throw new RuntimeException("Work not found");
        }
        Work work = workOpt.get();

        // Filtrar capítulos publicados.
        List<Chapter> publishedChapters = work.getChapters().stream()
                .filter(ch -> ch.getPublicationStatus() != null && ch.getPublicationStatus().equals("PUBLISHED"))
                .collect(Collectors.toList());

        Long authorId = work.getCreator() != null ? work.getCreator().getId() : null;
        boolean isOwner = authorId != null && authorId.equals(userId);
        boolean subscribedToAuthor = authorId != null && subscriptionQueryPort.isSubscribedToAuthor(userId, authorId);
        boolean subscribedToWork = subscriptionQueryPort.isSubscribedToWork(userId, workId);

        boolean fullAccess = isOwner || subscribedToAuthor || subscribedToWork;

        List<Long> unlockedChapterIds = subscriptionQueryPort.unlockedChapters(userId, workId);

        List<Chapter> accessibleChapters;
        if (fullAccess) {
            accessibleChapters = publishedChapters; // Tiene acceso a todos los publicados.
        } else {
            accessibleChapters = publishedChapters.stream().filter(ch -> {
                BigDecimal price = ch.getPrice();
                boolean isFree = price == null || price.compareTo(BigDecimal.ZERO) == 0;
                boolean unlockedIndividually = unlockedChapterIds.contains(ch.getId());
                return isFree || unlockedIndividually;
            }).collect(Collectors.toList());
        }

        if (accessibleChapters.isEmpty()) {
            throw new RuntimeException("No hay capítulos publicados accesibles para este usuario");
        }

        // Caching sólo si tiene acceso completo a todos los capítulos publicados.
        String basePath = EPUBPATH.replace("{workId}", work.getId().toString()) + "/";
        String sanitizedTitle = sanitizeFileName(work.getTitle());
        String fileName;
        boolean canUseCache = fullAccess && work.getHasEpub() != null && work.getHasEpub() && work.getLengthEpub() != null && work.getLengthEpub().equals(publishedChapters.size());
        if (canUseCache) {
            fileName = sanitizedTitle + ".epub";
            return awsS3Port.obtainFilePresignedUrl(basePath + fileName);
        }

        // Si acceso completo pero cache inválido generamos full; si acceso parcial generamos archivo diferenciado.
        fileName = fullAccess ? (sanitizedTitle + ".epub") : (sanitizedTitle + "-user-" + userId + ".epub");

        Book book = createEpub(accessibleChapters, work);
        String fullPath = basePath + fileName;
        String epubUrl = saveAndReturnEpubURL(book, fullPath);

        if (fullAccess) {
            work.setHasEpub(true);
            work.setLengthEpub(publishedChapters.size());
            workPort.updateWork(work);
        }

        return epubUrl;
    }

    private Book createEpub(List<Chapter> chaptersToInclude, Work work) {
        Book book = new Book();
        book.getMetadata().addTitle(work.getTitle());
        if (work.getCreator() != null) {
            book.getMetadata().addAuthor(new Author(safe(work.getCreator().getName()), safe(work.getCreator().getSurname())));
        }

        // Cover
        if (work.getCover() != null) {
            try {
                Resource cover = new Resource(downloadImage(awsS3Port.obtainPublicUrl(work.getCover())), "cover.png");
                book.setCoverImage(cover);
            } catch (Exception ignored) {}
        }

        // Cargar contenidos de capítulos accesibles
        List<ChapterContent> parsedContent = parseContent(loadContents(work.getId(), chaptersToInclude));

        for (Chapter chapter : chaptersToInclude) {
            Optional<ChapterContent> ccOpt = parsedContent.stream()
                    .filter(cc -> cc.getChapterId().equals(chapter.getId().toString()))
                    .findFirst();
            if (ccOpt.isEmpty()) continue; // Si no hay contenido, se omite el capítulo.
            ChapterContent cc = ccOpt.get();
            byte[] htmlBytes = cc.getContent(cc.getDefaultLanguage()).getBytes(StandardCharsets.UTF_8);
            Resource resource = new Resource(htmlBytes, sanitizeFileName(chapter.getTitle()) + ".html");
            book.addSection(chapter.getTitle(), resource);
        }
        return book;
    }

    private List<ChapterContent> loadContents(Long workId, List<Chapter> chapters) {
        List<ChapterContent> chapterContents = new ArrayList<>();
        chapters.forEach(ch -> loadChapterContentPort.loadContent(workId.toString(), ch.getId().toString())
                .ifPresent(chapterContents::add));
        return chapterContents;
    }

    private List<ChapterContent> parseContent(List<ChapterContent> chapterContents) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        List<ChapterContent> parsedContent = new ArrayList<>();
        chapterContents.forEach(chapterContent -> parsedContent.add(
                new ChapterContent(
                        chapterContent.getWorkId(),
                        chapterContent.getChapterId(),
                        Map.of(chapterContent.getDefaultLanguage(), renderer.render(parser.parse(
                                chapterContent.getContent(chapterContent.getDefaultLanguage())))),
                        chapterContent.getDefaultLanguage())));
        return parsedContent;
    }

    private String saveAndReturnEpubURL(Book book, String fullPath) {
        try {
            byte[] bytes = epubBytes(book);
            awsS3Port.uploadPrivateByte(fullPath, "application/epub+zip", bytes);
            return awsS3Port.obtainFilePresignedUrl(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("Error saving epub", e);
        }
    }

    private byte[] epubBytes(Book book) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, bytes);
        return bytes.toByteArray();
    }

    private byte[] downloadImage(String url) {
        try {
            return httpDownloadPort.downloadImage(url);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private String sanitizeFileName(String input) {
        if (input == null) return "work";
        return input.replaceAll("[^a-zA-Z0-9-_. ]", "").replaceAll("\\s+", "_");
    }

    private String safe(String s) { return s == null ? "" : s; }
}
