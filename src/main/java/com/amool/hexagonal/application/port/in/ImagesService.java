package com.amool.hexagonal.application.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImagesService {

    String uploadBannerImage(MultipartFile file, String workId) throws IOException;

    String getBannerImageUrl(String bannerFilePath);

    Boolean uploadCoverImage(MultipartFile file, String workId) throws IOException;

    String getCoverImageUrl(String coverFilePath);

    Boolean uploadProfileImage(MultipartFile file);

    String getProfileImageUrl(String profileFilePath);

    String uploadComicImage(MultipartFile file, String comicId) throws IOException;

    String getComicImageUrl(String comicFilePath);

}
