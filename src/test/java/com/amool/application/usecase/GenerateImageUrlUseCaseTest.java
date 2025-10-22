package com.amool.application.usecase;

import com.amool.application.port.out.OpenAIImagePort;
import com.amool.application.usecases.GenerateImageUrlUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class GenerateImageUrlUseCaseTest {

    private OpenAIImagePort openAIImagePort;
    private GenerateImageUrlUseCase useCase;

    @BeforeEach
    public void setUp() {
        openAIImagePort = Mockito.mock(OpenAIImagePort.class);
        useCase = new GenerateImageUrlUseCase(openAIImagePort);
    }

    @Test
    public void when_GenerateImageWithValidParameters_ThenReturnImageUrl() {
        String expectedUrl = "https://example.com/generated-image.jpg";
        
        Mockito.when(openAIImagePort.generateImageUrl(anyString()))
               .thenReturn(expectedUrl);

        String result = useCase.execute(
            "style", 
            "colors", 
            "composition", 
            "description"
        );

        assertNotNull(result);
        assertEquals(expectedUrl, result);
        
        Mockito.verify(openAIImagePort, Mockito.times(1))
               .generateImageUrl(anyString());
    }


    @Test
    public void when_OpenAIServiceFails_ThenThrowException() {
        Mockito.when(openAIImagePort.generateImageUrl(anyString()))
               .thenThrow(new RuntimeException("OpenAI service unavailable"));

        assertThrows(
            RuntimeException.class,
            () -> useCase.execute("style", "colors", "composition", "description")
        );
        
        Mockito.verify(openAIImagePort, Mockito.times(1))
               .generateImageUrl(anyString());
    }
}
