package com.amool.hexagonal.adapters.out.googlecloudapi;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import com.amool.hexagonal.application.port.out.GoogleTranslatePort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value; // ¡Necesario para @Value!

import java.util.List;
import java.util.Map;

@Component
public class GoogleTranslationAdapter implements GoogleTranslatePort {

    @Value("${GOOGLE_API_KEY:}")    
    private String API_KEY; 

    private static final String ENDPOINT = 
            "https://translation.googleapis.com/language/translate/v2"; 
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleTranslationAdapter.class);

    @Override
    public String translateText(String text, String targetLanguage) {
        try {
            logger.info("Using Google API Key: {}", API_KEY != null && !API_KEY.isEmpty() ? "KEY_FOUND" : "KEY_MISSING");

            if (API_KEY == null || API_KEY.isEmpty()) {
                 throw new RuntimeException("Google API Key is missing. Check your application.properties file.");
            }
            
            String raw = this.sendRequest(text, targetLanguage);
            return this.cleanRawTranslation(raw);

        } catch (HttpClientErrorException forbiddenE) {
            if (forbiddenE.getStatusCode().value() == 403) {
                logger.error("403 Forbidden from Google API. Response Body: {}", forbiddenE.getResponseBodyAsString());
                throw new RuntimeException("External Translation API Forbidden: " + forbiddenE.getResponseBodyAsString(), forbiddenE);
            }
            throw new RuntimeException("HTTP Client Error accessing Google API.", forbiddenE);
        } catch (Exception e) {
            logger.error("Unexpected Error during Google translation.", e);
            throw new RuntimeException("Translation service failed.", e);
        }
    }
    
    private String sendRequest(String text, String targetLanguage) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("q", text)
                .queryParam("target", targetLanguage)
                .queryParam("key", API_KEY);

        String uri = builder.toUriString();
        logger.info("Calling Google API URL: {}", uri.contains(API_KEY) ? uri.replace(API_KEY, "****************") : uri);

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        List<Map<String, Object>> translations =
                (List<Map<String, Object>>) ((Map) response.get("data")).get("translations");

        return (String) translations.getFirst().get("translatedText");
    }

    private String cleanRawTranslation(String raw) {
        if (raw == null) return "";
        String clean = raw;

        clean = clean.replaceAll("\\s+", " ").trim();
        clean = clean.replaceAll("[\\u200B-\\u200D\\uFEFF]", "");

        return clean;
    }
}