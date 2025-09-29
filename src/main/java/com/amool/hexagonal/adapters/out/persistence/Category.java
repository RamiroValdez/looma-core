package com.amool.hexagonal.adapters.out.persistence;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Relations

    // Obra tiene categorías
    @ManyToMany(mappedBy = "categories")
    private Set<Piece> pieces = new HashSet<>();

    public Set<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(Set<Piece> pieces) {
        this.pieces = pieces;
    }

    // Formato compone categorías
    @ManyToMany(mappedBy = "categories")
    private Set<Format> formats = new HashSet<>();

    public Set<Format> getFormats() {
        return formats;
    }

    public void setFormats(Set<Format> formats) {
        this.formats = formats;
    }

    // Usuario prefiere categorías
    @ManyToMany(mappedBy = "preferredCategories")
    private Set<User> usersWhoPrefer = new HashSet<>();

    public Set<User> getUsersWhoPrefer() {
        return usersWhoPrefer;
    }

    public void setUsersWhoPrefer(Set<User> usersWhoPrefer) {
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

