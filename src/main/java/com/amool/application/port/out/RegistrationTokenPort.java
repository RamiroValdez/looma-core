package com.amool.application.port.out;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RegistrationTokenPort {
    void upsert(String email, String code, LocalDateTime expiresAt,
                String name, String surname, String username, String passwordHash);
    Optional<TokenRecord> findByEmail(String email);
    void deleteByEmail(String email);

    class TokenRecord {
        public final String email;
        public final String code;
        public final LocalDateTime expiresAt;
        public final String name;
        public final String surname;
        public final String username;
        public final String passwordHash;
        public TokenRecord(String email, String code, LocalDateTime expiresAt,
                           String name, String surname, String username, String passwordHash) {
            this.email = email; this.code = code; this.expiresAt = expiresAt;
            this.name = name; this.surname = surname; this.username = username; this.passwordHash = passwordHash;
        }
    }
}
