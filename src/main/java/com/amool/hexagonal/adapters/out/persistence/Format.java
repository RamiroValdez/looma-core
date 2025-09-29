package com.amool.hexagonal.adapters.out.persistence;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "format")
public class Format {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Relations

    // Categoría compone formatos
    @ManyToMany
    @JoinTable(name = "comp_formato", joinColumns = @JoinColumn(name = "formato_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Category> categories = new HashSet<>();

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
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
