package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suscribe_autor")
@IdClass(SuscribeAutorEntity.PK.class)
@Getter
@Setter
public class SuscribeAutorEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "notified", nullable = false)
    private boolean notified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", insertable = false, updatable = false)
    private UserEntity autor;

    public SuscribeAutorEntity() {}
    
    public SuscribeAutorEntity(Long userId, Long autorId) {
        this.userId = userId;
        this.autorId = autorId;
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
        private Long autorId;
        
        public PK() {}
        
        public PK(Long userId, Long autorId) {
            this.userId = userId;
            this.autorId = autorId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return userId.equals(pk.userId) && autorId.equals(pk.autorId);
        }
        
        @Override
        public int hashCode() {
            return 31 * userId.hashCode() + autorId.hashCode();
        }
    }
}
