package com.amool.application.port.out;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

public interface AwsS3Port {

    Boolean uploadPublicFile(String fileName, MultipartFile file) throws IOException;

    Boolean uploadPrivateFile(String fileName, MultipartFile file) throws IOException;

    String obtainFilePresignedUrl(String key) throws RuntimeException;

    String obtainPublicUrl(String fileName);

    List<S3Object> obtainObjectsInPath(String path);

    void deleteObject(String key);

}
