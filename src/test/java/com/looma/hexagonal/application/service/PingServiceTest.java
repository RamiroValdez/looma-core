package test.java.com.looma.hexagonal.application.service;

import org.junit.jupiter.api.Test;
import main.java.com.looma.hexagonal.application.service.PingService;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PingServiceTest {

    @Test
    void whenPing_thenReturnPong() {
        PingService service = new PingService();

        String result = service.ping();

        assertEquals("pong", result);
    }
}
