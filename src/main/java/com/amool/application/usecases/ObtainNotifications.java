package com.amool.application.usecases;

import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;
import java.util.List;

public class ObtainNotifications {

    private final NotificationPort notificationPort;

    public ObtainNotifications(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }
    
    public List<Notification> execute(Long userId){
        return notificationPort.getNotifications(userId);
    }
}
