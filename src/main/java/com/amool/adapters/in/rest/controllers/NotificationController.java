package com.amool.adapters.in.rest.controllers;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.NotificationDto;
import com.amool.adapters.in.rest.mappers.NotificationMapper;
import com.amool.application.usecases.ObtainNotifications;
import com.amool.application.usecases.UpdateNotificationRead;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final ObtainNotifications obtainNotifications;
    private final UpdateNotificationRead updateNotificationRead;

    public NotificationController(ObtainNotifications obtainNotifications, UpdateNotificationRead updateNotificationRead) {
        this.obtainNotifications = obtainNotifications;
        this.updateNotificationRead = updateNotificationRead;
    }

    @GetMapping("/{userId}")
    public List<NotificationDto> getNotifications(@PathVariable Long userId) {
        return obtainNotifications.execute(userId).stream()
            .map(NotificationMapper::toDto)
            .toList();
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