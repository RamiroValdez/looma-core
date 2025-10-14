package com.amool.hexagonal.adapters.out.awsapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsS3AdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private PresignedGetObjectRequest presignedRequest;

    private AwsS3Adapter awsS3Adapter;

    @BeforeEach
    void setUp() {
        awsS3Adapter = new AwsS3Adapter(s3Client, s3Presigner, "test-bucket", "us-east-1");
    }

    @Test
    void uploadPublicFile_ThrowsRuntimeException() throws IOException {
        String fileName = "test-file.jpg";
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> awsS3Adapter.uploadPublicFile(fileName, multipartFile));
        assertEquals("S3 error", exception.getMessage());
    }

    @Test
    void uploadPublicFile_ThrowsS3Exception() throws IOException {
        String fileName = "test-file.jpg";
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Bucket not found").statusCode(404).build());

        S3Exception exception = assertThrows(S3Exception.class,
                () -> awsS3Adapter.uploadPublicFile(fileName, multipartFile));
        assertEquals("Bucket not found", exception.getMessage());
    }

    @Test
    void uploadPublicFile_ThrowsIOExceptionFromInputStream() throws IOException {
        String fileName = "test-file.jpg";
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenThrow(new IOException("File read error"));

        IOException exception = assertThrows(IOException.class,
                () -> awsS3Adapter.uploadPublicFile(fileName, multipartFile));
        assertEquals("File read error", exception.getMessage());
    }
}
