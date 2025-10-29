package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.PaymentProcessedEntity;
import com.amool.hexagonal.application.port.out.PaymentAuditPort;
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
        try {
            PaymentProcessedEntity e = new PaymentProcessedEntity(paymentId);
            entityManager.persist(e);
            entityManager.flush();
            return true;
        } catch (EntityExistsException | DataIntegrityViolationException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
}
