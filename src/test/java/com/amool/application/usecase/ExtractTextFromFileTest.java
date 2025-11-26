package com.amool.application.usecase;

import com.amool.application.usecases.ExtractTextFromFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ExtractTextFromFileTest {

    private ExtractTextFromFile useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExtractTextFromFile();
    }

    private byte[] givenEmptyFile() { return new byte[0]; }
    private byte[] givenArbitraryBytes() { return new byte[]{1,2,3,4,5}; }
    private String givenFilename(String name) { return name; }
    private byte[] givenDocxWithText(String text) throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        }
    }

    private String whenExtract(byte[] content, String filename) {
        return useCase.execute(content, filename);
    }

    private void thenThrowsIllegalArgumentWithMessage(Runnable action, String expectedMessage) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action::run);
        assertEquals(expectedMessage, ex.getMessage());
    }
    private void thenResultContains(String result, String fragment) { assertTrue(result.contains(fragment)); }

    @Test
    public void when_FileIsEmpty_ThenThrowException() {
        byte[] emptyFile = givenEmptyFile();
        String filename = givenFilename("test.doc");

        thenThrowsIllegalArgumentWithMessage(() -> whenExtract(emptyFile, filename), "El archivo está vacío.");
    }

    @Test
    public void when_FilenameIsEmpty_ThenThrowException() {
        byte[] fileContent = givenArbitraryBytes();
        String filename = givenFilename("");

        thenThrowsIllegalArgumentWithMessage(() -> whenExtract(fileContent, filename), "El nombre del archivo es obligatorio.");
    }

    @Test
    public void when_UnsupportedFileFormat_ThenThrowException() {
        byte[] fileContent = givenArbitraryBytes();
        String filename = givenFilename("documento.pdf");

        thenThrowsIllegalArgumentWithMessage(() -> whenExtract(fileContent, filename), "Formato de archivo no soportado: documento.pdf");
    }

    @Test
    public void when_ValidDocxFile_ThenReturnExtractedText() throws Exception {
        byte[] docxContent = givenDocxWithText("Este es un documento de prueba");
        String filename = givenFilename("test.docx");

        String result = whenExtract(docxContent, filename);

        thenResultContains(result, "Este es un documento de prueba");
    }
}
