package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.mappers.WorkMapper;
import com.amool.adapters.out.persistence.mappers.CategoryMapper;
import com.amool.adapters.out.persistence.mappers.TagMapper;
import com.amool.adapters.out.persistence.mappers.FormatMapper;
import com.amool.adapters.out.persistence.mappers.LanguageMapper;
import com.amool.adapters.out.persistence.mappers.UserMapper;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class WorksPersistenceAdapter implements ObtainWorkByIdPort, WorkPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Work> obtainWorkById(Long workId) {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                      "LEFT JOIN FETCH w.creator " +
                      "LEFT JOIN FETCH w.formatEntity " +
                      "LEFT JOIN FETCH w.chapters " +
                      "WHERE w.id = :workId";
        List<WorkEntity> results = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("workId", workId)
                .setMaxResults(1)
                .getResultList();

        return results.stream()
                .findFirst()
                .map(WorkMapper::toDomain);
    }

    @Override
    public List<Work> getWorksByUserId(Long userId) {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                      "LEFT JOIN FETCH w.creator c " +
                      "LEFT JOIN FETCH w.formatEntity " +
                      "LEFT JOIN FETCH w.chapters " +
                      "LEFT JOIN FETCH w.categories " +
                      "WHERE c.id = :userId";

        List<WorkEntity> entities = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return entities.stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public Long createWork(Work work) {
        WorkEntity workEntity = WorkMapper.toEntity(work);
        entityManager.persist(workEntity);
        entityManager.flush();
        return workEntity.getId();
    }

    @Override
    public Boolean updateWork(Work work) {
        try {
            WorkEntity existingEntity = entityManager.find(WorkEntity.class, work.getId());

            if (existingEntity == null) {
                return false;
            }

            // Update only scalar fields and simple associations. Do NOT touch chapters to avoid
            // cascading changes that could nullify chapter.workEntity (work_id).
            if (work.getTitle() != null) existingEntity.setTitle(work.getTitle());
            if (work.getDescription() != null) existingEntity.setDescription(work.getDescription());
            if (work.getCover() != null) existingEntity.setCover(work.getCover());
            if (work.getBanner() != null) existingEntity.setBanner(work.getBanner());
            if (work.getState() != null) existingEntity.setState(work.getState());
            if (work.getPrice() != null) existingEntity.setPrice(work.getPrice());
            if (work.getLikes() != null) existingEntity.setLikes(work.getLikes());
            if (work.getPublicationDate() != null) existingEntity.setPublicationDate(work.getPublicationDate());

            if (work.getCreator() != null) existingEntity.setCreator(UserMapper.toEntity(work.getCreator()));
            if (work.getFormat() != null) existingEntity.setFormatEntity(FormatMapper.toEntity(work.getFormat()));
            if (work.getOriginalLanguage() != null) existingEntity.setOriginalLanguageEntity(LanguageMapper.toEntity(work.getOriginalLanguage()));

            if (work.getCategories() != null) existingEntity.setCategories(CategoryMapper.toEntitySet(work.getCategories()));
            if (work.getTags() != null) existingEntity.setTags(TagMapper.toEntitySet(work.getTags()));

            entityManager.merge(existingEntity);
            entityManager.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
