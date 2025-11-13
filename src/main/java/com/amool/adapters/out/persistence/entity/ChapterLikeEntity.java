package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "chapter_likes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chapter_id"}))
public class ChapterLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private ChapterEntity chapter;

    private LocalDateTime likedAt;
    
    public ChapterLikeEntity() {}
    
    public ChapterLikeEntity(UserEntity user, ChapterEntity chapter) {
        this.user = user;
        this.chapter = chapter;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setChapter(ChapterEntity chapter) {
        this.chapter = chapter;
    }
    public Long getId() { 
        return id; 
    }
    public UserEntity getUser() { 
        return user; 
    }
    public ChapterEntity getChapter() { 
        return chapter; 
    }
    public LocalDateTime getLikedAt() { 
        return likedAt; 
    }
    public void setLikedAt(LocalDateTime likedAt) { 
        this.likedAt = likedAt; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChapterLikeEntity chapterLike = (ChapterLikeEntity) o;
        return Objects.equals(user.getId(), chapterLike.user.getId()) &&
               Objects.equals(chapter.getId(), chapterLike.chapter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), chapter.getId());
    }
}
