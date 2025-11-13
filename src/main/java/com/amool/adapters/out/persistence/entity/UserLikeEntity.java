package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "likes_work", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "work_id"})
})
public class UserLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity work;

    private LocalDateTime likedAt;

    protected UserLikeEntity() {}

    public UserLikeEntity(UserEntity user, WorkEntity work) {
        this.user = user;
        this.work = work;
    }

    public void setId(Long id) { this.id = id; }
    public void setUser(UserEntity user) { this.user = user; }
    public void setWork(WorkEntity work) { this.work = work; }
    public void setLikedAt(LocalDateTime likedAt) { this.likedAt = likedAt; }
    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public WorkEntity getWork() { return work; }
    public LocalDateTime getLikedAt() { return likedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLikeEntity userLike = (UserLikeEntity) o;
        return Objects.equals(user.getId(), userLike.user.getId()) &&
               Objects.equals(work.getId(), userLike.work.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), work.getId());
    }
}
