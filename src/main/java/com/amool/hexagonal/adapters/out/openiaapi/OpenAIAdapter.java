package com.amool.hexagonal.adapters.out.openiaapi;

import com.amool.hexagonal.application.port.out.OpenAIPort;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAIAdapter implements OpenAIPort {

    private final ChatModel chatModel;

    @Autowired
    public OpenAIAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String getOpenAIResponse(String userPrompt,  String systemPrompt, String model,  Double temperature) {

        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(userPrompt);


        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();


        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);

        return chatModel.call(prompt).getResult().getOutput().getText();

    }

}
