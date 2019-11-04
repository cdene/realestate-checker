package com.cdeneuve.realestate.core.notification;

import com.cdeneuve.realestate.core.model.Notification;

public interface NotificationManager {

    void sendNotification(Notification notification);

    boolean addNotificationService(NotificationService notificationService);

    boolean removeNotificationService(NotificationService notificationService);
}
