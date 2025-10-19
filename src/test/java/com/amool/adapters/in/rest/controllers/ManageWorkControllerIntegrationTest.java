package com.amool.adapters.in.rest.controllers;
/*
import com.amool.application.port.in.WorkService;
import com.amool.application.port.in.ChapterService;
import com.amool.application.port.out.LoadWorkOwnershipPort;
import com.amool.domain.model.Format;
import com.amool.domain.model.Language;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import com.amool.hexagonal.domain.model.*;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManageWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ManageWorkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkService workService;

    @MockitoBean
    private ChapterService chapterService;

    @MockitoBean
    private LoadWorkOwnershipPort loadWorkOwnershipPort;

    private void setAuthenticatedUser(Long userId) {
        var principal = new JwtUserPrincipal(userId, "u@e.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testGetWorkById_ShouldReturnWork_WhenWorkExists() throws Exception {
        Long workId = 1L;
        setAuthenticatedUser(1L);
        when(loadWorkOwnershipPort.isOwner(1L, 1L)).thenReturn(true);

        User creator = new User();
        creator.setId(1L);
        creator.setName("John");
        creator.setSurname("Doe");
        creator.setUsername("johndoe");

        Format format = new Format();
        format.setId(1L);
        format.setName("Novel");

        Language originalLanguage = new Language();
        originalLanguage.setId(1L);
        originalLanguage.setName("Español");

        Work work = new Work();
        work.setId(workId);
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        work.setState("PUBLISHED");
        work.setPrice(29.99);
        work.setLikes(500);
        work.setPublicationDate(LocalDate.of(2024, 1, 15));
        work.setCreator(creator);
        work.setFormat(format);
        work.setOriginalLanguage(originalLanguage);
        work.setChapters(new ArrayList<>());
        work.setCategories(new ArrayList<>());
        work.setTags(new HashSet<>());

        when(workService.obtainWorkById(workId)).thenReturn(Optional.of(work));

        mockMvc.perform(get("/api/manage-work/" + workId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workId))
                .andExpect(jsonPath("$.title").value("Test Work"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.state").value("PUBLISHED"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.likes").value(500))
                .andExpect(jsonPath("$.creator.name").value("John"))
                .andExpect(jsonPath("$.format.name").value("Novel"))
                .andExpect(jsonPath("$.originalLanguage.name").value("Español"));
    }

    @Test
    public void testGetWorkById_ShouldReturn404_WhenWorkDoesNotExist() throws Exception {
        Long workId = 999L;
        setAuthenticatedUser(999L);
        when(loadWorkOwnershipPort.isOwner(999L, 999L)).thenReturn(true);
        when(workService.obtainWorkById(workId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/manage-work/" + workId))
                .andExpect(status().isNotFound());
    }
}*/