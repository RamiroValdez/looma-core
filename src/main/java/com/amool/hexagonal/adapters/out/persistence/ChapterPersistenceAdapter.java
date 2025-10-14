package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.ChapterEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterPort;
import com.amool.hexagonal.application.port.out.UpdateChapterPort;
import com.amool.hexagonal.application.port.out.UpdateChapterStatusPort;
import com.amool.hexagonal.application.port.out.FindChaptersDueForPublicationPort;
import com.amool.hexagonal.domain.model.Chapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChapterPersistenceAdapter implements LoadChapterPort, SaveChapterPort, DeleteChapterPort, UpdateChapterPort, UpdateChapterStatusPort, FindChaptersDueForPublicationPort {

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

    @Override
    @Transactional
    public Optional<Chapter> updateChapter(Chapter chapter) {
        if (chapter == null || chapter.getId() == null) {
            return Optional.empty();
        }

        ChapterEntity entity = entityManager.find(ChapterEntity.class, chapter.getId());
        if (entity == null) {
            return Optional.empty();
        }

        entity.setTitle(chapter.getTitle());
        entity.setPrice(chapter.getPrice());
        entity.setLikes(chapter.getLikes());
        entity.setLastModified(chapter.getLastModified() != null ? chapter.getLastModified() : LocalDateTime.now());
        entity.setAllowAiTranslation(chapter.getAllowAiTranslation());
        entity.setPublicationStatus(chapter.getPublicationStatus());
        entity.setScheduledPublicationDate(chapter.getScheduledPublicationDate());
        entity.setPublishedAt(chapter.getPublishedAt());

        if (chapter.getWorkId() != null) {
            WorkEntity workEntity = entityManager.find(WorkEntity.class, chapter.getWorkId());
            entity.setWorkEntity(workEntity);
        }

        if (chapter.getLanguageId() != null) {
            LanguageEntity languageEntity = entityManager.find(LanguageEntity.class, chapter.getLanguageId());
            entity.setLanguageEntity(languageEntity);
        }

        entityManager.flush();

        return Optional.ofNullable(ChapterMapper.toDomain(entity));
    }

    @Override
    public Optional<Chapter> loadChapterForEdit(Long chapterId) {
        return Optional.ofNullable(entityManager.find(ChapterEntity.class, chapterId))
                .map(ChapterMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteChapter(Long workId, Long chapterId) {
        ChapterEntity chapter = entityManager.find(ChapterEntity.class, chapterId);
        if (chapter == null) return;
        WorkEntity work = chapter.getWorkEntity();
        if (work == null || !work.getId().equals(workId)) {
            return;
        }

        if (chapter.getVersions() != null) {
            chapter.getVersions().clear();
        }
        if (chapter.getUsersWhoAcquired() != null) {
            chapter.getUsersWhoAcquired().forEach(u -> u.getAcquiredChapterEntities().remove(chapter));
            chapter.getUsersWhoAcquired().clear();
        }

        entityManager.remove(chapter);
    }

    @Override
    @Transactional
    public void updatePublicationStatus(Long workId, Long chapterId, String status, LocalDateTime publishedAt) {
        int updated = entityManager.createQuery(
                "UPDATE ChapterEntity c SET c.publicationStatus = :status, c.publishedAt = :publishedAt, c.lastModified = :now " +
                        "WHERE c.id = :chapterId AND c.workEntity.id = :workId")
                .setParameter("status", status)
                .setParameter("publishedAt", publishedAt)
                .setParameter("now", LocalDateTime.now())
                .setParameter("chapterId", chapterId)
                .setParameter("workId", workId)
                .executeUpdate();

        if (updated == 0) {
            throw new NoSuchElementException("Capítulo no encontrado o no pertenece a la obra");
        }
    }

    @Override
    @Transactional
    public void schedulePublication(Long workId, Long chapterId, java.time.Instant when) {
        LocalDateTime whenLdt = LocalDateTime.ofInstant(when, java.time.ZoneOffset.UTC);
        int updated = entityManager.createQuery(
                "UPDATE ChapterEntity c SET c.publicationStatus = :status, c.scheduledPublicationDate = :when, c.publishedAt = NULL, c.lastModified = :now " +
                        "WHERE c.id = :chapterId AND c.workEntity.id = :workId")
                .setParameter("status", "SCHEDULED")
                .setParameter("when", whenLdt)
                .setParameter("now", LocalDateTime.now())
                .setParameter("chapterId", chapterId)
                .setParameter("workId", workId)
                .executeUpdate();

        if (updated == 0) {
            throw new NoSuchElementException("Capítulo no encontrado o no pertenece a la obra");
        }
    }

    @Override
    @Transactional
    public void clearSchedule(Long workId, Long chapterId) {
        int updated = entityManager.createQuery(
                "UPDATE ChapterEntity c SET c.publicationStatus = :status, c.scheduledPublicationDate = NULL, c.lastModified = :now " +
                        "WHERE c.id = :chapterId AND c.workEntity.id = :workId")
                .setParameter("status", "DRAFT")
                .setParameter("now", LocalDateTime.now())
                .setParameter("chapterId", chapterId)
                .setParameter("workId", workId)
                .executeUpdate();

        if (updated == 0) {
            throw new NoSuchElementException("Capítulo no encontrado o no pertenece a la obra");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<FindChaptersDueForPublicationPort.DueChapter> findDue(java.time.Instant now, int limit) {
        LocalDateTime nowLdt = LocalDateTime.ofInstant(now, java.time.ZoneOffset.UTC);
        var query = entityManager.createQuery(
                "SELECT c.workEntity.id, c.id FROM ChapterEntity c " +
                        "WHERE c.publicationStatus = :status AND c.scheduledPublicationDate <= :now AND c.publishedAt IS NULL",
                Object[].class
        );
        query.setParameter("status", "SCHEDULED");
        query.setParameter("now", nowLdt);
        query.setMaxResults(Math.max(1, limit));
        java.util.List<Object[]> rows = query.getResultList();
        java.util.List<FindChaptersDueForPublicationPort.DueChapter> result = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Long workId = (Long) row[0];
            Long chapterId = (Long) row[1];
            result.add(new FindChaptersDueForPublicationPort.DueChapter(workId, chapterId));
        }
        return result;
    }
}
