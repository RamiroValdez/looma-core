package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.GetAllLanguages;
import com.amool.domain.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LanguageControllerTest {

    private LanguageController languageController;
    private GetAllLanguages getAllLanguages;

    @BeforeEach
    void setUp() {
        getAllLanguages = Mockito.mock(GetAllLanguages.class);
        languageController = new LanguageController(getAllLanguages);
    }

    @Test
    @DisplayName("GET /api/languages/obtain-all - Debe devolver lista de lenguajes cuando hay datos")
    void getAllLanguages_shouldReturnList_whenDataExists() {
        List<Language> languages = givenLanguages(
                givenLanguage(1L, "es", "Español"),
                givenLanguage(2L, "en", "English")
        );
        givenUseCaseWillReturn(languages);

        ResponseEntity<List<Language>> response = whenGettingAllLanguages();

        thenShouldReturnOk(response);
        thenListSizeIs(response.getBody(), 2);
        thenFirstItemIs(response.getBody(), 1L, "es", "Español");
        thenUseCaseWasCalledOnce();
    }

    @Test
    @DisplayName("GET /api/languages/obtain-all - Debe devolver lista vacía cuando no hay datos")
    void getAllLanguages_shouldReturnEmpty_whenNoData() {
        givenUseCaseWillReturn(List.of());

        ResponseEntity<List<Language>> response = whenGettingAllLanguages();

        thenShouldReturnOk(response);
        thenListIsEmpty(response.getBody());
        thenUseCaseWasCalledOnce();
    }

    @Test
    @DisplayName("GET /api/languages/obtain-all - Debe devolver 200 aunque el body sea null")
    void getAllLanguages_shouldReturnOk_whenNullBody() {
        givenUseCaseWillReturnNull();

        ResponseEntity<List<Language>> response = whenGettingAllLanguages();

        thenShouldReturnOk(response);
        thenBodyIsNull(response);
        thenUseCaseWasCalledOnce();
    }

    private Language givenLanguage(Long id, String code, String name) {
        Language l = new Language();
        l.setId(id);
        l.setCode(code);
        l.setName(name);
        return l;
    }

    private List<Language> givenLanguages(Language... languages) {
        return List.of(languages);
    }

    private void givenUseCaseWillReturn(List<Language> languages) {
        when(getAllLanguages.execute()).thenReturn(languages);
    }

    private void givenUseCaseWillReturnNull() {
        when(getAllLanguages.execute()).thenReturn(null);
    }

    private ResponseEntity<List<Language>> whenGettingAllLanguages() {
        return languageController.getAllLanguages();
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenListSizeIs(List<?> list, int expectedSize) {
        assertNotNull(list);
        assertEquals(expectedSize, list.size());
    }

    private void thenFirstItemIs(List<Language> list, Long id, String code, String name) {
        Language l = list.get(0);
        assertEquals(id, l.getId());
        assertEquals(code, l.getCode());
        assertEquals(name, l.getName());
    }

    private void thenListIsEmpty(List<?> list) {
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    private void thenBodyIsNull(ResponseEntity<?> response) {
        assertNull(response.getBody());
    }

    private void thenUseCaseWasCalledOnce() {
        verify(getAllLanguages, times(1)).execute();
    }
}
