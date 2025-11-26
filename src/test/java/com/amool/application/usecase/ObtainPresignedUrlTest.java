package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.usecases.ObtainPresignedUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class ObtainPresignedUrlTest {

    private AwsS3Port awsS3Port;
    private ObtainPresignedUrl useCase;

    private static final String VALID_FILE_NAME = "test-file.txt";
    private static final String EMPTY_FILE_NAME = "";
    private static final String PRESIGNED_URL = "https://example.com/presigned-url";
    private static final String EMPTY_URL = "";

    @BeforeEach
    public void setUp() {
        awsS3Port = Mockito.mock(AwsS3Port.class);
        useCase = new ObtainPresignedUrl(awsS3Port);
    }

    @Test
    public void shouldReturnPresignedUrlForValidFileName() {
        givenPresignedUrlResponse(VALID_FILE_NAME, PRESIGNED_URL);

        String result = whenObtainingPresignedUrl(VALID_FILE_NAME);

        thenPresignedUrlEquals(result, PRESIGNED_URL);
        thenPortRequestedUrlFor(VALID_FILE_NAME);
    }

    @Test
    public void shouldReturnEmptyUrlForEmptyFileName() {
        givenPresignedUrlResponse(EMPTY_FILE_NAME, EMPTY_URL);

        String result = whenObtainingPresignedUrl(EMPTY_FILE_NAME);

        thenPresignedUrlEquals(result, EMPTY_URL);
        thenPortRequestedUrlFor(EMPTY_FILE_NAME);
    }

    @Test
    public void shouldReturnEmptyUrlForNullFileName() {
        givenPresignedUrlResponse(null, EMPTY_URL);

        String result = whenObtainingPresignedUrl(null);

        thenPresignedUrlEquals(result, EMPTY_URL);
        thenPortRequestedUrlFor(null);
    }

    private void givenPresignedUrlResponse(String fileName, String url) {
        Mockito.when(awsS3Port.obtainFilePresignedUrl(fileName)).thenReturn(url);
    }

    private String whenObtainingPresignedUrl(String fileName) {
        return useCase.execute(fileName);
    }

    private void thenPresignedUrlEquals(String actual, String expected) {
        assertEquals(expected, actual);
    }

    private void thenPortRequestedUrlFor(String fileName) {
        Mockito.verify(awsS3Port, Mockito.times(1)).obtainFilePresignedUrl(fileName);
    }
}
