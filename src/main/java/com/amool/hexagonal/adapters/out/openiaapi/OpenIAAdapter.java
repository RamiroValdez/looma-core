package com.amool.hexagonal.adapters.out.openiaapi;

import com.amool.hexagonal.application.port.out.OpenIAPort;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenIAAdapter implements OpenIAPort {

    private final ChatModel chatModel;

    @Autowired
    public OpenIAAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String getOpenIAResponse(String userPrompt) {

        SystemMessage systemMessage = new SystemMessage("You are a assintant to translate text to spanish");
        UserMessage userMessage = new UserMessage(userPrompt);


        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.7)
                .build();


        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);

        String responseContent = chatModel.call(prompt).getResult().getOutput().getText();

        return responseContent;

    }

}
