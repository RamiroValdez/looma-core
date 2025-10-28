package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.domain.model.Work;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=public"
})
@Import(WorksPersistenceAdapter.class)
class WorksPersistenceAdapterIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WorksPersistenceAdapter worksPersistenceAdapter;

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
        work.setPrice(0.0);
        work.setLikes(0);
        work.setPublicationDate(LocalDate.now());
        work.setCreator(user);
        work.setFormatEntity(format);
        work.setOriginalLanguageEntity(language);
        work.setCover("old-cover");
        entityManager.persist(work);

        chapter = new ChapterEntity();
        chapter.setWorkEntity(work);
        chapter.setLanguageEntity(language);
        chapter.setTitle("Cap 1");
        chapter.setPrice(0.0);
        entityManager.persist(chapter);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void updateWork_shouldNotNullifyChapterWorkId_whenUpdatingCoverOnly() {
        Work domainWork = new Work();
        domainWork.setId(work.getId());
        domainWork.setCover("new-cover");

        Boolean ok = worksPersistenceAdapter.updateWork(domainWork);
        assertTrue(ok);

        ChapterEntity reloadedChapter = entityManager.find(ChapterEntity.class, chapter.getId());
        assertNotNull(reloadedChapter);
        assertNotNull(reloadedChapter.getWorkEntity());
        assertEquals(work.getId(), reloadedChapter.getWorkEntity().getId());

        WorkEntity reloadedWork = entityManager.find(WorkEntity.class, work.getId());
        assertNotNull(reloadedWork);
        assertEquals("new-cover", reloadedWork.getCover());
    }
}
