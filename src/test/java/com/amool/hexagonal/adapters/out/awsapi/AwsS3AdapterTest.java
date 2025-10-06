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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

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
        awsS3Adapter = new AwsS3Adapter(s3Client, s3Presigner);
    }

    @Test
    void uploadPublicFile_Success() throws IOException {
        // Given
        String fileName = "test-file.jpg";
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        // When
        String result = awsS3Adapter.uploadPublicFile(fileName, multipartFile);

        // Then
        assertEquals("Upload successful", result);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadPublicFile_ThrowsIOException() throws IOException {
        // Given
        String fileName = "test-file.jpg";
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 error"));

        // When & Then
        IOException exception = assertThrows(IOException.class,
                () -> awsS3Adapter.uploadPublicFile(fileName, multipartFile));
        assertTrue(exception.getMessage().contains("Error uploading file to S3"));
    }

    @Test
    void obtainFileUrl_Success() throws Exception {
        // Given
        String key = "test-key";
        URL expectedUrl = new URL("https://presigned-url.com");
        when(presignedRequest.url()).thenReturn(expectedUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        // When
        String result = awsS3Adapter.obtainFileUrl(key);

        // Then
        assertEquals(expectedUrl.toString(), result);
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }
}
