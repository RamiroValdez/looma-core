package com.amool.application.port.out;

import java.util.*;

import com.amool.domain.model.Notification;

public interface NotificationPort {

    List<Notification> getNotifications(Long userId);

    boolean saveNotification(Notification notification);

}
