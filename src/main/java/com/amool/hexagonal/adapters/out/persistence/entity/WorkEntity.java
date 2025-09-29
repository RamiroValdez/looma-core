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

    public Set<UserEntity> getUsersWhoSaved() {
        return usersWhoSaved;
    }

    public void setUsersWhoSaved(Set<UserEntity> usersWhoSaved) {
        this.usersWhoSaved = usersWhoSaved;
    }

    // Obra tiene categorías
    @ManyToMany
    @JoinTable(name = "work_category", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories = new HashSet<>();

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

    // Suscripción a obras
    @ManyToMany(mappedBy = "subscribedWorks")
    private Set<UserEntity> subscribers = new HashSet<>();

    public Set<UserEntity> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<UserEntity> subscribers) {
        this.subscribers = subscribers;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public FormatEntity getFormat() {
        return formatEntity;
    }

    public void setFormat(FormatEntity formatEntity) {
        this.formatEntity = formatEntity;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
}
