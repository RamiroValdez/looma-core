package com.amool.hexagonal.adapters.out.mongodb.repository;

import com.amool.hexagonal.adapters.out.mongodb.document.ChapterContentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoChapterContentRepository extends MongoRepository<ChapterContentDocument, String> {
    @Deprecated
    Optional<ChapterContentDocument> findByChapterId(String chapterId);
    
    Optional<ChapterContentDocument> findByWorkIdAndChapterId(String workId, String chapterId);
}
