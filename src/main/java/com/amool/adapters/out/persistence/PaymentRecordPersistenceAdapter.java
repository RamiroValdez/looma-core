package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.PaymentRecordEntity;
import com.amool.application.port.out.PaymentRecordPort;
import com.amool.domain.model.PaymentRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        e.setExternalReference(record.getExternalReference());
        e.setSessionUuid(record.getSessionUuid());
        if (entityManager.contains(e)) {
            entityManager.merge(e);
        } else {
            entityManager.persist(e);
        }
    }

    @Override
    public Optional<PaymentRecord> findLatestByExternalReference(String externalReference) {
        if (externalReference == null) return Optional.empty();
        TypedQuery<PaymentRecordEntity> q = entityManager.createQuery(
                "SELECT e FROM PaymentRecordEntity e WHERE e.externalReference = :ref ORDER BY e.createdAt DESC",
                PaymentRecordEntity.class);
        q.setParameter("ref", externalReference);
        q.setMaxResults(1);
        return q.getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override
    public Optional<PaymentRecord> findBySessionUuid(String sessionUuid) {
        if (sessionUuid == null) return Optional.empty();
        TypedQuery<PaymentRecordEntity> q = entityManager.createQuery(
                "SELECT e FROM PaymentRecordEntity e WHERE e.sessionUuid = :uuid",
                PaymentRecordEntity.class);
        q.setParameter("uuid", sessionUuid);
        q.setMaxResults(1);
        return q.getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override
    public boolean updateSessionUuidByExternalReference(String externalReference, String sessionUuid) {
        Optional<PaymentRecord> recOpt = findLatestByExternalReference(externalReference);
        if (recOpt.isEmpty()) return false;
        PaymentRecord r = recOpt.get();
        r.setSessionUuid(sessionUuid);
        save(r);
        return true;
    }

    private PaymentRecord toDomain(PaymentRecordEntity e) {
        PaymentRecord r = new PaymentRecord();
        r.setId(e.getId());
        r.setUserId(e.getUserId());
        r.setTitle(e.getTitle());
        r.setProvider(e.getProvider());
        r.setAmount(e.getAmount());
        r.setCurrency(e.getCurrency());
        r.setPaymentMethod(e.getPaymentMethod());
        r.setStatus(e.getStatus());
        r.setSubscriptionType(e.getSubscriptionType());
        r.setTargetId(e.getTargetId());
        r.setCreatedAt(e.getCreatedAt());
        r.setExternalReference(e.getExternalReference());
        r.setSessionUuid(e.getSessionUuid());
        return r;
    }
}
