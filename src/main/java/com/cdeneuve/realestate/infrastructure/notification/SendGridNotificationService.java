package com.cdeneuve.realestate.infrastructure.notification;

import com.cdeneuve.realestate.core.model.Notification;
import com.cdeneuve.realestate.core.service.NotificationService;
import com.sendgrid.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class SendGridNotificationService implements NotificationService {
    private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
    private static final String RECIPIENT_EMAIL = "RECIPIENT_EMAIL";
    private static final String CONTENT_TYPE = "text/plain";

    private List<Notification> notifications = new LinkedList();
    private LocalDateTime silentTime = LocalDateTime.now().plusMinutes(3);
    private LocalDateTime lastTimeTriggered = LocalDateTime.now();
    private AtomicInteger counter = new AtomicInteger(0);

    private Lock lock = new ReentrantLock();
    private String key = System.getenv(SENDGRID_API_KEY);
    private String recipient = System.getenv(RECIPIENT_EMAIL);

    @Override
    public void sendNotification(Notification notification) {
        if (LocalDateTime.now().isAfter(silentTime)) {
            try {
                if (lock.tryLock(1, TimeUnit.MINUTES)) {
                    try {
                        notifications.add(notification);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while try to get SendGridNotificationService lock", e);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void flush() {
        log.info("Mail service triggered. Notifications in the queue = {}", notifications.size());
        LocalDateTime now = LocalDateTime.now();
        try {
            if (lock.tryLock(1, TimeUnit.MINUTES)) {
                try {
                    if (now.getDayOfMonth() != this.lastTimeTriggered.getDayOfMonth()) {
                        log.warn("The counter has been reset");
                        this.counter = new AtomicInteger(0);
                    }
                    this.lastTimeTriggered = now;
                    List<Notification> toSend = notifications;
                    notifications = new LinkedList();
                    CompletableFuture.runAsync(() -> this.sendEmail(toSend));
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Error while try to get SendGridNotificationService lock", e);
        }
    }

    private void sendEmail(List<Notification> notifications) {
        if (notifications.size() > 0 && counter.get() < 100) {
            createEmail(notifications).forEach(email -> {
                SendGrid sg = new SendGrid(key);
                Request request = new Request();
                try {
                    request.setMethod(Method.POST);
                    request.setEndpoint("mail/send");
                    request.setBody(email.build());
                    Response response = sg.api(request);
                    log.info("Email sent. Response: {}", response);
                    counter.incrementAndGet();
                } catch (Throwable throwable) {
                    log.error("Error on send email", throwable);
                }
            });
        }
    }

    private List<Mail> createEmail(List<Notification> notifications) {
        Email from = new Email("real-estate@check.com");
        Email to = new Email(recipient);

        Map<Class, List<Notification>> groupedNotifications = notifications.stream()
                .collect(Collectors.groupingBy(
                        Notification::getClass,
                        toList()
                ));

        List<Mail> mails = groupedNotifications.values().stream().map(notificationList -> {
            String subject = "" + notificationList.size() + " new " + notificationList.get(0).getClass().getSimpleName();
            Content content = new Content(CONTENT_TYPE, notificationList.stream()
                    .map(Notification::getPayload)
                    .collect(joining("\n\n\n\n\n")));
            return new Mail(from, subject, to, content);
        }).collect(toList());

        return mails;
    }
}
