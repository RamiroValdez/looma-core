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

public class CreateAuthorNotification {

    private final NotificationPort notificationPort;
    private final LoadUserPort loadUserPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final EmailPort emailPort;

    public CreateAuthorNotification(NotificationPort notificationPort, LoadUserPort loadUserPort, 
                                 ObtainWorkByIdPort obtainWorkByIdPort, EmailPort emailPort) {
        this.notificationPort = notificationPort;
        this.loadUserPort = loadUserPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.emailPort = emailPort;
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
                
                sendEmailNotification(subscriberId, notificacion.getMessage());
        }
    }

    private String generateMessage(Notification notification) {
       String username = loadUserPort.getById(notification.getRelatedUser())
         .map(user -> user.getUsername())
         .orElse("Un usuario");

       String message = String.format("%s ha publicado un nuevo libro llamado %s", username, obtainWorkByIdPort.obtainWorkById(notification.getRelatedWork()).get().getTitle());
       return message;
    }
    
    private void sendEmailNotification(Long userId, String message) {
        Optional<User> userOpt = loadUserPort.getById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
             String subject = "¡Un autor que sigues ha publicado un nuevo libro!";
             String body = String.format("""
                 <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                     <h2 style="color: #5C17A6; margin-top: 0;">¡Hola %s!</h2>
                     <p style="color: #333; line-height: 1.5;">%s</p>
                     <p style="color: #5C17A6; font-weight: 500; margin: 20px 0;">¡No te pierdas esta nueva publicación en Looma!</p>
                     <div style="border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px;">
                         <p style="color: #666; margin: 0;">Saludos,<br><span style="color: #5C17A6; font-weight: 600;">El equipo de Looma</span></p>
                     </div>
                 </div>
                 """, 
                 user.getUsername(), 
                 message);
             
             emailPort.send(user.getEmail(), subject, body);
        }
        
    }
    
}
