package com.amool.application.usecase;

import com.amool.application.port.out.GoogleTranslatePort;
import com.amool.application.port.out.OpenAIPort;
import com.amool.application.usecases.CreateLanguageVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateLanguageVersionTest {

    private OpenAIPort openAIPort;
    private GoogleTranslatePort googleTranslatePort;
    private CreateLanguageVersion useCase;

    @BeforeEach
    void setUp() {
        openAIPort = mock(OpenAIPort.class);
        googleTranslatePort = mock(GoogleTranslatePort.class);
        useCase = new CreateLanguageVersion(openAIPort, googleTranslatePort);
    }

    private void givenGoogleTranslateReturns(String originalText, String targetLanguage, String translated) {
        when(googleTranslatePort.translateText(originalText, targetLanguage)).thenReturn(translated);
    }

    private void givenOpenAIRespondsWithFinalText(String finalText) {
        String json = "{\"text_message\":\"message\",\"final_text\":\"" + finalText + "\"}";
        when(openAIPort.getOpenAIResponse(anyString(), anyString(), anyString(), anyDouble())).thenReturn(json);
    }

    private void givenOpenAIRespondsInvalidMissingFinalText() {
        String json = "{\"text_message\":\"message\",\"mid_textual\":\"¡Hola, mundo!\"}"; // falta final_text
        when(openAIPort.getOpenAIResponse(anyString(), anyString(), anyString(), anyDouble())).thenReturn(json);
    }

    private String whenCreateLanguageVersion(String sourceLang, String targetLang, String originalText) {
        return useCase.execute(sourceLang, targetLang, originalText);
    }

    private void thenResultIs(String result, String expected) {
        assertEquals(expected, result);
    }

    @Test
    void when_execute_thenReturnCreatedVersion() {
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        String originalText = "Hello, world!";
        givenGoogleTranslateReturns(originalText, targetLanguage, "¡Hola, mundo!");
        givenOpenAIRespondsWithFinalText("¡Hola, mundo!");

        String result = whenCreateLanguageVersion(sourceLanguage, targetLanguage, originalText);

        thenResultIs(result, "¡Hola, mundo!");
    }

    @Test
    void when_openAiResponseIsInvalid_thenReturnErrorMessage(){
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        String originalText = "Hello, world!";
        givenGoogleTranslateReturns(originalText, targetLanguage, "¡Hola, mundo!");
        givenOpenAIRespondsInvalidMissingFinalText();

        String result = whenCreateLanguageVersion(sourceLanguage, targetLanguage, originalText);

        thenResultIs(result, "Error parsing OpenAI response: Campo 'final_text' no encontrado en respuesta de OpenAI");
    }
}
