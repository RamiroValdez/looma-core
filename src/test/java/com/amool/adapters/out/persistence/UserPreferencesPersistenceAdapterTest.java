package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserPreferencesPersistenceAdapter.class)
@EntityScan(basePackages = "com.amool.adapters.out.persistence.entity")
public class UserPreferencesPersistenceAdapterTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserPreferencesPersistenceAdapter adapter;

    Long userId;
    Long cat1;
    Long cat2;

    @BeforeEach
    void setUp() {
        UserEntity u = new UserEntity();
        u.setName("Name"); u.setSurname("Surname"); u.setUsername("user"); u.setEmail("user@example.com"); u.setPassword("pass");
        em.persist(u);
        userId = u.getId();

        CategoryEntity c1 = new CategoryEntity();
        c1.setName("Cat 1");
        em.persist(c1);
        cat1 = c1.getId();

        CategoryEntity c2 = new CategoryEntity();
        c2.setName("Cat 2");
        em.persist(c2);
        cat2 = c2.getId();
        em.flush();
        em.clear();
    }

    @Test
    void setPreferredCategories_writesJoinTable() {
        adapter.setPreferredCategories(userId, List.of(cat1, cat2));
        em.flush();
        em.clear();

        UserEntity reloaded = em.find(UserEntity.class, userId);
        List<Long> saved = reloaded.getPreferredCategories().stream()
                .map(CategoryEntity::getId)
                .sorted()
                .collect(Collectors.toList());
        assertThat(saved).containsExactly(cat1, cat2);
    }

    @Test
    void setPreferredCategories_emptyClears() {
        adapter.setPreferredCategories(userId, List.of());
        em.flush(); em.clear();
        UserEntity reloaded = em.find(UserEntity.class, userId);
        assertThat(reloaded.getPreferredCategories()).isEmpty();
    }
}
