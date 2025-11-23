package com.amool.adapters.out.email;

import com.amool.application.port.out.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Deprecated
@Profile("prod")
public class SmtpEmailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);

    private final JavaMailSender mailSender;
    private final Environment env;

    @Value("${mail.from:}")
    private String configuredFrom;

    public SmtpEmailAdapter(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Override
    public void send(String to, String subject, String body) {
        String from = resolveFrom();

        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            try {
                var logo = new ClassPathResource("static/loomaLogo.png");
                if (logo.exists()) {
                    helper.addInline("loomaLogo", logo);
                } else {
                    log.warn("Logo not found at classpath: static/loomaLogo.png");
                }
            } catch (Exception e) {
                log.warn("Failed to attach inline logo: {}", e.getMessage());
            }
            mailSender.send(mime);
            log.info("Email sent to={} subject={}", to, subject);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    private String resolveFrom() {
        if (configuredFrom != null && !configuredFrom.isBlank()) {
            return configuredFrom;
        }
        String username = env.getProperty("spring.mail.username");
        if (username != null && !username.isBlank()) {
            return username;
        }
        throw new IllegalStateException("No sender address configured. Set 'mail.from' or 'spring.mail.username'.");
    }

}
