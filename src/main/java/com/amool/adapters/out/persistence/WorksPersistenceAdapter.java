package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.entity.TagEntity;
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
import com.amool.domain.model.WorkSearchFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<Work> getAllWorks() {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                "LEFT JOIN FETCH w.creator " +
                "LEFT JOIN FETCH w.formatEntity " +
                "LEFT JOIN FETCH w.chapters " +
                "LEFT JOIN FETCH w.categories " +
                "LEFT JOIN FETCH w.tags";
        
        return entityManager.createQuery(jpql, WorkEntity.class)
                .getResultList()
                .stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());
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
                return Boolean.FALSE;
            }

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
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public Page<Work> findByFilters(WorkSearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<WorkEntity> query = cb.createQuery(WorkEntity.class);
        Root<WorkEntity> root = query.from(WorkEntity.class);
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        query.select(root).distinct(true)
                .where(cb.and(predicates.toArray(new Predicate[0])));

        if (pageable.getSort().isUnsorted() && filter.getSortBy() != null && !filter.getSortBy().isBlank()) {
            Path<?> orderPath = root.get(filter.getSortBy());
            query.orderBy(filter.getAsc() ? cb.asc(orderPath) : cb.desc(orderPath));
        }

        TypedQuery<WorkEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<WorkEntity> entities = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WorkEntity> countRoot = countQuery.from(WorkEntity.class);
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);

        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        List<Work> works = entities.stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(works, pageable, total);
    }

    private List<Predicate> buildPredicates(WorkSearchFilter filter, CriteriaBuilder cb, Root<WorkEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getText() != null && !filter.getText().isBlank()) {
            String text = filter.getText().toLowerCase().trim();
            String phrasePattern = "%" + text + "%";

            String slugPattern = "%" + text.replace(" ", "-") + "%";
            List<String> wordPatterns = Arrays.stream(text.split("\\s+"))
                    .filter(word -> word.length() > 2)
                    .map(word -> "%" + word + "%")
                    .toList();

            Join<WorkEntity, TagEntity> tagJoin = root.join("tags", JoinType.LEFT);

            Predicate titleMatch = cb.like(cb.lower(root.get("title")), phrasePattern);
            Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), phrasePattern);

            List<Predicate> tagPredicates = new ArrayList<>();
            tagPredicates.add(cb.like(cb.lower(tagJoin.get("name")), phrasePattern));
            tagPredicates.add(cb.like(cb.lower(tagJoin.get("name")), slugPattern));
            for (String pattern : wordPatterns) {
                tagPredicates.add(cb.like(cb.lower(tagJoin.get("name")), pattern));
            }

            Predicate tagCombined = cb.or(tagPredicates.toArray(new Predicate[0]));
            predicates.add(cb.or(titleMatch, descriptionMatch, tagCombined));
        }

        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            Join<WorkEntity, CategoryEntity> categoryJoin = root.join("categories", JoinType.INNER);
            predicates.add(categoryJoin.get("id").in(filter.getCategoryIds()));
        }

        if (filter.getFormatIds() != null && !filter.getFormatIds().isEmpty()) {
            Join<WorkEntity, FormatEntity> formatJoin = root.join("formatEntity", JoinType.INNER);
            predicates.add(formatJoin.get("id").in(filter.getFormatIds()));
        }

        if (filter.getState() != null && !filter.getState().isBlank()) {
            predicates.add(cb.equal(root.get("state"), filter.getState()));
        }

        if (filter.getMinLikes() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("likes"), filter.getMinLikes()));
        }

        // Filtro de rangos de episodios
        if (filter.getRangeEpisodes() != null && !filter.getRangeEpisodes().isEmpty()) {
            List<Predicate> episodePredicates = new ArrayList<>();

            for (String range : filter.getRangeEpisodes()) {
                if (!"cualquiera".equals(range)) {
                    Subquery<Long> chapterCountSubquery = cb.createQuery().subquery(Long.class);
                    Root<WorkEntity> subRoot = chapterCountSubquery.from(WorkEntity.class);
                    Join<?, ?> chapterJoin = subRoot.join("chapters", JoinType.LEFT);

                    chapterCountSubquery.select(cb.count(chapterJoin))
                            .where(
                                    cb.and(
                                            cb.equal(subRoot.get("id"), root.get("id")),
                                            cb.equal(chapterJoin.get("publicationStatus"), "PUBLISHED")
                                    )
                            );

                    switch (range) {
                        case "1-5":
                            episodePredicates.add(cb.between(chapterCountSubquery, 1L, 5L));
                            break;
                        case "6-10":
                            episodePredicates.add(cb.between(chapterCountSubquery, 6L, 10L));
                            break;
                        case "11-20":
                            episodePredicates.add(cb.between(chapterCountSubquery, 11L, 20L));
                            break;
                        case "21+":
                            episodePredicates.add(cb.greaterThanOrEqualTo(chapterCountSubquery, 21L));
                            break;
                    }
                }
            }

            if (!episodePredicates.isEmpty()) {
                predicates.add(cb.or(episodePredicates.toArray(new Predicate[0])));
            }
        }

        // Filtro de períodos de actualización (última publicación de capítulo)
        if (filter.getLastUpdated() != null && !filter.getLastUpdated().isEmpty()) {
            List<Predicate> updatePredicates = new ArrayList<>();
            java.time.LocalDate today = java.time.LocalDate.now();

            for (String period : filter.getLastUpdated()) {
                java.time.LocalDate threshold;

                switch (period) {
                    case "today":
                        threshold = today;
                        break;
                    case "last_week":
                        threshold = today.minusWeeks(1);
                        break;
                    case "last_month":
                        threshold = today.minusMonths(1);
                        break;
                    case "last_3_months":
                        threshold = today.minusMonths(3);
                        break;
                    case "last_year":
                        threshold = today.minusYears(1);
                        break;
                    default:
                        continue;
                }

                updatePredicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), threshold));
            }

            if (!updatePredicates.isEmpty()) {
                predicates.add(cb.or(updatePredicates.toArray(new Predicate[0])));
            }
        }

        return predicates;
    }




}
