package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.application.port.out.RatingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RatingPersistenceAdapterTest {

    @Autowired
    private RatingPersistenceAdapter ratingAdapter;

    @Autowired
    private EntityManager em;

    private UserEntity user1;
    private UserEntity user2;
    private WorkEntity work;

    @BeforeEach
    void setUp() {
        UserEntity creator = new UserEntity();
        creator.setName("Autor");
        creator.setSurname("Prueba");
        creator.setUsername("autor_prueba");
        creator.setEmail("autor@example.com");
        creator.setPassword("secret");
        creator.setEnabled(true);
        em.persist(creator);

        user1 = new UserEntity();
        user1.setName("User");
        user1.setSurname("One");
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pwd");
        user1.setEnabled(true);
        em.persist(user1);

        user2 = new UserEntity();
        user2.setName("User");
        user2.setSurname("Two");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("pwd");
        user2.setEnabled(true);
        em.persist(user2);

        FormatEntity format = new FormatEntity();
        format.setName("Novel");
        em.persist(format);

        LanguageEntity language = new LanguageEntity();
        language.setName("Spanish");
        em.persist(language);

        work = buildWork(creator, format, language);
        em.persist(work);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("rateWork crea rating nuevo y actualiza promedio y conteo")
    void rateWork_creaYActualizaPromedioConteo() {
        Long workId = work.getId();
        Long userId = user1.getId();

        double returnedAvg = ratingAdapter.rateWork(workId, userId, 4.0, LocalDateTime.now());

        assertThat(returnedAvg).isEqualTo(4.0);
        assertThat(ratingAdapter.getAverageRating(workId)).isEqualTo(4.0);
        assertThat(ratingAdapter.getTotalRatingsCount(workId)).isEqualTo(1);

        WorkEntity refreshed = em.find(WorkEntity.class, workId);
        assertThat(refreshed.getAverageRating()).isEqualTo(4.0);
        assertThat(refreshed.getRatingCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("rateWork actualiza rating existente del usuario y recomputa promedio")
    void rateWork_actualizaExistente() {
        Long workId = work.getId();

        ratingAdapter.rateWork(workId, user1.getId(), 2.0, LocalDateTime.now());
        ratingAdapter.rateWork(workId, user2.getId(), 4.0, LocalDateTime.now());
        assertThat(ratingAdapter.getTotalRatingsCount(workId)).isEqualTo(2);
        assertThat(ratingAdapter.getAverageRating(workId)).isEqualTo(3.0);

        double newAvg = ratingAdapter.rateWork(workId, user1.getId(), 5.0, LocalDateTime.now());

        assertThat(newAvg).isEqualTo((5.0 + 4.0) / 2.0);
        assertThat(ratingAdapter.getTotalRatingsCount(workId)).isEqualTo(2);

        WorkEntity refreshed = em.find(WorkEntity.class, workId);
        assertThat(refreshed.getAverageRating()).isEqualTo((5.0 + 4.0) / 2.0);
        assertThat(refreshed.getRatingCount()).isEqualTo(2);

        assertThat(ratingAdapter.getUserRating(workId, user1.getId()))
                .isPresent()
                .contains(5.0);
    }

    @Test
    @DisplayName("getWorkRatings pagina correctamente y mapea DTOs")
    void getWorkRatings_paginacion() {
        Long workId = work.getId();

        for (int i = 0; i < 5; i++) {
            UserEntity u = createUser("user" + (i + 3), "user" + (i + 3) + "@example.com");
            em.persist(u);
            ratingAdapter.rateWork(workId, u.getId(), 3.0 + (i % 2), LocalDateTime.now());
        }

        PageRequest pageable = PageRequest.of(0, 3);
        Page<RatingPort.RatingDto> page = ratingAdapter.getWorkRatings(workId, pageable);

        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(ratingAdapter.getTotalRatingsCount(workId).longValue());
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).userId()).isNotNull();
        assertThat(page.getContent().get(0).rating()).isNotNull();
    }

    private WorkEntity buildWork(UserEntity creator, FormatEntity format, LanguageEntity language) {
        WorkEntity w = new WorkEntity();
        w.setTitle("Obra de prueba");
        w.setDescription("Descripción");
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

    private UserEntity createUser(String username, String email) {
        UserEntity u = new UserEntity();
        u.setName("Name");
        u.setSurname("Surname");
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("pwd");
        u.setEnabled(true);
        return u;
    }
}
