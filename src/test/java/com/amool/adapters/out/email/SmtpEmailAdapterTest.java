package com.amool.adapters.out.email;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmtpEmailAdapterTest {

    private JavaMailSender mailSender;
    private Environment env;
    private SmtpEmailAdapter adapter;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);
        env = Mockito.mock(Environment.class);
        adapter = new SmtpEmailAdapter(mailSender, env);
    }

    @Test
    void send_usesConfiguredFrom_andBuildsHtmlMimeMessage_withInlineLogo() throws Exception {
        MimeMessage mime = stubMimeMessageCreation();
        givenConfiguredFrom("no-reply@example.com");

        String to = "user@test.com";
        String subject = "Subject";
        String body = "<html><body><img src=\"cid:loomaLogo\"/>Hello</body></html>";

        sendEmail(to, subject, body);

        thenMailWasSent(mime);
        thenFromEquals(mime, "no-reply@example.com");
        thenSubjectEquals(mime, subject);
        thenSingleRecipientEquals(mime, to);
    }

    @Test
    void resolveFrom_fallsBackToSpringMailUsername() throws Exception {
        MimeMessage mime = stubMimeMessageCreation();
        givenConfiguredFrom("");
        givenSpringMailUsername("username@example.com");

        sendEmail("to@test.com", "Subj", "<html>ok</html>");

        thenFromEquals(mime, "username@example.com");
    }

    @Test
    void whenNoFromConfigured_thenThrows() {
        givenConfiguredFrom("");
        givenSpringMailUsername(null);

        IllegalStateException ex = expectSendFailure("to@test.com", "Subj", "<html>ok</html>");
        assertTrue(ex.getMessage().contains("No sender address configured"));
    }

    private MimeMessage stubMimeMessageCreation() throws Exception {
        MimeMessage mime = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mime);
        return mime;
    }

    private void givenConfiguredFrom(String address) {
        setPrivateField(adapter, "configuredFrom", address);
    }

    private void givenSpringMailUsername(String username) {
        when(env.getProperty("spring.mail.username")).thenReturn(username);
    }

    private void sendEmail(String to, String subject, String body) throws Exception {
        adapter.send(to, subject, body);
    }

    private IllegalStateException expectSendFailure(String to, String subject, String body) {
        return assertThrows(IllegalStateException.class, () -> adapter.send(to, subject, body));
    }

    private void thenMailWasSent(MimeMessage mime) {
        verify(mailSender).send(mime);
    }

    private void thenFromEquals(MimeMessage mime, String expected) throws Exception {
        assertNotNull(mime.getFrom());
        assertTrue(mime.getFrom()[0] instanceof InternetAddress);
        assertEquals(expected, ((InternetAddress) mime.getFrom()[0]).getAddress());
    }

    private void thenSubjectEquals(MimeMessage mime, String subject) throws Exception {
        assertEquals(subject, mime.getSubject());
    }

    private void thenSingleRecipientEquals(MimeMessage mime, String expected) throws Exception {
        assertEquals(1, mime.getAllRecipients().length);
        assertEquals(expected, ((InternetAddress) mime.getAllRecipients()[0]).getAddress());
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
