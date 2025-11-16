package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChatRequestDto;
import com.amool.application.usecases.GetChatConversationUseCase;
import com.amool.application.usecases.ProcessChatMessageUseCase;
import com.amool.domain.model.ChatMessage;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChatControllerTest {

    private ChatController chatController;
    private ProcessChatMessageUseCase processChatMessageUseCase;
    private GetChatConversationUseCase getChatConversationUseCase;

    private static final Long TEST_USER_ID = 100L;
    private static final Long TEST_CHAPTER_ID = 10L;
    private static final String TEST_MESSAGE = "¿Puedes resumir este capítulo?";
    private static final String TEST_CHAPTER_CONTENT = "Contenido del capítulo de prueba...";
    private static final String TEST_RESPONSE = "Este capítulo trata sobre...";

    private JwtUserPrincipal testUserPrincipal;

    @BeforeEach
    public void setUp() {
        processChatMessageUseCase = Mockito.mock(ProcessChatMessageUseCase.class);
        getChatConversationUseCase = Mockito.mock(GetChatConversationUseCase.class);

        chatController = new ChatController(
                processChatMessageUseCase,
                getChatConversationUseCase
        );

        testUserPrincipal = new JwtUserPrincipal(
                TEST_USER_ID,
                "test@example.com",
                "Test",
                "User",
                "testuser"
        );
    }

    // ========== Tests for sendMessage endpoint ==========

    @Test
    @DisplayName("POST /api/chat/message - Should process message and return AI response segments")
    public void sendMessage_shouldProcessMessage_whenValid() {
        // Given
        ChatRequestDto request = givenValidChatRequest();
        List<ChatMessage> expectedResponse = givenAIWillRespondSegments();

        // When
        ResponseEntity<List<ChatMessage>> response = whenSendMessage(request);

        // Then
        thenResponseIsOk(response);
        thenResponseContainsSegments(response, expectedResponse);
        thenProcessMessageUseCaseWasInvoked();
    }

    @Test
    @DisplayName("POST /api/chat/message - Should handle empty message")
    public void sendMessage_shouldHandleEmptyMessage_whenMessageIsEmpty() {
        // Given
        ChatRequestDto request = givenChatRequestWithEmptyMessage();
        givenAIWillRespondSegments();

        // When
        ResponseEntity<List<ChatMessage>> response = whenSendMessage(request);

        // Then
        thenResponseIsOk(response);
        verify(processChatMessageUseCase).execute(
                eq(TEST_USER_ID),
                eq(TEST_CHAPTER_ID),
                eq(""),
                anyString()
        );
    }

    @Test
    @DisplayName("POST /api/chat/message - Should process message without chapter content")
    public void sendMessage_shouldProcessMessage_whenChapterContentIsNull() {
        // Given
        ChatRequestDto request = givenChatRequestWithoutChapterContent();
        givenAIWillRespondSegments();

        // When
        ResponseEntity<List<ChatMessage>> response = whenSendMessage(request);

        // Then
        thenResponseIsOk(response);
        verify(processChatMessageUseCase).execute(
                eq(TEST_USER_ID),
                eq(TEST_CHAPTER_ID),
                eq(TEST_MESSAGE),
                isNull()
        );
    }

    // ========== Tests for getConversation endpoint ==========

    @Test
    @DisplayName("GET /api/chat/conversation/{chapterId} - Should return conversation history")
    public void getConversation_shouldReturnHistory_whenMessagesExist() {
        // Given
        List<ChatMessage> expectedConversation = givenConversationExists();

        // When
        ResponseEntity<List<ChatMessage>> response = whenGetConversation();

        // Then
        thenResponseIsOk(response);
        thenResponseContainsConversation(response, expectedConversation);
        thenGetConversationUseCaseWasInvoked();
    }

    @Test
    @DisplayName("GET /api/chat/conversation/{chapterId} - Should return empty list when no messages")
    public void getConversation_shouldReturnEmptyList_whenNoMessages() {
        // Given
        givenNoConversationExists();

        // When
        ResponseEntity<List<ChatMessage>> response = whenGetConversation();

        // Then
        thenResponseIsOk(response);
        thenResponseContainsEmptyConversation(response);
    }

    @Test
    @DisplayName("GET /api/chat/conversation/{chapterId} - Should call use case with correct parameters")
    public void getConversation_shouldPassCorrectParameters() {
        // Given
        givenConversationExists();

        // When
        whenGetConversation();

        // Then
        verify(getChatConversationUseCase).execute(TEST_USER_ID, TEST_CHAPTER_ID);
    }

    // ========== Given Methods ==========

    private ChatRequestDto givenValidChatRequest() {
        ChatRequestDto request = new ChatRequestDto();
        request.setChapterId(TEST_CHAPTER_ID);
        request.setMessage(TEST_MESSAGE);
        request.setChapterContent(TEST_CHAPTER_CONTENT);
        return request;
    }

    private ChatRequestDto givenChatRequestWithEmptyMessage() {
        ChatRequestDto request = new ChatRequestDto();
        request.setChapterId(TEST_CHAPTER_ID);
        request.setMessage("");
        request.setChapterContent(TEST_CHAPTER_CONTENT);
        return request;
    }

    private ChatRequestDto givenChatRequestWithoutChapterContent() {
        ChatRequestDto request = new ChatRequestDto();
        request.setChapterId(TEST_CHAPTER_ID);
        request.setMessage(TEST_MESSAGE);
        request.setChapterContent(null);
        return request;
    }

    private List<ChatMessage> givenAIWillRespondSegments() {
        List<ChatMessage> segments = List.of(
                new ChatMessage(
                        TEST_USER_ID,
                        TEST_CHAPTER_ID,
                        TEST_RESPONSE,
                        false,
                        LocalDateTime.now()
                )
        );
        when(processChatMessageUseCase.execute(
                eq(TEST_USER_ID),
                eq(TEST_CHAPTER_ID),
                anyString(),
                any()
        )).thenReturn(segments);
        return segments;
    }

    private List<ChatMessage> givenConversationExists() {
        List<ChatMessage> conversation = Arrays.asList(
                new ChatMessage(TEST_USER_ID, TEST_CHAPTER_ID, TEST_MESSAGE, true, LocalDateTime.now().minusMinutes(5)),
                new ChatMessage(TEST_USER_ID, TEST_CHAPTER_ID, TEST_RESPONSE, false, LocalDateTime.now().minusMinutes(4)),
                new ChatMessage(TEST_USER_ID, TEST_CHAPTER_ID, "¿Algo más?", true, LocalDateTime.now().minusMinutes(2)),
                new ChatMessage(TEST_USER_ID, TEST_CHAPTER_ID, "No, eso es todo.", false, LocalDateTime.now().minusMinutes(1))
        );
        when(getChatConversationUseCase.execute(TEST_USER_ID, TEST_CHAPTER_ID))
                .thenReturn(conversation);
        return conversation;
    }

    private void givenNoConversationExists() {
        when(getChatConversationUseCase.execute(TEST_USER_ID, TEST_CHAPTER_ID))
                .thenReturn(Collections.emptyList());
    }

    // ========== When Methods ==========

    private ResponseEntity<List<ChatMessage>> whenSendMessage(ChatRequestDto request) {
        return chatController.sendMessage(request, testUserPrincipal);
    }

    private ResponseEntity<List<ChatMessage>> whenGetConversation() {
        return chatController.getConversation(TEST_CHAPTER_ID, testUserPrincipal);
    }

    // ========== Then Methods ==========

    private void thenResponseIsOk(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenResponseContainsSegments(ResponseEntity<List<ChatMessage>> response, List<ChatMessage> expected) {
        assertNotNull(response.getBody());
        assertEquals(expected.size(), response.getBody().size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getContent(), response.getBody().get(i).getContent());
            assertEquals(expected.get(i).getUserId(), response.getBody().get(i).getUserId());
            assertEquals(expected.get(i).getChapterId(), response.getBody().get(i).getChapterId());
            assertFalse(response.getBody().get(i).isUserMessage());
        }
    }

    private void thenResponseContainsConversation(ResponseEntity<List<ChatMessage>> response, List<ChatMessage> expected) {
        assertNotNull(response.getBody());
        assertEquals(expected.size(), response.getBody().size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getContent(), response.getBody().get(i).getContent());
            assertEquals(expected.get(i).isUserMessage(), response.getBody().get(i).isUserMessage());
        }
    }

    private void thenResponseContainsEmptyConversation(ResponseEntity<List<ChatMessage>> response) {
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    private void thenProcessMessageUseCaseWasInvoked() {
        verify(processChatMessageUseCase, times(1)).execute(
                eq(TEST_USER_ID),
                eq(TEST_CHAPTER_ID),
                anyString(),
                any()
        );
    }

    private void thenGetConversationUseCaseWasInvoked() {
        verify(getChatConversationUseCase, times(1)).execute(TEST_USER_ID, TEST_CHAPTER_ID);
    }
}
