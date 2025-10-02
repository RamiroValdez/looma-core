package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.ChapterEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.domain.model.Chapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class ChapterPersistenceAdapter implements LoadChapterPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<Chapter> loadChapter(Long bookId, Long chapterId) {
        boolean bookExists = entityManager.createQuery(
                "SELECT COUNT(w) > 0 FROM WorkEntity w WHERE w.id = :bookId", 
                Boolean.class)
            .setParameter("bookId", bookId)
            .getSingleResult();
            
        if (!bookExists) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(entityManager.find(ChapterEntity.class, chapterId))
                .filter(chapter -> chapter.getWorkEntity() != null && 
                                 chapter.getWorkEntity().getId().equals(bookId))
                .map(ChapterMapper::toDomain);
    }
}
