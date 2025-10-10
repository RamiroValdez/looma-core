package com.amool.hexagonal.application.port.in;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

public interface AwsS3Service {

    Boolean uploadPublicFile(MultipartFile file, String fileName) throws IOException;

    Boolean uploadPrivateFile(MultipartFile file, String fileName) throws IOException;

    String obtainPresignedUrl(String fileName);

    String obtainPublicUrl(String fileName);

    List<S3Object> obtainObjectsInPath(String path);

    void deleteObject(String key);

}
