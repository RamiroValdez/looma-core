package com.amool.hexagonal.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.hexagonal.application.port.out.UserBalancePort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Transactional
public class UserBalancePersistenceAdapter implements UserBalancePort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addMoney(Long userId, BigDecimal amount) {
        if (userId == null || amount == null) return;
        UserEntity user = entityManager.find(UserEntity.class, userId);
        if (user == null) return;
        if (user.getMoney() == null) user.setMoney(BigDecimal.ZERO);
        user.setMoney(user.getMoney().add(amount));
        entityManager.merge(user);
    }
}
