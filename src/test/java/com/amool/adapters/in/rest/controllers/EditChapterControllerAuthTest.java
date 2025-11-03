package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.application.usecases.ExtractTextFromFileUseCase;
import com.amool.application.usecases.GetChapterForEditUseCase;
import com.amool.application.usecases.UpdateChapterUseCase;
import com.amool.application.usecases.ValidateChapterAccessUseCase;
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

    private GetChapterForEditUseCase getChapterForEditUseCase;
    private UpdateChapterUseCase updateChapterUseCase;
    private ExtractTextFromFileUseCase extractTextFromFileUseCase;
    private ValidateChapterAccessUseCase validateChapterAccessUseCase;

    private EditChapterController controller;

    private SecurityContext originalContext;

    @BeforeEach
    void setup() {
        getChapterForEditUseCase = mock(GetChapterForEditUseCase.class);
        updateChapterUseCase = mock(UpdateChapterUseCase.class);
        extractTextFromFileUseCase = mock(ExtractTextFromFileUseCase.class);
        validateChapterAccessUseCase = mock(ValidateChapterAccessUseCase.class);

        controller = new EditChapterController(
                getChapterForEditUseCase,
                updateChapterUseCase,
                extractTextFromFileUseCase,
                validateChapterAccessUseCase
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

    private void setAuthPrincipal(Long userId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new JwtUserPrincipal(userId, "e@x", "n", "s", "u"));
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void owner_canAccess() {
        setAuthPrincipal(5L);
        Chapter chapter = createChapter(7L, 10L);

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                ValidateChapterAccessUseCase.ChapterAccessResult.accessGranted(chapter, 10L, 5L, true);

        when(validateChapterAccessUseCase.validateAccess(7L, 5L)).thenReturn(accessResult);
        when(getChapterForEditUseCase.execute(eq(7L), any())).thenReturn(Optional.of(new ChapterResponseDto()));

        ResponseEntity<ChapterResponseDto> response = controller.getChapterForEdit(7L, null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(validateChapterAccessUseCase).validateAccess(7L, 5L);
        verify(getChapterForEditUseCase).execute(7L, null);
    }

    @Test
    void subscribedUser_canAccess() {
        setAuthPrincipal(9L);
        Chapter chapter = createChapter(7L, 10L);

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                ValidateChapterAccessUseCase.ChapterAccessResult.accessGranted(chapter, 10L, 3L, false);

        when(validateChapterAccessUseCase.validateAccess(7L, 9L)).thenReturn(accessResult);
        when(getChapterForEditUseCase.execute(eq(7L), any())).thenReturn(Optional.of(new ChapterResponseDto()));

        ResponseEntity<ChapterResponseDto> response = controller.getChapterForEdit(7L, null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(validateChapterAccessUseCase).validateAccess(7L, 9L);
        verify(getChapterForEditUseCase).execute(7L, null);
    }

    @Test
    void notSubscribedAndNotOwner_isForbidden() {
        setAuthPrincipal(9L);

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                ValidateChapterAccessUseCase.ChapterAccessResult.accessDenied();

        when(validateChapterAccessUseCase.validateAccess(7L, 9L)).thenReturn(accessResult);

        ResponseEntity<ChapterResponseDto> response = controller.getChapterForEdit(7L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        verify(validateChapterAccessUseCase).validateAccess(7L, 9L);
        verifyNoInteractions(getChapterForEditUseCase);
    }

    @Test
    void chapterNotFound_returnsNotFound() {
        setAuthPrincipal(5L);

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                ValidateChapterAccessUseCase.ChapterAccessResult.chapterNotFound();

        when(validateChapterAccessUseCase.validateAccess(999L, 5L)).thenReturn(accessResult);

        ResponseEntity<ChapterResponseDto> response = controller.getChapterForEdit(999L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        verify(validateChapterAccessUseCase).validateAccess(999L, 5L);
        verifyNoInteractions(getChapterForEditUseCase);
    }

    @Test
    void unauthenticatedUser_isForbidden() {
        SecurityContextHolder.clearContext();

        ResponseEntity<ChapterResponseDto> response = controller.getChapterForEdit(7L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        verifyNoInteractions(validateChapterAccessUseCase);
        verifyNoInteractions(getChapterForEditUseCase);
    }

    private Chapter createChapter(Long chapterId, Long workId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        return chapter;
    }
}
