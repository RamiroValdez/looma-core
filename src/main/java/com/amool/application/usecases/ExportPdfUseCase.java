// ...existing code...
package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.HttpDownloadPort;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.ChapterContent;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ExportPdfUseCase {
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final AwsS3Port awsS3Port;
    private final HttpDownloadPort httpDownloadPort;
    private final WorkPort workPort;
    private static final String PDFPATH = "works/{workId}/pdf";

    public ExportPdfUseCase(ObtainWorkByIdPort obtainWorkByIdPort,
                            LoadChapterContentPort loadChapterContentPort,
                            AwsS3Port awsS3Port,
                            HttpDownloadPort httpDownloadPort,
                            WorkPort workPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.awsS3Port = awsS3Port;
        this.httpDownloadPort = httpDownloadPort;
        this.workPort = workPort;
    }

    public String execute(Long workId) {
        Optional<Work> workOpt = obtainWorkByIdPort.obtainWorkById(workId);
        if (workOpt.isEmpty()) throw new RuntimeException("Work not found");
        Work work = workOpt.get();

        String pathBase = PDFPATH.replace("{workId}", work.getId().toString());
        String filename = sanitizeFileName(work.getTitle()) + ".pdf";
        String fullPath = pathBase + "/" + filename;

        if (work.getHasPdf() && work.getLengthPdf() == work.getChapters().size()) {
            return awsS3Port.obtainFilePresignedUrl(fullPath);
        }

        return generatePdf(work, fullPath);
    }

    private String generatePdf(Work work, String s3Path) {
        List<ChapterContent> chapterContents = new ArrayList<>();
        work.getChapters().forEach(ch -> {
            loadChapterContentPort.loadContent(work.getId().toString(), ch.getId().toString())
                .ifPresent(chc -> chapterContents.add(chc));
        });

        List<ChapterContent> parsed = parseContent(chapterContents);

        try {
            byte[] bytes = pdfBytes(work, parsed);
            awsS3Port.uploadPrivateByte(s3Path, "application/pdf", bytes);

            work.setHasPdf(true);
            work.setLengthPdf(work.getChapters().size());
            workPort.updateWork(work);

            return awsS3Port.obtainFilePresignedUrl(s3Path);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private List<ChapterContent> parseContent(List<ChapterContent> chapterContents) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        List<ChapterContent> parsedContent = new ArrayList<>();
        chapterContents.forEach(chapterContent -> {
            parsedContent.add(
                new ChapterContent(
                    chapterContent.getWorkId(),
                    chapterContent.getChapterId(),
                    java.util.Map.of(chapterContent.getDefaultLanguage(),
                        renderer.render(parser.parse(chapterContent.getContent(chapterContent.getDefaultLanguage())))),
                    chapterContent.getDefaultLanguage()
                )
            );
        });
        return parsedContent;
    }

   private byte[] pdfBytes(Work work, List<ChapterContent> chapterContents) throws Exception {
    StringBuilder html = new StringBuilder();
    html.append("<html><head><meta charset='utf-8'/><style>")
    .append("body{font-family: Arial, Helvetica, sans-serif; font-size:12px; line-height:1.4; margin:20px;} ")
    .append("p{font-size:16px;} ")
    .append("h1{font-size:32px;text-align:center;} ") 
    .append("h2{font-size:22px;margin-top:30px;} ")   
    .append("img.cover{width:450px; height:600px; object-fit:cover; margin-top:70px;} ")
    .append("</style></head><body>");

    html.append("<h1>").append(escapeHtml(safe(work.getTitle()))).append("</h1>");
    html.append("<p style='text-align:center;'>Autor: ")
        .append(escapeHtml(safe(work.getCreator() != null ? work.getCreator().getName() + " " + work.getCreator().getSurname() : "")))
        .append("</p>");
    html.append("<hr/>");

    if (work.getCover() != null && !work.getCover().isEmpty()) {
        try {
            byte[] img = downloadImage(awsS3Port.obtainPublicUrl(work.getCover()));
            if (img != null && img.length > 0) {
                String mime = detectImageMime(img);
                String b64 = Base64.getEncoder().encodeToString(img);
                html.append("<div style='text-align:center;'>")
                    .append("<img class='cover' src='data:").append(mime).append(";base64,").append(b64).append("'/>")
                    .append("</div>");
            }
        } catch (Exception ignored) {}
    }

    if (!chapterContents.isEmpty()) {
        html.append("<div style='page-break-after:always;'></div>");
    }

    for (int i = 0; i < chapterContents.size(); i++) {
        ChapterContent cc = chapterContents.get(i);
        int capNum = i + 1;
        String chapterTitle = work.getChapters().get(i).getTitle();
        html.append("<h2>").append("Capítulo ").append(capNum).append(": ")
            .append(escapeHtml(safe(chapterTitle))).append("</h2>");
        String contentHtml = cc.getContent(cc.getDefaultLanguage());
        html.append(contentHtml == null ? "" : contentHtml);

        if (i < chapterContents.size() - 1) {
            html.append("<div style='page-break-after:always;'></div>");
        }
    }

    html.append("</body></html>");

    Document jsoupDoc = Jsoup.parse(html.toString());
    OutputSettings settings = new OutputSettings();
    settings.syntax(OutputSettings.Syntax.xml);
    settings.charset(StandardCharsets.UTF_8);
    jsoupDoc.outputSettings(settings);
    String xhtml = jsoupDoc.html();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfRendererBuilder builder = new PdfRendererBuilder();
    builder.withHtmlContent(xhtml, null);
    builder.toStream(baos);
    builder.run();

    return baos.toByteArray();
}

    private byte[] downloadImage(String url) {
        try {
            return httpDownloadPort.downloadImage(url);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private String detectImageMime(byte[] img) {
        if (img.length >= 3 && (img[0] & 0xFF) == 0xFF && (img[1] & 0xFF) == 0xD8) return "image/jpeg";
        if (img.length >= 8 && (img[0] & 0xFF) == 0x89 && (img[1] & 0xFF) == 0x50) return "image/png";
        return "image/png";
    }

    private String sanitizeFileName(String input) {
        if (input == null) return "work";
        return input.replaceAll("[^a-zA-Z0-9-_\\. ]", "").replaceAll("\\s+", "_");
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
