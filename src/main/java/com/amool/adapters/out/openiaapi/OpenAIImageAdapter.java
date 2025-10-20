package com.amool.adapters.out.openiaapi;

import com.amool.application.port.out.OpenAIImagePort;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Component;

@Component
public class OpenAIImageAdapter implements OpenAIImagePort {

    private final ImageModel imageModel;

    public OpenAIImageAdapter(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Override
    public String generateImageUrl(String prompt) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .model("dall-e-3")
                .N(1)
                .width(1024)
                .height(1024)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);

        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return null;
        }

        return response.getResult().getOutput().getUrl();
    }
}
