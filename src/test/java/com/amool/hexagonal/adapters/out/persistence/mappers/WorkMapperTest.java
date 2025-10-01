package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.*;
import com.amool.hexagonal.domain.model.Work;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para WorkMapper de persistencia
 * Verifica el mapeo correcto de Entity → Domain
 */
public class WorkMapperTest {

    @Test
    public void testToDomain_ShouldMapAllFields() {
        // Arrange
        UserEntity creator = new UserEntity();
        creator.setId(1L);
        creator.setName("John");
        creator.setSurname("Doe");
        creator.setUsername("johndoe");
        creator.setEmail("john@example.com");
        creator.setPhoto("photo.jpg");

        FormatEntity format = new FormatEntity();
        format.setId(1L);
        format.setName("Novel");

        WorkEntity entity = new WorkEntity();
        entity.setId(1L);
        entity.setTitle("Test Work");
        entity.setDescription("Test Description");
        entity.setCover("cover.jpg");
        entity.setBanner("banner.jpg");
        entity.setState("PUBLISHED");
        entity.setPrice(29.99);
        entity.setLikes(500);
        entity.setPublicationDate(LocalDate.of(2024, 1, 15));
        entity.setCreator(creator);
        entity.setFormatEntity(format);
        entity.setChapters(new ArrayList<>());
        entity.setCategories(new HashSet<>());

        // Act
        Work work = WorkMapper.toDomain(entity);

        // Assert
        assertNotNull(work);
        assertEquals(1L, work.getId());
        assertEquals("Test Work", work.getTitle());
        assertEquals("Test Description", work.getDescription());
        assertEquals("cover.jpg", work.getCover());
        assertEquals("banner.jpg", work.getBanner());
        assertEquals("PUBLISHED", work.getState());
        assertEquals(29.99, work.getPrice());
        assertEquals(500, work.getLikes());
        assertEquals(LocalDate.of(2024, 1, 15), work.getPublicationDate());
        
        // Verificar relaciones
        assertNotNull(work.getCreator());
        assertEquals("John", work.getCreator().getName());
        assertNotNull(work.getFormat());
        assertEquals("Novel", work.getFormat().getName());
        assertNotNull(work.getChapters());
        assertNotNull(work.getCategories());
    }

    @Test
    public void testToDomain_ShouldReturnNull_WhenEntityIsNull() {
        // Act
        Work work = WorkMapper.toDomain(null);

        // Assert
        assertNull(work);
    }
}
