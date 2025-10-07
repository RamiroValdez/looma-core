package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.AwsS3Service;
import com.amool.hexagonal.application.port.out.AwsS3Port;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private final AwsS3Port awsS3Port;

    public AwsS3ServiceImpl(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
    }

    @Override
    public String uploadPublicFile(MultipartFile file, String fileName) throws IOException {

        return this.awsS3Port.uploadPublicFile(fileName, file);
    }

    @Override
    public String uploadPrivateFile(MultipartFile file, String fileName) throws IOException {
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
}
