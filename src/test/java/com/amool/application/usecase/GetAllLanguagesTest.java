package com.amool.application.usecase;

import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.usecases.GetAllLanguages;
import com.amool.domain.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetAllLanguagesTest {

    private LoadLanguagePort loadLanguagePort;
    private GetAllLanguages useCase;

    @BeforeEach
    public void setUp() {
        loadLanguagePort = Mockito.mock(LoadLanguagePort.class);
        useCase = new GetAllLanguages(loadLanguagePort);
    }

    private Language createLanguage(Long id, String name, String code) {
        Language language = new Language();
        language.setId(id);
        language.setName(name);
        language.setCode(code);
        return language;
    }

    private void givenLanguages(List<Language> languages) {
        when(loadLanguagePort.loadAllLanguages()).thenReturn(languages);
    }

    private void givenNoLanguages() {
        when(loadLanguagePort.loadAllLanguages()).thenReturn(List.of());
    }

    private List<Language> whenGetAllLanguages() {
        return useCase.execute();
    }

    private void thenResultSizeIs(List<Language> result, int expectedSize) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
    }

    private void thenLanguageNameIs(List<Language> result, int index, String expectedName) {
        assertEquals(expectedName, result.get(index).getName());
    }

    private void thenLoadAllCalledOnce() {
        Mockito.verify(loadLanguagePort, Mockito.times(1)).loadAllLanguages();
    }

    @Test
    public void when_GetAllLanguages_ThenReturnListOfLanguages() {
        List<Language> expectedLanguages = Arrays.asList(
            createLanguage(1L, "Spanish", "es"),
            createLanguage(2L, "English", "en")
        );
        givenLanguages(expectedLanguages);

        List<Language> result = whenGetAllLanguages();

        thenResultSizeIs(result, 2);
        thenLanguageNameIs(result, 0, "Spanish");
        thenLanguageNameIs(result, 1, "English");
        thenLoadAllCalledOnce();
    }

    @Test
    public void when_NoLanguagesExist_ThenReturnEmptyList() {
        givenNoLanguages();

        List<Language> result = whenGetAllLanguages();

        thenResultSizeIs(result, 0);
        assertTrue(result.isEmpty());
        thenLoadAllCalledOnce();
    }
}
