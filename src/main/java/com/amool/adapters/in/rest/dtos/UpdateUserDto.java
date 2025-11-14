package com.amool.adapters.in.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserDto {

    private Long id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String photo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }


}
