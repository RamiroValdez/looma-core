package com.amool.adapters.in.rest.controllers;
/*
import com.amool.application.port.in.ChapterService;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadWorkOwnershipPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    public void deleteChapter_ShouldReturnConflict_WhenServiceThrowsIllegalState() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );
        when(loadWorkOwnershipPort.isOwner(1L, 100L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new IllegalStateException("Solo DRAFT"))
                .when(chapterService).deleteChapter(1L, 10L);

        mockMvc.perform(delete("/api/work/{workId}/chapter/{chapterId}/delete", "1", "10"))
                .andExpect(status().isConflict());
    }

    @Test
    public void publishChapter_ShouldReturnConflict_WhenServiceThrowsIllegalState() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );

        org.mockito.Mockito.doThrow(new IllegalStateException("Solo DRAFT"))
                .when(chapterService).publishChapter(1L, 10L, 100L);

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/publish", "1", "10"))
                .andExpect(status().isConflict());
    }

    @Test
    public void scheduleChapter_ShouldReturnConflict_WhenServiceThrowsIllegalState() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );

        String body = "{\"when\":\"2025-12-31T00:00:00Z\"}";
        org.mockito.Mockito.doThrow(new IllegalStateException("Solo DRAFT"))
                .when(chapterService).schedulePublication(eq(1L), eq(10L), any(java.time.Instant.class), eq(100L));

        mockMvc.perform(post("/api/work/{workId}/chapter/{chapterId}/schedule", "1", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    public void cancelSchedule_ShouldReturnConflict_WhenServiceThrowsIllegalState() throws Exception {
        JwtUserPrincipal principal = new JwtUserPrincipal(100L, "user@example.com", "Name", "Surname", "username");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList())
        );

        org.mockito.Mockito.doThrow(new IllegalStateException("Solo SCHEDULED"))
                .when(chapterService).cancelScheduledPublication(1L, 10L, 100L);

        mockMvc.perform(delete("/api/work/{workId}/chapter/{chapterId}/schedule", "1", "10"))
                .andExpect(status().isConflict());
    }
}
*/