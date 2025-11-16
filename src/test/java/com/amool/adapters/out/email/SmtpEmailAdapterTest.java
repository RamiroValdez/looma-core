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
        MimeMessage mime = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mime);

        setPrivateField(adapter, "configuredFrom", "no-reply@example.com");

        String to = "user@test.com";
        String subject = "Subject";
        String body = "<html><body><img src=\"cid:loomaLogo\"/>Hello</body></html>";

        adapter.send(to, subject, body);

        verify(mailSender).send(mime);

        assertNotNull(mime.getFrom());
        assertTrue(mime.getFrom()[0] instanceof InternetAddress);
        assertEquals("no-reply@example.com", ((InternetAddress) mime.getFrom()[0]).getAddress());

        assertEquals(subject, mime.getSubject());
        assertEquals(1, mime.getAllRecipients().length);
        assertEquals(to, ((InternetAddress) mime.getAllRecipients()[0]).getAddress());

    }

    @Test
    void resolveFrom_fallsBackToSpringMailUsername() throws Exception {
        MimeMessage mime = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mime);

        setPrivateField(adapter, "configuredFrom", "");
        when(env.getProperty("spring.mail.username")).thenReturn("username@example.com");

        adapter.send("to@test.com", "Subj", "<html>ok</html>");

        assertEquals("username@example.com", ((InternetAddress) mime.getFrom()[0]).getAddress());
    }

    @Test
    void whenNoFromConfigured_thenThrows() {
        setPrivateField(adapter, "configuredFrom", "");
        when(env.getProperty("spring.mail.username")).thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adapter.send("to@test.com", "Subj", "<html>ok</html>"));
        assertTrue(ex.getMessage().contains("No sender address configured"));
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
