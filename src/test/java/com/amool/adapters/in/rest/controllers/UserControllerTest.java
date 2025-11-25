package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.GetUserById;
import com.amool.application.usecases.UpdateUser;
import com.amool.application.usecases.SetUserPreferences;
import com.amool.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetUserById getUserById;

    @MockitoBean
    private UpdateUser updateUser;

    @MockitoBean
    private SetUserPreferences setUserPreferences;

    private static final Long USER_ID = 5L;

    @Test
    @DisplayName("GET /api/users/{id} - 200 OK returns user when found")
    void getUser_returns200_whenFound() throws Exception {
        givenUserExists(USER_ID, "John", "Doe", "jdoe", "jdoe@example.com", "pic");

        ResultActions response = whenClientRequestsUser(USER_ID);

        thenResponseIsOkWithUser(response, USER_ID, "John", "Doe", "jdoe", "jdoe@example.com", "pic");
        thenUseCaseWasCalledWith(USER_ID);
    }

    @Test
    @DisplayName("GET /api/users/{id} - 404 Not Found when user does not exist")
    void getUser_returns404_whenNotFound() throws Exception {
        givenUserDoesNotExist(USER_ID);

        ResultActions response = whenClientRequestsUser(USER_ID);

        thenStatusIsNotFound(response);
        thenUseCaseWasCalledWith(USER_ID);
    }

    private void givenUserExists(Long id, String name, String surname, String username, String email, String photo) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        u.setEmail(email);
        u.setPhoto(photo);
        u.setMoney(BigDecimal.ZERO);
        when(getUserById.execute(eq(id))).thenReturn(Optional.of(u));
    }

    private void givenUserDoesNotExist(Long id) {
        when(getUserById.execute(eq(id))).thenReturn(Optional.empty());
    }

    private ResultActions whenClientRequestsUser(Long id) throws Exception {
        return mockMvc.perform(get("/api/users/{id}", id).accept(MediaType.APPLICATION_JSON));
    }

    private void thenResponseIsOkWithUser(ResultActions response, Long id, String name, String surname, String username, String email, String photo) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.surname").value(surname))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.photo").value(photo));
    }

    private void thenStatusIsNotFound(ResultActions response) throws Exception {
        response.andExpect(status().isNotFound());
    }

    private void thenUseCaseWasCalledWith(Long id) {
        verify(getUserById).execute(eq(id));
    }
}
