package com.amool.application.service;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.DownloadPort;
import com.amool.domain.model.InMemoryMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

public class ImagesService {

    private final FilesStoragePort filesStoragePort;
    private final DownloadPort downloadPort;
    private final String WORK_COVER_PATH = "works/{workId}/cover/";
    private final String WORK_BANNER_PATH = "works/{workId}/banner/";

    public ImagesService(FilesStoragePort filesStoragePort, DownloadPort downloadPort) {
        this.downloadPort = downloadPort;
        this.filesStoragePort = filesStoragePort;
    }

    public String downloadAndUploadCoverImage(String url, String workId) throws IOException, InterruptedException {
        byte[] imageBytes = downloadPort.downloadImage(url);

        InMemoryMultipartFile multipartFile = new InMemoryMultipartFile(
                "cover",
                "cover.png",
                "image/png",
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

        Boolean result = this.filesStoragePort.uploadPublicFile(filePath, file);

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

        this.filesStoragePort.uploadPublicFile(filePath,file);

        return filePath;
    }

    public String uploadUserImage(InMemoryMultipartFile file, String userId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID()+ "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filePath = "users/" + userId + "/profile/" + fileName;

        this.filesStoragePort.uploadPublicFile(filePath,file);

        return filePath;
    }

    public void deleteImage(String filePath) {
        if (filePath == null) return;
        if ("none".equalsIgnoreCase(filePath)) return;
        this.filesStoragePort.deleteObject(filePath);
    }

}
