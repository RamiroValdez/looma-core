package com.amool.application.usecases;

import com.amool.application.port.out.NotificationPort;
import com.amool.domain.model.Notification;
import java.util.List;

public class ObtainNotificationsUseCase {

    private final NotificationPort notificationPort;

    public ObtainNotificationsUseCase(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }
    
    public List<Notification> execute(Long userId){
        return notificationPort.getNotifications(userId);
    }
}
