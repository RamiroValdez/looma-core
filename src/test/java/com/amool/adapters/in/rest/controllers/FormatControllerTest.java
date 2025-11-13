package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.FormatDto;
import com.amool.application.usecases.ObtainAllFormatsUseCase;
import com.amool.domain.model.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FormatControllerTest {

    private FormatController formatController;
    private ObtainAllFormatsUseCase obtainAllFormatsUseCase;

    @BeforeEach
    public void setUp() {
        obtainAllFormatsUseCase = Mockito.mock(ObtainAllFormatsUseCase.class);
        formatController = new FormatController(obtainAllFormatsUseCase);
    }

    @Test
    @DisplayName("GET /api/format/obtain-all - Should return list of formats when available")
    public void obtainAllFormats_shouldReturnList_whenFormatsExist() {
        // Given
        List<Format> formats = givenFormats(
                givenFormat(1L, "Audiobook"),
                givenFormat(2L, "Comic")
        );
        givenUseCaseWillReturn(formats);

        // When
        ResponseEntity<List<FormatDto>> response = whenClientRequestsAllFormats();

        // Then
        thenShouldReturnOk(response);
        thenResponseContainsFormatsNamed(response, "Audiobook", "Comic");
        thenUseCaseWasCalledOnce();
    }

    @Test
    @DisplayName("GET /api/format/obtain-all - Should return 404 when no formats found (empty list)")
    public void obtainAllFormats_shouldReturnNotFound_whenEmptyList() {
        // Given
        givenUseCaseWillReturn(List.of());

        // When
        ResponseEntity<List<FormatDto>> response = whenClientRequestsAllFormats();

        // Then
        thenShouldReturnNotFound(response);
        thenUseCaseWasCalledOnce();
    }

    @Test
    @DisplayName("GET /api/format/obtain-all - Should return 404 when use case returns null")
    public void obtainAllFormats_shouldReturnNotFound_whenNull() {
        // Given
        givenUseCaseWillReturnNull();

        // When
        ResponseEntity<List<FormatDto>> response = whenClientRequestsAllFormats();

        // Then
        thenShouldReturnNotFound(response);
        thenUseCaseWasCalledOnce();
    }

    // ===== Given =====
    private Format givenFormat(Long id, String name) {
        Format f = new Format();
        f.setId(id);
        f.setName(name);
        return f;
    }

    private List<Format> givenFormats(Format... formats) {
        return List.of(formats);
    }

    private void givenUseCaseWillReturn(List<Format> formats) {
        when(obtainAllFormatsUseCase.execute()).thenReturn(formats);
    }

    private void givenUseCaseWillReturnNull() {
        when(obtainAllFormatsUseCase.execute()).thenReturn(null);
    }

    // ===== When =====
    private ResponseEntity<List<FormatDto>> whenClientRequestsAllFormats() {
        return formatController.obtainAllFormats();
    }

    // ===== Then =====
    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenShouldReturnNotFound(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void thenResponseContainsFormatsNamed(ResponseEntity<List<FormatDto>> response, String... expectedNames) {
        assertNotNull(response.getBody());
        assertEquals(expectedNames.length, response.getBody().size());
        for (int i = 0; i < expectedNames.length; i++) {
            assertEquals(expectedNames[i], response.getBody().get(i).getName());
        }
    }

    private void thenUseCaseWasCalledOnce() {
        verify(obtainAllFormatsUseCase, times(1)).execute();
    }
}
