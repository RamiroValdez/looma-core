package com.amool.hexagonal.adapters.out.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "language")
public class LanguageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "preferredLanguageEntities")
    private Set<UserEntity> users = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<UserEntity> getUsers() { return users; }
    public void setUsers(Set<UserEntity> users) { this.users = users; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
