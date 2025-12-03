package com.amool.adapters.out.dowloadImages;

import com.amool.application.port.out.DownloadPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class DownloadAdapter implements DownloadPort {

    private final HttpClient httpClient;

    public DownloadAdapter() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public byte[] downloadImage(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Error al descargar la imagen. HTTP status: " + response.statusCode());
        }

        return response.body();
    }
}
