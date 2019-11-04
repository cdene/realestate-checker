package com.cdeneuve.realestate.infrastructure.notification.telegram;

import com.cdeneuve.realestate.core.model.Notification;
import com.cdeneuve.realestate.core.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service("telegramNotificationService")
public class TelegramNotificationService implements NotificationService {
    private final NotificationBot notificationBot;
    private final Collection<String> users = new HashSet<>();
    private final Map<String, Long> userChatLinks = new ConcurrentHashMap<>();

    @Value("${notifications.telegram.enabled:false}")
    private boolean notificationEnabled;

    public TelegramNotificationService() {
        this.notificationBot = new NotificationBot();
    }

    public void sendNotification(Long chatId, Notification notification) {
        if (notificationEnabled) {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(chatId);
                sendMessage.setText(notification.getPayload());
                notificationBot.execute(sendMessage);
            } catch (Exception e) {
                log.error("Exception on sendMessage ", e);
            }
        }
    }

    @Override
    public boolean subscribe(String user) {
        return users.add(user);
    }

    @Override
    public boolean unsubscribe(String user) {
        return users.remove(user);
    }

    @Override
    public void sendNotificationToAllSubscribers(Notification notification) {
        userChatLinks.values()
                .forEach(chatId -> sendNotification(chatId, notification));
    }

    @Scheduled(fixedRate = 60000)
    public void getUpdates() {
        try {
            GetUpdates getUpdates = new GetUpdates();
            List<Update> updates = notificationBot.execute(getUpdates);
            updates.forEach(update -> {
                Chat chat = update.getMessage().getChat();
                if (chat.isUserChat()) {
                    String userName = Optional.ofNullable(getUserName(chat)).orElse(chat.getId().toString());
                    if (!userChatLinks.containsKey(userName)) {
                        log.debug("New user appears: {}", chat.getUserName());
                        userChatLinks.put(userName, chat.getId());
                    }
                }
            });
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getUserName(Chat chat) {
        if (chat.getUserName() != null && !chat.getUserName().isEmpty()) {
            return chat.getUserName();
        } else if (chat.getFirstName() != null && chat.getLastName() != null) {
            return String.format("%s_%s", chat.getFirstName(), chat.getLastName());
        } else {
            return chat.getFirstName() != null ? chat.getFirstName() : chat.getLastName();
        }
    }
}
