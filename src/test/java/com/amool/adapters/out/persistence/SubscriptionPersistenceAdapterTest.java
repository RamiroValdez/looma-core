package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.domain.model.Work;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SubscriptionPersistenceAdapterTest {

    @Autowired
    private SubscriptionPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private UserEntity subscriber;
    private UserEntity author;
    private WorkEntity work;
    private ChapterEntity chapter;

    @BeforeEach
    void setUp() {
        subscriber = buildUser("subscriber", "subscriber@example.com");
        em.persist(subscriber);

        author = buildUser("author", "author@example.com");
        em.persist(author);

        FormatEntity format = new FormatEntity();
        format.setName("Novel");
        em.persist(format);

        LanguageEntity language = new LanguageEntity();
        language.setName("Spanish");
        language.setCode("es");
        em.persist(language);

        work = buildWork(author, format, language, "Obra suscrita");
        em.persist(work);

        chapter = buildChapter(work, language, "Capítulo 1");
        em.persist(chapter);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("subscribeAuthor inserta/actualiza una fila en suscribe_autor y getAllSubscribedWorks retorna obras del autor")
    void subscribeAuthor_y_getAllSubscribedWorks_por_autor() {
        Long userId = subscriber.getId();
        Long authorId = author.getId();

        adapter.subscribeAuthor(userId, authorId);

        Number count1 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_autor WHERE user_id = ?1 AND autor_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, authorId)
                .getSingleResult();
        assertThat(count1.longValue()).isEqualTo(1L);

        adapter.subscribeAuthor(userId, authorId);

        Number count2 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_autor WHERE user_id = ?1 AND autor_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, authorId)
                .getSingleResult();
        assertThat(count2.longValue()).isEqualTo(1L);

        List<Work> works = adapter.getAllSubscribedWorks(userId);
        assertThat(works).extracting(Work::getId).contains(work.getId());
    }

    @Test
    @DisplayName("subscribeWork inserta/actualiza una fila en suscribe_work y getAllSubscribedWorks la retorna")
    void subscribeWork_y_getAllSubscribedWorks_por_obra() {
        Long userId = subscriber.getId();
        Long workId = work.getId();

        adapter.subscribeWork(userId, workId);

        Number count1 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_work WHERE user_id = ?1 AND work_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .getSingleResult();
        assertThat(count1.longValue()).isEqualTo(1L);

        adapter.subscribeWork(userId, workId);

        Number count2 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_work WHERE user_id = ?1 AND work_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .getSingleResult();
        assertThat(count2.longValue()).isEqualTo(1L);

        List<Work> works = adapter.getAllSubscribedWorks(userId);
        assertThat(works).extracting(Work::getId).contains(workId);
    }

    @Test
    @DisplayName("subscribeChapter inserta/actualiza en suscribe_chapter")
    void subscribeChapter_inserta_upsert() {
        Long userId = subscriber.getId();
        Long chapterId = chapter.getId();

        adapter.subscribeChapter(userId, chapterId);

        Number count1 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_chapter WHERE user_id = ?1 AND chapter_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, chapterId)
                .getSingleResult();
        assertThat(count1.longValue()).isEqualTo(1L);

        adapter.subscribeChapter(userId, chapterId);

        Number count2 = (Number) em.createNativeQuery("SELECT COUNT(*) FROM suscribe_chapter WHERE user_id = ?1 AND chapter_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, chapterId)
                .getSingleResult();
        assertThat(count2.longValue()).isEqualTo(1L);
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

    private WorkEntity buildWork(UserEntity creator, FormatEntity format, LanguageEntity language, String title) {
        WorkEntity w = new WorkEntity();
        w.setTitle(title);
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

    private ChapterEntity buildChapter(WorkEntity work, LanguageEntity language, String title) {
        ChapterEntity c = new ChapterEntity();
        c.setWorkEntity(work);
        c.setLanguageEntity(language);
        c.setTitle(title);
        c.setPrice(new BigDecimal("0.00"));
        return c;
    }
}
