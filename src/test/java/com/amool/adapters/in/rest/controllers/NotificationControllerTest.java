package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.application.usecases.ObtainNotificationsUseCase;
import com.amool.application.usecases.UpdateNotificationReadUseCase;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    private NotificationController controller;
    private ObtainNotificationsUseCase obtainNotificationsUseCase;
    private UpdateNotificationReadUseCase updateNotificationReadUseCase;

    private static final Long USER_ID = 99L;
    private static final Long NOTIF_ID = 123L;

    @BeforeEach
    void setUp() {
        obtainNotificationsUseCase = Mockito.mock(ObtainNotificationsUseCase.class);
        updateNotificationReadUseCase = Mockito.mock(UpdateNotificationReadUseCase.class);
        controller = new NotificationController(obtainNotificationsUseCase, updateNotificationReadUseCase);
    }


    @Test
    @DisplayName("GET /api/notification/{userId} - Debe devolver lista mapeada de notificaciones")
    void getNotifications_shouldReturnMappedList() {
        List<Notification> notifications = givenNotifications(
                givenNotification(1L, USER_ID, "Msg 1", false, NotificationType.WORK_UPDATED, 10L, 20L, 30L),
                givenNotification(2L, USER_ID, "Msg 2", true, NotificationType.NEW_WORK_PUBLISHED, 11L, null, null)
        );
        givenUseCaseReturnsNotifications(notifications);

        List<NotificationDto> response = whenGettingNotifications(USER_ID);

        thenListSizeIs(response, 2);
        thenDtoMatches(response.get(0), notifications.get(0));
        thenDtoMatches(response.get(1), notifications.get(1));
        thenObtainUseCaseWasCalled(USER_ID);
    }

    @Test
    @DisplayName("GET /api/notification/{userId} - Debe devolver lista vacía cuando no hay notificaciones")
    void getNotifications_shouldReturnEmpty_whenNoNotifications() {
        givenUseCaseReturnsNotifications(List.of());

        List<NotificationDto> response = whenGettingNotifications(USER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        thenObtainUseCaseWasCalled(USER_ID);
    }


    @Test
    @DisplayName("PUT /api/notification/update-read/{notificationId} - Debe devolver true cuando se actualiza correctamente")
    void updateNotificationRead_shouldReturnTrue_onSuccess() {
        givenUpdateReadWillReturn(true);

        ResponseEntity<Boolean> response = whenUpdatingRead(NOTIF_ID);

        thenShouldReturnOk(response);
        assertEquals(Boolean.TRUE, response.getBody());
        thenUpdateReadUseCaseWasCalled(NOTIF_ID);
    }

    @Test
    @DisplayName("PUT /api/notification/update-read/{notificationId} - Debe devolver false cuando el caso de uso retorna false")
    void updateNotificationRead_shouldReturnFalse_whenUseCaseReturnsFalse() {
        givenUpdateReadWillReturn(false);

        ResponseEntity<Boolean> response = whenUpdatingRead(NOTIF_ID);

        thenShouldReturnOk(response);
        assertEquals(Boolean.FALSE, response.getBody());
        thenUpdateReadUseCaseWasCalled(NOTIF_ID);
    }

    @Test
    @DisplayName("PUT /api/notification/update-read/{notificationId} - Debe devolver 400 cuando el caso de uso lanza excepción")
    void updateNotificationRead_shouldReturnBadRequest_whenUseCaseThrows() {
        givenUpdateReadWillThrow(new RuntimeException("error"));

        ResponseEntity<Boolean> response = whenUpdatingRead(NOTIF_ID);

        thenShouldReturnBadRequest(response);
    }

    private Notification givenNotification(Long id, Long userId, String message, boolean read,
                                           NotificationType type, Long relatedWork, Long relatedChapter, Long relatedUser) {
        Notification n = new Notification();
        n.setId(id);
        n.setUserId(userId);
        n.setMessage(message);
        n.setRead(read);
        n.setType(type);
        n.setCreatedAt(LocalDateTime.now());
        n.setRelatedWork(relatedWork);
        n.setRelatedChapter(relatedChapter);
        n.setRelatedUser(relatedUser);
        return n;
    }

    private List<Notification> givenNotifications(Notification... notifs) {
        return List.of(notifs);
    }

    private void givenUseCaseReturnsNotifications(List<Notification> notifications) {
        when(obtainNotificationsUseCase.execute(eq(USER_ID))).thenReturn(notifications);
    }

    private void givenUpdateReadWillReturn(boolean value) {
        when(updateNotificationReadUseCase.execute(eq(NOTIF_ID))).thenReturn(value);
    }

    private void givenUpdateReadWillThrow(RuntimeException ex) {
        when(updateNotificationReadUseCase.execute(eq(NOTIF_ID))).thenThrow(ex);
    }

    private List<NotificationDto> whenGettingNotifications(Long userId) {
        return controller.getNotifications(userId);
    }

    private ResponseEntity<Boolean> whenUpdatingRead(Long notificationId) {
        return controller.updateNotificationRead(notificationId);
    }

    private void thenListSizeIs(List<?> list, int expected) {
        assertNotNull(list);
        assertEquals(expected, list.size());
    }

    private void thenDtoMatches(NotificationDto dto, Notification domain) {
        assertEquals(domain.getId(), dto.getId());
        assertEquals(domain.getUserId(), dto.getUserId());
        assertEquals(domain.getMessage(), dto.getMessage());
        assertEquals(domain.isRead(), dto.isRead());
        assertEquals(domain.getCreatedAt(), dto.getCreatedAt());
        assertEquals(domain.getType(), dto.getType());
        assertEquals(domain.getRelatedWork(), dto.getRelatedWork());
        assertEquals(domain.getRelatedChapter(), dto.getRelatedChapter());
        assertEquals(domain.getRelatedUser(), dto.getRelatedUser());
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenShouldReturnBadRequest(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenObtainUseCaseWasCalled(Long userId) {
        verify(obtainNotificationsUseCase, times(1)).execute(eq(userId));
    }

    private void thenUpdateReadUseCaseWasCalled(Long notificationId) {
        verify(updateNotificationReadUseCase, times(1)).execute(eq(notificationId));
    }
}
