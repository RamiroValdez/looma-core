package com.amool.hexagonal.integration;

import com.amool.hexagonal.adapters.in.rest.controllers.MyWorksController;
import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.out.persistence.WorksPersistenceAdapter;
import com.amool.hexagonal.application.port.out.*;
import com.amool.hexagonal.security.JwtUserPrincipal;
import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.UserEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.FormatEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.application.service.WorkServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.amool.hexagonal.adapters.out.mongodb.repository.MongoChapterContentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.data.mongodb.repositories.enabled=false"
})
@Transactional
class MyWorksIntegrationTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public LoadChapterContentPort loadChapterContentPort() {
            return mock(LoadChapterContentPort.class);
        }

        @Bean
        @Primary
        public LoadChapterPort loadChapterPort() {
            return mock(LoadChapterPort.class);
        }

        @Bean
        @Primary
        public SaveChapterContentPort saveChapterContentPort() {
            return mock(SaveChapterContentPort.class);
        }

        @Bean
        @Primary
        public SaveChapterPort saveChapterPort() {
            return mock(SaveChapterPort.class);
        }

        @Bean
        @Primary
        public DeleteChapterPort deleteChapterPort() {
            return mock(DeleteChapterPort.class);
        }

        @Bean
        @Primary
        public MongoChapterContentRepository mongoChapterContentRepository() {
            return mock(MongoChapterContentRepository.class);
        }

        @Bean
        @Primary
        public MongoTemplate mongoTemplate() {
            return mock(MongoTemplate.class);
        }

        @Bean
        @Primary
        public MongoClient mongoClient() {
            return mock(MongoClient.class);
        }
    }

    @Autowired
    private MyWorksController myWorksController;

    @Autowired
    private WorkServiceImpl workServiceImpl;

    @Autowired
    private WorksPersistenceAdapter worksPersistenceAdapter;

    @Autowired
    private EntityManager entityManager;

    private UserEntity testUser;
    private FormatEntity testFormat;
    private LanguageEntity testLanguage;
    private WorkEntity testWork1;
    private WorkEntity testWork2;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        entityManager.persist(testUser);

        testFormat = new FormatEntity();
        testFormat.setName("Test Format");
        entityManager.persist(testFormat);

        testLanguage = new LanguageEntity();
        testLanguage.setName("Español");
        entityManager.persist(testLanguage);

        testWork1 = new WorkEntity();
        testWork1.setTitle("Test Work 1");
        testWork1.setDescription("Description 1");
        testWork1.setState("PUBLISHED");
        testWork1.setPrice(10.99);
        testWork1.setLikes(5);
        testWork1.setPublicationDate(LocalDate.now());
        testWork1.setCreator(testUser);
        testWork1.setFormatEntity(testFormat);
        testWork1.setOriginalLanguageEntity(testLanguage);
        entityManager.persist(testWork1);

        testWork2 = new WorkEntity();
        testWork2.setTitle("Test Work 2");
        testWork2.setDescription("Description 2");
        testWork2.setState("DRAFT");
        testWork2.setPrice(15.99);
        testWork2.setLikes(10);
        testWork2.setPublicationDate(LocalDate.now());
        testWork2.setCreator(testUser);
        testWork2.setFormatEntity(testFormat);
        testWork2.setOriginalLanguageEntity(testLanguage);
        entityManager.persist(testWork2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testCompleteFlow_GetWorksByUserId_ReturnsWorks() {
        JwtUserPrincipal principal = new JwtUserPrincipal(
            testUser.getId(), 
            "test@example.com", 
            "Test", 
            "User", 
            "testuser"
        );
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, List.of(() -> "ROLE_USER"))
        );
        
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getWorksByUserId(testUser.getId(), principal);
        List<WorkResponseDto> result = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertEquals(2, result.size());

        WorkResponseDto work1 = result.stream()
                .filter(w -> w.getTitle().equals("Test Work 1"))
                .findFirst()
                .orElse(null);
        assertNotNull(work1);
        assertEquals("Test Work 1", work1.getTitle());
        assertEquals("Description 1", work1.getDescription());
        assertEquals("PUBLISHED", work1.getState());
        assertEquals(10.99, work1.getPrice());
        assertEquals(5, work1.getLikes());

        
        WorkResponseDto work2 = result.stream()
                .filter(w -> w.getTitle().equals("Test Work 2"))
                .findFirst()
                .orElse(null);
        assertNotNull(work2);
        assertEquals("Test Work 2", work2.getTitle());
        assertEquals("Description 2", work2.getDescription());
        assertEquals("DRAFT", work2.getState());
        assertEquals(15.99, work2.getPrice());
        assertEquals(10, work2.getLikes());
    }

    @Test
    @WithMockUser(username = "nonexistent", roles = {"USER"})
    void testCompleteFlow_GetWorksByNonExistentUserId_ReturnsEmptyList() {
        JwtUserPrincipal mockPrincipal = new JwtUserPrincipal(
            99999L, 
            "nonexistent@example.com", 
            "Nonexistent", 
            "User", 
            "nonexistent"
        );
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(mockPrincipal, null, List.of(() -> "ROLE_USER"))
        );
        
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getWorksByUserId(99999L, mockPrincipal);
        List<WorkResponseDto> result = response.getBody();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testServiceLayer_GetWorksByUserId_CallsPersistenceAdapter() {
        
        var works = workServiceImpl.getWorksByUserId(testUser.getId());

        
        assertNotNull(works);
        assertEquals(2, works.size());
        assertTrue(works.stream().anyMatch(w -> w.getTitle().equals("Test Work 1")));
        assertTrue(works.stream().anyMatch(w -> w.getTitle().equals("Test Work 2")));
    }

    @Test
    void testPersistenceLayer_GetWorksByUserId_QueriesDatabase() {
        
        var works = worksPersistenceAdapter.getWorksByUserId(testUser.getId());

        assertNotNull(works);
        assertEquals(2, works.size());

        
        works.forEach(work -> {
            assertNotNull(work.getId());
            assertNotNull(work.getTitle());
            assertNotNull(work.getCreator());
            assertEquals(testUser.getId(), work.getCreator().getId());
        });
    }

    @Test
    @WithMockUser(username = "emptyuser", roles = {"USER"})
    void testCompleteFlow_UserWithNoWorks_ReturnsEmptyList() {
        UserEntity userWithNoWorks = new UserEntity();
        userWithNoWorks.setName("Empty");
        userWithNoWorks.setSurname("User");
        userWithNoWorks.setUsername("emptyuser");
        userWithNoWorks.setEmail("empty@example.com");
        userWithNoWorks.setPassword("password123");
        entityManager.persist(userWithNoWorks);
        entityManager.flush();
        
        JwtUserPrincipal principal = new JwtUserPrincipal(
            userWithNoWorks.getId(),
            "empty@example.com",
            "Empty",
            "User",
            "emptyuser"
        );
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, List.of(() -> "ROLE_USER"))
        );
        
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getWorksByUserId(userWithNoWorks.getId(), principal);
        List<WorkResponseDto> result = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
