package com.amool.hexagonal.application.port.in;

public interface ImageGenerationService {

    String generateImageUrl(String artisticStyle, String colorPalette, String composition, String description);
}
