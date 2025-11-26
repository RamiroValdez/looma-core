package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserPreferencesPersistenceAdapterTest {

    @Autowired
    private UserPreferencesPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private UserEntity user;
    private CategoryEntity cat1;
    private CategoryEntity cat2;
    private CategoryEntity cat3;

    @BeforeEach
    void setUp() {
        user = buildUser("userprefs", "userprefs@example.com");
        em.persist(user);

        Map<String, CategoryEntity> categoriesByName = loadSeededCategories();
        cat1 = getCategory(categoriesByName, "Fantasía");
        cat2 = getCategory(categoriesByName, "Ciencia Ficción");
        cat3 = getCategory(categoriesByName, "Misterio");

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("setPreferredCategories asigna el conjunto indicado de categorías")
    void setPreferredCategories_asigna() {
        Long userId = userId();
        List<Long> ids = List.of(cat1.getId(), cat3.getId());

        whenPreferencesAreSet(userId, ids);

        thenUserHasPreferences(userId, ids);
    }

    @Test
    @DisplayName("setPreferredCategories con lista vacía limpia las preferencias del usuario")
    void setPreferredCategories_lista_vacia_limpia() {
        Long userId = userId();
        whenPreferencesAreSet(userId, List.of(cat1.getId(), cat2.getId()));

        whenPreferencesAreCleared(userId);

        thenUserHasNoPreferences(userId);
    }

    @Test
    @DisplayName("setPreferredCategories ignora IDs inexistentes y asigna las existentes")
    void setPreferredCategories_ignora_inexistentes() {
        Long userId = userId();
        Long missingId = 999999L;
        List<Long> mixedIds = List.of(cat2.getId(), missingId);

        whenPreferencesAreSet(userId, mixedIds);

        thenUserHasPreferences(userId, List.of(cat2.getId()));
    }

    @Test
    @DisplayName("setPreferredCategories lanza si userId es null o usuario no existe")
    void setPreferredCategories_valida_user() {
        assertThatThrownBy(() -> adapter.setPreferredCategories(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId is required");

        assertThatThrownBy(() -> adapter.setPreferredCategories(123456789L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    private Long userId() {
        return user.getId();
    }

    private void whenPreferencesAreSet(Long userId, List<Long> categoryIds) {
        adapter.setPreferredCategories(userId, categoryIds);
    }

    private void whenPreferencesAreCleared(Long userId) {
        adapter.setPreferredCategories(userId, new ArrayList<>());
    }

    private void thenUserHasPreferences(Long userId, List<Long> expectedIds) {
        UserEntity refreshed = fetchUser(userId);
        assertPreferredCategoryIds(refreshed, expectedIds.toArray(Long[]::new));
    }

    private void thenUserHasNoPreferences(Long userId) {
        UserEntity refreshed = fetchUser(userId);
        assertThat(refreshed.getPreferredCategories()).isEmpty();
    }

    private Map<String, CategoryEntity> loadSeededCategories() {
        List<CategoryEntity> categories = em.createQuery("SELECT c FROM CategoryEntity c", CategoryEntity.class)
                .getResultList();
        return categories.stream().collect(java.util.stream.Collectors.toUnmodifiableMap(CategoryEntity::getName, c -> c));
    }

    private CategoryEntity getCategory(Map<String, CategoryEntity> categories, String name) {
        CategoryEntity category = categories.get(name);
        if (category == null) {
            throw new IllegalStateException("Missing seeded category: " + name);
        }
        return category;
    }

    private UserEntity fetchUser(Long userId) {
        return em.find(UserEntity.class, userId);
    }

    private void assertPreferredCategoryIds(UserEntity user, Long... expectedIds) {
        assertThat(user.getPreferredCategories())
                .extracting(CategoryEntity::getId)
                .containsExactlyInAnyOrder(expectedIds);
    }

    private UserEntity buildUser(String username, String email) {
        UserEntity u = new UserEntity();
        u.setName("Name");
        u.setSurname("Surname");
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("pwd");
        u.setEnabled(true);
        return u;
    }

    private CategoryEntity buildCategory(String name) {
        CategoryEntity c = new CategoryEntity();
        c.setName(name);
        return c;
    }
}
