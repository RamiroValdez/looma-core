package com.amool.adapters.in.rest.controllers;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amool.adapters.in.rest.dtos.WorkListDto;
import com.amool.adapters.in.rest.mappers.WorkListMapper;
import com.amool.application.usecases.ObtainWorkListUseCase;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final ObtainWorkListUseCase obtainWorkListUseCase;

    public HomeController(ObtainWorkListUseCase obtainWorkListUseCase) {
        this.obtainWorkListUseCase = obtainWorkListUseCase;
    }

    @GetMapping("/work-list/{userId}")
    public Map<String, List<WorkListDto>> getWorkList(@PathVariable Long userId) {
        Map<String, List<WorkListDto>> workList = new HashMap<>();
        obtainWorkListUseCase.execute(userId).forEach((category, works) -> 
            workList.put(category, works.stream()
                .map(WorkListMapper::toDto)
                .toList()
            )
         );
        return workList;
    }
    
    
}
