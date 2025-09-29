<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/ChapterEntity.java
package com.amool.hexagonal.adapters.out.persistence.entity;
========
package com.amool.hexagonal.adapters.out.persistence;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Chapter.java

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

<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/ChapterEntity.java
    @Column(name = "last_update", nullable = false)
========
    @Column(name = "last_modified", nullable = false)
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Chapter.java
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/ChapterEntity.java
    private WorkEntity workEntity;
========
    private Work work;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Chapter.java

    @Column(name = "likes", nullable = false)
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/ChapterEntity.java
    private LanguageEntity languageEntity;
========
    private Language language;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Chapter.java

    // Relations

    // Usuario adquiere capítulos
    @ManyToMany(mappedBy = "acquiredChapterEntities")
    private Set<UserEntity> usersWhoAcquired = new HashSet<>();

    public Set<UserEntity> getUsersWhoAcquired() {
        return usersWhoAcquired;
    }

    public void setUsersWhoAcquired(Set<UserEntity> usersWhoAcquired) {
        this.usersWhoAcquired = usersWhoAcquired;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/ChapterEntity.java
     public WorkEntity getPiece() {
        return workEntity;
    }

    public void setPiece(WorkEntity workEntity) {
        this.workEntity = workEntity;
========
     public Work getPiece() {
        return piece;
    }

    public void setPiece(Work piece) {
        this.piece = piece;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Chapter.java
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public LanguageEntity getLanguage() {
        return languageEntity;
    }

    public void setLanguage(LanguageEntity languageEntity) {
        this.languageEntity = languageEntity;
    }
}
