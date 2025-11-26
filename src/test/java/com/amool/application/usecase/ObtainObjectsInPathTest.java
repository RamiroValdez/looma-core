package com.amool.application.usecase;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.usecases.ObtainObjectsInPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObtainObjectsInPathTest {

    private FilesStoragePort filesStoragePort;
    private ObtainObjectsInPath useCase;
    
    private static final String TEST_PATH = "test/path/";
    private static final String KEY_1 = "test/path/file1.txt";
    private static final String KEY_2 = "test/path/file2.jpg";
    private static final String EMPTY_PATH = "";

    @BeforeEach
    public void setUp() {
        filesStoragePort = Mockito.mock(FilesStoragePort.class);
        useCase = new ObtainObjectsInPath(filesStoragePort);
    }

    @Test
    public void shouldReturnObjectsWhenPathContainsFiles() {
        List<S3Object> expectedObjects = givenObjectsInPath(TEST_PATH,
            createS3Object(KEY_1, 1024L),
            createS3Object(KEY_2, 2048L)
        );

        List<S3Object> result = whenObtainingObjects(TEST_PATH);

        thenObjectsMatch(result, expectedObjects);
    }

    @Test
    public void shouldReturnEmptyListWhenPathIsBlank() {
        givenEmptyPath(EMPTY_PATH);

        List<S3Object> result = whenObtainingObjects(EMPTY_PATH);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListWhenPathHasNoObjects() {
        givenEmptyPath(TEST_PATH);

        List<S3Object> result = whenObtainingObjects(TEST_PATH);

        assertTrue(result.isEmpty());
    }

    private List<S3Object> givenObjectsInPath(String path, S3Object... objects) {
        List<S3Object> objectList = Arrays.asList(objects);
        when(filesStoragePort.obtainObjectsInPath(path)).thenReturn(objectList);
        return objectList;
    }

    private void givenEmptyPath(String path) {
        when(filesStoragePort.obtainObjectsInPath(path)).thenReturn(Collections.emptyList());
    }

    private List<S3Object> whenObtainingObjects(String path) {
        return useCase.execute(path);
    }

    private void thenObjectsMatch(List<S3Object> actual, List<S3Object> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).key(), actual.get(i).key());
            assertEquals(expected.get(i).size(), actual.get(i).size());
        }
    }

    private S3Object createS3Object(String key, long size) {
        return S3Object.builder().key(key).size(size).build();
    }
}
