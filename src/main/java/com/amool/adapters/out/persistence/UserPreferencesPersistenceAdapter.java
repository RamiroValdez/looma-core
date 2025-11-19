package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.application.port.out.UserPreferencesPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
public class UserPreferencesPersistenceAdapter implements UserPreferencesPort {

    private final EntityManager em;

    public UserPreferencesPersistenceAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void setPreferredCategories(Long userId, List<Long> categoryIds) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        UserEntity user = em.find(UserEntity.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found: " + userId);

        if (categoryIds == null || categoryIds.isEmpty()) {
            user.setPreferredCategories(new HashSet<>());
            em.merge(user);
            return;
        }

        TypedQuery<CategoryEntity> q = em.createQuery(
                "SELECT c FROM CategoryEntity c WHERE c.id IN :ids", CategoryEntity.class);
        q.setParameter("ids", categoryIds);
        List<CategoryEntity> categories = q.getResultList();
        user.setPreferredCategories(new HashSet<>(categories));
        em.merge(user);
    }
}
