package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateWorkDto;
import com.amool.adapters.in.rest.dtos.TagSuggestionRequestDto;
import com.amool.adapters.in.rest.dtos.TagSuggestionResponseDto;
import com.amool.application.usecases.CreateAuthorNotification;
import com.amool.application.usecases.CreateWork;
import com.amool.application.usecases.SuggestTags;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CreateWorkControllerTest {

    private CreateWorkController createWorkController;
    private CreateWork createWork;
    private SuggestTags suggestTags;
    private CreateAuthorNotification createAuthorNotification;

    private static final Long TEST_USER_ID = 100L;
    private static final Long TEST_WORK_ID = 1L;
    private static final String TEST_TITLE = "Mi Nueva Obra";
    private static final String TEST_DESCRIPTION = "Una descripción detallada de la obra";
    private static final List<Long> TEST_CATEGORY_IDS = List.of(1L, 2L);
    private static final Long TEST_FORMAT_ID = 1L;
    private static final Long TEST_LANGUAGE_ID = 1L;
    private static final BigDecimal TEST_PRICE = new BigDecimal("9.99");
    private static final Set<String> TEST_TAG_IDS = Set.of("tag1", "tag2", "tag3");
    private static final String TEST_COVER_IA_URL = "https://example.com/cover.jpg";

    private JwtUserPrincipal testUserPrincipal;

    @BeforeEach
    public void setUp() {
        createWork = Mockito.mock(CreateWork.class);
        suggestTags = Mockito.mock(SuggestTags.class);
        createAuthorNotification = Mockito.mock(CreateAuthorNotification.class);

        createWorkController = new CreateWorkController(
                createWork,
                suggestTags,
                createAuthorNotification
        );

        testUserPrincipal = new JwtUserPrincipal(
                TEST_USER_ID,
                "test@example.com",
                "Test",
                "User",
                "testuser"
        );
    }


    @Test
    @DisplayName("POST /api/create-work/save - Should create work successfully with all files")
    public void saveWork_shouldCreateWork_whenAllFilesProvided() throws Exception {
        CreateWorkDto workDto = givenValidWorkDto();
        MultipartFile coverFile = givenValidCoverFile();
        MultipartFile bannerFile = givenValidBannerFile();
        givenWorkCreationWillSucceed();

        ResponseEntity<Long> response = whenSaveWork(workDto, coverFile, bannerFile);

        thenResponseIsOk(response);
        thenResponseContainsWorkId(response, TEST_WORK_ID);
        thenCreateWorkUseCaseWasInvoked();
        thenAuthorNotificationWasCreated();
    }

    @Test
    @DisplayName("POST /api/create-work/save - Should create work successfully without cover file")
    public void saveWork_shouldCreateWork_whenNoCoverFile() throws Exception {
        CreateWorkDto workDto = givenValidWorkDto();
        MultipartFile bannerFile = givenValidBannerFile();
        givenWorkCreationWillSucceed();

        ResponseEntity<Long> response = whenSaveWork(workDto, null, bannerFile);

        thenResponseIsOk(response);
        thenResponseContainsWorkId(response, TEST_WORK_ID);
        verify(createWork).execute(
                eq(TEST_TITLE),
                eq(TEST_DESCRIPTION),
                eq(TEST_CATEGORY_IDS),
                eq(TEST_FORMAT_ID),
                eq(TEST_LANGUAGE_ID),
                eq(TEST_PRICE),
                eq(TEST_TAG_IDS),
                eq(TEST_COVER_IA_URL),
                isNull(),
                eq(bannerFile),
                eq(TEST_USER_ID)
        );
    }

    @Test
    @DisplayName("POST /api/create-work/save - Should return 400 when creation fails")
    public void saveWork_shouldReturnBadRequest_whenCreationFails() throws Exception {
        CreateWorkDto workDto = givenValidWorkDto();
        MultipartFile coverFile = givenValidCoverFile();
        MultipartFile bannerFile = givenValidBannerFile();
        givenWorkCreationWillFail();

        ResponseEntity<Long> response = whenSaveWork(workDto, coverFile, bannerFile);

        thenResponseIsBadRequest(response);
        thenResponseBodyIsNull(response);
        thenAuthorNotificationWasNotCreated();
    }

    @Test
    @DisplayName("POST /api/create-work/save - Should create work with free price")
    public void saveWork_shouldCreateWork_whenPriceIsZero() throws Exception {
        CreateWorkDto workDto = givenWorkDtoWithPrice(BigDecimal.ZERO);
        MultipartFile bannerFile = givenValidBannerFile();
        givenWorkCreationWillSucceed();

        ResponseEntity<Long> response = whenSaveWork(workDto, null, bannerFile);

        thenResponseIsOk(response);
        verify(createWork).execute(
                anyString(),
                anyString(),
                anyList(),
                anyLong(),
                anyLong(),
                eq(BigDecimal.ZERO),
                anySet(),
                anyString(),
                isNull(),
                any(),
                eq(TEST_USER_ID)
        );
    }


    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should return tag suggestions")
    public void suggestTags_shouldReturnSuggestions_whenValidRequest() {
        TagSuggestionRequestDto request = givenValidTagSuggestionRequest();
        List<String> expectedSuggestions = givenTagSuggestionsWillBeReturned();

        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(request);

        thenResponseIsOk(response);
        thenResponseContainsSuggestions(response, expectedSuggestions);
        thenSuggestTagsUseCaseWasInvoked();
    }

    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should return 400 when request is null")
    public void suggestTags_shouldReturnBadRequest_whenRequestIsNull() {
        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(null);

        thenResponseIsBadRequest(response);
        thenSuggestTagsUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should return 400 when description is empty")
    public void suggestTags_shouldReturnBadRequest_whenDescriptionIsEmpty() {
        TagSuggestionRequestDto request = givenTagSuggestionRequestWithEmptyDescription();

        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(request);

        thenResponseIsBadRequest(response);
        thenSuggestTagsUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should return 400 when description is null")
    public void suggestTags_shouldReturnBadRequest_whenDescriptionIsNull() {
        TagSuggestionRequestDto request = givenTagSuggestionRequestWithNullDescription();

        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(request);

        thenResponseIsBadRequest(response);
        thenSuggestTagsUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should handle request without existing tags")
    public void suggestTags_shouldHandleRequest_whenNoExistingTags() {
        TagSuggestionRequestDto request = givenTagSuggestionRequestWithoutExistingTags();
        givenTagSuggestionsWillBeReturned();

        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(request);

        thenResponseIsOk(response);
        verify(suggestTags).execute(
                eq(TEST_DESCRIPTION),
                eq(TEST_TITLE),
                eq(Set.of())
        );
    }

    @Test
    @DisplayName("POST /api/create-work/suggest-tags - Should pass existing tags to use case")
    public void suggestTags_shouldPassExistingTags_whenProvided() {
        Set<String> existingTags = Set.of("fantasy", "adventure");
        TagSuggestionRequestDto request = givenTagSuggestionRequestWithExistingTags(existingTags);
        givenTagSuggestionsWillBeReturned();

        ResponseEntity<TagSuggestionResponseDto> response = whenSuggestTags(request);

        thenResponseIsOk(response);
        verify(suggestTags).execute(
                eq(TEST_DESCRIPTION),
                eq(TEST_TITLE),
                eq(existingTags)
        );
    }


    private CreateWorkDto givenValidWorkDto() {
        return new CreateWorkDto(
                TEST_TITLE,
                TEST_DESCRIPTION,
                TEST_CATEGORY_IDS,
                TEST_FORMAT_ID,
                TEST_LANGUAGE_ID,
                TEST_PRICE,
                TEST_TAG_IDS,
                TEST_COVER_IA_URL
        );
    }

    private CreateWorkDto givenWorkDtoWithPrice(BigDecimal price) {
        return new CreateWorkDto(
                TEST_TITLE,
                TEST_DESCRIPTION,
                TEST_CATEGORY_IDS,
                TEST_FORMAT_ID,
                TEST_LANGUAGE_ID,
                price,
                TEST_TAG_IDS,
                TEST_COVER_IA_URL
        );
    }

    private MultipartFile givenValidCoverFile() {
        return new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "cover content".getBytes()
        );
    }

    private MultipartFile givenValidBannerFile() {
        return new MockMultipartFile(
                "banner",
                "banner.jpg",
                "image/jpeg",
                "banner content".getBytes()
        );
    }

    private void givenWorkCreationWillSucceed() throws Exception {
        when(createWork.execute(
                anyString(),
                anyString(),
                anyList(),
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                anySet(),
                anyString(),
                any(),
                any(),
                anyLong()
        )).thenReturn(TEST_WORK_ID);
    }

    private void givenWorkCreationWillFail() throws Exception {
        when(createWork.execute(
                anyString(),
                anyString(),
                anyList(),
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                anySet(),
                anyString(),
                any(),
                any(),
                anyLong()
        )).thenThrow(new RuntimeException("Creation failed"));
    }

    private TagSuggestionRequestDto givenValidTagSuggestionRequest() {
        return new TagSuggestionRequestDto(
                TEST_DESCRIPTION,
                TEST_TITLE,
                Set.of("existing1", "existing2")
        );
    }

    private TagSuggestionRequestDto givenTagSuggestionRequestWithEmptyDescription() {
        return new TagSuggestionRequestDto(
                "",
                TEST_TITLE,
                Set.of()
        );
    }

    private TagSuggestionRequestDto givenTagSuggestionRequestWithNullDescription() {
        return new TagSuggestionRequestDto(
                null,
                TEST_TITLE,
                Set.of()
        );
    }

    private TagSuggestionRequestDto givenTagSuggestionRequestWithoutExistingTags() {
        return new TagSuggestionRequestDto(
                TEST_DESCRIPTION,
                TEST_TITLE,
                null
        );
    }

    private TagSuggestionRequestDto givenTagSuggestionRequestWithExistingTags(Set<String> existingTags) {
        return new TagSuggestionRequestDto(
                TEST_DESCRIPTION,
                TEST_TITLE,
                existingTags
        );
    }

    private List<String> givenTagSuggestionsWillBeReturned() {
        List<String> suggestions = List.of("fantasy", "adventure", "magic", "epic");
        when(suggestTags.execute(anyString(), anyString(), anySet()))
                .thenReturn(suggestions);
        return suggestions;
    }


    private ResponseEntity<Long> whenSaveWork(CreateWorkDto workDto, MultipartFile coverFile, MultipartFile bannerFile) {
        return createWorkController.saveWork(workDto, coverFile, bannerFile, testUserPrincipal);
    }

    private ResponseEntity<TagSuggestionResponseDto> whenSuggestTags(TagSuggestionRequestDto request) {
        return createWorkController.suggestTags(request);
    }


    private void thenResponseIsOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenResponseIsBadRequest(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenResponseContainsWorkId(ResponseEntity<Long> response, Long expectedWorkId) {
        assertNotNull(response.getBody());
        assertEquals(expectedWorkId, response.getBody());
    }

    private void thenResponseBodyIsNull(ResponseEntity<?> response) {
        assertNull(response.getBody());
    }

    private void thenResponseContainsSuggestions(ResponseEntity<TagSuggestionResponseDto> response, List<String> expectedSuggestions) {
        assertNotNull(response.getBody());
        assertEquals(expectedSuggestions, response.getBody().suggestions());
    }

    private void thenCreateWorkUseCaseWasInvoked() throws Exception {
        verify(createWork, times(1)).execute(
                anyString(),
                anyString(),
                anyList(),
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                anySet(),
                anyString(),
                any(),
                any(),
                anyLong()
        );
    }

    private void thenAuthorNotificationWasCreated() {
        verify(createAuthorNotification, times(1)).execute(eq(TEST_WORK_ID), eq(TEST_USER_ID));
    }

    private void thenAuthorNotificationWasNotCreated() {
        verify(createAuthorNotification, never()).execute(anyLong(), anyLong());
    }

    private void thenSuggestTagsUseCaseWasInvoked() {
        verify(suggestTags, times(1)).execute(anyString(), anyString(), anySet());
    }

    private void thenSuggestTagsUseCaseWasNotInvoked() {
        verify(suggestTags, never()).execute(anyString(), anyString(), anySet());
    }
}

