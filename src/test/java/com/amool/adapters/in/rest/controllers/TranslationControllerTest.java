package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.CreateLanguageVersionUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TranslationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateLanguageVersionUseCase createLanguageVersionUseCase;

    private static final String SOURCE = "en";
    private static final String TARGET = "es";
    private static final String ORIGINAL = "Hello world";
    private static final String RESULT = "Hola mundo";


    @Test
    @DisplayName("POST /api/translation/create-version - 200 OK returns translated version string")
    void createVersion_returns200_onSuccess() throws Exception {
        givenCreateVersionSucceedsWith(RESULT);

        ResultActions response = whenClientCreatesVersion(SOURCE, TARGET, ORIGINAL);

        thenResponseIsOkWithBody(response, RESULT);
        thenUseCaseWasCalledWith(SOURCE, TARGET, ORIGINAL);
    }

    @Test
    @DisplayName("POST /api/translation/create-version - 400 Bad Request when use case rejects input (IllegalArgumentException)")
    void createVersion_returns400_onIllegalArgument() throws Exception {
        givenCreateVersionThrowsIllegalArgument();

        ResultActions response = whenClientCreatesVersion(SOURCE, TARGET, ORIGINAL);

        thenStatusIsBadRequest(response);
    }

    @Test
    @DisplayName("POST /api/translation/create-version - 404 Not Found when resource is missing")
    void createVersion_returns404_onNotFound() throws Exception {
        givenCreateVersionThrowsNotFound();

        ResultActions response = whenClientCreatesVersion(SOURCE, TARGET, ORIGINAL);

        thenStatusIsNotFound(response);
    }

    @Test
    @DisplayName("POST /api/translation/create-version - 403 Forbidden when action not allowed")
    void createVersion_returns403_onForbidden() throws Exception {
        givenCreateVersionThrowsForbidden();

        ResultActions response = whenClientCreatesVersion(SOURCE, TARGET, ORIGINAL);

        thenStatusIsForbidden(response);
    }

    private void givenCreateVersionSucceedsWith(String result) {
        when(createLanguageVersionUseCase.execute(eq(SOURCE), eq(TARGET), eq(ORIGINAL))).thenReturn(result);
    }

    private void givenCreateVersionThrowsIllegalArgument() {
        when(createLanguageVersionUseCase.execute(eq(SOURCE), eq(TARGET), eq(ORIGINAL)))
                .thenThrow(new IllegalArgumentException("invalid"));
    }

    private void givenCreateVersionThrowsNotFound() {
        when(createLanguageVersionUseCase.execute(eq(SOURCE), eq(TARGET), eq(ORIGINAL)))
                .thenThrow(new java.util.NoSuchElementException("not found"));
    }

    private void givenCreateVersionThrowsForbidden() {
        doThrow(new SecurityException("forbidden")).when(createLanguageVersionUseCase)
                .execute(eq(SOURCE), eq(TARGET), eq(ORIGINAL));
    }

    private ResultActions whenClientCreatesVersion(String source, String target, String original) throws Exception {
        String body = asJson(source, target, original);
        return mockMvc.perform(post("/api/translation/create-version")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private void thenResponseIsOkWithBody(ResultActions response, String expectedBody) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
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

    private void thenUseCaseWasCalledWith(String source, String target, String original) {
        verify(createLanguageVersionUseCase).execute(eq(source), eq(target), eq(original));
    }

    private String asJson(String source, String target, String original) {
        return "{"
                + "\"sourceLanguage\":\"" + escape(source) + "\","
                + "\"targetLanguage\":\"" + escape(target) + "\","
                + "\"originalText\":\"" + escape(original) + "\""
                + "}";
    }

    private String escape(String v) { return v.replace("\\", "\\\\").replace("\"", "\\\""); }
}
