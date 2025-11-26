package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.domain.model.Format;
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
public class FormatPersistenceAdapterTest {

    @Autowired
    private FormatPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    private List<Format> formatsResult;
    private Optional<Format> formatByIdResult;

    @BeforeEach
    void cleanData() {
        entityManager.createQuery("DELETE FROM FormatEntity").executeUpdate();
    }

    @Test
    void should_get_all_formats_mapped_to_domain() {

        Long id1 = givenFormat("NOVEL");
        Long id2 = givenFormat("COMIC");

        whenGetAllFormats();

        thenFormatsHasSize(2);
        thenFormatsContainNames("NOVEL", "COMIC");
    }

    @Test
    void should_get_format_by_id_present_when_exists_and_empty_when_not_exists() {

        Long formatId = givenFormat("POEM");
        Long nonExistingId = 999999L;


        whenGetFormatById(formatId);

        thenFormatByIdIsPresentWithName("POEM");

        whenGetFormatById(nonExistingId);

        thenFormatByIdIsEmpty();
    }

    private Long givenFormat(String name) {
        FormatEntity e = new FormatEntity();
        e.setName(name);
        entityManager.persist(e);
        entityManager.flush();
        return e.getId();
    }

    private void whenGetAllFormats() {
        formatsResult = adapter.getAll();
    }

    private void whenGetFormatById(Long id) {
        formatByIdResult = adapter.getById(id);
    }

    private void thenFormatsHasSize(int expected) {
        assertThat(formatsResult).hasSize(expected);
    }

    private void thenFormatsContainNames(String... names) {
        assertThat(formatsResult.stream().map(Format::getName)).containsExactlyInAnyOrder(names);
    }

    private void thenFormatByIdIsPresentWithName(String expectedName) {
        assertThat(formatByIdResult).isPresent();
        assertThat(formatByIdResult.get().getName()).isEqualTo(expectedName);
    }

    private void thenFormatByIdIsEmpty() {
        assertThat(formatByIdResult).isEmpty();
    }
}
