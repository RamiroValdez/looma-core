package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

import java.util.Optional;

public class GetUserById {

    private final LoadUserPort loadUserPort;
    private final FilesStoragePort filesStoragePort;

    public GetUserById(LoadUserPort loadUserPort, FilesStoragePort filesStoragePort) {
        this.loadUserPort = loadUserPort;
        this.filesStoragePort = filesStoragePort;
    }

    public Optional<User> execute(Long userId) {
        Optional<User> user = loadUserPort.getById(userId);

        if(user.isPresent()) {
            String photoUrl = filesStoragePort.obtainPublicUrl(user.get().getPhoto());
            user.get().setPhoto(photoUrl);
            return user;
        } else {
            return Optional.empty();
        }
    }
}
