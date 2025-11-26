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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LikePersistenceAdapterTest {

    @Autowired
    private LikePersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private Long userId;
    private Long workId;
    private Long chapterId;

    @BeforeEach
    void cleanAndSetup() {
        em.createQuery("DELETE FROM ChapterEntity").executeUpdate();
        em.createQuery("DELETE FROM WorkEntity").executeUpdate();
        em.createQuery("DELETE FROM FormatEntity").executeUpdate();
        em.createQuery("DELETE FROM LanguageEntity").executeUpdate();
        em.createQuery("DELETE FROM UserEntity").executeUpdate();
        // preparar datos mínimos
        userId = givenUser("alice");
        Long langId = givenLanguage("es", "Español");
        workId = givenWork("Obra Like", langId);
        chapterId = givenChapter(workId, langId, "Cap 1");
    }

    @Test
    void should_like_and_unlike_work_and_check_hasLiked() {
        // Given: likes iniciales = 0
        assertThat(em.find(WorkEntity.class, workId).getLikes()).isEqualTo(0);
        LocalDateTime likedAt = LocalDateTime.parse("2024-01-01T10:00:00");

        // When: like al work
        Long total = adapter.likeWork(workId, userId, likedAt);

        // Then: incrementa y hasLiked = true
        assertThat(total).isEqualTo(1L);
        assertThat(adapter.hasUserLikedWork(workId, userId)).isTrue();
        assertThat(em.find(WorkEntity.class, workId).getLikes()).isEqualTo(1);

        // When: like otra vez (idempotente)
        Long total2 = adapter.likeWork(workId, userId, likedAt.plusMinutes(1));
        // Then: no duplica
        assertThat(total2).isEqualTo(1L);
        assertThat(em.find(WorkEntity.class, workId).getLikes()).isEqualTo(1);

        // When: unlike
        Long total3 = adapter.unlikeWork(workId, userId);
        // Then: decrementa y hasLiked = false
        assertThat(total3).isEqualTo(0L);
        assertThat(adapter.hasUserLikedWork(workId, userId)).isFalse();
        assertThat(em.find(WorkEntity.class, workId).getLikes()).isEqualTo(0);
    }

    @Test
    void should_like_and_unlike_chapter_and_check_hasLiked() {
        // Given
        assertThat(em.find(ChapterEntity.class, chapterId).getLikes()).isEqualTo(0L);
        LocalDateTime likedAt = LocalDateTime.parse("2024-01-02T10:00:00");

        // When: like chapter
        Long total = adapter.likeChapter(chapterId, userId, likedAt);

        // Then: incrementa y hasLiked = true
        assertThat(total).isEqualTo(1L);
        assertThat(adapter.hasUserLikedChapter(chapterId, userId)).isTrue();
        assertThat(em.find(ChapterEntity.class, chapterId).getLikes()).isEqualTo(1L);

        // When: like otra vez
        Long total2 = adapter.likeChapter(chapterId, userId, likedAt.plusMinutes(1));
        // Then: no duplica
        assertThat(total2).isEqualTo(1L);
        assertThat(em.find(ChapterEntity.class, chapterId).getLikes()).isEqualTo(1L);

        // When: unlike
        Long total3 = adapter.unlikeChapter(chapterId, userId);
        // Then: decrementa y hasLiked = false
        assertThat(total3).isEqualTo(0L);
        assertThat(adapter.hasUserLikedChapter(chapterId, userId)).isFalse();
        assertThat(em.find(ChapterEntity.class, chapterId).getLikes()).isEqualTo(0L);
    }

    // ===== Helpers =====
    private Long givenUser(String username) {
        UserEntity u = new UserEntity();
        u.setName("Alice");
        u.setSurname("Liddell");
        u.setUsername(username + System.nanoTime());
        u.setEmail(username + System.nanoTime() + "@mail.test");
        u.setPassword("pwd");
        u.setEnabled(true);
        em.persist(u);
        em.flush();
        return u.getId();
    }

    private Long givenLanguage(String code, String name) {
        LanguageEntity e = new LanguageEntity();
        e.setCode(code);
        e.setName(name);
        em.persist(e);
        em.flush();
        return e.getId();
    }

    private Long givenWork(String title, Long originalLanguageId) {
        FormatEntity format = new FormatEntity();
        format.setName("NOVEL");
        em.persist(format);

        LanguageEntity orig = em.find(LanguageEntity.class, originalLanguageId);

        UserEntity creator = em.find(UserEntity.class, userId);

        WorkEntity w = new WorkEntity();
        w.setTitle(title);
        w.setDescription("desc");
        w.setState("DRAFT");
        w.setPrice(new BigDecimal("100.00"));
        w.setLikes(0);
        w.setCreator(creator);
        w.setFormatEntity(format);
        w.setOriginalLanguageEntity(orig);
        w.setPublicationDate(LocalDate.now());
        w.setHasEpub(false);
        w.setHasPdf(false);
        em.persist(w);
        em.flush();
        return w.getId();
    }

    private Long givenChapter(Long workId, Long languageId, String title) {
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
}
