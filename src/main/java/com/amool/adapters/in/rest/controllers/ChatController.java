package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChatRequestDto;
import com.amool.application.service.ChatService;
import com.amool.domain.model.ChatMessage;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestBody ChatRequestDto request,
            @AuthenticationPrincipal JwtUserPrincipal user) {
        
        ChatMessage response = chatService.processMessage(
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
            chatService.getConversation(user.getUserId(), chapterId)
        );
    }
}
