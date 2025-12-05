package com.amool.application.usecase;

import com.amool.application.port.out.OpenAIImagePort;
import com.amool.application.usecases.GenerateImageUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class GenerateImageUrlTest {

    private OpenAIImagePort openAIImagePort;
    private GenerateImageUrl useCase;

    @BeforeEach
    public void setUp() {
        openAIImagePort = Mockito.mock(OpenAIImagePort.class);
        useCase = new GenerateImageUrl(openAIImagePort);
    }

    private void givenImageGenerationWillReturn(String expectedUrl) {
        when(openAIImagePort.generateImageUrl(anyString())).thenReturn(expectedUrl);
    }

    private void givenImageGenerationWillFail(String message) {
        when(openAIImagePort.generateImageUrl(anyString())).thenThrow(new RuntimeException(message));
    }

    private String whenGenerate(String style, String colors, String composition, String description) {
        return useCase.execute(style, colors, composition, description);
    }

    private void thenResultIs(String result, String expected) {
        assertNotNull(result);
        assertEquals(expected, result);
    }

    private void thenOpenAIGenerateCalledOnce() {
        verify(openAIImagePort, times(1)).generateImageUrl(anyString());
    }

    private void thenThrowsRuntimeWithMessage(Runnable action, String expectedMessage) {
        RuntimeException ex = assertThrows(RuntimeException.class, action::run);
        assertEquals(expectedMessage, ex.getMessage());
        thenOpenAIGenerateCalledOnce();
    }

    @Test
    public void when_GenerateImageWithValidParameters_ThenReturnImageUrl() {
        String expectedUrl = "https://example.com/generated-image.jpg";
        givenImageGenerationWillReturn(expectedUrl);

        String result = whenGenerate("style", "colors", "composition", "description");

        thenResultIs(result, expectedUrl);
        thenOpenAIGenerateCalledOnce();
    }

    @Test
    public void when_OpenAIServiceFails_ThenThrowException() {
        String errorMessage = "OpenAI service unavailable";
        givenImageGenerationWillFail(errorMessage);

        thenThrowsRuntimeWithMessage(() -> whenGenerate("style", "colors", "composition", "description"), errorMessage);
    }
}
