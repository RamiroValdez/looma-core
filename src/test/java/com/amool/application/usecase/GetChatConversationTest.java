package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.ChatConversationPort;
import com.amool.application.usecases.GetChatConversation;
import com.amool.domain.model.ChatMessage;

public class GetChatConversationTest {

    private GetChatConversation getChatConversation;
    private ChatConversationPort chatConversationPort;

    private static final Long USER_ID = 1L;
    private static final Long CHAPTER_ID = 100L;

    @BeforeEach
    public void setUp() {
        chatConversationPort = Mockito.mock(ChatConversationPort.class);
        getChatConversation = new GetChatConversation(chatConversationPort);
    }


    @Test
    public void when_ConversationExists_ThenReturnConversation() {
        givenConversationWithMessages(5);

        List<ChatMessage> result = whenGetConversation();

        thenShouldReturnAllMessages(result, 5);
    }

    @Test
    public void when_NoConversationExists_ThenReturnEmptyList() {
        givenEmptyConversation();

        List<ChatMessage> result = whenGetConversation();

        thenShouldReturnEmptyList(result);
    }

    @Test
    public void when_GetConversation_ThenMessagesAreInCorrectOrder() {
        givenConversationWithOrderedMessages();

        List<ChatMessage> result = whenGetConversation();

        thenMessagesShouldBeInOrder(result);
    }

    @Test
    public void when_GetConversation_ThenUserAndAssistantMessagesAreIncluded() {
        givenConversationWithMixedMessages();

        List<ChatMessage> result = whenGetConversation();

        thenShouldIncludeBothMessageTypes(result);
    }

    @Test
    public void when_GetConversation_ThenRetrieveFromPort() {
        givenConversationWithMessages(3);

        whenGetConversation();

        thenPortShouldBeCalledOnce();
    }

    @Test
    public void when_GetConversation_ThenMessagesContainAllFields() {
        givenConversationWithCompleteMessages();

        List<ChatMessage> result = whenGetConversation();

        thenMessagesShouldContainAllFields(result);
    }

    @Test
    public void when_GetConversationForDifferentUsers_ThenReturnCorrectConversation() {
        Long otherUserId = 2L;
        givenConversationForUser(USER_ID, 3);
        givenConversationForUser(otherUserId, 5);

        List<ChatMessage> result = whenGetConversation();

        thenShouldReturnMessagesForUser(result, USER_ID);
    }

    @Test
    public void when_GetConversationForDifferentChapters_ThenReturnCorrectConversation() {
        Long otherChapterId = 200L;
        givenConversationForChapter(CHAPTER_ID, 4);
        givenConversationForChapter(otherChapterId, 6);

        List<ChatMessage> result = whenGetConversation();

        thenShouldReturnMessagesForChapter(result, CHAPTER_ID);
    }


    private void givenEmptyConversation() {
        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID))
            .thenReturn(List.of());
    }

    private void givenConversationWithMessages(int numberOfMessages) {
        List<ChatMessage> messages = createMessages(numberOfMessages);
        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID))
            .thenReturn(messages);
    }

    private void givenConversationWithOrderedMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Primer mensaje",
            true, LocalDateTime.now().minusMinutes(10)));
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Segunda respuesta",
            false, LocalDateTime.now().minusMinutes(9)));
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Tercer mensaje",
            true, LocalDateTime.now().minusMinutes(8)));

        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID))
            .thenReturn(messages);
    }

    private void givenConversationWithMixedMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Mensaje de usuario 1",
            true, LocalDateTime.now().minusMinutes(5)));
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Respuesta del asistente 1",
            false, LocalDateTime.now().minusMinutes(4)));
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Mensaje de usuario 2",
            true, LocalDateTime.now().minusMinutes(3)));
        messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Respuesta del asistente 2",
            false, LocalDateTime.now().minusMinutes(2)));

        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID))
            .thenReturn(messages);
    }

    private void givenConversationWithCompleteMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(USER_ID, CHAPTER_ID, "Contenido del mensaje",
            true, LocalDateTime.now()));

        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID))
            .thenReturn(messages);
    }

    private void givenConversationForUser(Long userId, int numberOfMessages) {
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++) {
            messages.add(createChatMessage(userId, CHAPTER_ID, "Mensaje " + i,
                i % 2 == 0, LocalDateTime.now().minusMinutes(numberOfMessages - i)));
        }

        when(chatConversationPort.getConversation(userId, CHAPTER_ID))
            .thenReturn(messages);
    }

    private void givenConversationForChapter(Long chapterId, int numberOfMessages) {
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++) {
            messages.add(createChatMessage(USER_ID, chapterId, "Mensaje " + i,
                i % 2 == 0, LocalDateTime.now().minusMinutes(numberOfMessages - i)));
        }

        when(chatConversationPort.getConversation(USER_ID, chapterId))
            .thenReturn(messages);
    }


    private List<ChatMessage> whenGetConversation() {
        return getChatConversation.execute(USER_ID, CHAPTER_ID);
    }

    private void thenShouldReturnAllMessages(List<ChatMessage> result, int expectedSize) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
    }

    private void thenShouldReturnEmptyList(List<ChatMessage> result) {
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void thenMessagesShouldBeInOrder(List<ChatMessage> result) {
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Primer mensaje", result.get(0).getContent());
        assertEquals("Segunda respuesta", result.get(1).getContent());
        assertEquals("Tercer mensaje", result.get(2).getContent());
    }

    private void thenShouldIncludeBothMessageTypes(List<ChatMessage> result) {
        assertNotNull(result);
        assertEquals(4, result.size());

        long userMessages = result.stream().filter(ChatMessage::isUserMessage).count();
        long assistantMessages = result.stream().filter(msg -> !msg.isUserMessage()).count();

        assertEquals(2, userMessages);
        assertEquals(2, assistantMessages);
    }

    private void thenPortShouldBeCalledOnce() {
        verify(chatConversationPort, times(1)).getConversation(USER_ID, CHAPTER_ID);
    }

    private void thenMessagesShouldContainAllFields(List<ChatMessage> result) {
        assertNotNull(result);
        assertNotNull(result.getFirst());

        ChatMessage message = result.getFirst();
        assertNotNull(message.getUserId());
        assertNotNull(message.getChapterId());
        assertNotNull(message.getContent());
        assertNotNull(message.getTimestamp());
    }

    private void thenShouldReturnMessagesForUser(List<ChatMessage> result, Long expectedUserId) {
        assertNotNull(result);
        assertTrue(result.stream().allMatch(msg -> msg.getUserId().equals(expectedUserId)));
    }

    private void thenShouldReturnMessagesForChapter(List<ChatMessage> result, Long expectedChapterId) {
        assertNotNull(result);
        assertTrue(result.stream().allMatch(msg -> msg.getChapterId().equals(expectedChapterId)));
    }


    private List<ChatMessage> createMessages(int count) {
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            boolean isUserMessage = i % 2 == 0;
            messages.add(createChatMessage(USER_ID, CHAPTER_ID, "Mensaje " + i,
                isUserMessage, LocalDateTime.now().minusMinutes(count - i)));
        }
        return messages;
    }

    private ChatMessage createChatMessage(Long userId, Long chapterId, String content,
                                         boolean isUserMessage, LocalDateTime timestamp) {
        return new ChatMessage(userId, chapterId, content, isUserMessage, timestamp);
    }
}
