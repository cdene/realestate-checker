package com.cdeneuve.realestate.infrastructure.notification.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class NotificationBot extends TelegramLongPollingBot {
    private static final String BOT_NAME = System.getenv("TELEGRAM_BOT_NAME");
    private static final String TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
