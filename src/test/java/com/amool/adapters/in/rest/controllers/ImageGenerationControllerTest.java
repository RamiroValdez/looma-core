package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.*;
import com.amool.application.usecases.GenerateImageUrlUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ImageGenerationControllerTest {

    private ImageGenerationController controller;
    private GenerateImageUrlUseCase generateImageUrlUseCase;

    @BeforeEach
    void setUp() {
        generateImageUrlUseCase = Mockito.mock(GenerateImageUrlUseCase.class);
        controller = new ImageGenerationController(generateImageUrlUseCase);
    }

    @Test
    @DisplayName("POST /api/images/generate - Should return URL when prompt is valid")
    void generate_shouldReturnUrl_whenPromptIsValid() {
        // Given
        ImagePromptDto prompt = givenValidPrompt();
        String expectedUrl = "https://cdn.example.com/image.png";
        givenGenerationWillReturn(expectedUrl);

        // When
        ResponseEntity<ImageUrlResponseDto> response = whenGenerating(prompt);

        // Then
        thenShouldReturnOk(response);
        thenBodyHasUrl(response, expectedUrl);
        thenUseCaseWasCalledWith("style-1", "palette-2", "comp-3", "An epic dragon over mountains");
    }

    @Test
    @DisplayName("GET /api/images/color-palettes/obtain-all - Should return 8 color palettes")
    void getAllColorPalettes_shouldReturnEightPalettes() {
        // When
        ResponseEntity<List<ColorPaletteDto>> response = whenGettingAllColorPalettes();

        // Then
        thenShouldReturnOk(response);
        thenListSizeIs(response.getBody(), 8);
        thenItemNameIs(response.getBody(), 0, "Tonos Neblinosos y Pastel");
    }

    @Test
    @DisplayName("GET /api/images/compositions/obtain-all - Should return 8 compositions")
    void getAllCompositions_shouldReturnEightCompositions() {
        // When
        ResponseEntity<List<CompositionDto>> response = whenGettingAllCompositions();

        // Then
        thenShouldReturnOk(response);
        thenListSizeIs(response.getBody(), 8);
        thenItemNameIs(response.getBody(), 0, "Primer Plano y Foco");
    }

    @Test
    @DisplayName("GET /api/images/artistic-styles/obtain-all - Should return 8 artistic styles")
    void getAllArtisticStyles_shouldReturnEightStyles() {
        // When
        ResponseEntity<List<ArtisticStyleDto>> response = whenGettingAllArtisticStyles();

        // Then
        thenShouldReturnOk(response);
        thenListSizeIs(response.getBody(), 8);
        thenItemNameIs(response.getBody(), 0, "Fotorrealista");
    }

    // ===== Given =====
    private ImagePromptDto givenValidPrompt() {
        return new ImagePromptDto(
                "style-1",
                "palette-2",
                "comp-3",
                "An epic dragon over mountains"
        );
    }

    private void givenGenerationWillReturn(String url) {
        when(generateImageUrlUseCase.execute(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(url);
    }

    // ===== When =====
    private ResponseEntity<ImageUrlResponseDto> whenGenerating(ImagePromptDto prompt) {
        return controller.generate(prompt);
    }

    private ResponseEntity<List<ColorPaletteDto>> whenGettingAllColorPalettes() {
        return controller.getAllColorPalettes();
    }

    private ResponseEntity<List<CompositionDto>> whenGettingAllCompositions() {
        return controller.getAllCompositions();
    }

    private ResponseEntity<List<ArtisticStyleDto>> whenGettingAllArtisticStyles() {
        return controller.getAllArtisticStyles();
    }

    // ===== Then =====
    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenBodyHasUrl(ResponseEntity<ImageUrlResponseDto> response, String expectedUrl) {
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().url());
    }

    private void thenUseCaseWasCalledWith(String styleId, String paletteId, String compositionId, String description) {
        verify(generateImageUrlUseCase, times(1))
                .execute(eq(styleId), eq(paletteId), eq(compositionId), eq(description));
    }

    private <T> void thenListSizeIs(List<T> list, int expected) {
        assertNotNull(list);
        assertEquals(expected, list.size());
    }

    private void thenItemNameIs(List<? extends Object> list, int index, String expectedName) {
        Object item = list.get(index);
        if (item instanceof ColorPaletteDto cp) {
            assertEquals(expectedName, cp.name());
        } else if (item instanceof CompositionDto c) {
            assertEquals(expectedName, c.name());
        } else if (item instanceof ArtisticStyleDto a) {
            assertEquals(expectedName, a.name());
        } else {
            fail("Unexpected DTO type");
        }
    }
}
