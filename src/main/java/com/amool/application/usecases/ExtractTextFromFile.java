package com.amool.application.usecases;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

public class ExtractTextFromFile {

    public String execute(byte[] fileBytes, String filename) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }

        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio.");
        }

        String normalizedName = filename.toLowerCase(Locale.ROOT).trim();

        try {
            if (normalizedName.endsWith(".docx")) {
                return extractDocx(fileBytes);
            }

            if (normalizedName.endsWith(".doc")) {
                return extractDoc(fileBytes);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo procesar el archivo.", e);
        }

        throw new IllegalArgumentException("Formato de archivo no soportado: " + filename);
    }

    private String extractDocx(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return normalizeText(extractor.getText());
        }
    }

    private String extractDoc(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return normalizeText(extractor.getText());
        }
    }

    private String normalizeText(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }

        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        return normalized.trim();
    }
}
