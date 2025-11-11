package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainChapterByIdPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;

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
                createAuthorNotification(notification);
                published++;
            } catch (Exception e) {
                log.error("Error al procesar la notificación: " + notification, e);
            }
        }
        return published;
    }

    public boolean createAuthorNotification(Notification notification) {
        try {
            String message = generateMessage(notification);
            notification.setMessage(message);
            notification.setCreatedAt(LocalDateTime.now());
            return notificationPort.saveAuthorNotification(notification);
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

    public boolean createLectorNotification(Long workId, Long authorId) {
        try {
            List<Long> subscribers = loadUserPort.getAllAuthorSubscribers(authorId);
            
            if (subscribers.isEmpty()) {
                return false;
            }
            
            for (Long subscriberId : subscribers) {
                    Notification notificacion = new Notification();
                    notificacion.setRelatedWork(workId);
                    notificacion.setRelatedUser(authorId);
                    notificacion.setType(NotificationType.NEW_WORK_PUBLISHED);
                    notificacion.setRead(false);
                    notificacion.setCreatedAt(LocalDateTime.now());
                    notificacion.setUserId(subscriberId);
                    notificacion.setMessage(generateMessage(notificacion));
                    
                    notificationPort.saveLectorNotification(notificacion);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean createChapterNotification(Long workId, Long authorId, Long chapterId) {
        try {
            List<Long> subscribers = loadUserPort.getAllWorkSubscribers(workId);
            
            if (subscribers.isEmpty()) {
                return false;
            }
            
            for (Long subscriberId : subscribers) {
                    Notification notificacion = new Notification();
                    notificacion.setRelatedWork(workId);
                    notificacion.setRelatedUser(authorId);
                    notificacion.setType(NotificationType.WORK_UPDATED);
                    notificacion.setRead(false);
                    notificacion.setCreatedAt(LocalDateTime.now());
                    notificacion.setUserId(subscriberId);
                    notificacion.setMessage(generateMessage(notificacion));
                    notificacion.setRelatedChapter(chapterId);
                    
                    notificationPort.saveLectorNotification(notificacion);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
