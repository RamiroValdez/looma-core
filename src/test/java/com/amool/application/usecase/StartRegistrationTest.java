package com.amool.application.usecase;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.UserAccountPort;
import com.amool.application.usecases.StartRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.function.Executable;

public class StartRegistrationTest {

    private static final String NAME = "Nombre";
    private static final String SURNAME = "Apellido";
    private static final String USERNAME = "user";
    private static final String EMAIL = "mail@test.com";
    private static final String PASSWORD = "SuperPass123!";

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
    void shouldThrowWhenRequiredFieldsMissing() {
        RegistrationData data = defaultData().withName(null);

        thenThrows(IllegalArgumentException.class, whenExecuting(data), "Missing required fields");
    }

    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {
        RegistrationData data = defaultData().withConfirmPassword("other");

        thenThrows(IllegalArgumentException.class, whenExecuting(data), "Passwords do not match");
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegistrationData data = defaultData();
        givenEmailExists(data.email());

        thenThrows(IllegalArgumentException.class, whenExecuting(data), "Email already exists");
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        RegistrationData data = defaultData();
        givenUsernameExists(data.username());

        thenThrows(IllegalArgumentException.class, whenExecuting(data), "Username already exists");
    }

    @Test
    void shouldPersistPendingUserAndSendVerificationEmailWhenDataIsValid() {
        RegistrationData data = defaultData();
        givenUniqueUser(data);

        whenRegistrationStarts(data);

        PendingUserData pendingUser = capturePendingUser(data);
        thenPasswordIsHashed(pendingUser.hashedPassword(), data.password());
        thenVerificationCodeIsValid(pendingUser.verificationCode());
        thenExpirationIsAroundFifteenMinutes(pendingUser.expiresAt());
        thenVerificationEmailSent(data, pendingUser.verificationCode());
    }

    private RegistrationData defaultData() {
        return new RegistrationData(NAME, SURNAME, USERNAME, EMAIL, PASSWORD, PASSWORD);
    }

    private void givenEmailExists(String email) {
        when(userAccountPort.emailExists(email)).thenReturn(true);
    }

    private void givenUsernameExists(String username) {
        when(userAccountPort.usernameExists(username)).thenReturn(true);
    }

    private void givenUniqueUser(RegistrationData data) {
        when(userAccountPort.emailExists(data.email())).thenReturn(false);
        when(userAccountPort.usernameExists(data.username())).thenReturn(false);
    }

    private void whenRegistrationStarts(RegistrationData data) {
        useCase.execute(
            data.name(),
            data.surname(),
            data.username(),
            data.email(),
            data.password(),
            data.confirmPassword()
        );
    }

    private Executable whenExecuting(RegistrationData data) {
        return () -> whenRegistrationStarts(data);
    }

    private PendingUserData capturePendingUser(RegistrationData data) {
        ArgumentCaptor<String> hashCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> expCap = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(userAccountPort).upsertPendingUser(
            eq(data.name()),
            eq(data.surname()),
            eq(data.username()),
            eq(data.email()),
            hashCap.capture(),
            codeCap.capture(),
            expCap.capture()
        );

        return new PendingUserData(hashCap.getValue(), codeCap.getValue(), expCap.getValue());
    }

    private void thenPasswordIsHashed(String hash, String rawPassword) {
        assertNotNull(hash);
        assertNotEquals(rawPassword, hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(new BCryptPasswordEncoder().matches(rawPassword, hash));
    }

    private void thenVerificationCodeIsValid(String code) {
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    private void thenExpirationIsAroundFifteenMinutes(LocalDateTime expiration) {
        LocalDateTime now = LocalDateTime.now();
        assertTrue(!expiration.isBefore(now.plusMinutes(14)) && !expiration.isAfter(now.plusMinutes(16)),
            "expiresAt should be ~15 minutes in the future");
    }

    private void thenVerificationEmailSent(RegistrationData data, String code) {
        ArgumentCaptor<String> toCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);

        verify(emailPort).send(toCap.capture(), subjCap.capture(), bodyCap.capture());

        assertEquals(data.email(), toCap.getValue());
        assertEquals("Looma - Código de verificación", subjCap.getValue());
        String body = bodyCap.getValue();
        assertNotNull(body);
        assertTrue(body.toLowerCase().contains("<!doctype html>"));
        assertTrue(body.contains(data.name()));
        assertTrue(body.contains(code));
    }

    private void thenThrows(Class<? extends Throwable> expected, Executable action, String expectedMessage) {
        Throwable ex = assertThrows(expected, action);
        assertEquals(expectedMessage, ex.getMessage());
    }

    private record RegistrationData(
        String name,
        String surname,
        String username,
        String email,
        String password,
        String confirmPassword
    ) {
        RegistrationData withName(String newName) {
            return new RegistrationData(newName, surname, username, email, password, confirmPassword);
        }

        RegistrationData withConfirmPassword(String newConfirmPassword) {
            return new RegistrationData(name, surname, username, email, password, newConfirmPassword);
        }
    }

    private record PendingUserData(String hashedPassword, String verificationCode, LocalDateTime expiresAt) {}
}
