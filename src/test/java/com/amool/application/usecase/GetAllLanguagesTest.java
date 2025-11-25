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

    @Test
    public void when_GetAllLanguages_ThenReturnListOfLanguages() {
        List<Language> expectedLanguages = Arrays.asList(
            createLanguage(1L, "Spanish", "es"),
            createLanguage(2L, "English", "en")
        );
        
        when(loadLanguagePort.loadAllLanguages())
            .thenReturn(expectedLanguages);

            List<Language> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Spanish", result.get(0).getName());
        assertEquals("English", result.get(1).getName());
        
        Mockito.verify(loadLanguagePort, Mockito.times(1))
               .loadAllLanguages();
    }

    @Test
    public void when_NoLanguagesExist_ThenReturnEmptyList() {
        when(loadLanguagePort.loadAllLanguages())
            .thenReturn(List.of());

        List<Language> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        Mockito.verify(loadLanguagePort, Mockito.times(1))
               .loadAllLanguages();
    }
}
