package com.amool.hexagonal.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "chapter")
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity workEntity;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private LanguageEntity languageEntity;

    // Relations

    // Usuario adquiere capítulos
    @ManyToMany(mappedBy = "acquiredChapterEntities")
    private Set<UserEntity> usersWhoAcquired = new HashSet<>();

    public Set<UserEntity> getUsersWhoAcquired() {
        return usersWhoAcquired;
    }

    public void setUsersWhoAcquired(Set<UserEntity> usersWhoAcquired) {
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

     public WorkEntity getPiece() {
        return workEntity;
    }

    public void setPiece(WorkEntity workEntity) {
        this.workEntity = workEntity;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public LanguageEntity getLanguage() {
        return languageEntity;
    }

    public void setLanguage(LanguageEntity languageEntity) {
        this.languageEntity = languageEntity;
    }
}
