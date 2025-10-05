package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.out.OpenAIImagePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ImageGenerationServiceImplTest {

    @Test
    @DisplayName("generateImageUrl delegates to OpenAIImagePort and returns URL")
    void generateImageUrl_DelegatesAndReturns() {
        OpenAIImagePort port = mock(OpenAIImagePort.class);
        ImageGenerationServiceImpl service = new ImageGenerationServiceImpl(port);

        String prompt = "anime cover";
        String expectedUrl = "https://img.example/cover.png";
        when(port.generateImageUrl(eq(prompt))).thenReturn(expectedUrl);

        String result = service.generateImageUrl(prompt);

        assertEquals(expectedUrl, result);
        verify(port, times(1)).generateImageUrl(eq(prompt));
    }

    @Test
    @DisplayName("generateImageUrl returns null when port returns null")
    void generateImageUrl_Null() {
        OpenAIImagePort port = mock(OpenAIImagePort.class);
        ImageGenerationServiceImpl service = new ImageGenerationServiceImpl(port);

        String prompt = "anime cover";
        when(port.generateImageUrl(eq(prompt))).thenReturn(null);

        String result = service.generateImageUrl(prompt);

        assertNull(result);
        verify(port, times(1)).generateImageUrl(eq(prompt));
    }
}
