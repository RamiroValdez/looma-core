package com.amool.application.service;

import com.amool.application.port.out.AwsS3Port;
import com.amool.domain.model.InMemoryMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

public class ImagesService {

    private final AwsS3Port awsS3Port;
    private final String WORK_COVER_PATH = "works/{workId}/cover/";
    private final String WORK_BANNER_PATH = "works/{workId}/banner/";

    public ImagesService(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
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

        return uploadCoverImage(multipartFile, workId);
    }

    public String uploadBannerImage(MultipartFile file, String workId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID() + "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filePath = WORK_BANNER_PATH.replace("{workId}", workId) + fileName;

        Boolean result = this.awsS3Port.uploadPublicFile(filePath, file);

        if(result) {
            return filePath;
        } else {
            throw new IOException("Error uploading banner image");
        }

    }

    public String uploadCoverImage(MultipartFile file, String workId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID()+ "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filePath = WORK_COVER_PATH.replace("{workId}", workId) + fileName;

        this.awsS3Port.uploadPublicFile(filePath,file);

        return filePath;
    }

    public void deleteImage(String filePath) {
        if (filePath == null) return;
        if ("none".equalsIgnoreCase(filePath)) return;
        this.awsS3Port.deleteObject(filePath);
    }

}
