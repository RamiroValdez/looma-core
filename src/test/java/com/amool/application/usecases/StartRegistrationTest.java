package com.amool.application.usecases;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.UserAccountPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StartRegistrationTest {

    private UserAccountPort userAccountPort;
    private EmailPort emailPort;
    private StartRegistration useCase;

    @BeforeEach
    void setUp() {
        userAccountPort = Mockito.mock(UserAccountPort.class);
        emailPort = Mockito.mock(EmailPort.class);
        useCase = new StartRegistration(userAccountPort, emailPort);
    }

    @Test
    void whenMissingFields_thenThrows() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(null, "Ap", "user", "mail@test.com", "pass", "pass"));
        assertEquals("Missing required fields", ex.getMessage());
    }

    @Test
    void whenPasswordsMismatch_thenThrows() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute("Nombre", "Apellido", "user", "mail@test.com", "pass", "other"));
        assertEquals("Passwords do not match", ex.getMessage());
    }

    @Test
    void whenEmailExists_thenThrows() {
        when(userAccountPort.emailExists("mail@test.com")).thenReturn(true);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute("Nombre", "Apellido", "user", "mail@test.com", "pass", "pass"));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void whenUsernameExists_thenThrows() {
        when(userAccountPort.usernameExists("user")).thenReturn(true);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute("Nombre", "Apellido", "user", "mail@test.com", "pass", "pass"));
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void happyPath_persistsPending_andSendsEmailHtml_withCode() {
        when(userAccountPort.emailExists(anyString())).thenReturn(false);
        when(userAccountPort.usernameExists(anyString())).thenReturn(false);

        String name = "Nombre";
        String surname = "Apellido";
        String username = "user";
        String email = "mail@test.com";
        String password = "SuperPass123!";

        useCase.execute(name, surname, username, email, password, password);

        ArgumentCaptor<String> hashCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> expCap = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(userAccountPort).upsertPendingUser(eq(name), eq(surname), eq(username), eq(email),
                hashCap.capture(), codeCap.capture(), expCap.capture());

        String hash = hashCap.getValue();
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(new BCryptPasswordEncoder().matches(password, hash));

        String code = codeCap.getValue();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));

        LocalDateTime exp = expCap.getValue();
        var now = LocalDateTime.now();
        assertTrue(!exp.isBefore(now.plusMinutes(14)) && !exp.isAfter(now.plusMinutes(16)),
                "expiresAt should be ~15 minutes in the future");

        ArgumentCaptor<String> toCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);
        verify(emailPort).send(toCap.capture(), subjCap.capture(), bodyCap.capture());

        assertEquals(email, toCap.getValue());
        assertEquals("Looma - Código de verificación", subjCap.getValue());
        String body = bodyCap.getValue();
        assertNotNull(body);
        assertTrue(body.toLowerCase().contains("<!doctype html>"));
        assertTrue(body.contains(name));
        assertTrue(body.contains(code));
    }
}
