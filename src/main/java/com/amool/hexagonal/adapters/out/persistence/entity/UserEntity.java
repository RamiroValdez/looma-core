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


    @ManyToMany
    @JoinTable(name = "worksaved_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    private Set<WorkEntity> savedWorks = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_chapter", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chapter_id"))
    private Set<ChapterEntity> acquiredChapterEntities = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "suscribe_autor", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private Set<UserEntity> subscribedAuthors = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedAuthors")
    private Set<UserEntity> subscribers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "suscribe_work", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "work_id"))
    private Set<WorkEntity> subscribedWorks = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "preferred_language", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<LanguageEntity> preferredLanguageEntities = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "preferred_category", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> preferredCategories = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public Set<WorkEntity> getSavedWorks() { return savedWorks; }
    public void setSavedWorks(Set<WorkEntity> savedWorks) { this.savedWorks = savedWorks; }

    public Set<ChapterEntity> getAcquiredChapterEntities() { return acquiredChapterEntities; }
    public void setAcquiredChapterEntities(Set<ChapterEntity> acquiredChapterEntities) { this.acquiredChapterEntities = acquiredChapterEntities; }

    public Set<UserEntity> getSubscribedAuthors() { return subscribedAuthors; }
    public void setSubscribedAuthors(Set<UserEntity> subscribedAuthors) { this.subscribedAuthors = subscribedAuthors; }

    public Set<UserEntity> getSubscribers() { return subscribers; }
    public void setSubscribers(Set<UserEntity> subscribers) { this.subscribers = subscribers; }

    public Set<WorkEntity> getSubscribedWorks() { return subscribedWorks; }
    public void setSubscribedWorks(Set<WorkEntity> subscribedWorks) { this.subscribedWorks = subscribedWorks; }

    public Set<LanguageEntity> getPreferredLanguageEntities() { return preferredLanguageEntities; }
    public void setPreferredLanguageEntities(Set<LanguageEntity> preferredLanguageEntities) { this.preferredLanguageEntities = preferredLanguageEntities; }

    public Set<CategoryEntity> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(Set<CategoryEntity> preferredCategories) { this.preferredCategories = preferredCategories; }
}
