package com.amool.adapters.in.rest.controllers;
/*
import com.amool.application.port.in.UserService;
import com.amool.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/users/{id} returns 200 with user when found")
    void getUser_found_returnsOk() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setPhoto("pic");
        when(userService.getById(eq(5L))).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.username").value("jdoe"))
                .andExpect(jsonPath("$.email").value("jdoe@example.com"))
                .andExpect(jsonPath("$.photo").value("pic"));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns 404 when not found")
    void getUser_notFound_returns404() throws Exception {
        when(userService.getById(eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
*/