package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class ObtainObjectsInPath {

    private final FilesStoragePort filesStoragePort;

    public ObtainObjectsInPath(FilesStoragePort filesStoragePort) {
        this.filesStoragePort = filesStoragePort;
    }

    public List<S3Object> execute(String path) {
        return this.filesStoragePort.obtainObjectsInPath(path);
    }

}
