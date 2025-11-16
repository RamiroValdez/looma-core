package com.amool.adapters.out.chat;

import com.amool.application.port.out.ChatAIPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringAIChatAdapter implements ChatAIPort {
    
    private final ChatClient chatClient;

    public SpringAIChatAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateResponse(String prompt, String context, List<String> conversationHistory) {
        try {
            return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, hubo un error al generar la respuesta. Por favor, inténtalo de nuevo más tarde.";
        }
    }
}

