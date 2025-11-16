package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.PaymentProcessedEntity;
import com.amool.application.port.out.PaymentAuditPort;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class PaymentAuditPersistenceAdapter implements PaymentAuditPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean markProcessedIfFirst(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) return false;
        int affected = entityManager.createNativeQuery(
                "INSERT INTO payment_processed (payment_id) VALUES (:pid) ON CONFLICT DO NOTHING")
            .setParameter("pid", paymentId)
            .executeUpdate();
        return affected > 0;
    }
}
