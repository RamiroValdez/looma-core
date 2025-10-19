package com.amool.adapters.out.persistence.entity;

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


    @ManyToMany(mappedBy = "categories")
    private Set<WorkEntity> workEntities = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    private Set<FormatEntity> formatEntities = new HashSet<>();

    @ManyToMany(mappedBy = "preferredCategories")
    private Set<UserEntity> usersWhoPrefer = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<WorkEntity> getWorkEntities() { return workEntities; }
    public void setWorkEntities(Set<WorkEntity> workEntities) { this.workEntities = workEntities; }

    public Set<FormatEntity> getFormatEntities() { return formatEntities; }
    public void setFormatEntities(Set<FormatEntity> formatEntities) { this.formatEntities = formatEntities; }

    public Set<UserEntity> getUsersWhoPrefer() { return usersWhoPrefer; }
    public void setUsersWhoPrefer(Set<UserEntity> usersWhoPrefer) { this.usersWhoPrefer = usersWhoPrefer; }
}

