package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.domain.model.Work;

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
        
        // Mapear relaciones
        work.setCreator(UserMapper.toDomain(entity.getCreator()));
        work.setFormat(FormatMapper.toDomain(entity.getFormatEntity()));
        work.setChapters(ChapterMapper.toDomainList(entity.getChapters()));
        work.setCategories(CategoryMapper.toDomainList(entity.getCategories()));

        return work;
    }

}
