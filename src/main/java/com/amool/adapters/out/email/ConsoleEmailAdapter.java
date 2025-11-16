package com.amool.adapters.out.email;

import com.amool.application.port.out.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class ConsoleEmailAdapter implements EmailPort {
    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailAdapter.class);
    @Override
    public void send(String to, String subject, String body) {
        log.info("[EMAIL] to={} subject={} body=\n{}", to, subject, body);
    }
}
