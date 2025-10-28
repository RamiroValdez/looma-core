
package com.amool.adapters.out.persistence.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "work")
public class WorkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 2048)
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cover", length = 2048)
    private String cover;

    @Column(name = "banner", length = 2048)
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
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;

    @ManyToOne
    @JoinColumn(name = "format_id", nullable = false)
    private FormatEntity formatEntity;

    @ManyToOne
    @JoinColumn(name = "original_language_id", nullable = false)
    private LanguageEntity originalLanguageEntity;


    @OneToMany(mappedBy = "workEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterEntity> chapters = new ArrayList<>();

    @ManyToMany(mappedBy = "savedWorks")
    private Set<UserEntity> usersWhoSaved = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "work_category", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedWorks")
    private Set<UserEntity> subscribers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "work_tag",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public UserEntity getCreator() { return creator; }
    public void setCreator(UserEntity creator) { this.creator = creator; }

    public FormatEntity getFormatEntity() { return formatEntity; }
    public void setFormatEntity(FormatEntity formatEntity) { this.formatEntity = formatEntity; }

    public LanguageEntity getOriginalLanguageEntity() { return originalLanguageEntity; }
    public void setOriginalLanguageEntity(LanguageEntity originalLanguageEntity) { this.originalLanguageEntity = originalLanguageEntity; }

    public Set<UserEntity> getUsersWhoSaved() { return usersWhoSaved; }
    public void setUsersWhoSaved(Set<UserEntity> usersWhoSaved) { this.usersWhoSaved = usersWhoSaved; }

    public Set<CategoryEntity> getCategories() { return categories; }
    public void setCategories(Set<CategoryEntity> categories) { this.categories = categories; }
    public Set<UserEntity> getSubscribers() { return subscribers; }
    public void setSubscribers(Set<UserEntity> subscribers) { this.subscribers = subscribers; }

    public List<ChapterEntity> getChapters() { return chapters; }
    public void setChapters(List<ChapterEntity> chapters) { this.chapters = chapters; }

    public Set<TagEntity> getTags() { return tags; }
    public void setTags(Set<TagEntity> tags) { this.tags = tags; }
}

