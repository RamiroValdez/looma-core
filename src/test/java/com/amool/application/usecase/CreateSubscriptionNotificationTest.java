package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainChapterByIdPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.CreateSubscriptionNotification;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;
import com.amool.domain.model.User;

public class CreateSubscriptionNotificationTest {

    private CreateSubscriptionNotification useCase;
    private NotificationPort notificationPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private ObtainChapterByIdPort obtainChapterByIdPort;
    private LoadUserPort loadUserPort;

    @BeforeEach
    public void setUp() {
        notificationPort = Mockito.mock(NotificationPort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        obtainChapterByIdPort = Mockito.mock(ObtainChapterByIdPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);
        useCase = new CreateSubscriptionNotification(notificationPort, obtainWorkByIdPort, obtainChapterByIdPort, loadUserPort);
    }

    private void givenPendingNotifications(int batchSize, List<Notification> notifications) {
        when(notificationPort.getPendingNotifications(batchSize)).thenReturn(notifications);
    }

    private void givenNoPendingNotifications(int batchSize) {
        when(notificationPort.getPendingNotifications(batchSize)).thenReturn(List.of());
    }

    private void givenUserExists(Long userId, String username) {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(username);
        when(loadUserPort.getById(userId)).thenReturn(Optional.of(mockUser));
    }

    private void givenUserNotFound(Long userId) {
        when(loadUserPort.getById(userId)).thenReturn(Optional.empty());
    }

    private void givenAuthorNotificationWillSave() {
        when(notificationPort.saveAuthorNotification(any(Notification.class))).thenReturn(true);
    }

    private int whenExecute(int batchSize) {
        return useCase.execute(batchSize);
    }

    private boolean whenCreateNotification(Notification notification) {
        return useCase.createNotification(notification);
    }

    private void thenProcessedCountEquals(int processed, int expected) {
        assertEquals(expected, processed);
    }

    private void thenGetPendingCalled(int batchSize) {
        verify(notificationPort).getPendingNotifications(batchSize);
    }

    private void thenMessageContains(Notification notification, String fragment) {
        assertNotNull(notification.getMessage());
        assertTrue(notification.getMessage().contains(fragment));
    }

    private void thenMessageEquals(Notification notification, String expected) {
        assertEquals(expected, notification.getMessage());
    }

    private void thenCreatedAtSet(Notification notification) {
        assertNotNull(notification.getCreatedAt());
    }

    private Notification newNotification(NotificationType type, Long relatedUser, Long relatedWork) {
        Notification n = new Notification();
        n.setType(type);
        n.setRelatedUser(relatedUser);
        n.setRelatedWork(relatedWork);
        return n;
    }

    private List<Notification> sampleNotifications() {
        Notification n1 = newNotification(NotificationType.NEW_AUTHOR_SUBSCRIBER, 1L, null);
        Notification n2 = newNotification(NotificationType.NEW_WORK_SUBSCRIBER, 2L, 100L);
        return List.of(n1, n2);
    }

    @Test
    public void when_executeWithValidBatchSize_then_processNotifications() {
        int batchSize = 10;
        List<Notification> notifications = sampleNotifications();
        givenPendingNotifications(batchSize, notifications);

        int processed = whenExecute(batchSize);

        thenProcessedCountEquals(processed, notifications.size());
        thenGetPendingCalled(batchSize);
    }

    @Test
    public void when_noPendingNotifications_then_returnZero() {
        int batchSize = 10;
        givenNoPendingNotifications(batchSize);

        int processed = whenExecute(batchSize);

        thenProcessedCountEquals(processed, 0);
        thenGetPendingCalled(batchSize);
    }

    @Test
    public void when_createAuthorNotification_then_setCorrectMessage() {
        Notification notification = newNotification(NotificationType.NEW_AUTHOR_SUBSCRIBER, 1L, null);
        givenUserExists(1L, "testuser");
        givenAuthorNotificationWillSave();

        boolean result = whenCreateNotification(notification);

        assertTrue(result);
        thenMessageContains(notification, "testuser se ha suscrito a tu contenido");
        thenCreatedAtSet(notification);
    }

    @Test
    public void when_userNotFound_then_setDefaultMessage() {
        Notification notification = newNotification(NotificationType.NEW_AUTHOR_SUBSCRIBER, 1L, null);
        givenUserNotFound(1L);
        givenAuthorNotificationWillSave();

        boolean result = whenCreateNotification(notification);

        assertTrue(result);
        thenMessageEquals(notification, "Un usuario se ha suscrito a tu contenido");
    }
}
