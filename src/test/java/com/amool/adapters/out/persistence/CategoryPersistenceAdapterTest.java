package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.domain.model.Category;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryPersistenceAdapterTest {

    @Autowired
    private CategoryPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    private List<Category> categoriesResult;
    private Optional<Category> categoryByIdResult;
    private Long persistedId1;
    private Long persistedId2;

    @BeforeEach
    void cleanData() {
        entityManager.createQuery("DELETE FROM CategoryEntity").executeUpdate();
    }

    @Test
    void should_find_all_categories_mapped_to_domain() {
        persistedId1 = givenCategory("Ficción");
        persistedId2 = givenCategory("No Ficción");

        whenFindAllCategories();

        thenCategoriesHasSize(2);
        thenCategoriesContainNames("Ficción", "No Ficción");
    }

    @Test
    void should_get_category_by_id_when_exists_and_empty_when_not_exists() {
        persistedId1 = givenCategory("Ciencia");
        Long nonExistingId = 999999L;

        whenGetCategoryById(persistedId1);

        thenCategoryByIdIsPresentWithName("Ciencia");

        whenGetCategoryById(nonExistingId);
        thenCategoryByIdIsEmpty();
    }


    private Long givenCategory(String name) {
        CategoryEntity e = new CategoryEntity();
        e.setName(name);
        entityManager.persist(e);
        entityManager.flush();
        return e.getId();
    }

    private void whenFindAllCategories() {
        categoriesResult = adapter.findAllCategories();
    }

    private void whenGetCategoryById(Long id) {
        categoryByIdResult = adapter.getCategoryById(id);
    }

    private void thenCategoriesHasSize(int expected) {
        assertThat(categoriesResult).hasSize(expected);
    }

    private void thenCategoriesContainNames(String... names) {
        assertThat(categoriesResult.stream().map(Category::getName)).containsExactlyInAnyOrder(names);
    }

    private void thenCategoryByIdIsPresentWithName(String expectedName) {
        assertThat(categoryByIdResult).isPresent();
        assertThat(categoryByIdResult.get().getName()).isEqualTo(expectedName);
    }

    private void thenCategoryByIdIsEmpty() {
        assertThat(categoryByIdResult).isEmpty();
    }
}
