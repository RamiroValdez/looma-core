package com.amool.hexagonal.adapters.out.openiaapi;

import com.amool.hexagonal.application.port.out.TagSuggestionPort;
import com.amool.hexagonal.application.port.out.OpenAIPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagSuggestionAiAdapter implements TagSuggestionPort {

    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final double DEFAULT_TEMPERATURE = 0.4;

    private final OpenAIPort openAIPort;
    private final ObjectMapper objectMapper;
    private final String model;
    private final double temperature;

    public TagSuggestionAiAdapter(OpenAIPort openAIPort,
                                  ObjectMapper objectMapper,
                                  @Value("${ai.tags.model:" + DEFAULT_MODEL + "}") String model,
                                  @Value("${ai.tags.temperature:" + DEFAULT_TEMPERATURE + "}") double temperature) {
        this.openAIPort = openAIPort;
        this.objectMapper = objectMapper;
        this.model = StringUtils.hasText(model) ? model : DEFAULT_MODEL;
        this.temperature = temperature;
    }

    @Override
    public List<String> getSuggestedTags(String description, String title, Set<String> existingTags) {

        String systemPrompt = this.buildSystemPrompt();
        String userPrompt = this.buildUserPrompt(description, title, existingTags);

        String response = openAIPort.getOpenAIResponse(userPrompt, systemPrompt, model, temperature);

        List<String> parsedTags = parseTags(response);

        if (CollectionUtils.isEmpty(parsedTags)) {
            return List.of();
        }

        return parsedTags.stream()
                .filter(StringUtils::hasText)
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    private String buildSystemPrompt() {
        return "Eres un asistente que sugiere etiquetas concisas para obras literarias. " +
                "Devuelve solo un array JSON de strings en español, con máximo 10 elementos únicos. " +
                "Cada etiqueta debe ser de una o dos palabras, en minúsculas, sin símbolos especiales.";
    }

    private String buildUserPrompt(String description, String title, Set<String> existingTags) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.hasText(title)) {
            builder.append("Título: \"")
                    .append(title.trim())
                    .append("\"\n\n");
        }

        builder.append("Descripción:\n")
                .append(description.trim())
                .append("\n\n");

        if (!CollectionUtils.isEmpty(existingTags)) {
            builder.append("Etiquetas ya usadas: ")
                    .append(String.join(", ", existingTags))
                    .append("\n\nEvita repetirlas.\n");
        }

        builder.append("Genera hasta 10 etiquetas nuevas en formato JSON.");
        return builder.toString();
    }

    private List<String> parseTags(String response) {
        if (!StringUtils.hasText(response)) {
            return List.of();
        }

        String trimmedResponse = response.trim();

        try {
            return objectMapper.readValue(trimmedResponse, new TypeReference<List<String>>() {});
        } catch (Exception ignored) {
        }

        int start = trimmedResponse.indexOf('[');
        int end = trimmedResponse.lastIndexOf(']');

        if (start >= 0 && end > start) {
            String jsonArray = trimmedResponse.substring(start, end + 1);
            try {
                return objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});
            } catch (Exception ignored) {
            }
        }

        String[] candidates = trimmedResponse.split("[\n,]");
        Set<String> normalized = new LinkedHashSet<>();
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                normalized.add(candidate.trim());
            }
        }
        return new ArrayList<>(normalized);
    }
}
