package main.java.com.looma.hexagonal.adapters.in.rest;

import main.java.com.looma.hexagonal.application.port.in.PingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private final PingUseCase pingUseCase;

    public PingController(PingUseCase pingUseCase) {
        this.pingUseCase = pingUseCase;
    }

    @GetMapping("/health/ping")
    public ResponseEntity<String> ping() {
        String result = pingUseCase.ping();
        return ResponseEntity.ok(result); // texto plano "pong"
    }
}
