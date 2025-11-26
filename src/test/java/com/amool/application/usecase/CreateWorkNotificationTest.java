package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
        emailPort = Mockito.mock(EmailPort.class);
        createWorkNotification = new CreateWorkNotification(loadUserPort, notificationPort, obtainWorkByIdPort, emailPort);
    }

    private void givenWorkSubscribers(Long workId, List<Long> subscriberIds) {
        when(loadUserPort.getAllWorkSubscribers(workId)).thenReturn(subscriberIds);
    }

    private void givenWorkFound(Long workId, String title) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(createTestWork(title)));
    }

    private void givenWorkNotFound(Long workId) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private void givenNotificationsPersistSuccessfully() {
        when(notificationPort.saveLectorNotification(any(Notification.class))).thenReturn(true);
    }

    private boolean whenCreateWorkNotification(Long workId, Long authorId, Long chapterId) {
        return createWorkNotification.execute(workId, authorId, chapterId);
    }

    private void thenResultIsTrue(boolean result) { assertTrue(result); }
    private void thenResultIsFalse(boolean result) { assertFalse(result); }
    private void thenNotificationsSavedTimes(int times) { verify(notificationPort, times(times)).saveLectorNotification(any(Notification.class)); }
    private void thenNoNotificationsSaved() { verify(notificationPort, never()).saveLectorNotification(any()); }

    @Test
    public void when_executeWithSubscribers_then_createNotifications() {
        givenWorkSubscribers(1L, List.of(10L, 20L));
        givenWorkFound(1L, "Mi Obra");
        givenNotificationsPersistSuccessfully();

        boolean result = whenCreateWorkNotification(1L, 2L, 3L);

        thenResultIsTrue(result);
        thenNotificationsSavedTimes(2);
    }

    @Test
    public void when_noSubscribers_then_returnFalse() {
        givenWorkSubscribers(1L, List.of());

        boolean result = whenCreateWorkNotification(1L, 2L, 3L);

        thenResultIsFalse(result);
        thenNoNotificationsSaved();
    }

    @Test
    public void when_workNotFound_then_returnFalse() {
        givenWorkSubscribers(1L, List.of(10L));
        givenWorkNotFound(1L);

        boolean result = whenCreateWorkNotification(1L, 2L, 3L);

        thenResultIsFalse(result);
        thenNoNotificationsSaved();
    }

    private Work createTestWork(String title) {
        Work work = new Work();
        work.setTitle(title);
        return work;
    }
}
