package com.amool.hexagonal.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TranslationSystemServiceImpl.class);
    private final OpenAIPort openAIPort;
    private final GoogleTranslatePort googleTranslatePort;

    public TranslationSystemServiceImpl(OpenAIPort openAIPort, GoogleTranslatePort googleTranslatePort) {
        this.googleTranslatePort = googleTranslatePort;
        this.openAIPort = openAIPort;
    }

    @Override
    public String CreateLanguageVersion(String sourceLanguage, String targetLanguage , String originalText) {
        try {
            String cleanedText = this.cleanText(originalText);

            String translationText = this.sendTextToTranslate(cleanedText, targetLanguage);
            
            logger.info("Translation received from Google: {}", translationText);

            return this.createPromptToCompareAndCreateVersion(originalText,translationText,sourceLanguage,targetLanguage);

        } catch (RuntimeException e) {
            logger.error("CreateLanguageVersion failed.", e);
            return "ERROR: " + e.getMessage();
        }
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
        
        final int MAX_RETRIES = 3;
        
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                String openAiResponse = this.sendToOpenAI(userPrompt, systemPrompt, model, 0.1);
                return this.obtainFinalTextFromResponse(openAiResponse);
            } catch (RuntimeException e) {
                
                if (attempt < MAX_RETRIES - 1 && e.getMessage().contains("Error parsing OpenAI response")) {
                    logger.warn("OpenAI call failed on attempt {}. Retrying in 1 second...", attempt + 1);
                    try {
                        Thread.sleep(1000); 
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e; 
                }
            }
        }
        throw new RuntimeException("Failed to get valid response from OpenAI after " + MAX_RETRIES + " attempts.");
    }

    private String setLanguagesToSystemPrompt(String sourceLanguage, String targetLanguage) {
        String prompt = ""; 

        try {
            ClassPathResource filePrompt = new ClassPathResource("/static/systemPrompt.txt");
            
            prompt = new String(filePrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

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
            throw new RuntimeException("Error parsing OpenAI response: " + e.getMessage()); 
        }
    }

    private String sendToOpenAI(String userPrompt, String systemPrompt, String model, Double temperature) {

        return openAIPort.getOpenAIResponse(userPrompt, systemPrompt, model, temperature);

    }
}