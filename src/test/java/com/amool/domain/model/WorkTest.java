package com.amool.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkTest {

    @Test
    public void testWorkCreation() {
        Work work = new Work();
        work.setId(1L);
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        work.setPrice(BigDecimal.valueOf(19.99));
        work.setLikes(100);
        work.setState("PUBLISHED");

        assertEquals(1L, work.getId());
        assertEquals("Test Work", work.getTitle());
        assertEquals("Test Description", work.getDescription());
        assertEquals(BigDecimal.valueOf(19.99), work.getPrice());
        assertEquals(100, work.getLikes());
        assertEquals("PUBLISHED", work.getState());
    }

    @Test
    public void testWorkWithRelations() {
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

        work.setCreator(creator);
        work.setFormat(format);
        work.setChapters(chapters);
        assertNotNull(work.getCreator());
        assertEquals("John", work.getCreator().getName());
        assertNotNull(work.getFormat());
        assertEquals("Novel", work.getFormat().getName());
        assertEquals(1, work.getChapters().size());
        assertEquals("Chapter 1", work.getChapters().get(0).getTitle());
    }
}
