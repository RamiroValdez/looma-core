package com.amool.hexagonal.adapters.out.persistence.entity;
package com.amool.hexagonal.adapters.out.persistence;

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

    private Set<WorkEntity> works = new HashSet<>();

    public Set<WorkEntity> getWorks() {
        return works;
    }

    public void setWorks(Set<WorkEntity> works) {
        this.works = works;
    }

    // Formato compone categorías
    @ManyToMany(mappedBy = "categories")
    private Set<FormatEntity> formatEntities = new HashSet<>();

    // Usuario prefiere categorías
    @ManyToMany(mappedBy = "preferredCategories")
    private Set<UserEntity> usersWhoPrefer = new HashSet<>();


}

