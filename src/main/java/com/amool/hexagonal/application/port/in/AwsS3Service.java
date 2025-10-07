package com.amool.hexagonal.application.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AwsS3Service {

    String uploadPublicFile(MultipartFile file, String fileName) throws IOException;

    String uploadPrivateFile(MultipartFile file, String fileName) throws IOException;

    String obtainPresignedUrl(String fileName);

    String obtainPublicUrl(String fileName);

}
