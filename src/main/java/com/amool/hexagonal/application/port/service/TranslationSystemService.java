package com.amool.hexagonal.application.port.service;

import com.amool.hexagonal.adapters.out.openiaapi.OpenAIAdapter;
import com.amool.hexagonal.application.port.in.CreateLanguageVersionUseCase;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class TranslationSystemService implements CreateLanguageVersionUseCase {

    private final OpenAIAdapter openAIPort;

    public TranslationSystemService(OpenAIAdapter openAIPort) {
        this.openAIPort = openAIPort;
    }

    @Override
    public String execute(String sourceLanguage, String targetLanguage , String originalText) {

        String translationText = this.sendTextToTranslate(originalText);

        return this.createPromptToCompareAndCreateVersion(originalText,translationText,sourceLanguage,targetLanguage);
    }

    private String sendTextToTranslate(String originalText){
        return "";
    }

    private String createPromptToCompareAndCreateVersion(String originalText, String translationText, String sourceLanguage, String targetLanguage){

        String systemPrompt = this.setLanguagesToSystemPrompt(sourceLanguage, targetLanguage);

        String userPrompt = "SOURCE_TEXT: " + originalText + "\n" + "PROVIDED_TRANSLATION: " + translationText;
        String model = "gpt-4";

        return this.sendToOpenAI(userPrompt, systemPrompt, model, 0.5);
    }

    private String setLanguagesToSystemPrompt(String sourceLanguage, String targetLanguage){

        String prompt = new ClassPathResource("systemPrompt.txt").toString();
            prompt.replace("SOURCE_LANGUAGE: [Specify here, e.g., Spanish]", "SOURCE_LANGUAGE: " + sourceLanguage);
            prompt.replace("TARGET_LANGUAGE: [Specify here, e.g., English]",  "TARGET_LANGUAGE: " + targetLanguage);

       return prompt;
    }


    private String sendToOpenAI(String userPrompt, String systemPrompt, String model, Double temperature) {

        return openAIPort.getOpenAIResponse(userPrompt, systemPrompt, model, temperature);

    }
}
