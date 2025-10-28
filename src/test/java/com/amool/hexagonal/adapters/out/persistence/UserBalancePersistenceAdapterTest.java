package com.amool.hexagonal.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserBalancePersistenceAdapter.class)
class UserBalancePersistenceAdapterTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserBalancePersistenceAdapter adapter;

    Long userId;

    @BeforeEach
    void setUp() {
        UserEntity u = new UserEntity();
        u.setName("Name");
        u.setSurname("Surname");
        u.setUsername("user");
        u.setEmail("user@example.com");
        u.setPassword("pass");
        em.persist(u);
        em.flush();
        userId = u.getId();
    }

    @Test
    void addMoney_incrementsBalance() {
        adapter.addMoney(userId, new BigDecimal("123.45"));
        em.flush();
        em.clear();
        UserEntity reloaded = em.find(UserEntity.class, userId);
        assertThat(reloaded.getMoney()).isEqualByComparingTo(new BigDecimal("123.45"));
        adapter.addMoney(userId, new BigDecimal("10.00"));
        em.flush();
        em.clear();
        reloaded = em.find(UserEntity.class, userId);
        assertThat(reloaded.getMoney()).isEqualByComparingTo(new BigDecimal("133.45"));
    }
}
