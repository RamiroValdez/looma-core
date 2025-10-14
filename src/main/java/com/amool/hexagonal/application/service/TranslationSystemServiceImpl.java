package com.amool.hexagonal.application.service;


import com.amool.hexagonal.application.port.in.TranslationSystemService;
import com.amool.hexagonal.application.port.out.GoogleTranslatePort;
import com.amool.hexagonal.application.port.out.OpenAIPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TranslationSystemServiceImpl implements TranslationSystemService {

    private final OpenAIPort openAIPort;
    private final GoogleTranslatePort googleTranslatePort;

    public TranslationSystemServiceImpl(OpenAIPort openAIPort, GoogleTranslatePort googleTranslatePort) {
        this.googleTranslatePort = googleTranslatePort;
        this.openAIPort = openAIPort;
    }

    @Override
    public String CreateLanguageVersion(String sourceLanguage, String targetLanguage , String originalText) {

        String cleanedText = this.cleanText(originalText);

        String translationText = this.sendTextToTranslate(cleanedText, targetLanguage);

        return this.createPromptToCompareAndCreateVersion(originalText,translationText,sourceLanguage,targetLanguage);
    }

    private String cleanText(String text){
        return text.replaceAll("<br\\s*/?>", " ").replaceAll(" +", " ").trim();
    }

    private String sendTextToTranslate(String originalText, String targetLanguage){
        return this.googleTranslatePort.translateText(originalText, targetLanguage);
    }

    private String createPromptToCompareAndCreateVersion(String originalText, String translationText, String sourceLanguage, String targetLanguage){

        String systemPrompt = this.setLanguagesToSystemPrompt(sourceLanguage, targetLanguage);

        String userPrompt =
                "### Execution \n <<<SOURCE>>> \n SOURCE_TEXT: " + originalText + "\n <<<END SOURCE>>> \n" +
                "<<<PROVIDED_TRANSLATION>>> \n PROVIDED_TRANSLATION: " + translationText + "\n <<<END TRANSLATION>>>";

        String model = "gpt-4o-mini";

        String openAiResponse = this.sendToOpenAI(userPrompt, systemPrompt, model, 0.1);

        return this.obtainFinalTextFromResponse(openAiResponse);
    }

    private String setLanguagesToSystemPrompt(String sourceLanguage, String targetLanguage) {

        try {
            ClassPathResource filePrompt = new ClassPathResource("/static/systemPrompt.txt");
            String prompt = new String(filePrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            prompt = prompt.replace("SOURCE_LANGUAGE: []", "SOURCE_LANGUAGE: " + sourceLanguage);
            prompt = prompt.replace("TARGET_LANGUAGE: []", "TARGET_LANGUAGE: " + targetLanguage);

            return prompt;
        }catch (IOException e){
            return "Error reading system prompt file: " + e.getMessage();
        }

    }

    private String obtainFinalTextFromResponse(String openAiResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(openAiResponse);
            return jsonNode.get("final_text").asText();
        }catch (IOException e){
            return "Error parsing OpenAI response: " + e.getMessage();
        }
    }

    private String sendToOpenAI(String userPrompt, String systemPrompt, String model, Double temperature) {

        return openAIPort.getOpenAIResponse(userPrompt, systemPrompt, model, temperature);

    }
}
