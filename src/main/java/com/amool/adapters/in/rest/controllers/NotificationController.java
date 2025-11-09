package com.amool.adapters.in.rest.controllers;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.usecases.ObtainNotificationsUseCase;
import com.amool.application.usecases.SaveNotificationUseCase;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final ObtainNotificationsUseCase obtainNotificationsUseCase;
    private final SaveNotificationUseCase saveNotificationUseCase;

    public NotificationController(ObtainNotificationsUseCase obtainNotificationsUseCase, SaveNotificationUseCase saveNotificationUseCase) {
        this.obtainNotificationsUseCase = obtainNotificationsUseCase;
        this.saveNotificationUseCase = saveNotificationUseCase;
    }

    @GetMapping("/{userId}")
    public List<NotificationDto> getNotifications(@PathVariable Long userId) {
        return obtainNotificationsUseCase.execute(userId).stream()
            .map(NotificationMapper::toDto)
            .toList();
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> createNotification(
            @RequestBody NotificationDto notificationDto) {
                try {
                    var notification = saveNotificationUseCase.execute(notificationDto);
                    return ResponseEntity.ok(notification);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().build();
                }
        
    }
    
}
