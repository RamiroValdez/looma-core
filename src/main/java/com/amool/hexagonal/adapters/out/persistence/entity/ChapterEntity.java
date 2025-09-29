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

    // Relations

    // Usuario adquiere capítulos
    @ManyToMany(mappedBy = "acquiredChapterEntities")
    private Set<UserEntity> usersWhoAcquired = new HashSet<>();


}
