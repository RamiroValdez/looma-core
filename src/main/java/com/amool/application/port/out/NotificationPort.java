package com.amool.application.port.out;

import java.util.*;

import com.amool.domain.model.Notification;

public interface NotificationPort {

    List<Notification> getNotifications(Long userId);

    boolean saveAuthorNotification(Notification notification);

    void updateNotificationRead(Long notificationId);

    List<Notification> getPendingNotifications(int batchSize);

    boolean saveLectorNotification(Notification notification);

}
