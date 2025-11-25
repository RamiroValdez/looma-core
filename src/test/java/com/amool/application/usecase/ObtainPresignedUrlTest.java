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

    @BeforeEach
    public void setUp() {
        awsS3Port = Mockito.mock(AwsS3Port.class);
        useCase = new ObtainPresignedUrl(awsS3Port);
    }

    @Test
    public void when_ValidFileName_ThenReturnPresignedUrl() {
        String fileName = "test-file.txt";
        String expectedUrl = "https://example.com/presigned-url";
        
        Mockito.when(awsS3Port.obtainFilePresignedUrl(anyString()))
               .thenReturn(expectedUrl);

        String result = useCase.execute(fileName);

        assertEquals(expectedUrl, result);
        Mockito.verify(awsS3Port, Mockito.times(1)).obtainFilePresignedUrl(fileName);
    }

    @Test
    public void when_EmptyFileName_ThenReturnEmptyUrl() {
        String fileName = "";
        String expectedUrl = "";
        
        Mockito.when(awsS3Port.obtainFilePresignedUrl(anyString()))
               .thenReturn(expectedUrl);

        String result = useCase.execute(fileName);

        assertEquals(expectedUrl, result);
        Mockito.verify(awsS3Port, Mockito.times(1)).obtainFilePresignedUrl(fileName);
    }

    @Test
    public void when_NullFileName_ThenReturnEmptyUrl() {
        String expectedUrl = "";
        
        Mockito.when(awsS3Port.obtainFilePresignedUrl(null))
               .thenReturn(expectedUrl);

        String result = useCase.execute(null);

        assertEquals(expectedUrl, result);
        Mockito.verify(awsS3Port, Mockito.times(1)).obtainFilePresignedUrl(null);
    }
}
