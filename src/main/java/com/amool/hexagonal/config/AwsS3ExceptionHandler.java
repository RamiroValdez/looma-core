package com.amool.hexagonal.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class AwsS3ExceptionHandler {

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<Object> handleS3Exception(S3Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("service", "AWS S3");

        switch (ex.statusCode()) {
            case 403:
                body.put("status", HttpStatus.FORBIDDEN.value());
                body.put("error", "S3 Access Denied");
                body.put("message", "Sin permisos para acceder al recurso S3");
                return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
            case 404:
                body.put("status", HttpStatus.NOT_FOUND.value());
                body.put("error", "S3 Resource Not Found");
                body.put("message", "Recurso no encontrado en S3");
                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
            default:
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", "S3 Service Error");
                body.put("message", "Error en servicio AWS S3: " + ex.getMessage());
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleFileIOException(IOException ex, WebRequest request) throws IOException {
        if (ex.getMessage() != null && (ex.getMessage().contains("S3") || ex.getMessage().contains("file"))) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("service", "File Upload");
            body.put("error", "File Operation Error");
            body.put("message", "Error procesando archivo: " + ex.getMessage());

            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw ex;
    }
}
