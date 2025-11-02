package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.PaymentRecordEntity;
import com.amool.application.port.out.PaymentRecordPort;
import com.amool.domain.model.PaymentRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PaymentRecordPersistenceAdapter implements PaymentRecordPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(PaymentRecord record) {
        if (record == null || record.getId() == null) return;
        PaymentRecordEntity e = entityManager.find(PaymentRecordEntity.class, record.getId());
        if (e == null) {
            e = new PaymentRecordEntity();
            e.setId(record.getId());
        }
        e.setUserId(record.getUserId());
        e.setTitle(record.getTitle());
        e.setProvider(record.getProvider());
        e.setAmount(record.getAmount());
        e.setCurrency(record.getCurrency());
        e.setPaymentMethod(record.getPaymentMethod());
        e.setStatus(record.getStatus());
        e.setSubscriptionType(record.getSubscriptionType());
        e.setTargetId(record.getTargetId());
        e.setCreatedAt(record.getCreatedAt());
        if (entityManager.contains(e)) {
            entityManager.merge(e);
        } else {
            entityManager.persist(e);
        }
    }
}
