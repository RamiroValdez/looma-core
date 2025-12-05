package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;

public class ObtainPresignedUrl {

    private final FilesStoragePort filesStoragePort;

    public ObtainPresignedUrl(FilesStoragePort filesStoragePort) {
        this.filesStoragePort = filesStoragePort;
    }

    public String execute(String fileName) {
        return this.filesStoragePort.obtainFilePresignedUrl(fileName);
    }

}
