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
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setType(notification.getType());
        dto.setRelatedWork(notification.getRelatedWork());
        dto.setRelatedChapter(notification.getRelatedChapter());
        dto.setRelatedUser(notification.getRelatedUser());
        
        return dto;
    }
}
