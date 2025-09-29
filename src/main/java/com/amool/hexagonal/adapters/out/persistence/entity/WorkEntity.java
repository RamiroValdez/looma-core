package com.amool.hexagonal.adapters.out.persistence.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "work")
public class WorkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cover")
    private String cover;

    @Column(name = "banner")
    private String banner;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "likes", nullable = false)
    private Integer likes;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private UserEntity creator;

    @ManyToOne
    @JoinColumn(name = "format_id", nullable = false)
    private FormatEntity formatEntity;

    // Relations

    // Usuario guarda obras
    @ManyToMany(mappedBy = "savedWorks")
    private Set<UserEntity> usersWhoSaved = new HashSet<>();

    // Obra tiene categorías
    @ManyToMany
    @JoinTable(name = "work_category", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories = new HashSet<>();

    // Suscripción a obras
    @ManyToMany(mappedBy = "subscribedWorks")
    private Set<UserEntity> subscribers = new HashSet<>();
}
