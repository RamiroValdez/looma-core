package com.amool.application.service;
/*
import com.amool.application.port.out.OpenAIImagePort;
import com.amool.application.usecases.GenerateImageUrlUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GenerateImageUrlUseCaseTest {

    @Test
    @DisplayName("generateImageUrl delegates to OpenAIImagePort and returns URL")
    void generateImageUrl_DelegatesAndReturns() {
        OpenAIImagePort port = mock(OpenAIImagePort.class);
        GenerateImageUrlUseCase service = new GenerateImageUrlUseCase(port);

        String prompt = """
                Create a detailed **flat illustration** for a book cover. **Avoid showing the image as a physical book, a 3D object, or a photo of a book.**
                
                Artistic style: anime.
                Color palette: cold.
                Composition: first plane.
                
                Description of the scene: A man with blue eyes..
                
                The image should be a **high-quality, two-dimensional illustration** with strong visual focus and cinematic lighting. **No text, no title, no spine, no background context showing it as a book.**""";

        String expectedUrl = "https://img.example/cover.png";
        when(port.generateImageUrl(anyString())).thenReturn(expectedUrl);

        String artisticStyle = "anime";
        String colorPalette = "cold";
        String composition = "first plane";
        String description = "A man with blue eyes.";

        String result = service.generateImageUrl(artisticStyle, colorPalette, composition, description);

        assertEquals(expectedUrl, result);
        verify(port, times(1)).generateImageUrl(anyString());
    }

    @Test
    @DisplayName("generateImageUrl returns null when port returns null")
    void generateImageUrl_Null() {
        OpenAIImagePort port = mock(OpenAIImagePort.class);
        GenerateImageUrlUseCase service = new GenerateImageUrlUseCase(port);

        String prompt = """
                Create a detailed **flat illustration** for a book cover. **Avoid showing the image as a physical book, a 3D object, or a photo of a book.**
                
                Artistic style: anime.
                Color palette: cold.
                Composition: first plane.
                
                Description of the scene: A man with blue eyes..
                
                The image should be a **high-quality, two-dimensional illustration** with strong visual focus and cinematic lighting. **No text, no title, no spine, no background context showing it as a book.**""";

        when(port.generateImageUrl(eq(prompt))).thenReturn(null);

        String artisticStyle = "anime";
        String colorPalette = "cold";
        String composition = "first plane";
        String description = "A man with blue eyes.";

        String result = service.generateImageUrl(artisticStyle, colorPalette, composition, description);

        assertNull(result);
        verify(port, times(1)).generateImageUrl(anyString());
    }
}
*/