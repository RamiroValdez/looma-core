package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
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
        emailPort = Mockito.mock(EmailPort.class);
        createAuthorNotification = new CreateAuthorNotification(notificationPort, loadUserPort, obtainWorkByIdPort, emailPort);
    }

    private void givenAuthorSubscribers(Long authorId, List<Long> subscriberIds) {
        when(loadUserPort.getAllAuthorSubscribers(authorId)).thenReturn(subscriberIds);
    }

    private void givenAuthorExists(Long authorId, String username) {
        when(loadUserPort.getById(authorId)).thenReturn(Optional.of(createTestUser(username)));
    }

    private void givenAuthorNotFound(Long authorId) {
        when(loadUserPort.getById(authorId)).thenReturn(Optional.empty());
    }

    private void givenWorkExists(Long workId, String title) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(createTestWork(title)));
    }

    private void givenWorkNotFound(Long workId) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private void givenNotificationsWillSaveSuccessfully() {
        when(notificationPort.saveLectorNotification(any(Notification.class))).thenReturn(true);
    }

    private boolean whenCreateAuthorNotification(Long workId, Long authorId) {
        return createAuthorNotification.execute(workId, authorId);
    }

    private void thenResultIsTrue(boolean result) { assertTrue(result); }
    private void thenResultIsFalse(boolean result) { assertFalse(result); }
    private void thenNotificationsSavedTimes(int times) { verify(notificationPort, times(times)).saveLectorNotification(any(Notification.class)); }
    private void thenNoNotificationsSaved() { verify(notificationPort, never()).saveLectorNotification(any()); }

    @Test
    void createNotifications_successful_whenValidData() {
        Long authorId = 1L; Long workId = 2L;
        givenAuthorSubscribers(authorId, List.of(10L, 20L));
        givenAuthorExists(authorId, "autorPrueba");
        givenWorkExists(workId, "Título de la obra");
        givenNotificationsWillSaveSuccessfully();

        boolean result = whenCreateAuthorNotification(workId, authorId);

        thenResultIsTrue(result);
        thenNotificationsSavedTimes(2);
    }

    @Test
    void returnsFalse_whenNoSubscribers() {
        givenAuthorSubscribers(2L, List.of());

        boolean result = whenCreateAuthorNotification(1L, 2L);

        thenResultIsFalse(result);
        thenNoNotificationsSaved();
    }

    @Test
    void returnsFalse_whenAuthorNotFound() {
        givenAuthorSubscribers(2L, List.of(10L));
        givenAuthorNotFound(2L);

        boolean result = whenCreateAuthorNotification(1L, 2L);

        thenResultIsFalse(result);
        thenNoNotificationsSaved();
    }

    @Test
    void returnsFalse_whenWorkNotFound() {
        givenAuthorSubscribers(2L, List.of(10L));
        givenAuthorExists(2L, "autor");
        givenWorkNotFound(1L);

        boolean result = whenCreateAuthorNotification(1L, 2L);

        thenResultIsFalse(result);
        thenNoNotificationsSaved();
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
