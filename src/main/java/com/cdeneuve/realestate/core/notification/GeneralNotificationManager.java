package com.cdeneuve.realestate.core.notification;

import com.cdeneuve.realestate.core.model.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneralNotificationManager implements NotificationManager {
    private List<NotificationService> availableNotificationServices = new ArrayList<>();

    @Value("${notifications.enabled:false}")
    private boolean notificationEnabled;

    public GeneralNotificationManager(NotificationService emailNotificationService,
                                      NotificationService telegramNotificationService) {
        addNotificationService(emailNotificationService);
        addNotificationService(telegramNotificationService);
    }

    @Override
    public void sendNotification(Notification notification) {
        if(notificationEnabled) {
            availableNotificationServices.forEach(notificationService ->
                    notificationService.sendNotificationToAllSubscribers(notification));
        }
    }

    @Override
    public boolean addNotificationService(NotificationService notificationService) {
        return availableNotificationServices.add(notificationService);
    }

    @Override
    public boolean removeNotificationService(NotificationService notificationService) {
        return availableNotificationServices.remove(notificationService);
    }
}
