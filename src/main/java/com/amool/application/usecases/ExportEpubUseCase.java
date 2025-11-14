package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.AwsS3Port;
import java.util.List;
import java.util.Optional;
import com.amool.domain.model.Work;
import com.amool.domain.model.ChapterContent;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.domain.Resource;
import io.documentnode.epub4j.epub.EpubWriter;
import io.documentnode.epub4j.domain.Author;

import java.util.Map;

public class ExportEpubUseCase {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterContentPort loadChapterContentPort;
    private AwsS3Port awsS3Port;
    private final String EPUBPATH = "works/{workId}/epub";
    
    public ExportEpubUseCase(ObtainWorkByIdPort obtainWorkByIdPort, LoadChapterContentPort loadChapterContentPort, AwsS3Port awsS3Port) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.awsS3Port = awsS3Port;
    }
    
    public String execute(Long workId) {

        List<ChapterContent> chapterContents = new ArrayList<>();
        List<ChapterContent> parsedContent = new ArrayList<>();
        Optional<Work> work = obtainWorkByIdPort.obtainWorkById(workId);
        if (work.isEmpty()) {
            throw new RuntimeException("Work not found");
        } else {
            if (!work.get().getHasEpub()) {
            
            work.get().getChapters().stream().forEach(chapter -> {
                chapterContents.add(loadChapterContentPort.loadContent(workId.toString(), chapter.getId().toString()).get());
            });

            parsedContent = parseContent(chapterContents);

            Book book = createEpub(parsedContent, work.get());

            String epubUrl = saveAndReturnEpubURL(book, work.get());

            work.get().setHasEpub(true);
           
            return epubUrl;
        } else {
            return awsS3Port.obtainFilePresignedUrl(EPUBPATH.replace("{workId}", work.get().getId().toString()) + "/" + work.get().getTitle() + ".epub");
        }


        }

       
    }

    private List<ChapterContent> parseContent(List<ChapterContent> chapterContents) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        List<ChapterContent> parsedContent = new ArrayList<>();

        chapterContents.stream().forEach(chapterContent -> {
            parsedContent.add(
                new ChapterContent(
                    chapterContent.getWorkId(), 
                    chapterContent.getChapterId(), 
                    Map.of(chapterContent.getDefaultLanguage(), renderer.render(
                                                                parser.parse(chapterContent.getContent(chapterContent.getDefaultLanguage()))
                                                                )),
                    chapterContent.getDefaultLanguage()));
        });

        return parsedContent;
    }

    private Book createEpub(List<ChapterContent> chapterContents, Work work) {
        Book book = new Book();

        book.getMetadata().addTitle(work.getTitle());
        book.getMetadata().addAuthor(
            new Author(
                work.getCreator().getName(),
                 work.getCreator().getSurname())
                 );
        
        work.getChapters().stream().forEach(chapter -> {

            Resource resource = new Resource(
                chapterContents.stream()
                .filter(cc -> cc.getChapterId().equals(chapter.getId()))
                .findFirst()
                .get()
                .getContent(chapterContents.getFirst().getDefaultLanguage()).getBytes(StandardCharsets.UTF_8), chapter.getTitle() + ".html"
            );
            
            
          book.addSection(chapter.getTitle(), resource);

        });

        return book;
    }

    private String saveAndReturnEpubURL(Book book, Work work) {
         
        try{
            byte[] bytes = epubBytes(book, work);
            awsS3Port.uploadPrivateByte(EPUBPATH.replace("{workId}", work.getId().toString()) + "/" + work.getTitle() + ".epub", "application/epub+zip", bytes);
            return awsS3Port.obtainFilePresignedUrl(EPUBPATH.replace("{workId}", work.getId().toString()) + "/" + work.getTitle() + ".epub");
        }catch (IOException e) {
            return "Error saving epub";
        }
  
         

    }
    
    private byte[] epubBytes (Book book, Work work) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, bytes);
        return bytes.toByteArray();   
    }
}
