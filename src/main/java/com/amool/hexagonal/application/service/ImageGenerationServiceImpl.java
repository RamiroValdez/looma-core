package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.ImageGenerationService;
import com.amool.hexagonal.application.port.out.OpenAIImagePort;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerationServiceImpl implements ImageGenerationService {

    private final OpenAIImagePort openAIImagePort;

    public ImageGenerationServiceImpl(OpenAIImagePort openAIImagePort) {
        this.openAIImagePort = openAIImagePort;
    }

    @Override
    public String generateImageUrl(String userPrompt) {
        return openAIImagePort.generateImageUrl(userPrompt);
    }
}
