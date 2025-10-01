package com.amool.hexagonal.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el modelo de dominio Work
 */
public class WorkTest {

    @Test
    public void testWorkCreation() {
        // Arrange & Act
        Work work = new Work();
        work.setId(1L);
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        work.setPrice(19.99);
        work.setLikes(100);
        work.setState("PUBLISHED");

        // Assert
        assertEquals(1L, work.getId());
        assertEquals("Test Work", work.getTitle());
        assertEquals("Test Description", work.getDescription());
        assertEquals(19.99, work.getPrice());
        assertEquals(100, work.getLikes());
        assertEquals("PUBLISHED", work.getState());
    }

    @Test
    public void testWorkWithRelations() {
        // Arrange
        Work work = new Work();
        User creator = new User();
        creator.setId(1L);
        creator.setName("John");
        
        Format format = new Format();
        format.setId(1L);
        format.setName("Novel");
        
        List<Chapter> chapters = new ArrayList<>();
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Chapter 1");
        chapters.add(chapter);

        // Act
        work.setCreator(creator);
        work.setFormat(format);
        work.setChapters(chapters);

        // Assert
        assertNotNull(work.getCreator());
        assertEquals("John", work.getCreator().getName());
        assertNotNull(work.getFormat());
        assertEquals("Novel", work.getFormat().getName());
        assertEquals(1, work.getChapters().size());
        assertEquals("Chapter 1", work.getChapters().get(0).getTitle());
    }
}
