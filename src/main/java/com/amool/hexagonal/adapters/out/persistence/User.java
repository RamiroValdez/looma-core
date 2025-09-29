package com.amool.hexagonal.adapters.out.persistence;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

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
    @JoinTable(name = "user_saved_piece", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "piece_id"))
    private Set<Piece> savedPieces = new HashSet<>();

    public Set<Piece> getSavedPieces() {
        return savedPieces;
    }

    public void setSavedPieces(Set<Piece> savedPieces) {
        this.savedPieces = savedPieces;
    }

    // Usuario adquiere capítulos
    @ManyToMany
    @JoinTable(name = "user_acquired_chapter", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chapter_id"))
    private Set<Chapter> acquiredChapters = new HashSet<>();

    public Set<Chapter> getAcquiredChapters() {
        return acquiredChapters;
    }

    public void setAcquiredChapters(Set<Chapter> acquiredChapters) {
        this.acquiredChapters = acquiredChapters;
    }

    // Suscripción a autores
    @ManyToMany
    @JoinTable(name = "user_subscribed_author", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<User> subscribedAuthors = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedAuthors")
    private Set<User> subscribers = new HashSet<>();

    public Set<User> getSubscribedAuthors() {
        return subscribedAuthors;
    }

    public void setSubscribedAuthors(Set<User> subscribedAuthors) {
        this.subscribedAuthors = subscribedAuthors;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    // Suscripción a obras
    @ManyToMany
    @JoinTable(name = "suscription_piece", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "piece_id"))
    private Set<Piece> subscribedPieces = new HashSet<>();

    public Set<Piece> getSubscribedPieces() {
        return subscribedPieces;
    }

    public void setSubscribedPieces(Set<Piece> subscribedPieces) {
        this.subscribedPieces = subscribedPieces;
    }

    // Usuario prefiere idiomas
    @ManyToMany
    @JoinTable(name = "preference_language", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> preferredLanguages = new HashSet<>();

    public Set<Language> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(Set<Language> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }

    // Usuario prefiere categorías
    @ManyToMany
    @JoinTable(name = "preference_category", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> preferredCategories = new HashSet<>();

    public Set<Category> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(Set<Category> preferredCategories) {
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
