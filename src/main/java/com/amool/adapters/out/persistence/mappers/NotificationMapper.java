package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.NotificationEntity;
import com.amool.domain.model.Notification;

public class NotificationMapper {

    public static Notification toDomain(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Notification notification = new Notification();
        notification.setId(entity.getId());
        notification.setUserId(entity.getUserId());
        notification.setMessage(entity.getMessage());
        notification.setRead(entity.isRead());
        notification.setCreatedAt(entity.getCreatedAt());
        notification.setType(entity.getType());
        notification.setRelatedWork(entity.getRelatedWork());
        notification.setRelatedChapter(entity.getRelatedChapter());
        notification.setRelatedUser(entity.getRelatedUser());
        
        return notification;
    }

    public static NotificationEntity toEntity(Notification domain) {
        if (domain == null) {
            return null;
        }
        
        return NotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .message(domain.getMessage())
                .read(domain.isRead())
                .createdAt(domain.getCreatedAt())
                .type(domain.getType())
                .relatedWork(domain.getRelatedWork())
                .relatedChapter(domain.getRelatedChapter())
                .relatedUser(domain.getRelatedUser())
                .build();
    }
}
