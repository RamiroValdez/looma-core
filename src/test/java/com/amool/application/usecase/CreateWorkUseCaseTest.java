package com.amool.application.usecase;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.CreateWorkUseCase;
import com.amool.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CreateWorkUseCaseTest {

    private WorkPort workPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private TagPort tagPort;
    private LoadUserPort loadUserPort;
    private FormatPort formatPort;
    private LoadLanguagePort loadLanguagePort;
    private CategoryPort categoryPort;
    private ImagesService imagesService;
    private CreateWorkUseCase useCase;

    @BeforeEach
    void setUp() {
        workPort = Mockito.mock(WorkPort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        tagPort = Mockito.mock(TagPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);
        formatPort = Mockito.mock(FormatPort.class);
        loadLanguagePort = Mockito.mock(LoadLanguagePort.class);
        categoryPort = Mockito.mock(CategoryPort.class);
        imagesService = Mockito.mock(ImagesService.class);
        
        useCase = new CreateWorkUseCase(
            workPort,
            obtainWorkByIdPort,
            tagPort,
            loadUserPort,
            formatPort,
            loadLanguagePort,
            categoryPort,
            imagesService
        );
    }

    @Test
    public void when_CreateWorkWithValidData_ThenReturnWorkId() throws Exception {
        Work work = new Work();
        work.setId(1L);
        
        User user = new User();
        Format format = new Format();
        Language language = new Language();
        Category category = new Category();
        
        when(workPort.createWork(any(Work.class))).thenReturn(1L);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(loadUserPort.getById(anyLong())).thenReturn(Optional.of(user));
        when(formatPort.getById(anyLong())).thenReturn(Optional.of(format));
        when(loadLanguagePort.loadLanguageById(anyLong())).thenReturn(Optional.of(language));
        when(categoryPort.getCategoryById(anyLong())).thenReturn(Optional.of(category));
        when(imagesService.uploadCoverImage(any(), anyString())).thenReturn("cover-url");
        when(imagesService.uploadBannerImage(any(), anyString())).thenReturn("banner-url");

        Long result = useCase.execute(
            "Test Work",
            "Test Description",
            List.of(1L),
            1L,
            1L,
                BigDecimal.valueOf(19.99),
            Set.of("tag1"),
            "cover-url",
            null,
            null,
            1L
        );

        assertNotNull(result);
        assertEquals(1L, result);
        verify(workPort).createWork(any(Work.class));
    }

    @Test
    public void when_UserNotFound_ThenThrowException() {
        when(loadUserPort.getById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            useCase.execute(
                "Test Work",
                "Test Description",
                List.of(1L),
                1L,
                1L,
                    BigDecimal.valueOf(0.0),
                Set.of("tag1"),
                "cover-url",
                null,
                null,
                1L
            )
        );
    }
}
