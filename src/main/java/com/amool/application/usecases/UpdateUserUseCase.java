package com.amool.application.usecases;

import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

public class UpdateUserUseCase {

    private final LoadUserPort loadUserPort;

    public UpdateUserUseCase(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    public boolean execute(User user, String newPassword) {
        try {
            boolean result = loadUserPort.updateUser(user, newPassword);
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
}
