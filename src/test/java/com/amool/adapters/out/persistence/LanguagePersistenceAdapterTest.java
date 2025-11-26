package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.domain.model.Language;
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
public class LanguagePersistenceAdapterTest {

    @Autowired
    private LanguagePersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    private List<Language> languagesResult;
    private Optional<Language> languageByIdResult;

    @BeforeEach
    void cleanData() {
        entityManager.createQuery("DELETE FROM LanguageEntity").executeUpdate();
    }

    @Test
    void should_load_all_languages_mapped_to_domain() {
        givenLanguage("es", "Español");
        givenLanguage("en", "English");
        givenLanguage("fr", "Français");

        whenLoadAllLanguages();

        thenLanguagesHasSize(3);
        thenLanguagesContainNames("Español", "English", "Français");
        thenLanguagesContainCodes("es", "en", "fr");
    }

    @Test
    void should_load_language_by_id_present_when_exists_and_empty_when_not_exists() {
        Long langId = givenLanguage("pt", "Português");
        Long nonExisting = 999999L;

        whenLoadLanguageById(langId);

        thenLanguageByIdIsPresentWith("pt", "Português");

        whenLoadLanguageById(nonExisting);

        thenLanguageByIdIsEmpty();
    }

    @Test
    void should_get_languages_by_codes_only_matching_present_codes() {
        givenLanguage("es", "Español");
        givenLanguage("en", "English");

        whenGetLanguagesByCodes(List.of("es", "en", "it"));

        thenLanguagesHasSize(2);
        thenLanguagesContainCodes("es", "en");
    }

    @Test
    void should_get_languages_by_codes_returns_empty_on_null_or_empty_input() {
        givenLanguage("es", "Español");

        whenGetLanguagesByCodes(null);

        thenLanguagesHasSize(0);

        whenGetLanguagesByCodes(List.of());

        thenLanguagesHasSize(0);
    }

    private Long givenLanguage(String code, String name) {
        LanguageEntity e = new LanguageEntity();
        e.setCode(code);
        e.setName(name);
        entityManager.persist(e);
        entityManager.flush();
        return e.getId();
    }

    private void whenLoadAllLanguages() {
        languagesResult = adapter.loadAllLanguages();
    }

    private void whenLoadLanguageById(Long id) {
        languageByIdResult = adapter.loadLanguageById(id);
    }

    private void whenGetLanguagesByCodes(List<String> codes) {
        languagesResult = adapter.getLanguagesByCodes(codes);
    }

    private void thenLanguagesHasSize(int expected) {
        assertThat(languagesResult).hasSize(expected);
    }

    private void thenLanguagesContainNames(String... names) {
        assertThat(languagesResult.stream().map(Language::getName)).containsExactlyInAnyOrder(names);
    }

    private void thenLanguagesContainCodes(String... codes) {
        assertThat(languagesResult.stream().map(Language::getCode)).containsExactlyInAnyOrder(codes);
    }

    private void thenLanguageByIdIsPresentWith(String expectedCode, String expectedName) {
        assertThat(languageByIdResult).isPresent();
        assertThat(languageByIdResult.get().getCode()).isEqualTo(expectedCode);
        assertThat(languageByIdResult.get().getName()).isEqualTo(expectedName);
    }

    private void thenLanguageByIdIsEmpty() {
        assertThat(languageByIdResult).isEmpty();
    }
}
