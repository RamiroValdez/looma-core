package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;
import com.amool.domain.model.User;

public class CreateWorkNotification {

    private final LoadUserPort loadUserPort;
    private final NotificationPort notificationPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final EmailPort emailPort;

    public CreateWorkNotification(LoadUserPort loadUserPort, NotificationPort notificationPort, 
                               ObtainWorkByIdPort obtainWorkByIdPort, EmailPort emailPort) {
        this.loadUserPort = loadUserPort;
        this.notificationPort = notificationPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.emailPort = emailPort;
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
                
                sendEmailNotification(subscriberId, notificacion.getMessage());
        }
    }

    private String generateMessage(Notification notification) {
       String message = String.format("El libro %s ha lanzado un nuevo capítulo", obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
       return message;
    }
    
    private void sendEmailNotification(Long userId, String message) {
        try {
            Optional<User> userOpt = loadUserPort.getById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String subject = "¡Nueva actualización de un libro que sigues!";
                String body = String.format("""
                    <div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">
                        <h2 style=\"color: #5C17A6; margin-top: 0;\">¡Hola %s!</h2>
                        <p style=\"color: #333; line-height: 1.5;\">%s</p>
                        <p style=\"color: #5C17A6; font-weight: 500; margin: 20px 0;\">¡No te pierdas esta nueva actualización en Looma!</p>
                        <div style=\"border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px;\">
                            <p style=\"color: #666; margin: 0;\">Saludos,<br><span style=\"color: #5C17A6; font-weight: 600;\">El equipo de Looma</span></p>
                        </div>
                    </div>
                    """, 
                    user.getUsername(), 
                    message);
                
                emailPort.send(user.getEmail(), subject, body);
            }
        } catch (Exception e) {
            System.err.println("Error sending email notification: " + e.getMessage());
        }
    }
    
}
