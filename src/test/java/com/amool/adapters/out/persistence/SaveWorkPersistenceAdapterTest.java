package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.entity.WorkSavedEntity;
import com.amool.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SaveWorkPersistenceAdapterTest {

    @Autowired
    private SaveWorkPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private UserEntity user;
    private WorkEntity work;

    @BeforeEach
    void setUp() {
        user = buildUser("reader", "reader@example.com");
        em.persist(user);

        UserEntity creator = buildUser("author", "author@example.com");
        em.persist(creator);

        FormatEntity format = new FormatEntity();
        format.setName("Novel");
        em.persist(format);

        LanguageEntity language = new LanguageEntity();
        language.setName("Spanish");
        language.setCode("es");
        em.persist(language);

        work = buildWork(creator, format, language, "Titulo prueba");
        em.persist(work);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("saveWorkForUser persiste WorkSavedEntity y isWorkSavedByUser retorna true")
    void saveWork_persiste_y_isSaved_true() {
        Long userId = user.getId();
        Long workId = work.getId();

        adapter.saveWorkForUser(userId, workId);

        WorkSavedEntity saved = em.createQuery(
                "SELECT ws FROM WorkSavedEntity ws WHERE ws.user.id = :userId AND ws.work.id = :workId",
                WorkSavedEntity.class)
            .setParameter("userId", userId)
            .setParameter("workId", workId)
            .getSingleResult();
        assertThat(saved).isNotNull();
        assertThat(saved.getSavedAt()).isNotNull();
        assertThat(adapter.isWorkSavedByUser(userId, workId)).isTrue();
    }

    @Test
    @DisplayName("getSavedWorksByUser retorna la obra mapeada al dominio")
    void getSavedWorksByUser_retorna_obra() {
        Long userId = user.getId();
        Long workId = work.getId();

        List<Work> before = adapter.getSavedWorksByUser(userId);
        assertThat(before).isEmpty();

        adapter.saveWorkForUser(userId, workId);
        List<Work> saved = adapter.getSavedWorksByUser(userId);

        assertThat(saved).hasSize(1);
        Work w = saved.get(0);
        assertThat(w.getId()).isEqualTo(workId);
        assertThat(w.getTitle()).isEqualTo("Titulo prueba");
    }

    @Test
    @DisplayName("removeSavedWorkForUser elimina y luego isWorkSavedByUser retorna false")
    void removeSavedWork_elimina() {
        Long userId = user.getId();
        Long workId = work.getId();

        adapter.saveWorkForUser(userId, workId);
        assertThat(adapter.isWorkSavedByUser(userId, workId)).isTrue();

        adapter.removeSavedWorkForUser(userId, workId);

        assertThat(adapter.isWorkSavedByUser(userId, workId)).isFalse();
    }

    @Test
    @DisplayName("removeSavedWorkForUser lanza EntityNotFoundException si no estaba guardado")
    void removeSavedWork_lanza_si_no_existe() {
        Long userId = user.getId();
        Long workId = work.getId();

        assertThatThrownBy(() -> adapter.removeSavedWorkForUser(userId, workId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Work not saved by user");
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
}
