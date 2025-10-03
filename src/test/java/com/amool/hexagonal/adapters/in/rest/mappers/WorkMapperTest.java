package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class WorkMapperTest {

    @Test
    public void testToDto_ShouldMapAllFields() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("John");
        creator.setSurname("Doe");
        creator.setUsername("johndoe");
        creator.setPhoto("photo.jpg");

        Format format = new Format();
        format.setId(1L);
        format.setName("Novel");

        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Chapter 1");
        chapter.setPrice(2.99);

        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");

        Work work = new Work();
        work.setId(1L);
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        work.setCover("cover.jpg");
        work.setBanner("banner.jpg");
        work.setState("PUBLISHED");
        work.setPrice(29.99);
        work.setLikes(500);
        work.setPublicationDate(LocalDate.of(2024, 1, 15));
        work.setCreator(creator);
        work.setFormat(format);
        
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(chapter);
        work.setChapters(chapters);
        
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        work.setCategories(categories);

        WorkResponseDto dto = WorkMapper.toDto(work);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Work", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("PUBLISHED", dto.getState());
        assertEquals(29.99, dto.getPrice());
        assertEquals(500, dto.getLikes());
        
        assertNotNull(dto.getCreator());
        assertEquals("John", dto.getCreator().getName());
        assertNotNull(dto.getFormat());
        assertEquals("Novel", dto.getFormat().getName());
        assertEquals(1, dto.getChapters().size());
        assertEquals("Chapter 1", dto.getChapters().get(0).getTitle());
        assertEquals(1, dto.getCategories().size());
        assertEquals("Fantasy", dto.getCategories().get(0).getName());
    }

    @Test
    public void testToDto_ShouldReturnNull_WhenWorkIsNull() {
        WorkResponseDto dto = WorkMapper.toDto(null);
        assertNull(dto);
    }
}
