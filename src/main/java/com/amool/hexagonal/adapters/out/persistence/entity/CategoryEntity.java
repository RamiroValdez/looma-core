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


}
