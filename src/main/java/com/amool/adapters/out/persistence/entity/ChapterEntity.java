package com.amool.adapters.out.persistence.entity;
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

    @Column(name = "title")
    private String title = "";

    @Column(name = "price")  
    private Double price = 0.0;

    @Column(name = "last_update") 
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity workEntity;

    @Column(name = "likes", nullable = false)
    private Long likes = 0L;  

    @Column(name = "allow_ai_translation", nullable = false)
    private Boolean allowAiTranslation = true; 

    @ManyToOne
    @JoinColumn(name = "language_id") 
    private LanguageEntity languageEntity;

    @Column(name = "publication_status")  
    private String publicationStatus = "DRAFT"; 

    @Column(name = "scheduled_publication_date")
    private LocalDateTime scheduledPublicationDate;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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

    public Boolean getAllowAiTranslation() { return allowAiTranslation; }
    public void setAllowAiTranslation(Boolean allowAiTranslation) { this.allowAiTranslation = allowAiTranslation; }

    public LanguageEntity getLanguageEntity() { return languageEntity; }
    public void setLanguageEntity(LanguageEntity languageEntity) { this.languageEntity = languageEntity; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public LocalDateTime getScheduledPublicationDate() { return scheduledPublicationDate; }
    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) { this.scheduledPublicationDate = scheduledPublicationDate; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public Set<UserEntity> getUsersWhoAcquired() { return usersWhoAcquired; }
    public void setUsersWhoAcquired(Set<UserEntity> usersWhoAcquired) { this.usersWhoAcquired = usersWhoAcquired; }

    public Set<VersionEntity> getVersions() { return versions; }
    public void setVersions(Set<VersionEntity> versions) { this.versions = versions; }
}
