package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChatRequestDto;
import com.amool.application.usecases.ProcessChatMessageUseCase;
import com.amool.domain.model.ChatMessage;
import com.amool.security.JwtUserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ProcessChatMessageUseCase chatService;

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
