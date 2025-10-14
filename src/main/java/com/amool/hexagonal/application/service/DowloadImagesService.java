package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.ImagesService;
import com.amool.hexagonal.domain.model.InMemoryMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DowloadImagesService {

    private final ImagesService imagesService;

    public DowloadImagesService(ImagesService imagesService) {
        this.imagesService = imagesService;
    }


    public String downloadAndUploadCoverImage(String url, String workId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Error al descargar la imagen. HTTP status: " + response.statusCode());
        }

        byte[] imageBytes = response.body();
        String contentType = response.headers().firstValue("Content-Type").orElse("image/png");

        MultipartFile multipartFile = new InMemoryMultipartFile(
                "cover",
                "cover.png",
                contentType,
                imageBytes
        );

        return imagesService.uploadCoverImage(multipartFile, workId);
    }

}
