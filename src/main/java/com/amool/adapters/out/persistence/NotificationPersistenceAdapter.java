package com.amool.adapters.out.persistence;

import java.util.List;

import com.amool.adapters.out.persistence.entity.NotificationEntity;
import com.amool.adapters.out.persistence.mappers.NotificationMapper;
import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceAdapter implements NotificationPort {

    @PersistenceContext
    private EntityManager entityManager;   
    
    @Override
    public List<Notification> getNotifications(Long userId) {
        String jpql = "SELECT n FROM NotificationEntity n " +
                     "WHERE n.userId = :userId " +
                     "ORDER BY n.createdAt DESC";
        
        List<NotificationEntity> entities = entityManager
            .createQuery(jpql, NotificationEntity.class)
            .setParameter("userId", userId)
            .getResultList();
        
        return entities.stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }
}
