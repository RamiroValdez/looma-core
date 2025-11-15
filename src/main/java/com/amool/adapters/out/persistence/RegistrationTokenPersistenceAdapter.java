package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.RegistrationTokenEntity;
import com.amool.application.port.out.RegistrationTokenPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RegistrationTokenPersistenceAdapter implements RegistrationTokenPort {

    private final EntityManager em;

    public RegistrationTokenPersistenceAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void upsert(String email, String code, LocalDateTime expiresAt,
                       String name, String surname, String username, String passwordHash) {
        RegistrationTokenEntity token = findEntityByEmail(email).orElseGet(RegistrationTokenEntity::new);
        token.setEmail(email);
        token.setCode(code);
        token.setExpiresAt(expiresAt);
        token.setName(name);
        token.setSurname(surname);
        token.setUsername(username);
        token.setPasswordHash(passwordHash);
        if (token.getId() == null) {
            em.persist(token);
        } else {
            em.merge(token);
        }
    }

    @Override
    public Optional<TokenRecord> findByEmail(String email) {
        return findEntityByEmail(email).map(e -> new TokenRecord(
                e.getEmail(), e.getCode(), e.getExpiresAt(), e.getName(), e.getSurname(), e.getUsername(), e.getPasswordHash()
        ));
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        findEntityByEmail(email).ifPresent(e -> em.remove(em.contains(e) ? e : em.merge(e)));
    }

    private Optional<RegistrationTokenEntity> findEntityByEmail(String email) {
        TypedQuery<RegistrationTokenEntity> q = em.createQuery("SELECT t FROM RegistrationTokenEntity t WHERE t.email = :email", RegistrationTokenEntity.class);
        q.setParameter("email", email);
        return q.getResultList().stream().findFirst();
    }
}
