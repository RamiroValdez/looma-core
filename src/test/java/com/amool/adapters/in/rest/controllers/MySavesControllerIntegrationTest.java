package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.mappers.WorkMapper;
import com.amool.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.amool.adapters.out.persistence.UserPersistenceAdapter;
import com.amool.adapters.out.persistence.WorksPersistenceAdapter;
import jakarta.persistence.EntityManager;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MySavesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WorksPersistenceAdapter workRepository;

    private String authToken;
    private Long userId;
    private Long workId;

    @BeforeEach
    void setUp() {
        
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();
        this.userId = user.getId();

        WorkEntity work = new WorkEntity();
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        entityManager.persist(work);
        entityManager.flush();
        this.workId = work.getId();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId().toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        this.authToken = "Bearer " + tokenProvider.createToken(authentication);
    }

    @Test
    void toggleSaveWork_shouldSaveWorkWhenNotSaved() throws Exception {
        
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.isSaved").value(true));
    }

    @Test
    void toggleSaveWork_shouldRemoveWorkWhenAlreadySaved() throws Exception {
        
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .header("Authorization", authToken));

        
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.isSaved").value(false));
    }

    @Test
    void getSaveStatus_shouldReturnFalseWhenWorkNotSaved() throws Exception {
        
        mockMvc.perform(get("/api/saved-works/" + workId + "/status")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.isSaved").value(false));
    }

    @Test
    void getSaveStatus_shouldReturnTrueWhenWorkSaved() throws Exception {
        
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .header("Authorization", authToken));

        
        mockMvc.perform(get("/api/saved-works/" + workId + "/status")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.isSaved").value(true));
    }

    @Test
    void getSavedWorks_shouldReturnEmptyListWhenNoWorksSaved() throws Exception {
        
        mockMvc.perform(get("/api/saved-works")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getSavedWorks_shouldReturnSavedWorks() throws Exception {
        
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .header("Authorization", authToken));

        
        mockMvc.perform(get("/api/saved-works")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(workId))
                .andExpect(jsonPath("$[0].title").value("Test Work"));
    }

    @Test
    void toggleSaveWork_shouldReturnNotFoundWhenWorkDoesNotExist() throws Exception {
        Long nonExistentWorkId = 9999L;
        
        mockMvc.perform(post("/api/saved-works/" + nonExistentWorkId + "/toggle")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSaveStatus_shouldReturnNotFoundWhenWorkDoesNotExist() throws Exception {
        Long nonExistentWorkId = 9999L;
        
        mockMvc.perform(get("/api/saved-works/" + nonExistentWorkId + "/status")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleSaveWork_shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/saved-works/" + workId + "/toggle")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
