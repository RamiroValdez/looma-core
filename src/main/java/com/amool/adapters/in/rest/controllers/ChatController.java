package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChatRequestDto;
import com.amool.application.usecases.GetChatConversation;
import com.amool.application.usecases.ProcessChatMessage;
import com.amool.domain.model.ChatMessage;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ProcessChatMessage processChatMessage;
    private final GetChatConversation getChatConversation;

    public ChatController(
            ProcessChatMessage processChatMessage,
            GetChatConversation getChatConversation) {
        this.processChatMessage = processChatMessage;
        this.getChatConversation = getChatConversation;
    }

    @PostMapping("/message")
    public ResponseEntity<List<ChatMessage>> sendMessage(
            @RequestBody ChatRequestDto request,
            @AuthenticationPrincipal JwtUserPrincipal user) {
        List<ChatMessage> response = processChatMessage.execute(
                user.getUserId(),
                request.getChapterId(),
                request.getMessage(),
                request.getChapterContent()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{chapterId}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal JwtUserPrincipal user) {
        return ResponseEntity.ok(
            getChatConversation.execute(user.getUserId(), chapterId)
        );
    }
}
