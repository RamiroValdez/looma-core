package com.amool.application.port.out;

import java.util.List;

public interface ChatAIPort {
    String generateResponse(String prompt, String context, List<String> conversationHistory);
}

