package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReadingProgressPersistenceAdapterTest {

    @Autowired
    private ReadingProgressPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private UserEntity user;
    private WorkEntity work;
    private ChapterEntity chapter1;
    private ChapterEntity chapter2;

    @BeforeEach
    void setUp() {

        user = buildUser("reader1", "reader1@example.com");
        em.persist(user);

        FormatEntity format = new FormatEntity();
        format.setName("Novel");
        em.persist(format);

        LanguageEntity language = new LanguageEntity();
        language.setName("Spanish");
        language.setCode("es");
        em.persist(language);

        UserEntity creator = buildUser("author1", "author1@example.com");
        em.persist(creator);

        work = buildWork(creator, format, language);
        em.persist(work);

        chapter1 = buildChapter(work, language, "Capítulo 1");
        chapter2 = buildChapter(work, language, "Capítulo 2");
        em.persist(chapter1);
        em.persist(chapter2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("create upsert: crea si no existe y actualiza si existe")
    void create_upsert_behaviour() {
        Long userId = user.getId();
        Long workId = work.getId();
        Long ch1 = chapter1.getId();
        Long ch2 = chapter2.getId();

        boolean created = adapter.create(userId, workId, ch1);

        assertThat(created).isTrue();
        Long storedChapterId = (Long) em.createNativeQuery("SELECT chapter_id FROM user_reading_progress WHERE user_id = ?1 AND work_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .getSingleResult();
        assertThat(storedChapterId).isEqualTo(ch1);

        boolean updatedViaUpsert = adapter.create(userId, workId, ch2);

        assertThat(updatedViaUpsert).isTrue();
        Long storedChapterId2 = (Long) em.createNativeQuery("SELECT chapter_id FROM user_reading_progress WHERE user_id = ?1 AND work_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .getSingleResult();
        assertThat(storedChapterId2).isEqualTo(ch2);
    }

    @Test
    @DisplayName("update: retorna false si no existe y true si actualiza")
    void update_returnsFalseWhenNotExists_thenTrueWhenExists() {
        Long userId = user.getId();
        Long workId = work.getId();
        Long ch1 = chapter1.getId();
        Long ch2 = chapter2.getId();

        boolean updatedNonExisting = adapter.update(userId, workId, ch1);

        assertThat(updatedNonExisting).isFalse();

        adapter.create(userId, workId, ch1);

        boolean updatedExisting = adapter.update(userId, workId, ch2);

        assertThat(updatedExisting).isTrue();
        Long storedChapterId = (Long) em.createNativeQuery("SELECT chapter_id FROM user_reading_progress WHERE user_id = ?1 AND work_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .getSingleResult();
        assertThat(storedChapterId).isEqualTo(ch2);
    }

    @Test
    @DisplayName("addToHistory inserta un registro en user_reading_history con NOW()")
    void addToHistory_insertsRow() {
        Long userId = user.getId();
        Long workId = work.getId();
        Long ch1 = chapter1.getId();

        adapter.addToHistory(userId, workId, ch1);

        Number count = (Number) em.createNativeQuery("SELECT COUNT(*) FROM user_reading_history WHERE user_id = ?1 AND work_id = ?2 AND chapter_id = ?3")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .setParameter(3, ch1)
                .getSingleResult();
        assertThat(count.longValue()).isEqualTo(1L);
    }

    private UserEntity buildUser(String username, String email) {
        UserEntity u = new UserEntity();
        u.setName("Name");
        u.setSurname("Surname");
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("pwd");
        u.setEnabled(true);
        return u;
    }

    private WorkEntity buildWork(UserEntity creator, FormatEntity format, LanguageEntity language) {
        WorkEntity w = new WorkEntity();
        w.setTitle("Obra");
        w.setDescription("Desc");
        w.setState("DRAFT");
        w.setPrice(new BigDecimal("0.00"));
        w.setLikes(0);
        w.setPublicationDate(LocalDate.now());
        w.setCreator(creator);
        w.setFormatEntity(format);
        w.setOriginalLanguageEntity(language);
        w.setHasPdf(false);
        w.setHasEpub(false);
        return w;
    }

    private ChapterEntity buildChapter(WorkEntity w, LanguageEntity lang, String title) {
        ChapterEntity c = new ChapterEntity();
        c.setWorkEntity(w);
        c.setLanguageEntity(lang);
        c.setTitle(title);
        c.setPrice(new BigDecimal("0.00"));
        c.setPublicationStatus("DRAFT");
        return c;
    }
}
