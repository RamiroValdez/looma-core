package com.amool.application.port.out;

import java.time.LocalDateTime;

public interface UserAccountPort {
    boolean emailExists(String email);
    boolean usernameExists(String username);

    void upsertPendingUser(String name, String surname, String username, String email,
                           String passwordHash, String verificationCode, LocalDateTime expiresAt);

    Long enableUserIfCodeValid(String email, String code);
}
