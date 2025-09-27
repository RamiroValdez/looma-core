package main.java.com.looma.hexagonal.application.service;

import main.java.com.looma.hexagonal.application.port.in.PingUseCase;
import main.java.com.looma.hexagonal.domain.ping.Ping;
import org.springframework.stereotype.Service;

@Service
public class PingService implements PingUseCase {

    @Override
    public String ping() {
        Ping response = new Ping("pong");
        return response.getMessage();
    }
}
