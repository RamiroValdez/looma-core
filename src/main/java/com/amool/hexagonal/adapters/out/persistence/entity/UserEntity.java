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

    public Set<WorkEntity> getSavedPieces() {
        return savedWorks;
    }

    public void setSavedPieces(Set<WorkEntity> savedWorkEntities) {
        this.savedWorks = savedWorkEntities;
    }

    // Usuario adquiere capítulos
    @ManyToMany
    @JoinTable(name = "user_chapter", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chapter_id"))
    private Set<ChapterEntity> acquiredChapterEntities = new HashSet<>();

    public Set<ChapterEntity> getAcquiredChapters() {
        return acquiredChapterEntities;
    }

    public void setAcquiredChapters(Set<ChapterEntity> acquiredChapterEntities) {
        this.acquiredChapterEntities = acquiredChapterEntities;
    }

    // Suscripción a autores
    @ManyToMany
    @JoinTable(name = "suscribe_autor", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private Set<UserEntity> subscribedAuthors = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedAuthors")
    private Set<UserEntity> subscribers = new HashSet<>();

    public Set<UserEntity> getSubscribedAuthors() {
        return subscribedAuthors;
    }

    public void setSubscribedAuthors(Set<UserEntity> subscribedAuthors) {
        this.subscribedAuthors = subscribedAuthors;
    }

    public Set<UserEntity> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<UserEntity> subscribers) {
        this.subscribers = subscribers;
    }

    // Suscripción a obras
    @ManyToMany
    @JoinTable(name = "suscribe_work", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    private Set<WorkEntity> subscribedWorks = new HashSet<>();

    public Set<WorkEntity> getSubscribedPieces() {
        return subscribedWorks;
    }

    public void setSubscribedPieces(Set<WorkEntity> subscribedWorkEntities) {
        this.subscribedWorks = subscribedWorkEntities;
    }

    // Usuario prefiere idiomas
    @ManyToMany
    @JoinTable(name = "preferred_language", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<LanguageEntity> preferredLanguageEntities = new HashSet<>();

    public Set<LanguageEntity> getPreferredLanguages() {
        return preferredLanguageEntities;
    }

    public void setPreferredLanguages(Set<LanguageEntity> preferredLanguageEntities) {
        this.preferredLanguageEntities = preferredLanguageEntities;
    }

    // Usuario prefiere categorías
    @ManyToMany
    @JoinTable(name = "preferred_category", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> preferredCategories = new HashSet<>();

    public Set<CategoryEntity> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(Set<CategoryEntity> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    // Getters & setters

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
