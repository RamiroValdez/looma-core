package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.CreateAuthorNotification;
import com.amool.domain.model.Notification;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;

public class CreateAuthorNotificationTest {

    private CreateAuthorNotification createAuthorNotification;
    private NotificationPort notificationPort;
    private LoadUserPort loadUserPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private EmailPort emailPort;

    @BeforeEach
    public void setUp() {
        notificationPort = Mockito.mock(NotificationPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        createAuthorNotification = new CreateAuthorNotification(notificationPort, loadUserPort, obtainWorkByIdPort, emailPort);
    }

    @Test
    public void when_ExecuteWithValidData_ThenCreateNotifications() {
        Long authorId = 1L;
        Long workId = 2L;
        
        when(loadUserPort.getAllAuthorSubscribers(authorId)).thenReturn(List.of(10L, 20L));
        when(loadUserPort.getById(authorId)).thenReturn(Optional.of(createTestUser("autorPrueba")));
        when(obtainWorkByIdPort.obtainWorkById(workId))
            .thenReturn(Optional.of(createTestWork("Título de la obra")));
        when(notificationPort.saveLectorNotification(any(Notification.class))).thenReturn(true);

        boolean result = createAuthorNotification.execute(workId, authorId);

        assertTrue(result);
        verify(notificationPort, times(2)).saveLectorNotification(any(Notification.class));
    }

    @Test
    public void when_NoSubscribers_ThenReturnFalse() {
        when(loadUserPort.getAllAuthorSubscribers(anyLong())).thenReturn(List.of());

        boolean result = createAuthorNotification.execute(1L, 2L);

        assertFalse(result);
        verify(notificationPort, never()).saveLectorNotification(any());
    }

    @Test
    public void when_AuthorNotFound_ThenReturnFalse() {
        when(loadUserPort.getAllAuthorSubscribers(anyLong())).thenReturn(List.of(10L));
        when(loadUserPort.getById(anyLong())).thenReturn(Optional.empty());

        boolean result = createAuthorNotification.execute(1L, 2L);

        assertFalse(result);
        verify(notificationPort, never()).saveLectorNotification(any());
    }

    @Test
    public void when_WorkNotFound_ThenReturnFalse() {
        when(loadUserPort.getAllAuthorSubscribers(anyLong())).thenReturn(List.of(10L));
        when(loadUserPort.getById(anyLong())).thenReturn(Optional.of(createTestUser("autor")));
        when(obtainWorkByIdPort.obtainWorkById(anyLong())).thenReturn(Optional.empty());

        boolean result = createAuthorNotification.execute(1L, 2L);

        assertFalse(result);
        verify(notificationPort, never()).saveLectorNotification(any());
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private Work createTestWork(String title) {
        Work work = new Work();
        work.setTitle(title);
        return work;
    }
    
}
