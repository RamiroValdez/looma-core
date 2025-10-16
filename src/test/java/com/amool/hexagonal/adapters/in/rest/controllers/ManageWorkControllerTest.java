package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.User;
import com.amool.hexagonal.domain.model.Work;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManageWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ManageWorkControllerTest {

    @TestConfiguration
    static class SecurityTestConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(org.springframework.security.core.annotation.AuthenticationPrincipal.class)
                            && JwtUserPrincipal.class.isAssignableFrom(parameter.getParameterType());
                }

                @Override
                public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                                          org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                                          org.springframework.web.context.request.NativeWebRequest webRequest,
                                          org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                    return new JwtUserPrincipal(1L, "testuser", "test@example.com", "password", "USER");
                }
            });
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkService workService;

    @MockBean
    private ChapterService chapterService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        JwtUserPrincipal principal = new JwtUserPrincipal(
            testUser.getId(), 
            testUser.getUsername(), 
            testUser.getEmail(), 
            "password", 
            "USER"
        );
        
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("DELETE /api/manage-work/1 - Success")
    void deleteWork_Success() throws Exception {
        
        Long workId = 1L;
        doNothing().when(workService).deleteWork(workId, testUser.getId());

        
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(workService, times(1)).deleteWork(workId, testUser.getId());
    }

    @Test
    @DisplayName("DELETE /api/manage-work/1 - Work Not Found")
    void deleteWork_WorkNotFound() throws Exception {
       
        Long workId = 1L;
        doThrow(new NoSuchElementException("Obra no encontrada"))
            .when(workService).deleteWork(workId, testUser.getId());

       
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(workService, times(1)).deleteWork(workId, testUser.getId());
    }

    @Test
    @DisplayName("DELETE /api/manage-work/1 - Unauthorized")
    void deleteWork_Unauthorized() throws Exception {
        
        Long workId = 1L;
        Long differentUserId = 2L;
        
        
        JwtUserPrincipal otherUser = new JwtUserPrincipal(
            differentUserId, 
            "otheruser", 
            "other@example.com", 
            "password", 
            "USER"
        );
        
        Authentication auth = new UsernamePasswordAuthenticationToken(otherUser, null, otherUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        doThrow(new SecurityException("No autorizado para eliminar esta obra"))
            .when(workService).deleteWork(workId, differentUserId);

       
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(workService, times(1)).deleteWork(workId, differentUserId);
    }

    @Test
    @DisplayName("DELETE /api/manage-work/1 - Unauthenticated")
    void deleteWork_Unauthenticated() throws Exception {
        
        Long workId = 1L;
        SecurityContextHolder.clearContext();

       
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(workService, never()).deleteWork(anyLong(), anyLong());
    }

    @Test
    @DisplayName("DELETE /api/manage-work/1 - Internal Server Error")
    void deleteWork_InternalServerError() throws Exception {
       
        Long workId = 1L;
        doThrow(new RuntimeException("Error inesperado"))
            .when(workService).deleteWork(workId, testUser.getId());

        
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(workService, times(1)).deleteWork(workId, testUser.getId());
    }

    @Test
    @DisplayName("GET /api/manage-work/1 - Success")
    void getWorkById_Success() throws Exception {
        
        Long workId = 1L;
        Work work = new Work();
        work.setId(workId);
        work.setTitle("Test Work");
        
        when(workService.obtainWorkById(workId)).thenReturn(Optional.of(work));

        
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workId))
                .andExpect(jsonPath("$.title").value("Test Work"));

        verify(workService, times(1)).obtainWorkById(workId);
    }

    @Test
    @DisplayName("GET /api/manage-work/1 - Not Found")
    void getWorkById_NotFound() throws Exception {
       
        Long workId = 1L;
        when(workService.obtainWorkById(workId)).thenReturn(Optional.empty());

        
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/manage-work/" + workId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(workService, times(1)).obtainWorkById(workId);
    }
}
