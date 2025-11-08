package com.amool.adapters.in.rest.controllers;

import java.util.*;

import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.usecases.ObtainNotificationsUseCase;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final ObtainNotificationsUseCase obtainNotificationsUseCase;

    public NotificationController(ObtainNotificationsUseCase obtainNotificationsUseCase) {
        this.obtainNotificationsUseCase = obtainNotificationsUseCase;
    }

    @GetMapping("/{userId}")
    public List<NotificationDto> getNotifications(@PathVariable Long userId) {
        return obtainNotificationsUseCase.execute(userId).stream()
            .map(NotificationMapper::toDto)
            .toList();
    }
    
}
