package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.TagEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.TagMapper;
import com.amool.hexagonal.application.port.out.TagPort;
import com.amool.hexagonal.domain.model.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TagPersistenceAdapter implements TagPort {

    private final EntityManager entityManager;

    public TagPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Optional<Tag> searchTag(String tagName) {
       try{
              TagEntity tag = entityManager.createQuery("SELECT t FROM TagEntity t WHERE t.name = :name", TagEntity.class)
                     .setParameter("name", tagName)
                     .getSingleResult();
              return Optional.ofNullable(TagMapper.toDomain(tag));
         } catch (Exception e) {
              return Optional.empty();
       }
    }

    @Override
    @Transactional
    public Long createTag(String tagName) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagName);

        entityManager.persist(tagEntity);
        entityManager.flush();

        return tagEntity.getId();
    }
}
