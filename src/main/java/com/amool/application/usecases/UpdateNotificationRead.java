package com.amool.application.usecases;

import com.amool.application.port.out.NotificationPort;

public class UpdateNotificationRead {

    private final NotificationPort notificationPort;

    public UpdateNotificationRead(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public Boolean execute(Long notificationId) {
        try {
            notificationPort.updateNotificationRead(notificationId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
