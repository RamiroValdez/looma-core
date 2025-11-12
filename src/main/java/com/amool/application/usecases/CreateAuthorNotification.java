package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.List;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;

public class CreateAuthorNotification {

    private final NotificationPort notificationPort;
    private final LoadUserPort loadUserPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    public CreateAuthorNotification(NotificationPort notificationPort, LoadUserPort loadUserPort, ObtainWorkByIdPort obtainWorkByIdPort) {
        this.notificationPort = notificationPort;
        this.loadUserPort = loadUserPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    public boolean execute(Long workId, Long authorId) {
        try {
            List<Long> subscribers = loadUserPort.getAllAuthorSubscribers(authorId);
            
            if (subscribers.isEmpty()) {
                return false;
            }
            
            createNotification(workId, authorId, subscribers);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void createNotification(Long workId, Long authorId, List<Long> subscribers) {
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
    }

    private String generateMessage(Notification notification) {
       String username = loadUserPort.getById(notification.getRelatedUser())
         .map(user -> user.getUsername())
         .orElse("Un usuario");

       String message = String.format("%s ha publicado un nuevo libro llamado %s", username, obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
       return message;
    }
    
}
