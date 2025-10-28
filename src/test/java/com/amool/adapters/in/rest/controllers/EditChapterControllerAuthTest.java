package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.ExtractTextFromFileUseCase;
import com.amool.application.usecases.GetChapterForEditUseCase;
import com.amool.application.usecases.UpdateChapterUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import com.amool.hexagonal.application.port.out.SubscriptionQueryPort;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    private LoadChapterPort loadChapterPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private SubscriptionQueryPort subscriptionQueryPort;

    private EditChapterController controller;

    private SecurityContext originalContext;

    @BeforeEach
    void setup() {
        getChapterForEditUseCase = mock(GetChapterForEditUseCase.class);
        updateChapterUseCase = mock(UpdateChapterUseCase.class);
        extractTextFromFileUseCase = mock(ExtractTextFromFileUseCase.class);
        loadChapterPort = mock(LoadChapterPort.class);
        obtainWorkByIdPort = mock(ObtainWorkByIdPort.class);
        subscriptionQueryPort = mock(SubscriptionQueryPort.class);
        controller = new EditChapterController(
                getChapterForEditUseCase, updateChapterUseCase, extractTextFromFileUseCase,
                loadChapterPort, obtainWorkByIdPort, subscriptionQueryPort
        );
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (originalContext != null) SecurityContextHolder.setContext(originalContext);
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
        Chapter ch = new Chapter(); ch.setId(7L); ch.setWorkId(10L);
        when(loadChapterPort.loadChapterForEdit(7L)).thenReturn(Optional.of(ch));
        Work w = new Work(); User owner = new User(); owner.setId(5L); w.setCreator(owner);
        when(obtainWorkByIdPort.obtainWorkById(10L)).thenReturn(Optional.of(w));
        when(getChapterForEditUseCase.execute(eq(7L), any())).thenReturn(Optional.of(new ChapterResponseDto()));

        ResponseEntity<ChapterResponseDto> resp = controller.getChapterForEdit(7L, null);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void subscribedAuthor_canAccess() {
        setAuthPrincipal(9L);
        Chapter ch = new Chapter(); ch.setId(7L); ch.setWorkId(10L);
        when(loadChapterPort.loadChapterForEdit(7L)).thenReturn(Optional.of(ch));
        Work w = new Work(); User owner = new User(); owner.setId(3L); w.setCreator(owner);
        when(obtainWorkByIdPort.obtainWorkById(10L)).thenReturn(Optional.of(w));
        when(subscriptionQueryPort.isSubscribedToAuthor(9L, 3L)).thenReturn(true);
        when(getChapterForEditUseCase.execute(eq(7L), any())).thenReturn(Optional.of(new ChapterResponseDto()));

        ResponseEntity<ChapterResponseDto> resp = controller.getChapterForEdit(7L, null);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void notSubscribedAndNotOwner_isForbidden() {
        setAuthPrincipal(9L);
        Chapter ch = new Chapter(); ch.setId(7L); ch.setWorkId(10L);
        when(loadChapterPort.loadChapterForEdit(7L)).thenReturn(Optional.of(ch));
        Work w = new Work(); User owner = new User(); owner.setId(3L); w.setCreator(owner);
        when(obtainWorkByIdPort.obtainWorkById(10L)).thenReturn(Optional.of(w));
        when(subscriptionQueryPort.isSubscribedToAuthor(9L, 3L)).thenReturn(false);
        when(subscriptionQueryPort.isSubscribedToWork(9L, 10L)).thenReturn(false);
        when(subscriptionQueryPort.unlockedChapters(9L, 10L)).thenReturn(java.util.List.of());

        ResponseEntity<ChapterResponseDto> resp = controller.getChapterForEdit(7L, null);
        assertThat(resp.getStatusCode().value()).isEqualTo(403);
    }
}
