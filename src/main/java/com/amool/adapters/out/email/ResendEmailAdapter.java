package com.amool.adapters.out.email;

import com.amool.application.port.out.EmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class ResendEmailAdapter implements EmailPort {

    @Value("${RESEND_API_KEY}")
    private String apiKey;
    @Value("${LOOMA_DEFAULT_EMAIL:looma.tpi@gmail.com}")
    private String replyToEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void send(String to, String subject, String htmlBody) {
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        // TRUCO: El 'from' DEBE ser el de Resend si no has verificado dominio propio.
        // Pero le ponemos "Looma" antes para que se vea profesional.
        body.put("from", "Looma App <info@looma-app.lat>");

        body.put("to", to);
        body.put("subject", subject);
        body.put("html", htmlBody);

        // Aquí redirigimos las respuestas a TU Gmail
        body.put("reply_to", replyToEmail);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Email enviado a " + to);
        } catch (Exception e) {
            // Importante para ver el error en los logs de Railway si falla
            System.err.println("Error enviando email: " + e.getMessage());
            // Opcional: e.printStackTrace();
        }
    }
}
