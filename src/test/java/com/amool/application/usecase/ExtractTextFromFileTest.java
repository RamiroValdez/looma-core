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

    @Test
    public void when_FileIsEmpty_ThenThrowException() {
        byte[] emptyFile = new byte[0];
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(emptyFile, "test.doc")
        );
        
        assertEquals("El archivo está vacío.", exception.getMessage());
    }

    @Test
    public void when_FilenameIsEmpty_ThenThrowException() {
        byte[] fileContent = {1, 2, 3, 4, 5};
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(fileContent, "")
        );
        
        assertEquals("El nombre del archivo es obligatorio.", exception.getMessage());
    }

    @Test
    public void when_UnsupportedFileFormat_ThenThrowException() {
        byte[] fileContent = {1, 2, 3, 4, 5};
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(fileContent, "documento.pdf")
        );
        
        assertEquals("Formato de archivo no soportado: documento.pdf", exception.getMessage());
    }

    @Test
    public void when_ValidDocxFile_ThenReturnExtractedText() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Este es un documento de prueba");
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            byte[] docxContent = out.toByteArray();
            
            String result = useCase.execute(docxContent, "test.docx");
            
            assertTrue(result.contains("Este es un documento de prueba"));
        }
    }
    
}
