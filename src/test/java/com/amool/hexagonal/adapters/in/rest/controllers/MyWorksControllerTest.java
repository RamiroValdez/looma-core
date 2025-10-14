package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MyWorksController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MyWorksControllerTest {

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
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
                        return principal;
                    }
                    return new JwtUserPrincipal(999L, "test@example.com", "Test", "User", "testuser");
                }
            });
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkService workService;

    private UsernamePasswordAuthenticationToken authWith(Long userId) {
        JwtUserPrincipal principal = new JwtUserPrincipal(userId, "mail@test.com", "Name", "Surname", "user");
        return new UsernamePasswordAuthenticationToken(principal, null, List.of());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/cover -> 204 when success")
    void updateCover_shouldReturn204_whenSuccess() throws Exception {
        doNothing().when(workService).updateCover(anyLong(), any(), anyLong(), anyString());

        MockMultipartFile cover = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/cover", 1L)
                        .file(cover)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/cover -> 404 when not found")
    void updateCover_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new IllegalArgumentException("not found")).when(workService).updateCover(anyLong(), any(), anyLong(),isNull());

        MockMultipartFile cover = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/cover", 1L)
                        .file(cover)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/cover -> 403 when forbidden")
    void updateCover_shouldReturn403_whenForbidden() throws Exception {
        doThrow(new SecurityException("forbidden")).when(workService).updateCover(anyLong(), any(), anyLong(), any());

        MockMultipartFile cover = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/cover", 1L)
                        .file(cover)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/cover -> 400 when IO error")
    void updateCover_shouldReturn400_whenIOException() throws Exception {

        doThrow(new IOException("io")).when(workService).updateCover(any(), any(), any(), any());

        MockMultipartFile cover = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/cover", 1L)
                        .file(cover)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/banner -> 204 when success")
    void updateBanner_shouldReturn204_whenSuccess() throws Exception {
        doNothing().when(workService).updateBanner(anyLong(), any(), anyLong());

        MockMultipartFile banner = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/banner", 1L)
                        .file(banner)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/banner -> 404 when not found")
    void updateBanner_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new IllegalArgumentException("not found")).when(workService).updateBanner(anyLong(), any(), anyLong());

        MockMultipartFile banner = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/banner", 1L)
                        .file(banner)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/banner -> 403 when forbidden")
    void updateBanner_shouldReturn403_whenForbidden() throws Exception {
        doThrow(new SecurityException("forbidden")).when(workService).updateBanner(anyLong(), any(), anyLong());

        MockMultipartFile banner = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/banner", 1L)
                        .file(banner)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/my-works/{id}/banner -> 400 when IO error")
    void updateBanner_shouldReturn400_whenIOException() throws Exception {
        doThrow(new IOException("io")).when(workService).updateBanner(anyLong(), any(), anyLong());

        MockMultipartFile banner = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/my-works/{id}/banner", 1L)
                        .file(banner)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(authentication(authWith(10L)))
        ).andExpect(status().isBadRequest());
    }
}
