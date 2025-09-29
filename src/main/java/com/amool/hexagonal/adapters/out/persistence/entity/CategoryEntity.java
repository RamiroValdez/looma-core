package com.amool.hexagonal.adapters.out.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Relations

    // Obra tiene categorías
    @ManyToMany(mappedBy = "categories")
    private Set<WorkEntity> workEntities = new HashSet<>();

    // Formato compone categorías
    @ManyToMany(mappedBy = "categories")
    private Set<FormatEntity> formatEntities = new HashSet<>();

    // Usuario prefiere categorías
    @ManyToMany(mappedBy = "preferredCategories")
    private Set<UserEntity> usersWhoPrefer = new HashSet<>();

    public Set<UserEntity> getUsersWhoPrefer() {
        return usersWhoPrefer;
    }

    public void setUsersWhoPrefer(Set<UserEntity> usersWhoPrefer) {
        this.usersWhoPrefer = usersWhoPrefer;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
