package com.amool.application.usecase;

import com.amool.application.port.out.FormatPort;
import com.amool.domain.model.Format;
import com.amool.application.usecases.ObtainAllFormatsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObtainAllFormatsUseCaseTest {

    private FormatPort formatPort;
    private ObtainAllFormatsUseCase useCase;

    @BeforeEach
    public void setUp() {
        formatPort = Mockito.mock(FormatPort.class);
        useCase = new ObtainAllFormatsUseCase(formatPort);
    }

    @Test
    public void when_FormatsExist_ThenReturnSortedFormatsList() {
        Format format1 = createFormat(1L, "Novel");
        Format format2 = createFormat(2L, "Short Story");
        Format format3 = createFormat(3L, "Poetry");
        
        List<Format> mockFormats = Arrays.asList(format1, format2, format3);
        
        when(formatPort.getAll()).thenReturn(mockFormats);

        List<Format> result = useCase.execute();

        assertEquals(3, result.size());
        assertEquals("Novel", result.get(0).getName());
        assertEquals("Poetry", result.get(1).getName());
        assertEquals("Short Story", result.get(2).getName());
    }

    @Test
    public void when_NoFormatsExist_ThenReturnEmptyList() {
        when(formatPort.getAll()).thenReturn(Collections.emptyList());

        List<Format> result = useCase.execute();

        assertTrue(result.isEmpty());
    }

    private Format createFormat(Long id, String name) {
        Format format = new Format();
        format.setId(id);
        format.setName(name);
        return format;
    }
}
