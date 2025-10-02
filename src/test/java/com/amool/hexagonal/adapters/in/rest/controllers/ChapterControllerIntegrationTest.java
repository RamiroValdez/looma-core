package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.controllers.ChapterController;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.in.GetChapterUseCase;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.ChapterContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChapterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChapterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetChapterUseCase getChapterUseCase;

    @MockBean
    private LoadChapterContentPort loadChapterContentPort;
    
    @MockBean
    private SaveChapterContentPort saveChapterContentPort;

    @Test
    public void getChapter_ShouldReturnChapterWithContent() throws Exception {
        String bookId = "1";
        String chapterId = "1";
        String language = "es";
        
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Capítulo 1");
        chapter.setDescription("Descripción del capítulo");
        chapter.setPrice(9.99);
        
        ChapterContent content = new ChapterContent("1", "1", 
            Map.of("es", "Contenido en español"), "es");
        
        when(getChapterUseCase.getChapterWithContent(1L, 1L, language))
            .thenReturn(Optional.of(new GetChapterUseCase.ChapterWithContent(chapter, "Contenido en español")));
            
        when(loadChapterContentPort.loadContent(bookId, chapterId, language))
            .thenReturn(Optional.of(content));
            
        when(loadChapterContentPort.getAvailableLanguages(bookId, chapterId))
            .thenReturn(List.of("es", "en"));

        mockMvc.perform(get("/api/books/{bookId}/chapters/{chapterId}", bookId, chapterId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Capítulo 1"))
            .andExpect(jsonPath("$.content").value("Contenido en español"))
            .andExpect(jsonPath("$.availableLanguages[0]").value("es"))
            .andExpect(jsonPath("$.availableLanguages[1]").value("en"));
    }

    @Test
    public void getChapter_ShouldReturnNotFound_WhenChapterDoesNotExist() throws Exception {
        String bookId = "999";
        String chapterId = "999";
        String language = "es";
        
        when(getChapterUseCase.getChapterWithContent(999L, 999L, language))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{bookId}/chapters/{chapterId}", bookId, chapterId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
