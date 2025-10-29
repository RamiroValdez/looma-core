package com.amool.hexagonal.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "suscribe_chapter")
@IdClass(SuscribeChapterEntity.PK.class)
public class SuscribeChapterEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Id
    @Column(name = "chapter_id")
    private Long chapterId;

    public SuscribeChapterEntity() {}
    public SuscribeChapterEntity(Long userId, Long workId, Long chapterId) {
        this.userId = userId; this.workId = workId; this.chapterId = chapterId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }

    public static class PK implements Serializable {
        private Long userId; private Long workId; private Long chapterId;
        public PK() {}
        public PK(Long userId, Long workId, Long chapterId) { this.userId=userId; this.workId=workId; this.chapterId=chapterId; }
        @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof PK pk)) return false; return Objects.equals(userId, pk.userId)&&Objects.equals(workId, pk.workId)&&Objects.equals(chapterId, pk.chapterId);}    
        @Override public int hashCode(){ return Objects.hash(userId, workId, chapterId);}    
    }
}
