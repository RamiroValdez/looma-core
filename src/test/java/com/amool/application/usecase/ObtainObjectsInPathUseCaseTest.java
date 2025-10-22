package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.usecases.ObtainObjectsInPathUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObtainObjectsInPathUseCaseTest {

    private AwsS3Port awsS3Port;
    private ObtainObjectsInPathUseCase useCase;
    
    private static final String TEST_PATH = "test/path/";
    private static final String KEY_1 = "test/path/file1.txt";
    private static final String KEY_2 = "test/path/file2.jpg";

    @BeforeEach
    public void setUp() {
        awsS3Port = Mockito.mock(AwsS3Port.class);
        useCase = new ObtainObjectsInPathUseCase(awsS3Port);
    }

    @Test
    public void when_PathContainsObjects_ThenReturnObjectsList() {
        S3Object object1 = S3Object.builder().key(KEY_1).size(1024L).build();
        S3Object object2 = S3Object.builder().key(KEY_2).size(2048L).build();
        List<S3Object> expectedObjects = Arrays.asList(object1, object2);
        
        when(awsS3Port.obtainObjectsInPath(TEST_PATH)).thenReturn(expectedObjects);

        List<S3Object> result = useCase.execute(TEST_PATH);

        assertEquals(2, result.size());
        assertEquals(KEY_1, result.get(0).key());
        assertEquals(1024L, result.get(0).size());
        assertEquals(KEY_2, result.get(1).key());
        assertEquals(2048L, result.get(1).size());
    }

    @Test
    public void when_PathIsEmpty_ThenReturnEmptyList() {
        when(awsS3Port.obtainObjectsInPath("")).thenReturn(Collections.emptyList());

        List<S3Object> result = useCase.execute("");

        assertTrue(result.isEmpty());
    }

    @Test
    public void when_NoObjectsInPath_ThenReturnEmptyList() {
        when(awsS3Port.obtainObjectsInPath(TEST_PATH)).thenReturn(Collections.emptyList());

        List<S3Object> result = useCase.execute(TEST_PATH);

        assertTrue(result.isEmpty());
    }
}
