package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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

    private CreateSubscriptionNotification createSubscriptionNotification;
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
        createSubscriptionNotification = new CreateSubscriptionNotification(notificationPort, obtainWorkByIdPort, obtainChapterByIdPort, loadUserPort);
    }

    @Test
    public void when_ExecuteWithValidBatchSize_ThenProcessNotifications() {
        int batchSize = 10;
        List<Notification> mockNotifications = createMockNotifications();
        when(notificationPort.getPendingNotifications(batchSize)).thenReturn(mockNotifications);
        
        int processed = createSubscriptionNotification.execute(batchSize);
        
        assertEquals(mockNotifications.size(), processed);
        verify(notificationPort).getPendingNotifications(batchSize);
    }
    
    @Test
    public void when_NoPendingNotifications_ThenReturnZero() {
        int batchSize = 10;
        when(notificationPort.getPendingNotifications(batchSize)).thenReturn(List.of());
        
        int processed = createSubscriptionNotification.execute(batchSize);
        
        assertEquals(0, processed);
    }
    
    @Test
    public void when_CreateAuthorNotification_ThenSetCorrectMessage() {
        Notification notification = new Notification();
        notification.setType(NotificationType.NEW_AUTHOR_SUBSCRIBER);
        notification.setRelatedUser(1L);
        
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(loadUserPort.getById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(notificationPort.saveAuthorNotification(any(Notification.class))).thenReturn(true);
        
        boolean result = createSubscriptionNotification.createNotification(notification);
        
        assertTrue(result);
        assertTrue(notification.getMessage().contains("testuser se ha suscrito a tu contenido"));
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    public void when_UserNotFound_ThenSetDefaultMessage() {
        Notification notification = new Notification();
        notification.setType(NotificationType.NEW_AUTHOR_SUBSCRIBER);
        notification.setRelatedUser(1L);
        
        when(loadUserPort.getById(1L)).thenReturn(java.util.Optional.empty());
        when(notificationPort.saveAuthorNotification(any(Notification.class))).thenReturn(true);
        
        boolean result = createSubscriptionNotification.createNotification(notification);
        
        assertTrue(result);
        assertEquals("Un usuario se ha suscrito a tu contenido", notification.getMessage());
    }
    
    private List<Notification> createMockNotifications() {
        Notification n1 = new Notification();
        n1.setType(NotificationType.NEW_AUTHOR_SUBSCRIBER);
        n1.setRelatedUser(1L);
        
        Notification n2 = new Notification();
        n2.setType(NotificationType.NEW_WORK_SUBSCRIBER);
        n2.setRelatedUser(2L);
        n2.setRelatedWork(100L);
        
        return List.of(n1, n2);
    }
    
}
