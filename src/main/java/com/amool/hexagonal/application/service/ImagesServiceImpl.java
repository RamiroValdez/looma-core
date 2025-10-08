package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.AwsS3Service;
import com.amool.hexagonal.application.port.in.ImagesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

@Service
public class ImagesServiceImpl implements ImagesService {

    private final String WORK_BANNER_PATH = "works/{workId}/banner/";
    private final String WORK_COVER_PATH = "works/{workId}/cover/";
    private final String USER_PROFILE_PATH = "users/profiles/";
    private final String COMIC_IMAGES_PATH = "chapters/{chapterId}/";

    private final AwsS3Service awsS3Service;

    public ImagesServiceImpl(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @Override
    public String uploadBannerImage(MultipartFile file, String workId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID() + "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filePath = WORK_BANNER_PATH.replace("{workId}", workId) + fileName;

        Boolean result = this.awsS3Service.uploadPublicFile(file, filePath);

        if(result) {
            return filePath;
        } else {
            throw new IOException("Error uploading banner image");
        }

    }

    @Override
    public String getBannerImageUrl(String bannerFilePath) {
        return this.awsS3Service.obtainPublicUrl(bannerFilePath);
    }

    @Override
    public String uploadCoverImage(MultipartFile file, String workId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID()+ "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filePath = WORK_COVER_PATH.replace("{workId}", workId) + fileName;

         this.awsS3Service.uploadPublicFile(file, filePath);

         return filePath;
    }

    @Override
    public String getCoverImageUrl(String coverFilePath) {
        return this.awsS3Service.obtainPublicUrl(coverFilePath);
    }

    @Override
    public Boolean uploadProfileImage(MultipartFile file) {
        return false;
    }

    @Override
    public String getProfileImageUrl(String profileFilePath) {
        return "not-implemented";
    }

    @Override
    public String uploadComicImage(MultipartFile file, String comicId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String fileName = UUID.randomUUID()+ "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        String filePath = COMIC_IMAGES_PATH.replace("{chapterId}", comicId) + fileName + extension;

        Boolean result = this.awsS3Service.uploadPrivateFile(file, filePath);

        if (result) {
            return filePath;
        } else {
            throw new IOException("Error uploading banner image");
        }

    }

    @Override
    public String getComicImageUrl(String comicFilePath) {
        return this.awsS3Service.obtainPresignedUrl(comicFilePath);
    }
}
