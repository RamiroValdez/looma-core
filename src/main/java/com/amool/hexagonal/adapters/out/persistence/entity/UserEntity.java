package com.amool.hexagonal.adapters.out.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "photo")
    private String photo;

    // Relations

    // Usuario guarda obras
    @ManyToMany
    @JoinTable(name = "worksaved_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    private Set<WorkEntity> savedWorks = new HashSet<>();

    // Usuario adquiere capítulos
    @ManyToMany
    @JoinTable(name = "user_chapter", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chapter_id"))
    private Set<ChapterEntity> acquiredChapterEntities = new HashSet<>();

    // Suscripción a autores
    @ManyToMany
    @JoinTable(name = "suscribe_autor", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private Set<UserEntity> subscribedAuthors = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedAuthors")
    private Set<UserEntity> subscribers = new HashSet<>();

    // Suscripción a obras
    @ManyToMany
    @JoinTable(name = "suscribe_work", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    private Set<WorkEntity> subscribedWorks = new HashSet<>();

    // Usuario prefiere idiomas
    @ManyToMany
    @JoinTable(name = "preferred_language", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<LanguageEntity> preferredLanguageEntities = new HashSet<>();

    // Usuario prefiere categorías
    @ManyToMany
    @JoinTable(name = "preferred_category", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> preferredCategories = new HashSet<>();
}
