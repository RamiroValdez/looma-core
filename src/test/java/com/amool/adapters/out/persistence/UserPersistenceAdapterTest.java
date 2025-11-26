package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.LanguageEntity;
import com.amool.adapters.out.persistence.entity.SuscribeAutorEntity;
import com.amool.adapters.out.persistence.entity.SuscribeWorkEntity;
import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.domain.model.InMemoryMultipartFile;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserPersistenceAdapterTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity baseUser;

    @BeforeEach
    void setUp() {
        baseUser = buildUserEntity("Base", "User", "baseuser", "base@example.com", true);
        baseUser.setPassword(passwordEncoder.encode("oldpass"));
        baseUser.setPhoto("old-photo.jpg");
        baseUser.setMoney(new BigDecimal("5.00"));
        baseUser.setPrice(new BigDecimal("10.00"));
        em.persist(baseUser);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("getById retorna el usuario mapeado cuando existe")
    void getById_exists() {
        Optional<com.amool.domain.model.User> result = adapter.getById(baseUser.getId());

        assertThat(result).isPresent();
        com.amool.domain.model.User u = result.get();
        assertThat(u.getId()).isEqualTo(baseUser.getId());
        assertThat(u.getName()).isEqualTo("Base");
        assertThat(u.getSurname()).isEqualTo("User");
        assertThat(u.getUsername()).isEqualTo("baseuser");
        assertThat(u.getEmail()).isEqualTo("base@example.com");
        assertThat(u.getMoney()).isEqualTo(new BigDecimal("5.00"));
        assertThat(u.getPrice()).isEqualTo(new BigDecimal("10.00"));
        assertThat(u.getPhoto()).isEqualTo("old-photo.jpg");
    }

    @Test
    @DisplayName("getById retorna Optional.empty cuando no existe")
    void getById_not_exists() {
        Optional<com.amool.domain.model.User> result = adapter.getById(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllAuthorSubscribers retorna IDs de suscriptores de un autor")
    void getAllAuthorSubscribers_returnsIds() {
        UserEntity author = buildUserEntity("Author", "One", "author1", "author1@example.com", true);
        em.persist(author);
        UserEntity sub1 = buildUserEntity("S1", "S", "sub1", "sub1@example.com", true);
        UserEntity sub2 = buildUserEntity("S2", "S", "sub2", "sub2@example.com", true);
        em.persist(sub1); em.persist(sub2);
        em.persist(new SuscribeAutorEntity(sub1.getId(), author.getId()));
        em.persist(new SuscribeAutorEntity(sub2.getId(), author.getId()));
        em.flush(); em.clear();

        List<Long> ids = adapter.getAllAuthorSubscribers(author.getId());

        assertThat(ids).containsExactlyInAnyOrder(sub1.getId(), sub2.getId());
    }

    @Test
    @DisplayName("getAllWorkSubscribers retorna IDs de suscriptores de una obra")
    void getAllWorkSubscribers_returnsIds() {
        UserEntity creator = buildUserEntity("Creator", "W", "creatorW", "creatorW@example.com", true);
        em.persist(creator);
        FormatEntity format = new FormatEntity(); format.setName("Novel"); em.persist(format);
        LanguageEntity language = new LanguageEntity(); language.setName("Spanish"); language.setCode("es"); em.persist(language);
        WorkEntity work = buildWorkEntity(creator, format, language, "Obra"); em.persist(work);
        UserEntity sub1 = buildUserEntity("S1", "S", "wsub1", "wsub1@example.com", true);
        UserEntity sub2 = buildUserEntity("S2", "S", "wsub2", "wsub2@example.com", true);
        em.persist(sub1); em.persist(sub2);
        em.persist(new SuscribeWorkEntity(sub1.getId(), work.getId()));
        em.persist(new SuscribeWorkEntity(sub2.getId(), work.getId()));
        em.flush(); em.clear();

        List<Long> ids = adapter.getAllWorkSubscribers(work.getId());

        assertThat(ids).containsExactlyInAnyOrder(sub1.getId(), sub2.getId());
    }

    @Test
    @DisplayName("updateUser actualiza nombre, apellidos, username, email, precio, foto y contraseña (codificada)")
    void updateUser_updates_fields_and_password_and_photo() {
        User user = new User();
        user.setId(baseUser.getId());
        user.setName("Nuevo");
        user.setSurname("Apellido");
        user.setUsername("nuevoUser");
        user.setEmail("nuevo@example.com");
        user.setPrice(new BigDecimal("25.50"));
        user.setPhoto("new-photo.jpg");
        user.setMultipartFile(new InMemoryMultipartFile("photo","photo.jpg","image/jpeg", new byte[]{1,2,3}));

        String newPassword = "newSecret";

        boolean updated = adapter.updateUser(user, newPassword);

        assertThat(updated).isTrue();
        UserEntity refreshed = em.find(UserEntity.class, baseUser.getId());
        assertThat(refreshed.getName()).isEqualTo("Nuevo");
        assertThat(refreshed.getSurname()).isEqualTo("Apellido");
        assertThat(refreshed.getUsername()).isEqualTo("nuevoUser");
        assertThat(refreshed.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(refreshed.getPrice()).isEqualTo(new BigDecimal("25.50"));
        assertThat(refreshed.getPhoto()).isEqualTo("new-photo.jpg");
        assertThat(passwordEncoder.matches(newPassword, refreshed.getPassword())).isTrue();
    }

    @Test
    @DisplayName("updateUser retorna false cuando el usuario no existe")
    void updateUser_returns_false_when_not_found() {
        User user = new User();
        user.setId(123456789L);
        user.setName("X"); user.setSurname("Y");
        boolean updated = adapter.updateUser(user, "pwd");
        assertThat(updated).isFalse();
    }

    private UserEntity buildUserEntity(String name, String surname, String username, String email, boolean enabled) {
        UserEntity u = new UserEntity();
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("hash");
        u.setEnabled(enabled);
        u.setMoney(BigDecimal.ZERO);
        u.setPrice(BigDecimal.ZERO);
        u.setPhoto("none");
        return u;
    }

    private WorkEntity buildWorkEntity(UserEntity creator, FormatEntity format, LanguageEntity language, String title) {
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
