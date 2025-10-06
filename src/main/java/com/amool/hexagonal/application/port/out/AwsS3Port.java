package com.amool.hexagonal.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AwsS3Port {

    String uploadPublicFile(String fileName, MultipartFile file) throws IOException;

    String uploadPrivateFile(String fileName, MultipartFile file) throws IOException;

    String obtainFileUrl(String key);

}
