package com.amool.adapters.out.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amool.adapters.out.persistence.entity.NotificationEntity;
import com.amool.adapters.out.persistence.mappers.NotificationMapper;
import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;
import com.amool.domain.model.Notification.NotificationType;

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
        try {
            // Guardar la notificación
            NotificationEntity notificationEntity = NotificationMapper.toEntity(notification);
            entityManager.persist(notificationEntity);
            
            // Marcar como notificado en la tabla correspondiente
            String type = notification.getType().name();
            if (type.startsWith("NEW_") && type.endsWith("_SUBSCRIBER")) {
                String entityType = type.replace("NEW_", "").replace("_SUBSCRIBER", "");
                Long relatedId = switch (entityType) {
                    case "AUTHOR" -> notification.getUserId(); // Para autores, el relatedId es el ID del autor
                    case "WORK" -> notification.getRelatedWork();
                    case "CHAPTER" -> notification.getRelatedChapter();
                    default -> null;
                };
                
                if (relatedId != null) {
                    markAsNotified(entityType, notification.getRelatedUser(), relatedId);
                }
            }
            
            return true;
        } catch (Exception e) {
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

    @Override
    @Transactional
    public List<Notification> getPendingNotifications(int batchSize) {
        List<Notification> pendingNotifications = new ArrayList<>();
        
        // Obtener notificaciones pendientes de cada tipo
        pendingNotifications.addAll(getPendingAuthorSubscriptions(batchSize));
        if (pendingNotifications.size() < batchSize) {
            int remaining = batchSize - pendingNotifications.size();
            pendingNotifications.addAll(getPendingWorkSubscriptions(remaining));
        }
        if (pendingNotifications.size() < batchSize) {
            int remaining = batchSize - pendingNotifications.size();
            pendingNotifications.addAll(getPendingChapterSubscriptions(remaining));
        }
        
        return pendingNotifications.stream().limit(batchSize).toList();
    }

    private List<Notification> getPendingAuthorSubscriptions(int limit) {
        String sql = 
            "SELECT " +
            "  sa.user_id, " +                    // [0] Quien se suscribió (related_user)
            "  sa.autor_id as related_id, " +     // [1] ID del autor (user_id)
            "  sa.autor_id as recipient_id, " +   // [2] El autor es el destinatario
            "  'AUTHOR' as type, " +              // [3] Tipo de notificación
            "  sa.created_at " +                  // [4] Fecha de creación
            "FROM suscribe_autor sa " +
            "WHERE sa.notified = false " +        // Solo suscripciones no notificadas
            "  AND sa.created_at >= :since " +
            "ORDER BY sa.created_at " +
            "LIMIT :limit";
        
        return executeSubscriptionQuery(sql, limit);
    }

    private List<Notification> getPendingWorkSubscriptions(int limit) {
        String sql = 
            "SELECT " +
            "  sw.user_id, " +                    // [0] Quien se suscribió (related_user)
            "  sw.work_id as related_id, " +      // [1] ID de la obra (related_work)
            "  w.creator_id as recipient_id, " +  // [2] Dueño de la obra (user_id)
            "  'WORK' as type, " +                // [3] Tipo de notificación
            "  sw.created_at " +                  // [4] Fecha de creación
            "FROM suscribe_work sw " +
            "JOIN work w ON sw.work_id = w.id " +
            "WHERE sw.notified = false " +        // Solo suscripciones no notificadas
            "  AND sw.created_at >= :since " +
            "ORDER BY sw.created_at " +
            "LIMIT :limit";
        
        return executeSubscriptionQuery(sql, limit);
    }

    private List<Notification> getPendingChapterSubscriptions(int limit) {
        String sql = 
            "SELECT " +
            "  sc.user_id, " +                    // [0] Quien se suscribió (related_user)
            "  sc.chapter_id as related_id, " +   // [1] ID del capítulo (related_chapter)
            "  w.creator_id as recipient_id, " +  // [2] Dueño de la obra (user_id)
            "  'CHAPTER' as type, " +             // [3] Tipo de notificación
            "  sc.created_at " +                  // [4] Fecha de creación
            "FROM suscribe_chapter sc " +
            "JOIN chapter c ON sc.chapter_id = c.id " +
            "JOIN work w ON c.work_id = w.id " +
            "WHERE sc.notified = false " +        // Solo suscripciones no notificadas
            "  AND sc.created_at >= :since " +
            "ORDER BY sc.created_at " +
            "LIMIT :limit";
        
        return executeSubscriptionQuery(sql, limit);
    }

    @SuppressWarnings("unchecked")
    private List<Notification> executeSubscriptionQuery(String sql, int limit) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("since", LocalDateTime.now().minusHours(24))
                .setParameter("limit", limit)
                .getResultList();

            return results.stream()
                .map(this::mapToNotification)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    private Notification mapToNotification(Object[] result) {
        Long subscriberId = ((Number) result[0]).longValue();  // Quien se suscribió
        Long relatedId = ((Number) result[1]).longValue();     // ID del contenido
        Long recipientId = ((Number) result[2]).longValue();   // Dueño del contenido
        String type = ((String) result[3]).toUpperCase();      // Tipo en mayúsculas
        
        Notification notification = new Notification();
        notification.setType(NotificationType.valueOf("NEW_" + type + "_SUBSCRIBER"));
        notification.setRelatedUser(subscriberId);  // Quien se suscribió
        notification.setUserId(recipientId);        // Dueño del contenido
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        
        // Establecer el ID relacionado según el tipo
        switch (type) {
            case "WORK" -> notification.setRelatedWork(relatedId);
            case "CHAPTER" -> notification.setRelatedChapter(relatedId);
        }
        
        return notification;
    }

    @Transactional
    private void markAsNotified(String type, Long subscriberId, Long relatedId) {
        String updateQuery = switch (type) {
            case "AUTHOR" -> 
                "UPDATE suscribe_autor SET notified = true WHERE user_id = :userId AND autor_id = :relatedId";
            case "WORK" -> 
                "UPDATE suscribe_work SET notified = true WHERE user_id = :userId AND work_id = :relatedId";
            case "CHAPTER" -> 
                "UPDATE suscribe_chapter SET notified = true WHERE user_id = :userId AND chapter_id = :relatedId";
            default -> null;
        };

        if (updateQuery != null) {
            entityManager.createNativeQuery(updateQuery)
                .setParameter("userId", subscriberId)
                .setParameter("relatedId", relatedId)
                .executeUpdate();
        }
    }
}