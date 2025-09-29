package com.amool.hexagonal.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "chapter")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Piece piece;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "idioma_id", nullable = false)
    private Language language;

    // Relations

    // Usuario adquiere capítulos
    @ManyToMany(mappedBy = "acquiredChapters")
    private Set<User> usersWhoAcquired = new HashSet<>();

    public Set<User> getUsersWhoAcquired() {
        return usersWhoAcquired;
    }

    public void setUsersWhoAcquired(Set<User> usersWhoAcquired) {
        this.usersWhoAcquired = usersWhoAcquired;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

     public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
