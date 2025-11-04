package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=public"
})
@EntityScan(basePackages = "com.amool.adapters.out.persistence.entity")
@Import(ChapterPersistenceAdapter.class)
class ChapterPersistenceAdapterIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChapterPersistenceAdapter chapterPersistenceAdapter;

    private UserEntity user;
    private FormatEntity format;
    private LanguageEntity language;
    private WorkEntity work;
    private ChapterEntity chapter;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setName("Test");
        user.setSurname("User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        format = new FormatEntity();
        format.setName("Format");
        entityManager.persist(format);

        language = new LanguageEntity();
        language.setName("Español");
        entityManager.persist(language);

        work = new WorkEntity();
        work.setTitle("Work");
        work.setDescription("Desc");
        work.setState("DRAFT");
        work.setPrice(BigDecimal.valueOf(0.0));
        work.setLikes(0);
        work.setPublicationDate(LocalDate.now());
        work.setCreator(user);
        work.setFormatEntity(format);
        work.setOriginalLanguageEntity(language);
        entityManager.persist(work);

        chapter = new ChapterEntity();
        chapter.setWorkEntity(work);
        chapter.setLanguageEntity(language);
        chapter.setTitle("Cap 1");
        chapter.setPrice(BigDecimal.valueOf(0.0));
        entityManager.persist(chapter);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void updatePublicationStatus_shouldSetStatusAndTimestamps_whenOk() {
        LocalDateTime now = LocalDateTime.now();
        chapterPersistenceAdapter.updatePublicationStatus(work.getId(), chapter.getId(), "PUBLISHED", now);

        ChapterEntity reloaded = entityManager.find(ChapterEntity.class, chapter.getId());
        assertNotNull(reloaded);
        assertEquals("PUBLISHED", reloaded.getPublicationStatus());
        assertNotNull(reloaded.getPublishedAt());
        assertNotNull(reloaded.getLastModified());
    }

    @Test
    void updatePublicationStatus_shouldThrow_whenWrongWorkId() {
        assertThrows(java.util.NoSuchElementException.class, () ->
                chapterPersistenceAdapter.updatePublicationStatus(9999L, chapter.getId(), "PUBLISHED", LocalDateTime.now())
        );
    }

    @Test
    void schedulePublication_shouldSetScheduledStatusAndDate() {
        Instant when = Instant.now().plusSeconds(3600);
        chapterPersistenceAdapter.schedulePublication(work.getId(), chapter.getId(), when);

        ChapterEntity reloaded = entityManager.find(ChapterEntity.class, chapter.getId());
        assertNotNull(reloaded);
        assertEquals("SCHEDULED", reloaded.getPublicationStatus());
        assertNotNull(reloaded.getScheduledPublicationDate());
        assertNull(reloaded.getPublishedAt());
    }

    @Test
    void clearSchedule_shouldUnsetScheduleAndDraftStatus() {
        Instant when = Instant.now().plusSeconds(3600);
        chapterPersistenceAdapter.schedulePublication(work.getId(), chapter.getId(), when);

        chapterPersistenceAdapter.clearSchedule(work.getId(), chapter.getId());

        ChapterEntity reloaded = entityManager.find(ChapterEntity.class, chapter.getId());
        assertNotNull(reloaded);
        assertEquals("DRAFT", reloaded.getPublicationStatus());
        assertNull(reloaded.getScheduledPublicationDate());
    }

    @Test
    void findDue_shouldReturnScheduledChaptersWithElapsedTime() {
        Instant past = Instant.now().minusSeconds(60);
        chapterPersistenceAdapter.schedulePublication(work.getId(), chapter.getId(), past);

        var due = chapterPersistenceAdapter.findDue(Instant.now(), 10);
        assertFalse(due.isEmpty());
        assertTrue(due.stream().anyMatch(dc -> dc.workId().equals(work.getId()) && dc.chapterId().equals(chapter.getId())));
    }
}
