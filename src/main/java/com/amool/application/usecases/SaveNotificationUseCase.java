package com.amool.application.usecases;

import java.time.LocalDateTime;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.NotificationPort;
import com.amool.application.port.out.ObtainChapterByIdPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Notification;

public class SaveNotificationUseCase {

    private final NotificationPort notificationPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final ObtainChapterByIdPort obtainChapterByIdPort;
    private final LoadUserPort loadUserPort;

    public SaveNotificationUseCase(NotificationPort notificationPort, ObtainWorkByIdPort obtainWorkByIdPort, ObtainChapterByIdPort obtainChapterByIdPort, LoadUserPort loadUserPort) {
        this.notificationPort = notificationPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.obtainChapterByIdPort = obtainChapterByIdPort;
        this.loadUserPort = loadUserPort;
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
        if (dto.getType() == null) {
            return dto.getMessage() != null ? dto.getMessage() : "Tienes una nueva notificación";
        }

        return switch (dto.getType()) {
            case NEW_WORK_PUBLISHED -> String.format("%s ha subido el %s", 
                getUsernameOrDefault(dto.getRelatedUser(), "Un autor"),
                getWorkTitleOrDefault(dto.getRelatedWork(), "Una obra"));
                
            case WORK_UPDATED -> String.format("%s ha tenido una nueva actualización", 
                getWorkTitleOrDefault(dto.getRelatedWork(), "Una obra"));
                
            case NEW_WORK_SUBSCRIBER -> String.format("%s se ha suscrito a %s", 
                getUsernameOrDefault(dto.getRelatedUser(), "Un usuario"),
                getWorkTitleOrDefault(dto.getRelatedWork(), "tu obra"));
                
            case NEW_AUTHOR_SUBSCRIBER -> String.format("%s se ha suscrito a tu contenido completo", 
                getUsernameOrDefault(dto.getRelatedUser(), "Un usuario"));
                
            case NEW_CHAPTER_SUBSCRIBER -> String.format("%s se ha suscrito a %s", 
                getUsernameOrDefault(dto.getRelatedUser(), "Un usuario"),
                getChapterTitleOrDefault(dto.getRelatedChapter(), "un capítulo"));
                
            default -> dto.getMessage() != null ? 
                dto.getMessage() : "Tienes una nueva notificación";
        };
    }

    private String getUsernameOrDefault(Long userId, String defaultValue) {
        if (userId == null) return defaultValue;
        return loadUserPort.getById(userId)
            .map(user -> user.getUsername())
            .orElse(defaultValue);
    }

    private String getWorkTitleOrDefault(Long workId, String defaultValue) {
        if (workId == null) return defaultValue;
        return obtainWorkByIdPort.obtainWorkById(workId)
            .map(work -> work.getTitle())
            .orElse(defaultValue);
    }

    private String getChapterTitleOrDefault(Long chapterId, String defaultValue) {
        if (chapterId == null) return defaultValue;
        return obtainChapterByIdPort.obtainChapterById(chapterId)
            .map(chapter -> chapter.getTitle())
            .orElse(defaultValue);
    }
    
    
}
