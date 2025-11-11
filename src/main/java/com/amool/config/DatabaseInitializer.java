package com.amool.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                logger.info("Iniciando la carga de datos en la base de datos...");

                ClassPathResource resource = new ClassPathResource("data.sql");
                
                if (!resource.exists()) {
                    logger.warn("El archivo data.sql no existe en resources. Omitiendo la inicialización.");
                    return;
                }
                
                String sqlScript = readResource(resource);

                // Eliminar comentarios de bloque y líneas de comentario
                sqlScript = sqlScript.replaceAll("--[^\n]*", ""); // Comentarios de línea
                sqlScript = sqlScript.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", ""); // Comentarios de bloque

                String[] sqlStatements = sqlScript.split(";");
                
                int executedStatements = 0;
                for (String statement : sqlStatements) {
                    String trimmedStatement = statement.trim();
                    if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                        try {
                            jdbcTemplate.execute(trimmedStatement);
                            executedStatements++;
                        } catch (Exception e) {
                            logger.error("Error al ejecutar la sentencia SQL: {}", trimmedStatement, e);
                            throw e;
                        }
                    }
                }
                
                logger.info("Script SQL ejecutado exitosamente. {} sentencias procesadas.", executedStatements);
                
            } catch (IOException e) {
                logger.error("Error al leer el archivo SQL", e);
                throw new RuntimeException("No se pudo cargar el script SQL de inicialización", e);
            } catch (Exception e) {
                logger.error("Error al ejecutar el script SQL", e);
                throw new RuntimeException("Error durante la inicialización de la base de datos", e);
            }
        };
    }

    private String readResource(ClassPathResource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}

