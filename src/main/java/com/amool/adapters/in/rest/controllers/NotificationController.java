package com.amool.adapters.in.rest.controllers;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.usecases.ObtainNotificationsUseCase;
import com.amool.application.usecases.SaveNotificationUseCase;
import com.amool.application.usecases.UpdateNotificationReadUseCase;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final ObtainNotificationsUseCase obtainNotificationsUseCase;
    private final SaveNotificationUseCase saveNotificationUseCase;
    private final UpdateNotificationReadUseCase updateNotificationRead;

    public NotificationController(ObtainNotificationsUseCase obtainNotificationsUseCase, SaveNotificationUseCase saveNotificationUseCase, UpdateNotificationReadUseCase updateNotificationRead) {
        this.obtainNotificationsUseCase = obtainNotificationsUseCase;
        this.saveNotificationUseCase = saveNotificationUseCase;
        this.updateNotificationRead = updateNotificationRead;
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

    @PutMapping("/update-read/{notificationId}")
    public ResponseEntity<Boolean> updateNotificationRead(@PathVariable Long notificationId) {
        try {
            var notification = updateNotificationRead.execute(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}
