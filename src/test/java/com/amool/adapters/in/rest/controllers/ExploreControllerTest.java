package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.dtos.WorkSearchFilterDto;
import com.amool.application.usecases.SearchAndFiltrateUseCase;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExploreControllerTest {

    private ExploreController exploreController;
    private SearchAndFiltrateUseCase searchAndFiltrateUseCase;

    @BeforeEach
    public void setUp() {
        searchAndFiltrateUseCase = Mockito.mock(SearchAndFiltrateUseCase.class);
        exploreController = new ExploreController(searchAndFiltrateUseCase);
    }

    @Test
    @DisplayName("POST /api/explore - Should return page of works when use case returns results")
    public void explore_shouldReturnPage_whenUseCaseReturnsResults() {
        WorkSearchFilterDto filterDto = givenFilterWithCategory(1L);
        Pageable pageable = givenPageable(0, 10);
        Page<Work> page = givenPageOfWorks(givenWork(1L, "Title 1"), givenWork(2L, "Title 2"), pageable);
        givenSearchWillReturn(page);

        ResponseEntity<Page<WorkResponseDto>> response = whenClientCallsExplore(filterDto, pageable);

        thenShouldReturnOk(response);
        thenResponseContainsTitles(response, "Title 1", "Title 2");
        thenSearchWasExecutedWith(pageable);
    }

    @Test
    @DisplayName("POST /api/explore - Should return empty page when use case returns no results")
    public void explore_shouldReturnEmptyPage_whenNoResults() {
        WorkSearchFilterDto filterDto = givenEmptyFilter();
        Pageable pageable = givenPageable(0, 10);
        Page<Work> emptyPage = givenEmptyPage(pageable);
        givenSearchWillReturn(emptyPage);

        ResponseEntity<Page<WorkResponseDto>> response = whenClientCallsExplore(filterDto, pageable);

        thenShouldReturnOk(response);
        thenResponseIsEmpty(response);
        thenSearchWasExecutedWith(pageable);
    }

    @Test
    @DisplayName("POST /api/explore - Should handle null filter (pass null to use case)")
    public void explore_shouldHandleNullFilter() {
        Pageable pageable = givenPageable(0, 5);
        Page<Work> page = givenPageOfWorks(givenWork(5L, "Solo"), pageable);
        givenSearchWillReturnForNullFilter(page);

        ResponseEntity<Page<WorkResponseDto>> response = whenClientCallsExplore(null, pageable);

        thenShouldReturnOk(response);
        thenResponseContainsTitles(response, "Solo");
        thenSearchWasExecutedWithNullFilter(pageable);
    }

    private WorkSearchFilterDto givenFilterWithCategory(Long catId) {
        return new WorkSearchFilterDto(Set.of(catId), null, null, null, null, null, "text", null, null);
    }

    private WorkSearchFilterDto givenEmptyFilter() {
        return new WorkSearchFilterDto(null, null, null, null, null, null, null, null, null);
    }

    private Pageable givenPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Work> givenPageOfWorks(Work work, Pageable pageable) {
        return new PageImpl<>(List.of(work), pageable, 1);
    }

    private Page<Work> givenPageOfWorks(Work w1, Work w2, Pageable pageable) {
        return new PageImpl<>(List.of(w1, w2), pageable, 2);
    }

    private Page<Work> givenEmptyPage(Pageable pageable) {
        return Page.empty(pageable);
    }

    private Work givenWork(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        w.setCover("cover");
        w.setBanner("banner");
        w.setDescription("desc");
        w.setPrice(BigDecimal.ZERO);
        w.setLikes(0);
        return w;
    }

    private void givenSearchWillReturn(Page<Work> page) {
        when(searchAndFiltrateUseCase.execute(any(), any())).thenReturn(page);
    }

    private void givenSearchWillReturnForNullFilter(Page<Work> page) {
        when(searchAndFiltrateUseCase.execute(isNull(), any())).thenReturn(page);
    }

    private ResponseEntity<Page<WorkResponseDto>> whenClientCallsExplore(WorkSearchFilterDto filterDto, Pageable pageable) {
        return exploreController.explore(filterDto, pageable);
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenResponseContainsTitles(ResponseEntity<Page<WorkResponseDto>> response, String... expectedTitles) {
        assertNotNull(response.getBody());
        List<WorkResponseDto> content = response.getBody().getContent();
        assertEquals(expectedTitles.length, content.size());
        for (int i = 0; i < expectedTitles.length; i++) {
            assertEquals(expectedTitles[i], content.get(i).getTitle());
        }
    }

    private void thenResponseIsEmpty(ResponseEntity<Page<WorkResponseDto>> response) {
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    private void thenSearchWasExecutedWith(Pageable pageable) {
        verify(searchAndFiltrateUseCase, times(1)).execute(any(), eq(pageable));
    }

    private void thenSearchWasExecutedWithNullFilter(Pageable pageable) {
        verify(searchAndFiltrateUseCase, times(1)).execute(isNull(), eq(pageable));
    }

}
