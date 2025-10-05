package com.amool.hexagonal.integration;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.out.persistence.repository.LanguageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
public class LanguageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LanguageRepository languageRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data
        languageRepository.deleteAll();

        // Insert test data
        LanguageEntity english = new LanguageEntity();
        english.setName("English");
        
        LanguageEntity spanish = new LanguageEntity();
        spanish.setName("Spanish");
        
        languageRepository.saveAll(Arrays.asList(english, spanish));
    }

    @Test
    void getAllLanguages_ShouldReturnAllLanguages() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(oneOf("English", "Spanish"))))
                .andExpect(jsonPath("$[1].name", is(oneOf("English", "Spanish"))));
    }

    @Test
    void getAllLanguages_WhenNoLanguagesExist_ShouldReturnEmptyList() throws Exception {
        // Given
        languageRepository.deleteAll();

        // When & Then
        mockMvc.perform(get("/api/v1/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
