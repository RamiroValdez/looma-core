package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserAuthenticationPersistenceAdapterTest {

    private EntityManager entityManager;
    private TypedQuery<UserEntity> typedQuery;
    private UserAuthenticationPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        entityManager = Mockito.mock(EntityManager.class);
        typedQuery = Mockito.mock(TypedQuery.class);
        adapter = new UserAuthenticationPersistenceAdapter(entityManager);

        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
    }

    @Test
    void authenticate_returnsUser_whenBcryptMatches_andEnabledInQuery() {
        var encoder = new BCryptPasswordEncoder();
        var e = new UserEntity();
        e.setId(1L);
        e.setEmail("mail@test.com");
        e.setPassword(encoder.encode("Pass1!"));
        e.setEnabled(true); 

        when(typedQuery.getResultList()).thenReturn(List.of(e));

        Optional<com.amool.domain.model.User> result = adapter.authenticate("mail@test.com", "Pass1!");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(entityManager).createQuery(contains("enabled = true"), eq(UserEntity.class));
    }

    @Test
    void authenticate_returnsUser_whenPlaintextMatches_forLegacyPasswords() {
        var e = new UserEntity();
        e.setId(2L);
        e.setEmail("user2@test.com");
        e.setPassword("legacyPlain");
        e.setEnabled(true);
        when(typedQuery.getResultList()).thenReturn(List.of(e));

        Optional<com.amool.domain.model.User> result = adapter.authenticate("user2@test.com", "legacyPlain");

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
    }

    @Test
    void authenticate_returnsEmpty_whenQueryFindsNoEnabledUser() {
        when(typedQuery.getResultList()).thenReturn(List.of());
        Optional<com.amool.domain.model.User> result = adapter.authenticate("x@test.com", "any");
        assertTrue(result.isEmpty());
    }
}
