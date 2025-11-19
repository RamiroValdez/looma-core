package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChapterWithContentDto;
import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.adapters.in.rest.dtos.SchedulePublicationRequestDto;
import com.amool.adapters.in.rest.dtos.UpdateChapterContentRequest;
import com.amool.application.usecases.*;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import com.amool.domain.model.ChapterWithContent;
import com.amool.domain.model.ChapterWithContentResult;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChapterControllerTest {

    private ChapterController chapterController;
    private GetChapterWithContentUseCase getChapterWithContentUseCase;
    private DeleteChapterUseCase deleteChapterUseCase;
    private PublishChapterUseCase publishChapterUseCase;
    private SchedulePublicationUseCase schedulePublicationUseCase;
    private CancelScheduledPublicationUseCase cancelScheduledPublicationUseCase;
    private UpdateChapterContentUseCase updateChapterContentUseCase;
    private ToggleChapterLikeUseCase toggleChapterLikeUseCase;
    private CreateWorkNotification createWorkNotification;

    private static final Long TEST_WORK_ID = 1L;
    private static final Long TEST_CHAPTER_ID = 10L;
    private static final Long TEST_USER_ID = 100L;
    private static final String TEST_LANGUAGE = "es";

    private JwtUserPrincipal testUserPrincipal;

    @BeforeEach
    public void setUp() {
        getChapterWithContentUseCase = Mockito.mock(GetChapterWithContentUseCase.class);
        deleteChapterUseCase = Mockito.mock(DeleteChapterUseCase.class);
        publishChapterUseCase = Mockito.mock(PublishChapterUseCase.class);
        schedulePublicationUseCase = Mockito.mock(SchedulePublicationUseCase.class);
        cancelScheduledPublicationUseCase = Mockito.mock(CancelScheduledPublicationUseCase.class);
        updateChapterContentUseCase = Mockito.mock(UpdateChapterContentUseCase.class);
        toggleChapterLikeUseCase = Mockito.mock(ToggleChapterLikeUseCase.class);
        createWorkNotification = Mockito.mock(CreateWorkNotification.class);

        chapterController = new ChapterController(
                getChapterWithContentUseCase,
                deleteChapterUseCase,
                publishChapterUseCase,
                schedulePublicationUseCase,
                cancelScheduledPublicationUseCase,
                updateChapterContentUseCase,
                toggleChapterLikeUseCase,
                createWorkNotification
        );

        testUserPrincipal = new JwtUserPrincipal(
                TEST_USER_ID,
                "test@example.com",
                "Test",
                "User",
                "testuser"
        );

        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("GET /api/work/{workId}/chapter/{chapterId} - Should return chapter when found")
    public void getChapter_shouldReturnChapter_whenFound() {
        givenChapterExists();

        ResponseEntity<ChapterWithContentDto> response = whenGetChapter();

        thenResponseIsOk(response);
        thenResponseContainsChapterData(response);
        thenGetChapterUseCaseWasInvoked();
    }

    @Test
    @DisplayName("GET /api/work/{workId}/chapter/{chapterId} - Should return 404 when not found")
    public void getChapter_shouldReturn404_whenNotFound() {
        givenChapterDoesNotExist();

        ResponseEntity<ChapterWithContentDto> response = whenGetChapter();

        thenResponseIsNotFound(response);
        thenResponseBodyIsNull(response);
    }


    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/content - Should update content successfully")
    public void updateChapterContent_shouldUpdateContent_whenValid() {
        UpdateChapterContentRequest request = givenValidUpdateContentRequest();
        givenContentUpdateWillSucceed();

        ResponseEntity<ChapterContent> response = whenUpdateChapterContent(request);

        thenResponseIsOk(response);
        thenUpdateContentUseCaseWasInvoked();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/content - Should return 400 when IDs don't match")
    public void updateChapterContent_shouldReturn400_whenIdsMismatch() {
        UpdateChapterContentRequest request = givenUpdateContentRequestWithMismatchedIds();

        ResponseEntity<ChapterContent> response = whenUpdateChapterContent(request);

        thenResponseIsBadRequest(response);
        thenUpdateContentUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/content - Should return 403 when forbidden")
    public void updateChapterContent_shouldReturn403_whenForbidden() {
        UpdateChapterContentRequest request = givenValidUpdateContentRequest();
        givenContentUpdateWillBeForbidden();

        ResponseEntity<ChapterContent> response = whenUpdateChapterContent(request);

        thenResponseIsForbidden(response);
    }


    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/delete - Should delete chapter successfully")
    public void deleteChapter_shouldDeleteChapter_whenValid() {
        givenChapterCanBeDeleted();

        ResponseEntity<Void> response = whenDeleteChapter();

        thenResponseIsNoContent(response);
        thenDeleteChapterUseCaseWasInvoked();
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/delete - Should return 404 when not found")
    public void deleteChapter_shouldReturn404_whenNotFound() {
        givenChapterToDeleteDoesNotExist();

        ResponseEntity<Void> response = whenDeleteChapter();

        thenResponseIsNotFound(response);
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/delete - Should return 403 when forbidden")
    public void deleteChapter_shouldReturn403_whenForbidden() {
        givenChapterDeleteWillBeForbidden();

        ResponseEntity<Void> response = whenDeleteChapter();

        thenResponseIsForbidden(response);
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/delete - Should return 409 when illegal state")
    public void deleteChapter_shouldReturn409_whenIllegalState() {
        givenChapterDeleteWillFailDueToIllegalState();

        ResponseEntity<Void> response = whenDeleteChapter();

        thenResponseIsConflict(response);
    }


    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/publish - Should publish chapter successfully")
    public void publishChapter_shouldPublishChapter_whenValid() {
        givenUserIsAuthenticated();
        givenChapterCanBePublished();

        ResponseEntity<Void> response = whenPublishChapter();

        thenResponseIsNoContent(response);
        thenPublishChapterUseCaseWasInvoked();
        thenWorkNotificationWasCreated();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/publish - Should return 401 when not authenticated")
    public void publishChapter_shouldReturn401_whenNotAuthenticated() {
        givenUserIsNotAuthenticated();

        ResponseEntity<Void> response = whenPublishChapter();

        thenResponseIsUnauthorized(response);
        thenPublishChapterUseCaseWasNotInvoked();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/publish - Should return 404 when not found")
    public void publishChapter_shouldReturn404_whenNotFound() {
        givenUserIsAuthenticated();
        givenChapterToPublishDoesNotExist();

        ResponseEntity<Void> response = whenPublishChapter();

        thenResponseIsNotFound(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/publish - Should return 403 when forbidden")
    public void publishChapter_shouldReturn403_whenForbidden() {
        givenUserIsAuthenticated();
        givenChapterPublishWillBeForbidden();

        ResponseEntity<Void> response = whenPublishChapter();

        thenResponseIsForbidden(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/publish - Should return 409 when already published")
    public void publishChapter_shouldReturn409_whenAlreadyPublished() {
        givenUserIsAuthenticated();
        givenChapterIsAlreadyPublished();

        ResponseEntity<Void> response = whenPublishChapter();

        thenResponseIsConflict(response);
    }


    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should schedule chapter successfully")
    public void scheduleChapter_shouldScheduleChapter_whenValid() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();
        givenChapterCanBeScheduled();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsNoContent(response);
        thenSchedulePublicationUseCaseWasInvoked();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 401 when not authenticated")
    public void scheduleChapter_shouldReturn401_whenNotAuthenticated() {
        givenUserIsNotAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsUnauthorized(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 400 when invalid date format")
    public void scheduleChapter_shouldReturn400_whenInvalidDateFormat() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenInvalidScheduleRequest();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsBadRequest(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 403 when forbidden")
    public void scheduleChapter_shouldReturn403_whenForbidden() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();
        givenSchedulePublicationWillBeForbidden();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsForbidden(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 404 when chapter not found")
    public void scheduleChapter_shouldReturn404_whenNotFound() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();
        givenChapterToScheduleDoesNotExist();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsNotFound(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 400 when date in past")
    public void scheduleChapter_shouldReturn400_whenDateInPast() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();
        givenScheduleDateIsInThePast();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsBadRequest(response);
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/schedule - Should return 409 when illegal state")
    public void scheduleChapter_shouldReturn409_whenIllegalState() {
        givenUserIsAuthenticated();
        SchedulePublicationRequestDto request = givenValidScheduleRequest();
        givenChapterScheduleWillFailDueToIllegalState();

        ResponseEntity<Void> response = whenScheduleChapter(request);

        thenResponseIsConflict(response);
    }


    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/schedule - Should cancel schedule successfully")
    public void cancelSchedule_shouldCancelSchedule_whenValid() {
        givenUserIsAuthenticated();
        givenScheduleCanBeCancelled();

        ResponseEntity<Void> response = whenCancelSchedule();

        thenResponseIsNoContent(response);
        thenCancelScheduledPublicationUseCaseWasInvoked();
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/schedule - Should return 401 when not authenticated")
    public void cancelSchedule_shouldReturn401_whenNotAuthenticated() {
        givenUserIsNotAuthenticated();

        ResponseEntity<Void> response = whenCancelSchedule();

        thenResponseIsUnauthorized(response);
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/schedule - Should return 404 when not found")
    public void cancelSchedule_shouldReturn404_whenNotFound() {
        givenUserIsAuthenticated();
        givenScheduleToCancelDoesNotExist();

        ResponseEntity<Void> response = whenCancelSchedule();

        thenResponseIsNotFound(response);
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/schedule - Should return 403 when forbidden")
    public void cancelSchedule_shouldReturn403_whenForbidden() {
        givenUserIsAuthenticated();
        givenCancelScheduleWillBeForbidden();

        ResponseEntity<Void> response = whenCancelSchedule();

        thenResponseIsForbidden(response);
    }

    @Test
    @DisplayName("DELETE /api/work/{workId}/chapter/{chapterId}/schedule - Should return 409 when illegal state")
    public void cancelSchedule_shouldReturn409_whenIllegalState() {
        givenUserIsAuthenticated();
        givenCancelScheduleWillFailDueToIllegalState();

        ResponseEntity<Void> response = whenCancelSchedule();

        thenResponseIsConflict(response);
    }


    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/like - Should toggle like successfully")
    public void toggleChapterLike_shouldToggleLike_whenValid() {
        givenChapterCanBeLiked();

        ResponseEntity<LikeResponseDto> response = whenToggleChapterLike();

        thenResponseIsOk(response);
        thenResponseContainsLikeData(response, true);
        thenToggleChapterLikeUseCaseWasInvoked();
    }

    @Test
    @DisplayName("POST /api/work/{workId}/chapter/{chapterId}/like - Should unlike when toggling off")
    public void toggleChapterLike_shouldUnlike_whenTogglingOff() {
        givenChapterCanBeUnliked();

        ResponseEntity<LikeResponseDto> response = whenToggleChapterLike();

        thenResponseIsOk(response);
        thenResponseContainsLikeData(response, false);
        thenToggleChapterLikeUseCaseWasInvoked();
    }


    private ChapterWithContentResult createChapterWithContentResult() {
        Chapter chapter = new Chapter();
        chapter.setId(TEST_CHAPTER_ID);
        chapter.setTitle("Test Chapter");
        chapter.setPrice(BigDecimal.valueOf(9.99));

        ChapterWithContent chapterWithContent = new ChapterWithContent(chapter, "Chapter content in Spanish");
        List<String> availableLanguages = List.of("es", "en");

        return new ChapterWithContentResult(chapterWithContent, "Chapter content in Spanish", availableLanguages);
    }

    private ChapterContent createChapterContent() {
        return new ChapterContent(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                java.util.Map.of(TEST_LANGUAGE, "Updated content"),
                TEST_LANGUAGE
        );
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                testUserPrincipal,
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private void givenChapterExists() {
        ChapterWithContentResult result = createChapterWithContentResult();
        when(getChapterWithContentUseCase.execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_LANGUAGE))
                .thenReturn(Optional.of(result));
    }

    private void givenChapterDoesNotExist() {
        when(getChapterWithContentUseCase.execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_LANGUAGE))
                .thenReturn(Optional.empty());
    }

    private UpdateChapterContentRequest givenValidUpdateContentRequest() {
        return new UpdateChapterContentRequest(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                TEST_LANGUAGE,
                "Updated content"
        );
    }

    private UpdateChapterContentRequest givenUpdateContentRequestWithMismatchedIds() {
        return new UpdateChapterContentRequest(
                "999",
                TEST_CHAPTER_ID.toString(),
                TEST_LANGUAGE,
                "Content"
        );
    }

    private void givenContentUpdateWillSucceed() {
        ChapterContent updatedContent = createChapterContent();
        when(updateChapterContentUseCase.execute(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                TEST_LANGUAGE,
                "Updated content",
                TEST_USER_ID
        )).thenReturn(updatedContent);
    }

    private void givenContentUpdateWillBeForbidden() {
        when(updateChapterContentUseCase.execute(any(), any(), any(), any(), any()))
                .thenThrow(new SecurityException("Not authorized"));
    }

    private void givenChapterCanBeDeleted() {
        doNothing().when(deleteChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterToDeleteDoesNotExist() {
        doThrow(new NoSuchElementException("Chapter not found"))
                .when(deleteChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterDeleteWillBeForbidden() {
        doThrow(new SecurityException("Not authorized"))
                .when(deleteChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterDeleteWillFailDueToIllegalState() {
        doThrow(new IllegalStateException("Cannot delete published chapter"))
                .when(deleteChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenUserIsAuthenticated() {
        setSecurityContext();
    }

    private void givenUserIsNotAuthenticated() {
        SecurityContextHolder.clearContext();
    }

    private void givenChapterCanBePublished() {
        doNothing().when(publishChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
        when(createWorkNotification.execute(TEST_WORK_ID, TEST_USER_ID, TEST_CHAPTER_ID)).thenReturn(true);
    }

    private void givenChapterToPublishDoesNotExist() {
        doThrow(new NoSuchElementException("Chapter not found"))
                .when(publishChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterPublishWillBeForbidden() {
        doThrow(new SecurityException("Not authorized"))
                .when(publishChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterIsAlreadyPublished() {
        doThrow(new IllegalStateException("Chapter already published"))
                .when(publishChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private SchedulePublicationRequestDto givenValidScheduleRequest() {
        return new SchedulePublicationRequestDto("2025-12-31T10:00:00-03:00");
    }

    private SchedulePublicationRequestDto givenInvalidScheduleRequest() {
        return new SchedulePublicationRequestDto("invalid-date");
    }

    private void givenChapterCanBeScheduled() {
        doNothing().when(schedulePublicationUseCase).execute(
                eq(TEST_WORK_ID),
                eq(TEST_CHAPTER_ID),
                any(Instant.class),
                eq(TEST_USER_ID)
        );
    }

    private void givenSchedulePublicationWillBeForbidden() {
        doThrow(new SecurityException("Not authorized"))
                .when(schedulePublicationUseCase).execute(any(), any(), any(), any());
    }

    private void givenChapterToScheduleDoesNotExist() {
        doThrow(new NoSuchElementException("Chapter not found"))
                .when(schedulePublicationUseCase).execute(any(), any(), any(), any());
    }

    private void givenScheduleDateIsInThePast() {
        doThrow(new IllegalArgumentException("Date must be in the future"))
                .when(schedulePublicationUseCase).execute(any(), any(), any(), any());
    }

    private void givenChapterScheduleWillFailDueToIllegalState() {
        doThrow(new IllegalStateException("Chapter already published"))
                .when(schedulePublicationUseCase).execute(any(), any(), any(), any());
    }

    private void givenScheduleCanBeCancelled() {
        doNothing().when(cancelScheduledPublicationUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenScheduleToCancelDoesNotExist() {
        doThrow(new NoSuchElementException("Schedule not found"))
                .when(cancelScheduledPublicationUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenCancelScheduleWillBeForbidden() {
        doThrow(new SecurityException("Not authorized"))
                .when(cancelScheduledPublicationUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenCancelScheduleWillFailDueToIllegalState() {
        doThrow(new IllegalStateException("Cannot cancel"))
                .when(cancelScheduledPublicationUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void givenChapterCanBeLiked() {
        LikeResponseDto response = new LikeResponseDto(TEST_WORK_ID, 5L, true);
        when(toggleChapterLikeUseCase.execute(TEST_CHAPTER_ID, TEST_USER_ID))
                .thenReturn(response);
    }

    private void givenChapterCanBeUnliked() {
        LikeResponseDto response = new LikeResponseDto(TEST_WORK_ID, 4L, false);
        when(toggleChapterLikeUseCase.execute(TEST_CHAPTER_ID, TEST_USER_ID))
                .thenReturn(response);
    }


    private ResponseEntity<ChapterWithContentDto> whenGetChapter() {
        return chapterController.getChapter(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                TEST_LANGUAGE
        );
    }

    private ResponseEntity<ChapterContent> whenUpdateChapterContent(UpdateChapterContentRequest request) {
        return chapterController.updateChapterContent(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                request,
                testUserPrincipal
        );
    }

    private ResponseEntity<Void> whenDeleteChapter() {
        return chapterController.deleteChapter(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                testUserPrincipal
        );
    }

    private ResponseEntity<Void> whenPublishChapter() {
        return chapterController.publishChapter(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString()
        );
    }

    private ResponseEntity<Void> whenScheduleChapter(SchedulePublicationRequestDto request) {
        return chapterController.scheduleChapter(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                request
        );
    }

    private ResponseEntity<Void> whenCancelSchedule() {
        return chapterController.cancelSchedule(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString()
        );
    }

    private ResponseEntity<LikeResponseDto> whenToggleChapterLike() {
        return chapterController.toggleChapterLike(
                TEST_WORK_ID,
                TEST_CHAPTER_ID,
                testUserPrincipal
        );
    }


    private void thenResponseIsOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private void thenResponseIsNoContent(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private void thenResponseIsNotFound(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void thenResponseIsBadRequest(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenResponseIsUnauthorized(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private void thenResponseIsForbidden(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private void thenResponseIsConflict(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    private void thenResponseBodyIsNull(ResponseEntity<?> response) {
        assertNull(response.getBody());
    }

    private void thenResponseContainsChapterData(ResponseEntity<ChapterWithContentDto> response) {
        ChapterWithContentDto body = response.getBody();
        assertEquals(TEST_CHAPTER_ID, body.id());
        assertEquals("Test Chapter", body.title());
    }

    private void thenGetChapterUseCaseWasInvoked() {
        verify(getChapterWithContentUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_LANGUAGE);
    }

    private void thenUpdateContentUseCaseWasInvoked() {
        verify(updateChapterContentUseCase).execute(
                TEST_WORK_ID.toString(),
                TEST_CHAPTER_ID.toString(),
                TEST_LANGUAGE,
                "Updated content",
                TEST_USER_ID
        );
    }

    private void thenUpdateContentUseCaseWasNotInvoked() {
        verify(updateChapterContentUseCase, never()).execute(any(), any(), any(), any(), any());
    }

    private void thenDeleteChapterUseCaseWasInvoked() {
        verify(deleteChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void thenPublishChapterUseCaseWasInvoked() {
        verify(publishChapterUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void thenPublishChapterUseCaseWasNotInvoked() {
        verify(publishChapterUseCase, never()).execute(any(), any(), any());
    }

    private void thenWorkNotificationWasCreated() {
        verify(createWorkNotification).execute(TEST_WORK_ID, TEST_USER_ID, TEST_CHAPTER_ID);
    }

    private void thenSchedulePublicationUseCaseWasInvoked() {
        verify(schedulePublicationUseCase).execute(
                eq(TEST_WORK_ID),
                eq(TEST_CHAPTER_ID),
                any(Instant.class),
                eq(TEST_USER_ID)
        );
    }

    private void thenCancelScheduledPublicationUseCaseWasInvoked() {
        verify(cancelScheduledPublicationUseCase).execute(TEST_WORK_ID, TEST_CHAPTER_ID, TEST_USER_ID);
    }

    private void thenResponseContainsLikeData(ResponseEntity<LikeResponseDto> response, boolean isLiked) {
        LikeResponseDto body = response.getBody();
        assertEquals(TEST_WORK_ID, body.getWorkId());
        assertEquals(isLiked ? 5L : 4L, body.getLikeCount());
        assertEquals(isLiked, body.isLikedByUser());
    }

    private void thenToggleChapterLikeUseCaseWasInvoked() {
        verify(toggleChapterLikeUseCase).execute(TEST_CHAPTER_ID, TEST_USER_ID);
    }
}
