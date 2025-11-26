package com.amool.application.usecase;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.CreateWork;
import com.amool.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateWorkTest {

    private WorkPort workPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private TagPort tagPort;
    private LoadUserPort loadUserPort;
    private FormatPort formatPort;
    private LoadLanguagePort loadLanguagePort;
    private CategoryPort categoryPort;
    private ImagesService imagesService;
    private CreateWork useCase;

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
        
        useCase = new CreateWork(
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

    private Work givenWorkWillBeCreated(Long newWorkId) {
        Work work = new Work();
        work.setId(newWorkId);
        when(workPort.createWork(any(Work.class))).thenReturn(newWorkId);
        when(obtainWorkByIdPort.obtainWorkById(newWorkId)).thenReturn(Optional.of(work));
        return work;
    }

    private void givenUserExists(Long userId) {
        User user = new User();
        user.setId(userId);
        when(loadUserPort.getById(userId)).thenReturn(Optional.of(user));
    }

    private void givenUserNotFound(Long userId) {
        when(loadUserPort.getById(userId)).thenReturn(Optional.empty());
    }

    private void givenFormatExists(Long formatId) {
        Format format = new Format();
        format.setId(formatId);
        when(formatPort.getById(formatId)).thenReturn(Optional.of(format));
    }

    private void givenLanguageExists(Long languageId) {
        Language language = new Language();
        language.setId(languageId);
        when(loadLanguagePort.loadLanguageById(languageId)).thenReturn(Optional.of(language));
    }

    private void givenCategoryExists(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        when(categoryPort.getCategoryById(categoryId)).thenReturn(Optional.of(category));
    }

    private void givenImagesUploadSucceeds(String coverUrl, String bannerUrl) throws Exception {
        when(imagesService.uploadCoverImage(any(), anyString())).thenReturn(coverUrl);
        when(imagesService.uploadBannerImage(any(), anyString())).thenReturn(bannerUrl);
    }

    private void givenTagIds(Set<String> tagIds) {
        for (String tag : tagIds) {
            when(tagPort.searchTag(tag)).thenReturn(Optional.empty());
            when(tagPort.createTag(tag)).thenReturn(100L); // id ficticio
        }
    }

    private Long whenCreateWork(String title, String description, List<Long> categoryIds, Long formatId, Long languageId,
                                BigDecimal price, Set<String> tagIds, String coverIaUrl, MultipartFile coverFile, MultipartFile bannerFile,
                                Long userId) throws Exception {
        return useCase.execute(title, description, categoryIds, formatId, languageId, price, tagIds, coverIaUrl, coverFile, bannerFile, userId);
    }

    private void thenWorkIdEquals(Long actual, Long expected) {
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    private void thenWorkPersisted() {
        verify(workPort).createWork(any(Work.class));
    }

    private void thenThrowsIllegalArgument(ThrowingRunnable action) {
        assertThrows(IllegalArgumentException.class, () -> action.run());
    }

    @Test
    public void when_CreateWorkWithValidData_ThenReturnWorkId() throws Exception {
        Long userId = 1L; Long workId = 1L; Long formatId = 1L; Long languageId = 1L; Long categoryId = 1L;
        givenWorkWillBeCreated(workId);
        givenUserExists(userId);
        givenFormatExists(formatId);
        givenLanguageExists(languageId);
        givenCategoryExists(categoryId);
        givenImagesUploadSucceeds("cover-url", "banner-url");
        givenTagIds(Set.of("tag1"));

        Long result = whenCreateWork(
            "Test Work",
            "Test Description",
            List.of(categoryId),
            formatId,
            languageId,
            BigDecimal.valueOf(19.99),
            Set.of("tag1"),
            "cover-url", // coverIaUrl
            null,          // coverFile
            null,          // bannerFile
            userId
        );

        thenWorkIdEquals(result, workId);
        thenWorkPersisted();
    }

    @Test
    public void when_UserNotFound_ThenThrowException() {
        Long missingUserId = 1L;
        givenUserNotFound(missingUserId);

        thenThrowsIllegalArgument(() -> whenCreateWork(
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
                missingUserId
        ));
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }
}
