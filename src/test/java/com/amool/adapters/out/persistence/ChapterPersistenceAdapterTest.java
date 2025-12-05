package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.application.port.out.FindChaptersDueForPublicationPort;
import com.amool.domain.model.Chapter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ChapterPersistenceAdapterTest {

    @Autowired
    private ChapterPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private Long workId;
    private Long languageId;

    @BeforeEach
    void clean() {
        em.createQuery("DELETE FROM ChapterEntity").executeUpdate();
        em.createQuery("DELETE FROM WorkEntity").executeUpdate();
        em.createQuery("DELETE FROM LanguageEntity").executeUpdate();
        em.createQuery("DELETE FROM FormatEntity").executeUpdate();
        em.createQuery("DELETE FROM UserEntity").executeUpdate();
        languageId = givenLanguage("es", "Español");
        workId = givenWork("Obra X", languageId);
    }

    @Test
    void should_save_chapter_with_relations_and_defaults() {
        Chapter chapter = new Chapter();
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);
        chapter.setLastModified(LocalDateTime.parse("2024-03-01T10:00:00"));

        Chapter saved = adapter.saveChapter(chapter);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getWorkId()).isEqualTo(workId);
        assertThat(saved.getLanguageId()).isEqualTo(languageId);
        assertThat(saved.getPublicationStatus()).isEqualTo("DRAFT");
    }

    @Test
    void should_load_chapter_by_work_and_chapter_when_work_exists() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap 1");

        Optional<Chapter> loaded = adapter.loadChapter(workId, chapterId);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(chapterId);
        assertThat(loaded.get().getWorkId()).isEqualTo(workId);
    }

    @Test
    void should_return_empty_when_loading_chapter_if_work_not_exists() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap 1");

        Optional<Chapter> loaded = adapter.loadChapter(999999L, chapterId);

        assertThat(loaded).isEmpty();
    }

    @Test
    void should_throw_when_update_publication_status_if_work_mismatch() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap pub");

        assertThatThrownBy(() -> adapter.updatePublicationStatus(999999L, chapterId, "PUBLISHED", LocalDateTime.now()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void should_schedule_publication_and_then_clear_schedule() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap sched");
        Instant when = LocalDateTime.parse("2025-01-01T08:00:00")
                .atZone(ZoneId.of("America/Argentina/Buenos_Aires")).toInstant();

        adapter.schedulePublication(workId, chapterId, when);
        em.flush();
        em.clear();
        Object[] scheduledRow = em.createQuery(
                        "SELECT c.publicationStatus, c.publishedAt, c.scheduledPublicationDate FROM ChapterEntity c WHERE c.id = :id",
                        Object[].class)
                .setParameter("id", chapterId)
                .getSingleResult();

        assertThat((String) scheduledRow[0]).isEqualTo("SCHEDULED");
        assertThat(scheduledRow[1]).isNull();
        assertThat(scheduledRow[2]).isNotNull();

        adapter.clearSchedule(workId, chapterId);
        em.flush();
        em.clear();
        Object[] clearedRow = em.createQuery(
                        "SELECT c.publicationStatus, c.scheduledPublicationDate FROM ChapterEntity c WHERE c.id = :id",
                        Object[].class)
                .setParameter("id", chapterId)
                .getSingleResult();

        assertThat((String) clearedRow[0]).isEqualTo("DRAFT");
        assertThat(clearedRow[1]).isNull();
    }

    @Test
    void should_find_due_chapters_by_scheduled_date_and_status() {
        Long dueId = givenChapterEntity(workId, languageId, "Cap due");
        Long futureId = givenChapterEntity(workId, languageId, "Cap future");
        LocalDateTime nowLdt = LocalDateTime.parse("2025-01-01T10:00:00");
        scheduleChapter(workId, dueId, nowLdt.minusHours(1));
        scheduleChapter(workId, futureId, nowLdt.plusHours(1));

        List<FindChaptersDueForPublicationPort.DueChapter> due = adapter.findDue(nowLdt.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).toInstant(), 10);

        assertThat(due).hasSize(1);
        assertThat(due.get(0).chapterId()).isEqualTo(dueId);
        assertThat(due.get(0).workId()).isEqualTo(workId);
    }

    @Test
    void should_obtain_chapter_by_id() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap ob");

        Optional<Chapter> byId = adapter.obtainChapterById(chapterId);

        assertThat(byId).isPresent();
        assertThat(byId.get().getId()).isEqualTo(chapterId);
    }

    @Test
    void should_delete_chapter_when_belongs_to_work() {
        Long chapterId = givenChapterEntity(workId, languageId, "Cap del");

        adapter.deleteChapter(workId, chapterId);

        assertThat(em.find(ChapterEntity.class, chapterId)).isNull();
    }

    private Long givenLanguage(String code, String name) {
        LanguageEntity lang = new LanguageEntity();
        lang.setCode(code);
        lang.setName(name);
        em.persist(lang);
        em.flush();
        return lang.getId();
    }

    private Long givenWork(String title, Long originalLanguageId) {
        UserEntity creator = new UserEntity();
        creator.setName("John");
        creator.setSurname("Doe");
        creator.setUsername("jdoe" + System.nanoTime());
        creator.setEmail("jdoe" + System.nanoTime() + "@mail.test");
        creator.setPassword("pwd");
        creator.setEnabled(true);
        em.persist(creator);

        FormatEntity format = new FormatEntity();
        format.setName("NOVEL");
        em.persist(format);

        LanguageEntity orig = em.find(LanguageEntity.class, originalLanguageId);

        WorkEntity work = new WorkEntity();
        work.setTitle(title);
        work.setDescription("desc");
        work.setState("DRAFT");
        work.setPrice(new BigDecimal("100.00"));
        work.setLikes(0);
        work.setCreator(creator);
        work.setFormatEntity(format);
        work.setOriginalLanguageEntity(orig);
        work.setPublicationDate(LocalDate.now());
        work.setHasEpub(false);
        work.setHasPdf(false);
        em.persist(work);
        em.flush();
        return work.getId();
    }

    private Long givenChapterEntity(Long workId, Long languageId, String title) {
        WorkEntity work = em.find(WorkEntity.class, workId);
        LanguageEntity lang = em.find(LanguageEntity.class, languageId);
        ChapterEntity ch = new ChapterEntity();
        ch.setWorkEntity(work);
        ch.setLanguageEntity(lang);
        ch.setTitle(title);
        ch.setLastModified(LocalDateTime.now());
        em.persist(ch);
        em.flush();
        return ch.getId();
    }

    private void scheduleChapter(Long workId, Long chapterId, LocalDateTime when) {
        adapter.schedulePublication(workId, chapterId, when.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).toInstant());
    }
}
