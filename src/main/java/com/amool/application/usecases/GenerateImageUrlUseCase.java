package com.amool.application.usecases;

import com.amool.application.port.out.OpenAIImagePort;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GenerateImageUrlUseCase {

    private final OpenAIImagePort openAIImagePort;

    public GenerateImageUrlUseCase(OpenAIImagePort openAIImagePort) {
        this.openAIImagePort = openAIImagePort;
    }

    public String execute(String artisticStyle, String colorPalette, String composition, String description) {

        String prompt = this.generatePrompt(artisticStyle, colorPalette, composition, description);

        return openAIImagePort.generateImageUrl(prompt);
    }

    private String generatePrompt(String artisticStyle, String colorPalette, String composition, String description) {
        try {
            ClassPathResource filePrompt = new ClassPathResource("/static/imageGeneratorPrompt.txt");
            String prompt = new String(filePrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            prompt = prompt.replace("{artisticStyle}", artisticStyle);
            prompt = prompt.replace("{colorPalette}", colorPalette);
            prompt = prompt.replace("{composition}", composition);
            prompt = prompt.replace("{description}", description);

            return prompt;
        }catch (IOException e){
            return "Error reading system prompt file: " + e.getMessage();
        }
    }
}
