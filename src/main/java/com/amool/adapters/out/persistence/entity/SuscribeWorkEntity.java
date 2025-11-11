package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suscribe_work")
@IdClass(SuscribeWorkEntity.PK.class)
@Getter
@Setter
public class SuscribeWorkEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "notified", nullable = false)
    private boolean notified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", insertable = false, updatable = false)
    private WorkEntity work;

    public SuscribeWorkEntity() {}
    
    public SuscribeWorkEntity(Long userId, Long workId) {
        this.userId = userId;
        this.workId = workId;
        this.notified = false;
    }
    
    public boolean isNotified() {
        return notified;
    }
    
    public void markAsNotified() {
        this.notified = true;
    }

    public static class PK implements java.io.Serializable {
        private Long userId;
        private Long workId;
        
        public PK() {}
        
        public PK(Long userId, Long workId) {
            this.userId = userId;
            this.workId = workId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return userId.equals(pk.userId) && workId.equals(pk.workId);
        }
        
        @Override
        public int hashCode() {
            return 31 * userId.hashCode() + workId.hashCode();
        }
    }
}
