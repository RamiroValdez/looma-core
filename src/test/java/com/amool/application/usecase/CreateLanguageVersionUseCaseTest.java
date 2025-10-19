package com.amool.application.usecase;

import com.amool.application.port.out.GoogleTranslatePort;
import com.amool.application.port.out.OpenAIPort;
import com.amool.application.usecases.CreateLanguageVersionUseCase;
import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateLanguageVersionUseCaseTest {

    private OpenAIPort openAIPort;
    private GoogleTranslatePort googleTranslatePort;
    private CreateLanguageVersionUseCase useCase;

    @BeforeEach
    void setUp() {
        openAIPort = Mockito.mock(OpenAIPort.class);
        googleTranslatePort = Mockito.mock(GoogleTranslatePort.class);

        useCase = new CreateLanguageVersionUseCase(openAIPort, googleTranslatePort);

    }

    @Test
    void when_execute_thenReturnCreatedVersion() {
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        String originalText = "Hello, world!";

        Mockito.when(googleTranslatePort.translateText(originalText, targetLanguage))
                .thenReturn("¡Hola, mundo!");

        Mockito.when(openAIPort.getOpenAIResponse(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble()))
                .thenReturn("{\"text_message\":\"message\",\"final_text\":\"¡Hola, mundo!\"}");

        String result = useCase.execute(sourceLanguage, targetLanguage, originalText);

        assertEquals("¡Hola, mundo!", result);
    }

    @Test
    void when_openAiResponseIsInvalid_thenReturnErrorMessage(){
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        String originalText = "Hello, world!";

        Mockito.when(googleTranslatePort.translateText(originalText, targetLanguage))
                .thenReturn("¡Hola, mundo!");

        Mockito.when(openAIPort.getOpenAIResponse(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble()))
                .thenReturn("{\"text_message\":\"message\",\"mid_textual\":\"¡Hola, mundo!\"}");

        String result = useCase.execute(sourceLanguage, targetLanguage, originalText);

        assertEquals("Error parsing OpenAI response: Campo 'final_text' no encontrado en respuesta de OpenAI", result);

    }
}
