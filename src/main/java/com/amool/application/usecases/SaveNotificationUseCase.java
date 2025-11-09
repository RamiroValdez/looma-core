package com.amool.application.usecases;

import java.time.LocalDateTime;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;

public class SaveNotificationUseCase {

    private final NotificationPort notificationPort;

    public SaveNotificationUseCase(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public boolean execute(NotificationDto notificationDto) {
        String message = generateMessage(notificationDto);
        notificationDto.setMessage(message);
        
        Notification notification = NotificationMapper.toDomain(notificationDto);
        notification.setCreatedAt(LocalDateTime.now()); 
        notification.setRead(false); 
        
        return notificationPort.saveNotification(notification);
    }

        private String generateMessage(NotificationDto dto) {
        return switch (dto.getType()) {
            case NEW_WORK_PUBLISHED -> String.format("%s ha subido un nuevo libro", 
                dto.getRelatedUser() != null ? dto.getRelatedUser() : "Un autor");
                
            case WORK_UPDATED -> String.format("%s ha tenido una nueva actualización", 
                dto.getRelatedWork() != null ? dto.getRelatedWork() : "Una obra");
                
            case NEW_WORK_SUBSCRIBER -> String.format("%s se ha suscrito a %s", 
                dto.getRelatedUser() != null ? dto.getRelatedUser() : "Un usuario",
                dto.getRelatedWork() != null ? dto.getRelatedWork() : "tu obra");
                
            case NEW_AUTHOR_SUBSCRIBER -> String.format("%s se ha suscrito a tu contenido completo", 
                dto.getRelatedUser() != null ? dto.getRelatedUser() : "Un usuario");
                
            case NEW_CHAPTER_SUBSCRIBER -> String.format("%s se ha suscrito a %s", 
                dto.getRelatedUser() != null ? dto.getRelatedUser() : "Un usuario",
                dto.getRelatedChapter() != null ? dto.getRelatedChapter() : "un capítulo");
                
            default -> dto.getMessage() != null ? 
                dto.getMessage() : "Tienes una nueva notificación";
        };
    }
    
    
}
