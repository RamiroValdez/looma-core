package com.amool.adapters.out.awsapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
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

    private AwsS3Adapter awsS3Adapter;

    private static final String DEFAULT_FILE_NAME = "test-file.jpg";

    @BeforeEach
    void setUp() {
        awsS3Adapter = new AwsS3Adapter(s3Client, s3Presigner, "test-bucket", "us-east-1");
    }

    // --- Helpers: Given ---
    private void givenValidImageMultipartFile() throws IOException {
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    }

    private void givenMultipartFileInputStreamThrows(IOException ioException) throws IOException {
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenThrow(ioException);
    }

    private void givenS3PutObjectThrows(RuntimeException exception) {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(exception);
    }

    // --- Helpers: When ---
    private <T extends Throwable> T whenUploadingPublicFileExpecting(Class<T> exceptionClass) {
        return assertThrows(exceptionClass, () -> awsS3Adapter.uploadPublicFile(DEFAULT_FILE_NAME, multipartFile));
    }

    // --- Helpers: Then ---
    private void thenMessageIs(Throwable exception, String expectedMessage) {
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void uploadPublicFile_ThrowsRuntimeException() throws IOException {
        // Given
        givenValidImageMultipartFile();
        givenS3PutObjectThrows(new RuntimeException("S3 error"));

        // When
        RuntimeException exception = whenUploadingPublicFileExpecting(RuntimeException.class);

        // Then
        thenMessageIs(exception, "S3 error");
    }

    @Test
    void uploadPublicFile_ThrowsS3Exception() throws IOException {
        // Given
        givenValidImageMultipartFile();
        givenS3PutObjectThrows(S3Exception.builder().message("Bucket not found").statusCode(404).build());

        // When
        S3Exception exception = whenUploadingPublicFileExpecting(S3Exception.class);

        // Then
        thenMessageIs(exception, "Bucket not found");
    }

    @Test
    void uploadPublicFile_ThrowsIOExceptionFromInputStream() throws IOException {
        // Given
        givenMultipartFileInputStreamThrows(new IOException("File read error"));

        // When
        IOException exception = whenUploadingPublicFileExpecting(IOException.class);

        // Then
        thenMessageIs(exception, "File read error");
    }
}
