package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import com.amool.hexagonal.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ManageWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ManageWorkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObtainWorkByIdUseCase obtainWorkByIdUseCase;

    @Test
    public void testGetWorkById_ShouldReturnWork_WhenWorkExists() throws Exception {
        Long workId = 1L;
        
        User creator = new User();
        creator.setId(1L);
        creator.setName("John");
        creator.setSurname("Doe");
        creator.setUsername("johndoe");

        Format format = new Format();
        format.setId(1L);
        format.setName("Novel");

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
        work.setChapters(new ArrayList<>());
        work.setCategories(new ArrayList<>());

        when(obtainWorkByIdUseCase.execute(workId)).thenReturn(work);

        mockMvc.perform(get("/api/manage-work/" + workId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workId))
                .andExpect(jsonPath("$.title").value("Test Work"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.state").value("PUBLISHED"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.likes").value(500))
                .andExpect(jsonPath("$.creator.name").value("John"))
                .andExpect(jsonPath("$.format.name").value("Novel"));
    }

    @Test
    public void testGetWorkById_ShouldReturnNull_WhenWorkDoesNotExist() throws Exception {
        Long workId = 999L;
        when(obtainWorkByIdUseCase.execute(workId)).thenReturn(null);

        mockMvc.perform(get("/api/manage-work/" + workId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
