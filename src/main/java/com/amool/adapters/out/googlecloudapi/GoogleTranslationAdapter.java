package com.amool.adapters.out.googlecloudapi;

import com.amool.application.port.out.GoogleTranslatePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class GoogleTranslationAdapter implements GoogleTranslatePort {

    @Value("${GOOGLE_API_KEY}")
    private String API_KEY;

    private static final String ENDPOINT =
            "https://translation.googleapis.com/language/translate/v2";

    @Override
    public String translateText(String text, String targetLanguage) {
       try {
           String raw = this.sendRequest(text, targetLanguage);

           return this.cleanRawTranslation(raw);

       } catch (Exception e) {
              e.printStackTrace();
              return "Translation Error";
       }
    }

    private String sendRequest(String text, String targetLanguage) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("q", text)
                .queryParam("target", targetLanguage)
                .queryParam("key", API_KEY);

        Map<String, Object> response = restTemplate.getForObject(
                builder.toUriString(), Map.class
        );

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
