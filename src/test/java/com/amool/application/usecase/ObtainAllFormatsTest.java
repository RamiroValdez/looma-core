package com.amool.application.usecase;

import com.amool.application.port.out.FormatPort;
import com.amool.domain.model.Format;
import com.amool.application.usecases.ObtainAllFormats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObtainAllFormatsTest {

    private FormatPort formatPort;
    private ObtainAllFormats useCase;

    @BeforeEach
    public void setUp() {
        formatPort = Mockito.mock(FormatPort.class);
        useCase = new ObtainAllFormats(formatPort);
    }

    private void givenFormatsExist(List<Format> formats) {
        when(formatPort.getAll()).thenReturn(formats);
    }

    private void givenNoFormatsExist() {
        when(formatPort.getAll()).thenReturn(Collections.emptyList());
    }

    private List<Format> whenObtainAllFormats() {
        return useCase.execute();
    }

    private void thenFormatsOrdered(List<Format> result, int expectedSize, List<String> expectedNamesInOrder) {
        assertEquals(expectedSize, result.size(), "Cantidad de formatos inesperada");
        for (int i = 0; i < expectedNamesInOrder.size(); i++) {
            assertEquals(expectedNamesInOrder.get(i), result.get(i).getName(), "Orden inesperado en índice " + i);
        }
    }

    private void thenFormatsEmpty(List<Format> result) {
        assertTrue(result.isEmpty(), "Se esperaba lista vacía");
    }

    @Test
    public void when_FormatsExist_ThenReturnSortedFormatsList() {
        Format format1 = createFormat(1L, "Novel");
        Format format2 = createFormat(2L, "Short Story");
        Format format3 = createFormat(3L, "Poetry");
        givenFormatsExist(Arrays.asList(format1, format2, format3));

        List<Format> result = whenObtainAllFormats();

        thenFormatsOrdered(result, 3, List.of("Novel", "Poetry", "Short Story"));
    }

    @Test
    public void when_NoFormatsExist_ThenReturnEmptyList() {
        givenNoFormatsExist();

        List<Format> result = whenObtainAllFormats();

        thenFormatsEmpty(result);
    }

    private Format createFormat(Long id, String name) {
        Format format = new Format();
        format.setId(id);
        format.setName(name);
        return format;
    }
}
