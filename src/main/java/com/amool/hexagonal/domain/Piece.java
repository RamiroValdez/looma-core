package com.amool.hexagonal.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "piece")
public class Piece {

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

    @Column(name = "fecha_creacion")
    private LocalDate publicationDate;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "formato_id", nullable = false)
    private Format format;

    // Relations

    // Usuario guarda obras
    @ManyToMany(mappedBy = "savedPieces")
    private Set<User> usersWhoSaved = new HashSet<>();

    public Set<User> getUsersWhoSaved() {
        return usersWhoSaved;
    }

    public void setUsersWhoSaved(Set<User> usersWhoSaved) {
        this.usersWhoSaved = usersWhoSaved;
    }

    // Obra tiene categorías
    @ManyToMany
    @JoinTable(name = "obra_categoria", joinColumns = @JoinColumn(name = "obra_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Category> categories = new HashSet<>();

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    // Suscripción a obras
    @ManyToMany(mappedBy = "subscribedPieces")
    private Set<User> subscribers = new HashSet<>();

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
}
