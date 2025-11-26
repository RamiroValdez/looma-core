package com.amool.application.usecases;

import com.amool.application.port.out.GoogleTranslatePort;
import com.amool.application.port.out.OpenAIPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CreateLanguageVersion {

    private final OpenAIPort openAIPort;
    private final GoogleTranslatePort googleTranslatePort;
    private static final int CHUNKING_CHAR_THRESHOLD = 4500;
    private static final int MAX_SEGMENT_SIZE = 3500;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public CreateLanguageVersion(OpenAIPort openAIPort, GoogleTranslatePort googleTranslatePort) {
        this.googleTranslatePort = googleTranslatePort;
        this.openAIPort = openAIPort;
    }

    public String execute(String sourceLanguage, String targetLanguage , String originalText) {
        String cleanedText = this.cleanText(originalText);
        if (needsChunking(cleanedText)) {
            return executeChunked(sourceLanguage, targetLanguage, cleanedText);
        }
        String translationText = sendTextToTranslate(cleanedText, targetLanguage);
        return createPromptToCompareAndCreateVersion(cleanedText, translationText, sourceLanguage, targetLanguage);
    }

    private boolean needsChunking(String text) {
        return text.length() > CHUNKING_CHAR_THRESHOLD;
    }

    private String executeChunked(String sourceLanguage, String targetLanguage, String cleanedText) {
        List<TextSegment> segments = organizeText(cleanedText, MAX_SEGMENT_SIZE);
        List<SegmentResult> results = processSegmentsParallel(segments, sourceLanguage, targetLanguage);
        return results.stream()
                .sorted(Comparator.comparingInt(SegmentResult::index))
                .map(SegmentResult::finalText)
                .collect(Collectors.joining("\n\n"));
    }

    @SuppressWarnings("unused")
    private List<SegmentResult> processSegmentsParallel(List<TextSegment> segments,
                                                        String sourceLanguage,
                                                        String targetLanguage) {
        List<CompletableFuture<SegmentResult>> futures = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            final int idx = i;
            futures.add(CompletableFuture.supplyAsync(() ->
                    processSingleSegment(idx, segments.get(idx), sourceLanguage, targetLanguage, idx > 0 ? segments.get(idx - 1).text() : null), executor));
        }
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private List<SegmentResult> processSegmentsSequential(List<TextSegment> segments,
                                                          String sourceLanguage,
                                                          String targetLanguage) {
        List<SegmentResult> results = new ArrayList<>();
        String previousText = null;
        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);
            SegmentResult result = processSingleSegment(i, segment, sourceLanguage, targetLanguage, previousText);
            results.add(result);
            previousText = segment.text();
        }
        return results;
    }

    private SegmentResult processSingleSegment(int index,
                                               TextSegment segment,
                                               String sourceLanguage,
                                               String targetLanguage,
                                               String previousSegmentText) {
        String sourceChunk = segment.text();
        String translatedChunk = sendTextToTranslate(sourceChunk, targetLanguage);

        String systemPrompt = setLanguagesToSystemPrompt(sourceLanguage, targetLanguage);
        String userPrompt = buildUserPromptWithMetadata(index, sourceChunk, translatedChunk, previousSegmentText);

        String model = "gpt-4o-mini";
        String openAiResponse = sendToOpenAI(userPrompt, systemPrompt, model, 0.1);
        String finalText = obtainFinalTextFromResponse(openAiResponse);

        return new SegmentResult(index, finalText);
    }

    private String buildUserPromptWithMetadata(int index,
                                               String sourceChunk,
                                               String translatedChunk,
                                               String previousSegmentText) {
        StringBuilder sb = new StringBuilder();
        sb.append("### SEGMENT_COMPARISON\n");
        sb.append("SEGMENT_INDEX: ").append(index).append("\n");
        if (previousSegmentText != null) {
            sb.append("PREVIOUS_SOURCE_SEGMENT: ").append(previousSegmentText).append("\n");
        }
        sb.append("<<<SOURCE_SEGMENT>>> ").append(sourceChunk).append(" <<<END_SOURCE_SEGMENT>>>\n");
        sb.append("<<<PROVIDED_TRANSLATION_SEGMENT>>> ").append(translatedChunk).append(" <<<END_PROVIDED_TRANSLATION_SEGMENT>>>\n");
        sb.append("Instrucciones: Analiza sólo este segmento asegurando coherencia con el anterior si se proporciona.");
        return sb.toString();
    }

    private String cleanText(String text){
        return text.replaceAll("<br\\s*/?>", " ").replaceAll(" +", " ").trim();
    }

    private String sendTextToTranslate(String originalText, String targetLanguage){
        return this.googleTranslatePort.translateText(originalText, targetLanguage);
    }

    private List<TextSegment> organizeText(String text, int maxSize) {
        Document document = Document.from(text);
        DocumentSplitter splitter = DocumentSplitters.recursive(maxSize, 0);
        return splitter.split(document);
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
            JsonNode finalTextNode = jsonNode.get("final_text");
            if (finalTextNode == null) {
                throw new IOException("Campo 'final_text' no encontrado en respuesta de OpenAI");
            }
            return jsonNode.get("final_text").asText();
        }catch (IOException e){
            return "Error parsing OpenAI response: " + e.getMessage();
        }
    }

    private String sendToOpenAI(String userPrompt, String systemPrompt, String model, Double temperature) {
        return openAIPort.getOpenAIResponse(userPrompt, systemPrompt, model, temperature);
    }

    private record SegmentResult(int index, String finalText) {}

    public void shutdown() {
        executor.shutdown();
    }
}
