package com.amool.application.usecases;

import java.util.Optional;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

public class GetUserPhoto {

    private final FilesStoragePort filesStoragePort;
    private final LoadUserPort loadUserPort;

    public GetUserPhoto(FilesStoragePort filesStoragePort, LoadUserPort loadUserPort) {
        this.filesStoragePort = filesStoragePort;
        this.loadUserPort = loadUserPort;
    }

    public String execute(Long userId) {
        Optional<User> user = loadUserPort.getById(userId);
        return filesStoragePort.obtainPublicUrl(user.get().getPhoto());
    }
    
}
