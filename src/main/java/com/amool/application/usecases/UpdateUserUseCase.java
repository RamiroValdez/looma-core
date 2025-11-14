package com.amool.application.usecases;

import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCase {

    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(LoadUserPort loadUserPort, PasswordEncoder passwordEncoder) {
        this.loadUserPort = loadUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean execute(User user, String newPassword) {
        try {
            String encodedPassword = null;
            if (newPassword != null && !newPassword.isBlank()) {
                encodedPassword = passwordEncoder.encode(newPassword);
            }
            boolean result = loadUserPort.updateUser(user, encodedPassword);
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
}
