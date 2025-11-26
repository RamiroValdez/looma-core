package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.amool.application.port.out.AwsS3Port;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.ObtainWorkList;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

public class ObtainWorkListTest {

    private static final long DEFAULT_USER_ID = 1L;
    private static final int EXPECTED_SECTION_COUNT = 5;
    private static final String TOP_TEN = "topTen";
    private static final String CURRENTLY_READING = "currentlyReading";
    private static final String NEW_RELEASES = "newReleases";
    private static final String RECENTLY_UPDATED = "recentlyUpdated";

    private ObtainWorkList obtainWorkList;
    private WorkPort workPort;
    private AwsS3Port awsS3Port;
    
    @BeforeEach
    public void setUp() {
        workPort = Mockito.mock(WorkPort.class);
        awsS3Port = Mockito.mock(AwsS3Port.class);
        obtainWorkList = new ObtainWorkList(workPort, awsS3Port);
    }

    @Test
    public void shouldReturnAllSectionsWhenWorksExist() {
        givenExistingWorks(defaultWorks());

        Map<String, List<Work>> result = whenObtainingWorkList();

        thenSectionCountIs(result, EXPECTED_SECTION_COUNT);
    }

    @Test 
    public void shouldReturnEmptySectionsWhenNoWorksExist() {
        givenNoWorks();

        Map<String, List<Work>> result = whenObtainingWorkList();

        thenSectionsAreEmpty(result, TOP_TEN, CURRENTLY_READING, NEW_RELEASES, RECENTLY_UPDATED);
    }

    @Test
    public void shouldReturnTopTenOrderedByLikes() {
        givenExistingWorks(defaultWorks());

        Map<String, List<Work>> result = whenObtainingWorkList();

        thenTopTenFirstPlaceHasLikes(result, 20);
    }

    @Test
    public void shouldReturnLatestWorkInNewReleases() {
        givenExistingWorks(defaultWorks());

        Map<String, List<Work>> result = whenObtainingWorkList();

        thenNewReleasesLatestPublicationDateIsToday(result);
    }

    @Test
    public void shouldReturnRecentlyUpdatedOrderedByChapterDate() {
        givenExistingWorks(defaultWorks());

        Map<String, List<Work>> result = whenObtainingWorkList();

        thenRecentlyUpdatedMatchesDate(result, LocalDate.now().minusDays(2));
    }

    private void givenExistingWorks(List<Work> works) {
        Mockito.when(workPort.getAllWorks()).thenReturn(works);
    }

    private void givenNoWorks() {
        Mockito.when(workPort.getAllWorks()).thenReturn(Collections.emptyList());
    }

    private Map<String, List<Work>> whenObtainingWorkList() {
        return obtainWorkList.execute(DEFAULT_USER_ID);
    }

    private void thenSectionCountIs(Map<String, List<Work>> result, int expectedSections) {
        assertEquals(expectedSections, result.size());
    }

    private void thenSectionsAreEmpty(Map<String, List<Work>> result, String... sectionNames) {
        for (String section : sectionNames) {
            assertTrue(result.get(section).isEmpty());
        }
    }

    private void thenTopTenFirstPlaceHasLikes(Map<String, List<Work>> result, int expectedLikes) {
        assertEquals(expectedLikes, result.get(TOP_TEN).get(0).getLikes());
    }

    private void thenNewReleasesLatestPublicationDateIsToday(Map<String, List<Work>> result) {
        assertEquals(LocalDate.now(), result.get(NEW_RELEASES).get(0).getPublicationDate());
    }

    private void thenRecentlyUpdatedMatchesDate(Map<String, List<Work>> result, LocalDate expectedDate) {
        LocalDateTime actualDateTime = result.get(RECENTLY_UPDATED).get(0).getChapters().get(0).getPublishedAt();
        assertEquals(expectedDate, actualDateTime.toLocalDate());
    }

    private List<Work> defaultWorks() {
        return List.of(
            createWork(1L, LocalDate.now().minusDays(1), 10, 7),
            createWork(2L, LocalDate.now(), 20, 2),
            createWork(3L, LocalDate.now().minusDays(2), 5, 6)
        );
    }

    private Work createWork(Long id, LocalDate publicationDate, int likes, int daysSinceLastUpdate) {
        Work work = Mockito.mock(Work.class);
        Mockito.when(work.getId()).thenReturn(id);
        Mockito.when(work.getPublicationDate()).thenReturn(publicationDate);
        Mockito.when(work.getLikes()).thenReturn(likes);
        
        Chapter chapter = createChapter(id, LocalDateTime.now().minusDays(daysSinceLastUpdate));
        Mockito.when(work.getChapters()).thenReturn(List.of(chapter));
        return work;
    }

    private Chapter createChapter(Long id, LocalDateTime publishedAt) {
        Chapter chapter = new Chapter();
        chapter.setId(id);
        chapter.setPublishedAt(publishedAt);
        chapter.setPublicationStatus("PUBLISHED");
        return chapter;
}
    
}
