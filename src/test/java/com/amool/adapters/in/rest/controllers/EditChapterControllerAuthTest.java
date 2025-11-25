package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.application.usecases.ExtractTextFromFile;
import com.amool.application.usecases.GetChapterForEdit;
import com.amool.application.usecases.UpdateChapter;
import com.amool.application.usecases.ValidateChapterAccess;
import com.amool.domain.model.Chapter;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EditChapterControllerAuthTest {

    private GetChapterForEdit getChapterForEdit;
    private UpdateChapter updateChapter;
    private ExtractTextFromFile extractTextFromFile;
    private ValidateChapterAccess validateChapterAccess;

    private EditChapterController controller;

    private SecurityContext originalContext;

    @BeforeEach
    void setup() {
        getChapterForEdit = mock(GetChapterForEdit.class);
        updateChapter = mock(UpdateChapter.class);
        extractTextFromFile = mock(ExtractTextFromFile.class);
        validateChapterAccess = mock(ValidateChapterAccess.class);

        controller = new EditChapterController(
                getChapterForEdit,
                updateChapter,
                extractTextFromFile,
                validateChapterAccess
        );
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (originalContext != null) {
            SecurityContextHolder.setContext(originalContext);
        }
    }

    // ------------------- given -------------------
    private void givenAuthenticatedUser(Long userId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new JwtUserPrincipal(userId, "e@x", "n", "s", "u"));
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private void givenNoAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private void givenAccessGrantedForOwner(Long chapterId, Long workId, Long ownerUserId) {
        Chapter chapter = createChapter(chapterId, workId);
        ValidateChapterAccess.ChapterAccessResult accessResult =
                ValidateChapterAccess.ChapterAccessResult.accessGranted(chapter, workId, ownerUserId, true);
        when(validateChapterAccess.validateAccess(chapterId, ownerUserId)).thenReturn(accessResult);
    }

    private void givenAccessGrantedForSubscriber(Long chapterId, Long workId, Long subscriberUserId) {
        Chapter chapter = createChapter(chapterId, workId);
        ValidateChapterAccess.ChapterAccessResult accessResult =
                ValidateChapterAccess.ChapterAccessResult.accessGranted(chapter, workId, 3L, false);
        when(validateChapterAccess.validateAccess(chapterId, subscriberUserId)).thenReturn(accessResult);
    }

    private void givenAccessDenied(Long chapterId, Long userId) {
        ValidateChapterAccess.ChapterAccessResult accessResult =
                ValidateChapterAccess.ChapterAccessResult.accessDenied();
        when(validateChapterAccess.validateAccess(chapterId, userId)).thenReturn(accessResult);
    }

    private void givenChapterNotFound(Long chapterId, Long userId) {
        ValidateChapterAccess.ChapterAccessResult accessResult =
                ValidateChapterAccess.ChapterAccessResult.chapterNotFound();
        when(validateChapterAccess.validateAccess(chapterId, userId)).thenReturn(accessResult);
    }

    private void givenGetChapterForEditWillReturnDto(Long chapterId) {
        when(getChapterForEdit.execute(eq(chapterId), any())).thenReturn(Optional.of(new ChapterResponseDto()));
    }

    // ------------------- when -------------------
    private ResponseEntity<ChapterResponseDto> whenGetChapterForEdit(Long chapterId) {
        return controller.getChapterForEdit(chapterId, null);
    }

    // ------------------- then -------------------
    private void thenResponseIs2xx(ResponseEntity<?> response) {
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    private void thenResponseStatusIs(ResponseEntity<?> response, int expectedStatus) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    private void thenValidateAccessCalledWith(Long chapterId, Long userId) {
        verify(validateChapterAccess).validateAccess(chapterId, userId);
    }

    private void thenGetChapterForEditCalledWith(Long chapterId) {
        verify(getChapterForEdit).execute(chapterId, null);
    }

    private void thenNoInteractionsWithGetChapterForEdit() {
        verifyNoInteractions(getChapterForEdit);
    }

    private void thenNoInteractionsWithValidateAccess() {
        verifyNoInteractions(validateChapterAccess);
    }


    @Test
    void owner_canAccess() {
        Long userId = 5L;
        Long chapterId = 7L;
        Long workId = 10L;
        givenAuthenticatedUser(userId);
        givenAccessGrantedForOwner(chapterId, workId, userId);
        givenGetChapterForEditWillReturnDto(chapterId);

        ResponseEntity<ChapterResponseDto> response = whenGetChapterForEdit(chapterId);

        thenResponseIs2xx(response);
        thenValidateAccessCalledWith(chapterId, userId);
        thenGetChapterForEditCalledWith(chapterId);
    }

    @Test
    void subscribedUser_canAccess() {
        Long userId = 9L;
        Long chapterId = 7L;
        Long workId = 10L;
        givenAuthenticatedUser(userId);
        givenAccessGrantedForSubscriber(chapterId, workId, userId);
        givenGetChapterForEditWillReturnDto(chapterId);

        ResponseEntity<ChapterResponseDto> response = whenGetChapterForEdit(chapterId);

        thenResponseIs2xx(response);
        thenValidateAccessCalledWith(chapterId, userId);
        thenGetChapterForEditCalledWith(chapterId);
    }

    @Test
    void notSubscribedAndNotOwner_isForbidden() {
        Long userId = 9L;
        Long chapterId = 7L;
        givenAuthenticatedUser(userId);
        givenAccessDenied(chapterId, userId);

        ResponseEntity<ChapterResponseDto> response = whenGetChapterForEdit(chapterId);

        thenResponseStatusIs(response, 403);
        thenValidateAccessCalledWith(chapterId, userId);
        thenNoInteractionsWithGetChapterForEdit();
    }

    @Test
    void chapterNotFound_returnsNotFound() {
        Long userId = 5L;
        Long chapterId = 999L;
        givenAuthenticatedUser(userId);
        givenChapterNotFound(chapterId, userId);

        ResponseEntity<ChapterResponseDto> response = whenGetChapterForEdit(chapterId);

        thenResponseStatusIs(response, 404);
        thenValidateAccessCalledWith(chapterId, userId);
        thenNoInteractionsWithGetChapterForEdit();
    }

    @Test
    void unauthenticatedUser_isForbidden() {
        givenNoAuthentication();

        ResponseEntity<ChapterResponseDto> response = whenGetChapterForEdit(7L);

        thenResponseStatusIs(response, 403);
        thenNoInteractionsWithValidateAccess();
        thenNoInteractionsWithGetChapterForEdit();
    }

    private Chapter createChapter(Long chapterId, Long workId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        return chapter;
    }
}
