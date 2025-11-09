package com.amool.adapters.out.persistence;

import java.util.List;

import com.amool.adapters.out.persistence.entity.NotificationEntity;
import com.amool.adapters.out.persistence.mappers.NotificationMapper;
import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

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

    @Override
    @Transactional
    public boolean saveNotification(Notification notification) {
        try{
            NotificationEntity notificationEntity = NotificationMapper.toEntity(notification);
            entityManager.persist(notificationEntity);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    @Transactional
    public void updateNotificationRead(Long notificationId) {
        String jpql = "UPDATE NotificationEntity n " +
                     "SET n.read = true " +
                     "WHERE n.id = :notificationId";
        
        entityManager.createQuery(jpql)
            .setParameter("notificationId", notificationId)
            .executeUpdate();
    }
}
