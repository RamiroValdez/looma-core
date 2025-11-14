package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.List;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;

public class CreateWorkNotification {

    private final LoadUserPort loadUserPort;
    private final NotificationPort notificationPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    public CreateWorkNotification(LoadUserPort loadUserPort, NotificationPort notificationPort, ObtainWorkByIdPort obtainWorkByIdPort) {
        this.loadUserPort = loadUserPort;
        this.notificationPort = notificationPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    public boolean execute(Long workId, Long authorId, Long chapterId) {
        try {
            List<Long> subscribers = loadUserPort.getAllWorkSubscribers(workId);
            
            if (subscribers.isEmpty()) {
                return false;
            }
            
            createNotification(workId, authorId, chapterId, subscribers);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void createNotification(Long workId, Long authorId, Long chapterId, List<Long> subscribers) {
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
    }

    private String generateMessage(Notification notification) {
       String message = String.format("El libro %s ha lanzado un nuevo capítulo", obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
       return message;
    }
    
}
