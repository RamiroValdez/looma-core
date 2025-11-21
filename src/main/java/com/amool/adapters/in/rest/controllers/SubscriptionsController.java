package com.amool.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.GetSubscriptions;
import com.amool.security.JwtUserPrincipal;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {

    private final GetSubscriptions getSubscriptions;

    public SubscriptionsController(GetSubscriptions getSubscriptions) {
        this.getSubscriptions = getSubscriptions;
    }

    @GetMapping
    public List<WorkResponseDto> getSubscriptions(@AuthenticationPrincipal JwtUserPrincipal userDetails) {
        Long userId = userDetails.getUserId();
        return getSubscriptions.execute(userId).stream()
            .map(WorkMapper::toDto)
            .toList();
    }
    
}
