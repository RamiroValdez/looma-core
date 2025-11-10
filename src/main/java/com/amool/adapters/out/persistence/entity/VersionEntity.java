package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "version")
public class VersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private ChapterEntity chapterEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private LanguageEntity languageEntity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ChapterEntity getChapterEntity() { return chapterEntity; }
    public void setChapterEntity(ChapterEntity chapterEntity) { this.chapterEntity = chapterEntity; }

    public LanguageEntity getLanguageEntity() { return languageEntity; }
    public void setLanguageEntity(LanguageEntity languageEntity) { this.languageEntity = languageEntity; }
}
