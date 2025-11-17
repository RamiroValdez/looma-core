package com.amool.application.usecases;

import com.amool.application.port.out.UserAccountPort;

public class VerifyRegistrationUseCase {

    private final UserAccountPort userAccountPort;

    public VerifyRegistrationUseCase(UserAccountPort userAccountPort) {
        this.userAccountPort = userAccountPort;
    }

    public Long execute(String email, String code) {
        return userAccountPort.enableUserIfCodeValid(email, code);
    }
}

