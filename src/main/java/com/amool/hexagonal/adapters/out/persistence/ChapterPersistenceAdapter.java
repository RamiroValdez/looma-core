package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.ChapterEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.domain.model.Chapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChapterPersistenceAdapter implements LoadChapterPort, SaveChapterPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<Chapter> loadChapter(Long workId, Long chapterId) {
        boolean workExists = entityManager.createQuery(
                "SELECT COUNT(w) > 0 FROM WorkEntity w WHERE w.id = :workId",
                Boolean.class)
            .setParameter("workId", workId)
            .getSingleResult();

        if (!workExists) {
            return Optional.empty();
        }

        return Optional.ofNullable(entityManager.find(ChapterEntity.class, chapterId))
                .filter(chapter -> chapter.getWorkEntity() != null &&
                                 chapter.getWorkEntity().getId().equals(workId))
                .map(ChapterMapper::toDomain);
    }

    @Override
    @Transactional
    public Chapter saveChapter(Chapter chapter) {
        ChapterEntity entity = new ChapterEntity();

        entity.setLastModified(chapter.getLastModified() != null ?
            chapter.getLastModified() : LocalDateTime.now());

        if (chapter.getWorkId() != null) {
            WorkEntity workEntity = entityManager.find(WorkEntity.class, chapter.getWorkId());
            entity.setWorkEntity(workEntity);
        }

        if (chapter.getLanguageId() != null) {
            LanguageEntity languageEntity = entityManager.find(LanguageEntity.class, chapter.getLanguageId());
            entity.setLanguageEntity(languageEntity);
        }

        entityManager.persist(entity);

        Chapter domain = ChapterMapper.toDomain(entity);

        return domain;
    }
}
