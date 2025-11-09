package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    
    public static NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setType(notification.getType());
        dto.setRelatedWork(notification.getRelatedWork());
        dto.setRelatedChapter(notification.getRelatedChapter());
        dto.setRelatedUser(notification.getRelatedUser());
        
        return dto;
    }

    public static Notification toDomain(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }
        
        Notification notification = new Notification();
        notification.setId(notificationDto.getId());
        notification.setUserId(notificationDto.getUserId());
        notification.setMessage(notificationDto.getMessage());
        notification.setRead(notificationDto.isRead());
        notification.setCreatedAt(notificationDto.getCreatedAt());
        notification.setType(notificationDto.getType());
        notification.setRelatedWork(notificationDto.getRelatedWork());
        notification.setRelatedChapter(notificationDto.getRelatedChapter());
        notification.setRelatedUser(notificationDto.getRelatedUser());
        
        return notification;
    }
}
