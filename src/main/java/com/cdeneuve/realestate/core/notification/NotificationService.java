package com.cdeneuve.realestate.core.notification;

import com.cdeneuve.realestate.core.model.Notification;

public interface NotificationService {

    boolean subscribe(String user);

    boolean unsubscribe(String user);

    void sendNotificationToAllSubscribers(Notification notification);

}
