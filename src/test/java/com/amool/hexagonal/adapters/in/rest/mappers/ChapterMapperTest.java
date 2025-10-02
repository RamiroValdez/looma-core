package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterDto;
import com.amool.hexagonal.adapters.in.rest.dto.ChapterWithContentDto;
import com.amool.hexagonal.application.port.in.GetChapterUseCase.ChapterWithContent;
import com.amool.hexagonal.domain.model.Chapter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChapterMapperTest {

    @Test
    void toDto_ShouldMapChapterToDto() {
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Capítulo 1");
        chapter.setDescription("Descripción");
        chapter.setPrice(9.99);
        chapter.setLikes(10L);
        chapter.setLastModified(LocalDateTime.now());

        ChapterDto result = ChapterMapper.toDto(chapter);

        assertNotNull(result);
        assertEquals(chapter.getId(), result.getId());
        assertEquals(chapter.getTitle(), result.getTitle());
        assertEquals(chapter.getPrice(), result.getPrice());
        assertEquals(chapter.getLikes(), result.getLikes());
        assertEquals(chapter.getLastModified(), result.getLastModified());
    }

    @Test
    void toDto_ShouldReturnNull_WhenChapterIsNull() {
        ChapterDto result = ChapterMapper.toDto((Chapter) null);

        assertNull(result);
    }

    @Test
    void toDto_ShouldMapChapterWithContentToDto() {
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Capítulo 1");
        chapter.setDescription("Descripción");
        chapter.setPrice(9.99);
        
        ChapterWithContent chapterWithContent = new ChapterWithContent(chapter, "Contenido");
        String content = "Contenido del capítulo";
        List<String> availableLanguages = List.of("es", "en");

        ChapterWithContentDto result = ChapterMapper.toDto(chapterWithContent, content, availableLanguages);

        assertNotNull(result);
        assertEquals(chapter.getId(), result.id());
        assertEquals(chapter.getTitle(), result.title());
        assertEquals(chapter.getDescription(), result.description());
        assertEquals(chapter.getPrice(), result.price());
        assertEquals(content, result.content());
        assertEquals(availableLanguages, result.availableLanguages());
    }

    @Test
    void toDto_ShouldReturnNull_WhenChapterWithContentIsNull() {
        ChapterWithContentDto result = ChapterMapper.toDto(
            (ChapterWithContent) null, 
            "content", 
            List.of()
        );

        assertNull(result);
    }
}
