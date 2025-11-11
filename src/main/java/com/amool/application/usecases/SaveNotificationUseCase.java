package com.amool.application.usecases;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainChapterByIdPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;

public class SaveNotificationUseCase {
    private static final Logger log = LoggerFactory.getLogger(SaveNotificationUseCase.class);

    private final NotificationPort notificationPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final ObtainChapterByIdPort obtainChapterByIdPort;
    private final LoadUserPort loadUserPort;

    public SaveNotificationUseCase(NotificationPort notificationPort, 
                                 ObtainWorkByIdPort obtainWorkByIdPort, 
                                 ObtainChapterByIdPort obtainChapterByIdPort, 
                                 LoadUserPort loadUserPort) {
        this.notificationPort = notificationPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.obtainChapterByIdPort = obtainChapterByIdPort;
        this.loadUserPort = loadUserPort;
    }

    public int publishNotification(int batchSize) {
        var pendingNotifications = notificationPort.getPendingNotifications(batchSize);
        int published = 0;
        for (var notification : pendingNotifications) {
            try {
                createNotification(notification);
                published++;
            } catch (Exception e) {
                log.error("Error al procesar la notificación: " + notification, e);
            }
        }
        return published;
    }

    public boolean createNotification(Notification notification) {
        try {
            String message = generateMessage(notification);
            notification.setMessage(message);
            notification.setCreatedAt(LocalDateTime.now());
            return notificationPort.saveNotification(notification);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateMessage(Notification notification) {

       String username = loadUserPort.getById(notification.getRelatedUser())
         .map(user -> user.getUsername())
         .orElse("Un usuario");
                
         return switch (notification.getType()) {
            case NEW_WORK_PUBLISHED -> 
                 String.format("%s ha publicado un nuevo libro llamado %s", username, obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
            case WORK_UPDATED -> 
                String.format("El libro %s ha sido actualizado", obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
            case NEW_WORK_SUBSCRIBER -> 
                String.format("%s se ha suscrito a tu libro %s", username, obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
            case NEW_AUTHOR_SUBSCRIBER -> 
                String.format("%s se ha suscrito a tu contenido", username);
            case NEW_CHAPTER_SUBSCRIBER -> 
                String.format("%s se ha suscrito al capítulo %s", username, obtainChapterByIdPort.obtainChapterById(notification.getRelatedChapter()).get().getTitle());
             default -> "Tienes una nueva notificación";
        };
    }
}
