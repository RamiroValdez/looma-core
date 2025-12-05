package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.GetUserRating;
import com.amool.application.usecases.GetWorkRatings;
import com.amool.application.usecases.RateWork;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private RateWork rateWork;
    @MockitoBean private GetUserRating getUserRating;
    @MockitoBean private GetWorkRatings getWorkRatings;

    private static final long WORK_ID = 1L;
    private static final long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        var principal = new JwtUserPrincipal(USER_ID, "user@example.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 200 OK on success")
    void rateWork_returns200_onSuccess() throws Exception {
        givenRateSucceeds(4.5, 4.0);

        ResultActions response = whenClientRates(4.5);

        thenResponseIsOkWithRating(response, 4.5, 4.0);
        thenRateUseCaseWasCalledWith(4.5);
    }

    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 400 Bad Request when DTO validation fails (out of bounds)")
    void rateWork_returns400_onDtoValidationError() throws Exception {
        ResultActions response = whenClientRatesRawBody("{\"rating\": 0.1}");

        thenStatusIsBadRequest(response);
        thenRateUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 400 Bad Request when domain validation fails (IllegalArgumentException)")
    void rateWork_returns400_onIllegalArgument() throws Exception {
        givenRateThrowsIllegalArgument(4.7);

        ResultActions response = whenClientRates(4.7);

        thenStatusIsBadRequest(response);
    }

    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 404 Not Found when work does not exist")
    void rateWork_returns404_onNotFound() throws Exception {
        givenRateThrowsNotFound(3.0);

        ResultActions response = whenClientRates(3.0);

        thenStatusIsNotFound(response);
    }

    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 400 Bad Request when rating field is missing")
    void rateWork_returns400_whenRatingMissing() throws Exception {
        ResultActions response = whenClientRatesRawBody("{}");

        thenStatusIsBadRequest(response);
        thenRateUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/works/{workId}/ratings - 403 Forbidden when action is not allowed")
    void rateWork_returns403_onSecurityException() throws Exception {
        givenRateThrowsForbidden(2.5);

        ResultActions response = whenClientRates(2.5);

        thenStatusIsForbidden(response);
    }


    @Test
    @DisplayName("GET /api/works/{workId}/ratings - 200 OK returns total count")
    void getWorkRatings_returns200_withTotal() throws Exception {
        givenTotalRatingsIs(123);

        ResultActions response = whenClientRequestsTotalRatings();

        thenResponseIsOkWithBody(response, "123");
        thenGetWorkRatingsUseCaseWasCalled();
    }


    @Test
    @DisplayName("GET /api/works/{workId}/ratings/me - 200 OK returns user's rating")
    void getUserRating_returns200_withValue() throws Exception {
        givenUserRatingIs(Optional.of(4.0));

        ResultActions response = whenClientRequestsMyRating();

        thenResponseIsOkWithBody(response, "4.0");
        thenGetUserRatingUseCaseWasCalled();
    }

    @Test
    @DisplayName("GET /api/works/{workId}/ratings/me - 200 OK with empty body when user hasn't rated")
    void getUserRating_returns200_withNullWhenEmpty() throws Exception {
        givenUserRatingIs(Optional.empty());

        ResultActions response = whenClientRequestsMyRating();

        thenResponseIsOkWithEmptyBody(response);
        thenGetUserRatingUseCaseWasCalled();
    }

    private void givenRateSucceeds(double requestRating, double average) {
        when(rateWork.execute(eq(WORK_ID), eq(USER_ID), eq(requestRating))).thenReturn(average);
    }

    private void givenRateThrowsIllegalArgument(double requestRating) {
        when(rateWork.execute(eq(WORK_ID), eq(USER_ID), eq(requestRating)))
                .thenThrow(new IllegalArgumentException("invalid step"));
    }

    private void givenRateThrowsNotFound(double requestRating) {
        when(rateWork.execute(eq(WORK_ID), eq(USER_ID), eq(requestRating)))
                .thenThrow(new java.util.NoSuchElementException("not found"));
    }

    private void givenRateThrowsForbidden(double requestRating) {
        doThrow(new SecurityException("Forbidden")).when(rateWork)
                .execute(eq(WORK_ID), eq(USER_ID), eq(requestRating));
    }

    private void givenTotalRatingsIs(int total) {
        when(getWorkRatings.execute(eq(WORK_ID), any())).thenReturn(total);
    }

    private void givenUserRatingIs(Optional<Double> rating) {
        when(getUserRating.execute(eq(WORK_ID), eq(USER_ID))).thenReturn(rating);
    }

    private ResultActions whenClientRates(double rating) throws Exception {
        return whenClientRatesRawBody(bodyWithRating(rating));
    }

    private ResultActions whenClientRatesRawBody(String body) throws Exception {
        return mockMvc.perform(post("/api/works/{workId}/ratings", WORK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions whenClientRequestsTotalRatings() throws Exception {
        return mockMvc.perform(get("/api/works/{workId}/ratings", WORK_ID)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions whenClientRequestsMyRating() throws Exception {
        return mockMvc.perform(get("/api/works/{workId}/ratings/me", WORK_ID)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void thenResponseIsOkWithRating(ResultActions response, double rating, double average) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.work_id").value((int) WORK_ID))
                .andExpect(jsonPath("$.user_id").value((int) USER_ID))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.average_rating").value(average));
    }

    private void thenResponseIsOkWithBody(ResultActions response, String expectedBody) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    private void thenResponseIsOkWithEmptyBody(ResultActions response) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private void thenStatusIsBadRequest(ResultActions response) throws Exception {
        response.andExpect(status().isBadRequest());
    }

    private void thenStatusIsNotFound(ResultActions response) throws Exception {
        response.andExpect(status().isNotFound());
    }

    private void thenStatusIsForbidden(ResultActions response) throws Exception {
        response.andExpect(status().isForbidden());
    }

    private void thenRateUseCaseWasCalledWith(double rating) {
        verify(rateWork).execute(WORK_ID, USER_ID, rating);
    }

    private void thenRateUseCaseWasNotInvoked() {
        verify(rateWork, never()).execute(anyLong(), anyLong(), anyDouble());
    }

    private void thenGetWorkRatingsUseCaseWasCalled() {
        verify(getWorkRatings).execute(eq(WORK_ID), any());
    }

    private void thenGetUserRatingUseCaseWasCalled() {
        verify(getUserRating).execute(WORK_ID, USER_ID);
    }

    private String bodyWithRating(double rating) {
        return String.format("{\"rating\": %s}", Double.toString(rating));
    }
}
