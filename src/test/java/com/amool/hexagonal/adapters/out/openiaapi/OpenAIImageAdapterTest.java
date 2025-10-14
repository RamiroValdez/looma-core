package com.amool.hexagonal.adapters.out.openiaapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAIImageAdapterTest {

    @Test
    @DisplayName("generateImageUrl returns URL when ImageModel returns a valid response")
    void generateImageUrl_ReturnsUrl() {
        ImageModel imageModel = mock(ImageModel.class);
        ImageResponse response = mock(ImageResponse.class, Mockito.RETURNS_DEEP_STUBS);

        when(response.getResult().getOutput().getUrl()).thenReturn("https://img.example/test.png");
        when(imageModel.call(any(ImagePrompt.class))).thenReturn(response);

        OpenAIImageAdapter adapter = new OpenAIImageAdapter(imageModel);
        String url = adapter.generateImageUrl("prompt");

        assertEquals("https://img.example/test.png", url);
        verify(imageModel, times(1)).call(any(ImagePrompt.class));
    }

    @Test
    @DisplayName("generateImageUrl returns null when ImageModel returns null response")
    void generateImageUrl_NullResponse() {
        ImageModel imageModel = mock(ImageModel.class);
        when(imageModel.call(any(ImagePrompt.class))).thenReturn(null);

        OpenAIImageAdapter adapter = new OpenAIImageAdapter(imageModel);
        String url = adapter.generateImageUrl("prompt");

        assertNull(url);
    }

    @Test
    @DisplayName("generateImageUrl returns null when nested result is null")
    void generateImageUrl_NestedNull() {
        ImageModel imageModel = mock(ImageModel.class);
        ImageResponse response = mock(ImageResponse.class, Mockito.RETURNS_DEEP_STUBS);

        when(response.getResult()).thenReturn(null);
        when(imageModel.call(any(ImagePrompt.class))).thenReturn(response);

        OpenAIImageAdapter adapter = new OpenAIImageAdapter(imageModel);
        String url = adapter.generateImageUrl("prompt");

        assertNull(url);
    }
}
