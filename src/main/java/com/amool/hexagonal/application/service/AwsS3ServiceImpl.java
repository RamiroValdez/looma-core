package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.AwsS3Service;
import com.amool.hexagonal.application.port.out.AwsS3Port;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private final AwsS3Port awsS3Port;

    public AwsS3ServiceImpl(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
    }

    @Override
    public Boolean uploadPublicFile(MultipartFile file, String fileName) throws IOException {

        return this.awsS3Port.uploadPublicFile(fileName, file);
    }

    @Override
    public Boolean uploadPrivateFile(MultipartFile file, String fileName) throws IOException {
        return this.awsS3Port.uploadPrivateFile(fileName, file);
    }

    @Override
    public String obtainPresignedUrl(String fileName) {
        return this.awsS3Port.obtainFilePresignedUrl(fileName);
    }

    @Override
    public String obtainPublicUrl(String fileName) {
        return this.awsS3Port.obtainPublicUrl(fileName);
    }

    @Override
    public List<S3Object> obtainObjectsInPath(String path) {
        return this.awsS3Port.obtainObjectsInPath(path);
    }

    @Override
    public void deleteObject(String key) {
        this.awsS3Port.deleteObject(key);
    }

}
