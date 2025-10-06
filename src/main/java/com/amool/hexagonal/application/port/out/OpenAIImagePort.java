package com.amool.hexagonal.application.port.out;

public interface OpenAIImagePort {

    String generateImageUrl(String prompt);
}
