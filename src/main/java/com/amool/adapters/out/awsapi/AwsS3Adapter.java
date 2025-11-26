package com.amool.adapters.out.awsapi;

import com.amool.application.port.out.FilesStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class AwsS3Adapter implements FilesStoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;


    private String bucketName;

    private String region;

    AwsS3Adapter(S3Client s3Client, S3Presigner s3Presigner, @Value("${AWS_S3_BUCKET}") String bucketName, @Value("${AWS_REGION}") String region) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    @Override
    public Boolean uploadPublicFile(String fileName, MultipartFile file) throws IOException {

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .acl("public-read")
                            .contentType(file.getContentType())
                            .contentLength(
                                    file.getSize()).build(),
                                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()
                                    )
            );

            return true;
    }

    @Override
    public Boolean uploadPrivateFile(String fileName, MultipartFile file) throws IOException {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .contentLength(
                                    file.getSize()).build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()
                    )
            );

            return true;
    }

   @Override
   public String obtainFilePresignedUrl(String key) {

           GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                   .bucket(bucketName)
                   .key(key)
                   .build();

           GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                   .signatureDuration(Duration.ofMinutes(15))
                   .getObjectRequest(getObjectRequest)
                   .build();

           return s3Presigner.presignGetObject(presignRequest).url().toString();
   }

    @Override
    public String obtainPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    @Override
    public List<S3Object> obtainObjectsInPath(String path) {
        return s3Client.listObjectsV2(builder ->
                builder.bucket(bucketName).prefix(path)
        ).contents();
    }

    @Override
    public void deleteObject(String key) {
        if (key == null || key.isBlank()) return;
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    @Override
    public Boolean uploadPrivateByte (String key, String header, byte[] bytes) {
        s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(header)
                            .build(),
                    RequestBody.fromBytes(bytes)
                    );

            return true;
    }

}
