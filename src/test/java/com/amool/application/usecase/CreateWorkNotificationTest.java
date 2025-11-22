package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.CreateWorkNotification;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Work;

public class CreateWorkNotificationTest {

    private CreateWorkNotification createWorkNotification;
    private NotificationPort notificationPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadUserPort loadUserPort;
    private EmailPort emailPort;
    
    @BeforeEach
    public void setUp() {
        notificationPort = Mockito.mock(NotificationPort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);
        createWorkNotification = new CreateWorkNotification(loadUserPort, notificationPort, obtainWorkByIdPort, emailPort);
    }

    @Test
    public void when_ExecuteWithSubscribers_ThenCreateNotifications() {
        when(loadUserPort.getAllWorkSubscribers(1L)).thenReturn(List.of(10L, 20L));
        when(obtainWorkByIdPort.obtainWorkById(1L))
            .thenReturn(java.util.Optional.of(createTestWork("Mi Obra")));
        when(notificationPort.saveLectorNotification(any(Notification.class))).thenReturn(true);

        boolean result = createWorkNotification.execute(1L, 2L, 3L);

        assertTrue(result);
        verify(notificationPort, times(2)).saveLectorNotification(any(Notification.class));
    }

    @Test
    public void when_NoSubscribers_ThenReturnFalse() {
        when(loadUserPort.getAllWorkSubscribers(1L)).thenReturn(List.of());

        boolean result = createWorkNotification.execute(1L, 2L, 3L);

        assertFalse(result);
        verify(notificationPort, never()).saveLectorNotification(any());
    }

    @Test
    public void when_WorkNotFound_ThenReturnFalse() {
        when(loadUserPort.getAllWorkSubscribers(1L)).thenReturn(List.of(10L));
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(java.util.Optional.empty());

        boolean result = createWorkNotification.execute(1L, 2L, 3L);

        assertFalse(result);
    }


    private Work createTestWork(String title) {
        Work work = new Work();
        work.setTitle(title);
        return work;
    }
    
}
