package com.amool.adapters.in.rest.controllers;

import com.amool.application.port.out.AwsS3Port;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.amool.adapters.in.rest.dtos.WorkListDto;
import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.ObtainWorkListUseCase;

public class HomeControllerTest {

    private HomeController homeController;
    private ObtainWorkListUseCase obtainWorkListUseCase;
    private WorkPort workPort;

    private static final Long TEST_USER_ID = 1L;

    private AwsS3Port awsS3Port;

    @BeforeEach
    public void setUp() {
        workPort = Mockito.mock(WorkPort.class);
        obtainWorkListUseCase = new ObtainWorkListUseCase(workPort, awsS3Port);
        homeController = new HomeController(obtainWorkListUseCase);
    }

    @Test
    @DisplayName("GET /api/home/work-list/{userId} - Debe devolver 4 secciones")
    public void getWorkList_shouldReturnFourSections() {
        givenNoWorksAvailable();

        Map<String, List<WorkListDto>> result = whenRequestingWorkList(TEST_USER_ID);

        thenSectionsCountIs(result, 4);
    }

    @Test
    @DisplayName("GET /api/home/work-list/{userId} - Debe devolver listas vacías cuando no hay datos")
    public void getWorkList_shouldReturnEmptyLists_whenNoData() {
        givenNoWorksAvailable();

        Map<String, List<WorkListDto>> workList = whenRequestingWorkList(TEST_USER_ID);

        thenSectionIsEmpty(workList, "topTen");
        thenSectionIsEmpty(workList, "currentlyReading");
        thenSectionIsEmpty(workList, "newReleases");
        thenSectionIsEmpty(workList, "recentlyUpdated");
    }

    private void givenNoWorksAvailable() {
    }

    private Map<String, List<WorkListDto>> whenRequestingWorkList(Long userId) {
        return homeController.getWorkList(userId);
    }

    private void thenSectionsCountIs(Map<String, List<WorkListDto>> result, int expected) {
        assertEquals(expected, result.size());
    }

    private void thenSectionIsEmpty(Map<String, List<WorkListDto>> workList, String section) {
        assertTrue(workList.get(section).isEmpty());
    }
}
