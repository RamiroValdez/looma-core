package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.domain.model.Work;

public class WorkMapper {

    public static Work toDomain(WorkEntity entity) {
        if (entity == null) {
            return null;
        }
        Work work = new Work();
        work.setId(entity.getId());
        work.setTitle(entity.getTitle());
        work.setDescription(entity.getDescription());
        work.setCover(entity.getCover());
        work.setBanner(entity.getBanner());
        work.setState(entity.getState());
        work.setPrice(entity.getPrice());
        work.setLikes(entity.getLikes());
        work.setPublicationDate(entity.getPublicationDate());
        work.setCreator(UserMapper.toDomain(entity.getCreator()));
        work.setFormat(FormatMapper.toDomain(entity.getFormatEntity()));
        work.setOriginalLanguage(LanguageMapper.toDomain(entity.getOriginalLanguageEntity()));
        work.setChapters(ChapterMapper.toDomainList(entity.getChapters()));
        work.setCategories(CategoryMapper.toDomainList(entity.getCategories()));
        work.setTags(TagMapper.toDomainSet(entity.getTags()));

        return work;
    }

    public  static WorkEntity toEntity(Work work) {
        if (work == null) {
            return null;
        }
        WorkEntity entity = new WorkEntity();
        entity.setId(work.getId());
        entity.setTitle(work.getTitle());
        entity.setDescription(work.getDescription());
        entity.setCover(work.getCover());
        entity.setBanner(work.getBanner());
        entity.setState(work.getState());
        entity.setPrice(work.getPrice());
        entity.setLikes(work.getLikes());
        entity.setPublicationDate(work.getPublicationDate());
        entity.setCreator(UserMapper.toEntity(work.getCreator()));
        entity.setFormatEntity(FormatMapper.toEntity(work.getFormat()));
        entity.setOriginalLanguageEntity(LanguageMapper.toEntity(work.getOriginalLanguage()));
        entity.setChapters(ChapterMapper.toEntityList(work.getChapters()));
        entity.setCategories(CategoryMapper.toEntitySet(work.getCategories()));
        entity.setTags(TagMapper.toEntitySet(work.getTags()));

        return entity;
    }

}
