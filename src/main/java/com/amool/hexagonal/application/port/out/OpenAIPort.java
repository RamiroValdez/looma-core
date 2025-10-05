package com.amool.hexagonal.application.port.out;

public interface OpenAIPort {

    String getOpenAIResponse(String userPrompt, String systemPrompt, String model, Double temperature);

}
