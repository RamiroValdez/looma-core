package com.amool.hexagonal.security;

public class JwtUserPrincipal {
    private final Long userId;
    private final String email;
    private final String name;
    private final String surname;
    private final String username;

    public JwtUserPrincipal(Long userId, String email, String name, String surname, String username) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.username = username;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getUsername() { return username; }
}
