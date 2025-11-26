package com.amool.adapters.out.persistence;

import com.amool.application.port.out.RegistrationTokenPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegistrationTokenPersistenceAdapterTest {

    @Autowired
    private RegistrationTokenPersistenceAdapter adapter;

    private String email() { return "user@example.com"; }
    private String code() { return "ABC123"; }
    private String name() { return "John"; }
    private String surname() { return "Doe"; }
    private String username() { return "johndoe"; }
    private String passwordHash() { return "hash"; }

    @Test
    @DisplayName("upsert crea un nuevo token si no existe")
    void upsert_crea_si_no_existe() {
        String email = email();
        LocalDateTime expires = LocalDateTime.now().plusDays(1);

        adapter.upsert(email, code(), expires, name(), surname(), username(), passwordHash());

        Optional<RegistrationTokenPort.TokenRecord> recOpt = adapter.findByEmail(email);
        assertThat(recOpt).isPresent();
        RegistrationTokenPort.TokenRecord rec = recOpt.get();
        assertThat(rec.email).isEqualTo(email);
        assertThat(rec.code).isEqualTo(code());
        assertThat(rec.expiresAt).isEqualTo(expires);
        assertThat(rec.name).isEqualTo(name());
        assertThat(rec.surname).isEqualTo(surname());
        assertThat(rec.username).isEqualTo(username());
        assertThat(rec.passwordHash).isEqualTo(passwordHash());
    }

    @Test
    @DisplayName("upsert actualiza token existente")
    void upsert_actualiza_si_existe() {
        String email = email();
        LocalDateTime expires1 = LocalDateTime.now().plusDays(1);
        adapter.upsert(email, code(), expires1, name(), surname(), username(), passwordHash());
        assertThat(adapter.findByEmail(email)).isPresent();

        String newCode = "XYZ789";
        LocalDateTime expires2 = LocalDateTime.now().plusDays(2);
        String newName = "Jane";
        String newSurname = "Roe";
        String newUsername = "janeroe";
        String newHash = "hash2";
        adapter.upsert(email, newCode, expires2, newName, newSurname, newUsername, newHash);

        RegistrationTokenPort.TokenRecord rec = adapter.findByEmail(email).orElseThrow();
        assertThat(rec.code).isEqualTo(newCode);
        assertThat(rec.expiresAt).isEqualTo(expires2);
        assertThat(rec.name).isEqualTo(newName);
        assertThat(rec.surname).isEqualTo(newSurname);
        assertThat(rec.username).isEqualTo(newUsername);
        assertThat(rec.passwordHash).isEqualTo(newHash);
    }

    @Test
    @DisplayName("findByEmail retorna Optional.empty cuando no existe")
    void findByEmail_empty_when_no_existe() {
        String email = "missing@example.com";

        Optional<RegistrationTokenPort.TokenRecord> recOpt = adapter.findByEmail(email);

        assertThat(recOpt).isEmpty();
    }

    @Test
    @DisplayName("deleteByEmail elimina el token")
    void deleteByEmail_elimina() {
        String email = email();
        adapter.upsert(email, code(), LocalDateTime.now().plusDays(1), name(), surname(), username(), passwordHash());
        assertThat(adapter.findByEmail(email)).isPresent();

        adapter.deleteByEmail(email);

        assertThat(adapter.findByEmail(email)).isEmpty();
    }
}
