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
import com.amool.application.usecases.ObtainWorkListUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

public class ObtainWorkListUseCaseTest {

    private ObtainWorkListUseCase obtainWorkListUseCase;
    private WorkPort workPort;
    private AwsS3Port awsS3Port;
    
    @BeforeEach
    public void setUp() {
        workPort = Mockito.mock(WorkPort.class);
        obtainWorkListUseCase = new ObtainWorkListUseCase(workPort, awsS3Port);
    }

    @Test
    public void when_ExecuteObtainWorkList_ThenReturnAllSections() {
        List<Work> works = mockWorks();
        Mockito.when(workPort.getAllWorks()).thenReturn(works);
        
        Map<String, List<Work>> result = obtainWorkListUseCase.execute(1L);

        int lists = 4;

        assertEquals(lists, result.size());
    }

    @Test 
    public void when_NoWorksExist_ThenReturnEmptyMap() {
        Mockito.when(workPort.getAllWorks()).thenReturn(new ArrayList<>());
        
        Map<String, List<Work>> result = obtainWorkListUseCase.execute(1L);

        assertTrue(result.get("topTen").isEmpty());
        assertTrue(result.get("currentlyReading").isEmpty());
        assertTrue(result.get("newReleases").isEmpty());
        assertTrue(result.get("recentlyUpdated").isEmpty());
    }

    @Test
    public void when_TopTenWorksExist_ThenReturnTopTen() {
        List<Work> works = mockWorks();
        Mockito.when(workPort.getAllWorks()).thenReturn(works);
        
        Map<String, List<Work>> result = obtainWorkListUseCase.execute(1L);

        int likesOfTheTopWork = 20; 

        assertEquals(likesOfTheTopWork, result.get("topTen").get(0).getLikes());
    }

    @Test
    public void when_NewReleasesWorksExist_ThenReturnNewReleases() {
        List<Work> works = mockWorks();
        Mockito.when(workPort.getAllWorks()).thenReturn(works);
        
        Map<String, List<Work>> result = obtainWorkListUseCase.execute(1L);

        LocalDate DateOfTheLastestWork = java.time.LocalDate.now();

        assertEquals(DateOfTheLastestWork, result.get("newReleases").get(0).getPublicationDate());
    }

    @Test
    public void when_RecentlyUpdatedWorksExist_ThenReturnRecentlyUpdated() {
        List<Work> works = mockWorks();
        Mockito.when(workPort.getAllWorks()).thenReturn(works);
        
        Map<String, List<Work>> result = obtainWorkListUseCase.execute(1L);

        LocalDate expectedDate = LocalDate.now().minusDays(2);
        LocalDateTime actualDateTime = result.get("recentlyUpdated").get(0).getChapters().get(0).getPublishedAt();

        assertEquals(expectedDate, actualDateTime.toLocalDate());
    }


    private List<Work> mockWorks() {
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
        return chapter;
}
    
}
