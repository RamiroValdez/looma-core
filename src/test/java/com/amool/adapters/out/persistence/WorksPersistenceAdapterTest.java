package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.*;
import com.amool.domain.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorksPersistenceAdapterTest {

    @Autowired
    private WorksPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    private UserEntity author1;
    private UserEntity author2;
    private FormatEntity format;
    private LanguageEntity language;
    private CategoryEntity catFantasy;
    private CategoryEntity catSciFi;

    private WorkEntity workA; // by author1, fantasy
    private WorkEntity workB; // by author2, scifi

    @BeforeEach
    void setUp() {
        author1 = buildUser("author1", "author1@example.com");
        author2 = buildUser("author2", "author2@example.com");
        em.persist(author1);
        em.persist(author2);

        format = new FormatEntity();
        format.setName("Novel");
        em.persist(format);

        language = new LanguageEntity();
        language.setName("Spanish");
        language.setCode("es");
        em.persist(language);

        catFantasy = new CategoryEntity();
        catFantasy.setName("Fantasy");
        em.persist(catFantasy);
        catSciFi = new CategoryEntity();
        catSciFi.setName("SciFi");
        em.persist(catSciFi);

        workA = buildWork(author1, format, language, "Dragon story", "InProgress", 50);
        workA.getCategories().add(catFantasy);
        em.persist(workA);

        workB = buildWork(author2, format, language, "Space opera", "finished", 10);
        workB.getCategories().add(catSciFi);
        em.persist(workB);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("obtainWorkById retorna la obra mapeada cuando existe, y empty cuando no")
    void obtainWorkById_happy_and_empty() {
        Optional<Work> found = findWork(workA.getId());
        Optional<Work> missing = findWork(999999L);

        assertWorkFound(found, "Dragon story", author1.getId());
        assertThat(missing).isEmpty();
    }

    @Test
    @DisplayName("getAllWorks retorna todas las obras")
    void getAllWorks_returns_all() {
        List<Work> works = fetchAllWorks();

        assertWorkIdsInAnyOrder(works, workA.getId(), workB.getId());
    }

    @Test
    @DisplayName("getWorksByUserId retorna solo las obras del autor indicado")
    void getWorksByUserId_filters_by_creator() {
        assertWorkIdsExactly(fetchWorksByUser(author1.getId()), workA.getId());
        assertWorkIdsExactly(fetchWorksByUser(author2.getId()), workB.getId());
    }

    @Test
    @DisplayName("createWork persiste una nueva obra desde dominio y retorna su id")
    void createWork_persists_domain() {
        Work work = buildDomainWork(author1.getId(), format.getId(), language.getId(), "Nueva obra");

        Long id = createWork(work);

        assertWorkPersisted(id, "Nueva obra", author1.getId());
    }

    @Test
    @DisplayName("updateWork actualiza campos existentes y retorna true; con id inexistente retorna false")
    void updateWork_updates_or_returns_false() {
        Boolean ok = updateWork(buildWorkUpdatePayload(workA.getId(), "Dragon legend", 77));

        assertThat(ok).isTrue();
        assertWorkValues(workA.getId(), "Dragon legend", 77);

        assertThat(updateWork(minimalWork(123456789L))).isFalse();
    }

    @Test
    @DisplayName("findByFilters filtra por texto (título/desc/tags) y categorías, y pagina resultados")
    void findByFilters_text_and_category() {
        WorkEntity other = buildWork(author1, format, language, "Cooking book", "InProgress", 0);
        other.getCategories().add(catFantasy);
        em.persist(other);
        em.flush(); em.clear();

        WorkSearchFilter filter = buildFilter("dragon", "InProgress", Set.of(catFantasy.getId()));
        PageRequest page = PageRequest.of(0, 10);

        Page<Work> result = searchWorks(filter, page);

        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(1);
        List<Long> ids = workIds(result.getContent());
        assertThat(ids).contains(workA.getId());
        assertThat(ids).doesNotContain(workB.getId());
    }

    @Test
    @DisplayName("getWorksCurrentlyReading retorna obras del progreso de lectura del usuario")
    void getWorksCurrentlyReading_returns_progress_works() {
        Long userId = author1.getId();
        persistReadingProgress(userId, workA.getId(), 1L);

        List<Work> works = adapter.getWorksCurrentlyReading(userId);

        assertWorkIdsContain(works, workA.getId());
    }

    @Test
    @DisplayName("getUserPreferences retorna obras que comparten categorías con preferred_category del usuario")
    void getUserPreferences_returns_preferred_category_matches() {
        Long userId = author2.getId();

        persistPreferredCategory(userId, catSciFi.getId());

        List<Work> prefs = adapter.getUserPreferences(userId);

        assertWorkIdsExactly(prefs, workB.getId());
    }

    private Optional<Work> findWork(Long id) {
        return adapter.obtainWorkById(id);
    }

    private void assertWorkFound(Optional<Work> maybeWork, String expectedTitle, Long expectedCreatorId) {
        assertThat(maybeWork).isPresent();
        Work work = maybeWork.get();
        assertThat(work.getTitle()).isEqualTo(expectedTitle);
        assertThat(work.getCreator().getId()).isEqualTo(expectedCreatorId);
    }

    private List<Work> fetchAllWorks() {
        return adapter.getAllWorks();
    }

    private List<Work> fetchWorksByUser(Long userId) {
        return adapter.getWorksByUserId(userId);
    }

    private void assertWorkIdsInAnyOrder(List<Work> works, Long... expectedIds) {
        assertThat(works).extracting(Work::getId)
                .containsExactlyInAnyOrder(expectedIds);
    }

    private void assertWorkIdsExactly(List<Work> works, Long... expectedIds) {
        assertThat(works).extracting(Work::getId)
                .containsExactly(expectedIds);
    }

    private void assertWorkIdsContain(List<Work> works, Long... expectedIds) {
        assertThat(works).extracting(Work::getId)
                .contains(expectedIds);
    }

    private Work buildDomainWork(Long creatorId, Long formatId, Long languageId, String title) {
        Work w = new Work();
        w.setTitle(title);
        w.setDescription("Desc");
        w.setState("InProgress");
        w.setPrice(new BigDecimal("1.99"));
        w.setLikes(0);
        w.setPublicationDate(LocalDate.now());
        w.setHasEpub(false);
        w.setHasPdf(false);

        User creator = new User();
        creator.setId(creatorId);
        w.setCreator(creator);

        Format fmt = new Format();
        fmt.setId(formatId);
        w.setFormat(fmt);

        Language lang = new Language();
        lang.setId(languageId);
        w.setOriginalLanguage(lang);

        return w;
    }

    private Long createWork(Work work) {
        return adapter.createWork(work);
    }

    private void assertWorkPersisted(Long workId, String expectedTitle, Long expectedCreatorId) {
        WorkEntity persisted = em.find(WorkEntity.class, workId);
        assertThat(persisted.getTitle()).isEqualTo(expectedTitle);
        assertThat(persisted.getCreator().getId()).isEqualTo(expectedCreatorId);
    }

    private Work buildWorkUpdatePayload(Long workId, String newTitle, Integer newLikes) {
        Work work = new Work();
        work.setId(workId);
        work.setTitle(newTitle);
        work.setLikes(newLikes);
        return work;
    }

    private boolean updateWork(Work work) {
        return adapter.updateWork(work);
    }

    private void assertWorkValues(Long workId, String expectedTitle, Integer expectedLikes) {
        WorkEntity refreshed = em.find(WorkEntity.class, workId);
        assertThat(refreshed.getTitle()).isEqualTo(expectedTitle);
        assertThat(refreshed.getLikes()).isEqualTo(expectedLikes);
    }

    private Work minimalWork(Long workId) {
        Work work = new Work();
        work.setId(workId);
        return work;
    }

    private Page<Work> searchWorks(WorkSearchFilter filter, PageRequest pageRequest) {
        return adapter.findByFilters(filter, pageRequest);
    }

    private WorkSearchFilter buildFilter(String text, String state, Set<Long> categoryIds) {
        WorkSearchFilter filter = new WorkSearchFilter();
        filter.setText(text);
        filter.setState(state);
        filter.setCategoryIds(categoryIds);
        return filter;
    }

    private List<Long> workIds(List<Work> works) {
        return works.stream().map(Work::getId).toList();
    }

    private void persistReadingProgress(Long userId, Long workId, Long chapterId) {
        em.createNativeQuery("INSERT INTO user_reading_progress(user_id, work_id, chapter_id) VALUES (?1, ?2, ?3)")
                .setParameter(1, userId)
                .setParameter(2, workId)
                .setParameter(3, chapterId)
                .executeUpdate();
        em.flush();
    }

    private void persistPreferredCategory(Long userId, Long categoryId) {
        em.createNativeQuery("INSERT INTO preferred_category(user_id, category_id) VALUES (?1, ?2)")
                .setParameter(1, userId)
                .setParameter(2, categoryId)
                .executeUpdate();
        em.flush();
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

    private WorkEntity buildWork(UserEntity creator, FormatEntity format, LanguageEntity language, String title, String state, int likes) {
        WorkEntity w = new WorkEntity();
        w.setTitle(title);
        w.setDescription("Desc");
        w.setState(state);
        w.setPrice(new BigDecimal("0.00"));
        w.setLikes(likes);
        w.setPublicationDate(LocalDate.now());
        w.setCreator(creator);
        w.setFormatEntity(format);
        w.setOriginalLanguageEntity(language);
        w.setHasPdf(false);
        w.setHasEpub(false);
        return w;
    }
}
