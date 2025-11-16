package com.amool.adapters.in.rest.controllers;

import com.amool.application.port.out.AwsS3Port;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amool.adapters.in.rest.dtos.WorkListDto;
import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.ObtainWorkListUseCase;

public class HomeControllerTest {

    private HomeController homeController;
    private ObtainWorkListUseCase obtainWorkListUseCase;
    private WorkPort workPort;
    private AwsS3Port awsS3Port;
    
    @BeforeEach
    public void setUp() {
        workPort = Mockito.mock(WorkPort.class);
        obtainWorkListUseCase = new ObtainWorkListUseCase(workPort, awsS3Port);
        homeController = new HomeController(obtainWorkListUseCase);
    }

    @Test
    public void when_GetWorkList_ThenReturnWorkList() {
        Map<String, List<WorkListDto>> result = homeController.getWorkList(1L);

        assertEquals(4, result.size());
    }

    @Test
    public void when_WorkListIsEmpty_ThenReturnEmptyList(){
         Map<String, List<WorkListDto>> workList = homeController.getWorkList(1L);
         assertTrue(workList.get("topTen").isEmpty());
         assertTrue(workList.get("currentlyReading").isEmpty());
         assertTrue(workList.get("newReleases").isEmpty());
         assertTrue(workList.get("recentlyUpdated").isEmpty());

    }


    
}
