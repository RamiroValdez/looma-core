package com.amool.hexagonal.adapters.in.rest.dtos;

public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String name;
    private String surname;
    private String username;

    public AuthResponse(String token, Long userId, String email, String name, String surname, String username) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.username = username;
    }

    // Getters
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getUsername() { return username; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setUsername(String username) { this.username = username; }
}
