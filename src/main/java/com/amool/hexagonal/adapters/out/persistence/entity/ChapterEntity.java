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

    @OneToMany(mappedBy = "chapterEntity", cascade = CascadeType.ALL)
    private Set<VersionEntity> versions = new HashSet<>();

    @ManyToMany(mappedBy = "acquiredChapterEntities")
    private Set<UserEntity> usersWhoAcquired = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public WorkEntity getWorkEntity() { return workEntity; }
    public void setWorkEntity(WorkEntity workEntity) { this.workEntity = workEntity; }

    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public LanguageEntity getLanguageEntity() { return languageEntity; }
    public void setLanguageEntity(LanguageEntity languageEntity) { this.languageEntity = languageEntity; }

    public Set<UserEntity> getUsersWhoAcquired() { return usersWhoAcquired; }
    public void setUsersWhoAcquired(Set<UserEntity> usersWhoAcquired) { this.usersWhoAcquired = usersWhoAcquired; }

    public Set<VersionEntity> getVersions() { return versions; }
    public void setVersions(Set<VersionEntity> versions) { this.versions = versions; }
}
