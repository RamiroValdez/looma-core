package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.PaymentSessionLinkEntity;
import com.amool.application.port.out.PaymentSessionLinkPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@Transactional
public class PaymentSessionLinkPersistenceAdapter implements PaymentSessionLinkPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLink(String externalReference, String sessionUuid) {
        if (externalReference == null || externalReference.isBlank() || sessionUuid == null || sessionUuid.isBlank()) return;
        PaymentSessionLinkEntity e = em.find(PaymentSessionLinkEntity.class, externalReference);
        if (e == null) {
            e = new PaymentSessionLinkEntity();
            e.setExternalReference(externalReference);
        }
        e.setSessionUuid(sessionUuid);
        e.setCreatedAt(OffsetDateTime.now());
        if (em.contains(e)) em.merge(e); else em.persist(e);
    }

    @Override
    public Optional<String> findSessionUuid(String externalReference) {
        PaymentSessionLinkEntity e = em.find(PaymentSessionLinkEntity.class, externalReference);
        return Optional.ofNullable(e != null ? e.getSessionUuid() : null);
    }
}
