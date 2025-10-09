package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.out.LoadWorkOwnershipPort;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.ChapterContent;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChapterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChapterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChapterService chapterService;

    @MockBean
    private LoadChapterContentPort loadChapterContentPort;
    
    @MockBean
    private SaveChapterContentPort saveChapterContentPort;

    @MockBean
    private LoadWorkOwnershipPort loadWorkOwnershipPort;

    @Test
    public void getChapter_ShouldReturnChapterWithContent() throws Exception {
        String workId = "1";
        String chapterId = "1";
        String language = "es";
        
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Capítulo 1");
        chapter.setPrice(9.99);
        
        ChapterContent content = new ChapterContent("1", "1", 
            Map.of("es", "Contenido en español"), "es");
        
        when(chapterService.getChapterWithContent(1L, 1L, language))
            .thenReturn(Optional.of(new ChapterService.ChapterWithContent(chapter, "Contenido en español")));
            
        when(loadChapterContentPort.loadContent(workId, chapterId, language))
            .thenReturn(Optional.of(content));
            
        when(loadChapterContentPort.getAvailableLanguages(workId, chapterId))
            .thenReturn(List.of("es", "en"));

        mockMvc.perform(get("/api/work/{workId}/chapter/{chapterId}", workId, chapterId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Capítulo 1"))
            .andExpect(jsonPath("$.price").value(9.99))
            .andExpect(jsonPath("$.content").value("Contenido en español"))
            .andExpect(jsonPath("$.availableLanguages[0]").value("es"))
            .andExpect(jsonPath("$.availableLanguages[1]").value("en"));
    }

    @Test
    public void getChapter_ShouldReturnNotFound_WhenChapterDoesNotExist() throws Exception {
        String workId = "999";
        String chapterId = "999";
        String language = "es";
        
        when(chapterService.getChapterWithContent(999L, 999L, language))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/work/{workId}/chapter/{chapterId}", workId, chapterId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateChapterContent_ShouldReturnOk_WhenValidAndOwner() throws Exception {
        String workId = "1";
        String chapterId = "10";
        String language = "es";
        String content = "Nuevo contenido";

        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );

        when(loadWorkOwnershipPort.isOwner(1L, 100L)).thenReturn(true);
        when(saveChapterContentPort.saveContent(workId, chapterId, language, content))
                .thenReturn(new ChapterContent(workId, chapterId, Map.of(language, content), language));

        String body = "{"+
                "\"workId\":\""+workId+"\","+
                "\"chapterId\":\""+chapterId+"\","+
                "\"language\":\""+language+"\","+
                "\"content\":\""+content+"\""+
                "}";

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/content", workId, chapterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.chapterId").value(chapterId))
                .andExpect(jsonPath("$.defaultLanguage").value(language))
                .andExpect(jsonPath("$.contentByLanguage."+language).value(content));
    }

    @Test
    public void updateChapterContent_ShouldReturnBadRequest_WhenPathMismatch() throws Exception {
        String workId = "1";
        String chapterId = "10";
        String body = "{"+
                "\"workId\":\"DIFF\","+
                "\"chapterId\":\""+chapterId+"\","+
                "\"language\":\"es\","+
                "\"content\":\"X\""+
                "}";

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/content", workId, chapterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateChapterContent_ShouldReturnUnauthorized_WhenNoPrincipal() throws Exception {
        SecurityContextHolder.clearContext();

        String workId = "1";
        String chapterId = "10";
        String body = "{"+
                "\"workId\":\""+workId+"\","+
                "\"chapterId\":\""+chapterId+"\","+
                "\"language\":\"es\","+
                "\"content\":\"X\""+
                "}";

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/content", workId, chapterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateChapterContent_ShouldReturnForbidden_WhenNotOwner() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );
        when(loadWorkOwnershipPort.isOwner(1L, 100L)).thenReturn(false);

        String workId = "1";
        String chapterId = "10";
        String body = "{"+
                "\"workId\":\""+workId+"\","+
                "\"chapterId\":\""+chapterId+"\","+
                "\"language\":\"es\","+
                "\"content\":\"X\""+
                "}";

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/content", workId, chapterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteChapter_ShouldReturnNoContent_WhenOwner() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );
        when(loadWorkOwnershipPort.isOwner(1L, 100L)).thenReturn(true);

        mockMvc.perform(delete("/api/work/{workId}/chapter/{chapterId}/delete", "1", "10"))
                .andExpect(status().isNoContent());

        verify(chapterService, times(1)).deleteChapter(1L, 10L);
    }

    @Test
    public void deleteChapter_ShouldReturnUnauthorized_WhenNoPrincipal() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(delete("/api/work/{workId}/chapter/{chapterId}/delete", "1", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteChapter_ShouldReturnForbidden_WhenNotOwner() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );
        when(loadWorkOwnershipPort.isOwner(1L, 100L)).thenReturn(false);

        mockMvc.perform(delete("/api/work/{workId}/chapter/{chapterId}/delete", "1", "10"))
                .andExpect(status().isForbidden());
    }
}
