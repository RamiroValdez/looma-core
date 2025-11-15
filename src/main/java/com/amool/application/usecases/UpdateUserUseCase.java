package com.amool.application.usecases;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.service.ImagesService;
import com.amool.domain.model.User;

public class UpdateUserUseCase {

    private final LoadUserPort loadUserPort;
    private final ImagesService imagesService;

    public UpdateUserUseCase(LoadUserPort loadUserPort, ImagesService imagesService) {
        this.imagesService = imagesService;
        this.loadUserPort = loadUserPort;
    }

    public boolean execute(User user, String newPassword) {
        try {
            user.setPhoto(imagesService.uploadUserImage(user.getMultipartFile(), user.getId().toString()));
            boolean result = loadUserPort.updateUser(user, newPassword);
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
}
