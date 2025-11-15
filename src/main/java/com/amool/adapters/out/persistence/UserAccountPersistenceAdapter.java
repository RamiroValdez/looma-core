package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.application.port.out.UserAccountPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@Component
public class UserAccountPersistenceAdapter implements UserAccountPort {

    private final EntityManager em;

    public UserAccountPersistenceAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean emailExists(String email) {
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class);
        q.setParameter("email", email);
        return q.getSingleResult() > 0;
    }

    @Override
    public boolean usernameExists(String username) {
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username", Long.class);
        q.setParameter("username", username);
        return q.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public void upsertPendingUser(String name, String surname, String username, String email,
                                  String passwordHash, String verificationCode, LocalDateTime expiresAt) {
        TypedQuery<UserEntity> byEmail = em.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class);
        byEmail.setParameter("email", email);
        UserEntity user = byEmail.getResultList().stream().findFirst().orElse(null);

        if (user != null && Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalArgumentException("Email already exists");
        }

        TypedQuery<Long> usernameTaken = em.createQuery(
                "SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username AND (u.email <> :email OR :email IS NULL) AND u.enabled = true",
                Long.class);
        usernameTaken.setParameter("username", username);
        usernameTaken.setParameter("email", email);
        if (usernameTaken.getSingleResult() > 0) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setEnabled(Boolean.FALSE);
        }

        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(passwordHash);
        user.setVerificationCode(verificationCode);
        user.setVerificationExpiresAt(expiresAt);

        if (user.getId() == null) {
            em.persist(user);
        } else {
            em.merge(user);
        }
    }

    @Override
    @Transactional
    public Long enableUserIfCodeValid(String email, String code) {
        TypedQuery<UserEntity> byEmail = em.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class);
        byEmail.setParameter("email", email);
        UserEntity user = byEmail.getResultList().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Verification pending not found"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Invalid code");
        }
        LocalDateTime exp = user.getVerificationExpiresAt();
        if (exp != null && exp.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Code expired");
        }

        user.setEnabled(Boolean.TRUE);
        user.setVerificationCode(null);
        user.setVerificationExpiresAt(null);
        em.merge(user);
        return user.getId();
    }
}
