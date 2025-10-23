package com.amool.domain.model;

import java.util.HashSet;
import java.util.Set;

public class User {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String photo;
    private Set<Work> savedWorks = new HashSet<>();

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
    
    public Set<Work> getSavedWorks() { return savedWorks; }
    public void setSavedWorks(Set<Work> savedWorks) { this.savedWorks = savedWorks; }
    public void addSavedWork(Work work) { this.savedWorks.add(work); }
    public void removeSavedWork(Work work) { this.savedWorks.remove(work); }
}
