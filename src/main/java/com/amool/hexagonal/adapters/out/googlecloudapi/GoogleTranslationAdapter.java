package com.amool.hexagonal.adapters.out.googlecloudapi;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import com.amool.hexagonal.application.port.out.GoogleTranslatePort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;
// Se elimina la importación de @Value ya que no la usaremos:
// import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Component
public class GoogleTranslationAdapter implements GoogleTranslatePort {

    // --- 1. PROPIEDADES INYECTADAS Y CONSTANTES ---
    
    // 1. Clave API: Vuelve a ser una constante estática que lee la variable de entorno.
    // Esto evita el error de inyección de Spring.
    private static final String API_KEY = System.getenv("GOOGLE_API_KEY"); 

    // 2. Endpoint: Constante (static final)
    private static final String ENDPOINT = 
            "https://translation.googleapis.com/language/translate/v2"; 
    
    // 3. Logger: Constante (static final)
    private static final Logger logger = LoggerFactory.getLogger(GoogleTranslationAdapter.class);

    // --- 4. CONSTRUCTOR (Ahora simple, sin inyección de @Value) ---
    // Se elimina el constructor con @Value, ya que API_KEY es estática.
    
    // Si necesitas inyectar otras dependencias, el constructor debe ser así:
    // public GoogleTranslationAdapter() { } 
    // Si no tienes otras dependencias, el constructor es opcional.

    // --- 5. MÉTODOS DE SERVICIO ---

    @Override
    public String translateText(String text, String targetLanguage) {
        try {
            // Log para verificar la clave cargada.
            // Usamos la variable estática API_KEY
            logger.info("Using Google API Key: {}", API_KEY != null && !API_KEY.isEmpty() ? "KEY_FOUND" : "KEY_MISSING");

            // Si la clave es nula o vacía, fallará aquí ANTES de la llamada externa
            if (API_KEY == null || API_KEY.isEmpty()) {
                 throw new RuntimeException("Google API Key (GOOGLE_API_KEY) is missing from the environment.");
            }
            
            String raw = this.sendRequest(text, targetLanguage);
            return this.cleanRawTranslation(raw);

        } catch (HttpClientErrorException forbiddenE) {
            // Manejo de error específico 403
            if (forbiddenE.getStatusCode().value() == 403) {
                logger.error("403 Forbidden from Google API. Response Body: {}", forbiddenE.getResponseBodyAsString());
                throw new RuntimeException("External Translation API Forbidden: " + forbiddenE.getResponseBodyAsString(), forbiddenE);
            }
            // Manejo de cualquier otro error 4xx
            throw new RuntimeException("HTTP Client Error accessing Google API.", forbiddenE);
        } catch (Exception e) {
            logger.error("Unexpected Error during Google translation.", e);
            throw new RuntimeException("Translation service failed.", e);
        }
    }
    
    private String sendRequest(String text, String targetLanguage) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("q", text)
                .queryParam("target", targetLanguage)
                .queryParam("key", API_KEY); // Usa la API_KEY estática

        String uri = builder.toUriString();
        // Log de URL (oculta la clave para seguridad)
        logger.info("Calling Google API URL: {}", uri.contains(API_KEY) ? uri.replace(API_KEY, "****************") : uri);

        // Llamada a la API externa
        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        // Lógica de parsing para extraer la traducción
        List<Map<String, Object>> translations =
                (List<Map<String, Object>>) ((Map) response.get("data")).get("translations");

        return (String) translations.getFirst().get("translatedText");
    }

    private String cleanRawTranslation(String raw) {
        if (raw == null) return "";
        String clean = raw;

        clean = clean.replaceAll("\\s+", " ").trim();
        clean = clean.replaceAll("[\\u200B-\\u200D\\uFEFF]", "");

        return clean;
    }
}