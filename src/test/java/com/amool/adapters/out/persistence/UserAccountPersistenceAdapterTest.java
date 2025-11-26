package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserAccountPersistenceAdapterTest {

    @Autowired
    private UserAccountPersistenceAdapter adapter;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("emailExists y usernameExists reflejan el estado antes y después de upsertPendingUser")
    void email_username_exists_checks() {
        String email = "newuser@example.com";
        String username = "newuser";

        assertThat(adapter.emailExists(email)).isFalse();
        assertThat(adapter.usernameExists(username)).isFalse();

        adapter.upsertPendingUser("Name", "Surname", username, email, "hash", "CODE1", LocalDateTime.now().plusHours(1));

        assertThat(adapter.emailExists(email)).isTrue();
        assertThat(adapter.usernameExists(username)).isTrue();
    }

    @Test
    @DisplayName("upsertPendingUser crea usuario pendiente con campos y verificación")
    void upsert_crea_usuario_pendiente() {
        String email = "pending@example.com";
        String username = "pendinguser";
        LocalDateTime exp = LocalDateTime.now().plusDays(1);

        adapter.upsertPendingUser("John", "Doe", username, email, "hash1", "V1", exp);

        UserEntity u = findByEmail(email);
        assertThat(u).isNotNull();
        assertThat(u.getEnabled()).isFalse();
        assertThat(u.getName()).isEqualTo("John");
        assertThat(u.getSurname()).isEqualTo("Doe");
        assertThat(u.getUsername()).isEqualTo(username);
        assertThat(u.getPassword()).isEqualTo("hash1");
        assertThat(u.getVerificationCode()).isEqualTo("V1");
        assertThat(u.getVerificationExpiresAt()).isEqualTo(exp);
        assertThat(u.getPhoto()).isEqualTo("none");
    }

    @Test
    @DisplayName("upsertPendingUser actualiza usuario pendiente existente por email")
    void upsert_actualiza_usuario_pendiente() {
        String email = "upd@example.com";
        adapter.upsertPendingUser("A", "B", "userA", email, "hashA", "C1", LocalDateTime.now().plusHours(2));

        LocalDateTime exp2 = LocalDateTime.now().plusDays(2);
        adapter.upsertPendingUser("A2", "B2", "userB", email, "hashB", "C2", exp2);

        UserEntity u = findByEmail(email);
        assertThat(u.getName()).isEqualTo("A2");
        assertThat(u.getSurname()).isEqualTo("B2");
        assertThat(u.getUsername()).isEqualTo("userB");
        assertThat(u.getPassword()).isEqualTo("hashB");
        assertThat(u.getVerificationCode()).isEqualTo("C2");
        assertThat(u.getVerificationExpiresAt()).isEqualTo(exp2);
    }

    @Test
    @DisplayName("upsertPendingUser lanza si el email ya pertenece a un usuario habilitado")
    void upsert_lanza_email_existente_habilitado() {
        String email = "enabled@example.com";
        persistEnabledUser("En", "Abled", "enableduser", email);

        assertThatThrownBy(() -> adapter.upsertPendingUser("N", "S", "userX", email, "hash", "C", LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("upsertPendingUser lanza si el username ya está tomado por usuario habilitado distinto email")
    void upsert_lanza_username_existente_habilitado() {
        String takenUsername = "taken";
        persistEnabledUser("Name", "Surname", takenUsername, "other@example.com");

        assertThatThrownBy(() -> adapter.upsertPendingUser("N", "S", takenUsername, "new@example.com", "hash", "C", LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("enableUserIfCodeValid habilita usuario y limpia verificación cuando el código es válido y no expiró")
    void enableUserIfCodeValid_exito() {
        String email = "verify@example.com";
        String code = "OK";
        adapter.upsertPendingUser("Name", "Surname", "verifyuser", email, "hash", code, LocalDateTime.now().plusHours(2));

        Long id = adapter.enableUserIfCodeValid(email, code);

        UserEntity u = findByEmail(email);
        assertThat(id).isEqualTo(u.getId());
        assertThat(u.getEnabled()).isTrue();
        assertThat(u.getVerificationCode()).isNull();
        assertThat(u.getVerificationExpiresAt()).isNull();
    }

    @Test
    @DisplayName("enableUserIfCodeValid lanza si no existe verificación pendiente para el email")
    void enableUserIfCodeValid_no_encontrado() {
        assertThatThrownBy(() -> adapter.enableUserIfCodeValid("missing@example.com", "X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verification pending not found");
    }

    @Test
    @DisplayName("enableUserIfCodeValid lanza si el código es inválido")
    void enableUserIfCodeValid_codigo_invalido() {
        String email = "invalidcode@example.com";
        adapter.upsertPendingUser("N", "S", "userX", email, "hash", "RIGHT", LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> adapter.enableUserIfCodeValid(email, "WRONG"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid code");
    }

    @Test
    @DisplayName("enableUserIfCodeValid lanza si el código expiró")
    void enableUserIfCodeValid_codigo_expirado() {
        String email = "expired@example.com";
        adapter.upsertPendingUser("N", "S", "userY", email, "hash", "EXP", LocalDateTime.now().minusMinutes(1));

        assertThatThrownBy(() -> adapter.enableUserIfCodeValid(email, "EXP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Code expired");
    }

    private void persistEnabledUser(String name, String surname, String username, String email) {
        UserEntity u = new UserEntity();
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("pwd");
        u.setEnabled(true);
        em.persist(u);
        em.flush();
    }

    private UserEntity findByEmail(String email) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
