package com.cdeneuve.realestate.infrastructure.notification.sendgrid;

import com.cdeneuve.realestate.core.model.Notification;
import com.cdeneuve.realestate.core.notification.NotificationService;
import com.sendgrid.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service("emailNotificationService")
public class SendGridNotificationService implements NotificationService {
    private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
    private static final String CONTENT_TYPE = "text/plain";

    private final SendGrid sg = new SendGrid(System.getenv(SENDGRID_API_KEY));

    private Map<String, List<Notification>> subscriberNotifications = new ConcurrentHashMap<>();
    private Collection<String> emails = new HashSet<>();

    private LocalDateTime silentTime;
    private LocalDateTime lastTimeTriggered;
    private AtomicInteger counter = new AtomicInteger(0);

    private Lock lock = new ReentrantLock();

    @Value("${notifications.email.enabled:false}")
    private boolean notificationEnabled;

    public SendGridNotificationService(@Value("${notifications.email.silenceInterval:20}")
                                               Integer silenceInterval) {
        this.silentTime = LocalDateTime.now().plusMinutes(silenceInterval);
        this.lastTimeTriggered = LocalDateTime.now();
    }

    @Override
    public boolean subscribe(String email) {
        return emails.add(email);
    }

    @Override
    public boolean unsubscribe(String email) {
        return emails.remove(email);
    }

    @Override
    public void sendNotificationToAllSubscribers(Notification notification) {
        if (LocalDateTime.now().isAfter(silentTime)) {
            try {
                if (lock.tryLock(1, TimeUnit.MINUTES)) {
                    try {
                        emails.forEach(email -> sendNotification(email, notification));
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while try to get SendGridNotificationService lock", e);
            }
        }
    }

    private void sendNotification(String email, Notification notification) {
        subscriberNotifications.putIfAbsent(email, new ArrayList<>());
        subscriberNotifications.get(email).add(notification);
    }

    private void sendEmails(List<Mail> emailNotifications) {
        if (notificationEnabled) {
            if (emailNotifications.size() > 0 && counter.get() < 100) {
                emailNotifications.forEach(email -> {

                    Request request = new Request();
                    try {
                        request.setMethod(Method.POST);
                        request.setEndpoint("mail/send");
                        request.setBody(email.build());
                        send(request);
                        counter.incrementAndGet();
                    } catch (Throwable throwable) {
                        log.error("Error on send email", throwable);
                    }
                });
            }
        }
    }

    private Response send(Request request) throws IOException {
        Response response = sg.api(request);
        log.info("Email sent. Response: {}", request.getBody());
        return response;
    }

    private List<Mail> createEmail(String recipient, List<Notification> notifications) {
        Email from = new Email("real-estate@check.com");
        Email to = new Email(recipient);

        Map<Class, List<Notification>> groupedNotifications = notifications.stream()
                .collect(Collectors.groupingBy(
                        Notification::getClass,
                        toList()));

        return groupedNotifications.values().stream().map(notificationList -> {
            String subject = "[=" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "=] " + notificationList.size() + " new " + notificationList.get(0).getClass().getSimpleName();
            Content content = new Content(CONTENT_TYPE, notificationList.stream()
                    .map(Notification::getPayload)
                    .collect(joining("\n\n\n\n\n")));
            return new Mail(from, subject, to, content);
        }).collect(toList());
    }


    @Scheduled(fixedRate = 60000)
    public void flush() {
        log.info("Mail service triggered. Notifications in the queue = {}", subscriberNotifications.values().stream()
                .map(Collection::size)
                .max(Comparator.naturalOrder()));
        try {
            if (lock.tryLock(1, TimeUnit.MINUTES)) {
                try {
                    resetCounterIfNeeded(LocalDateTime.now());
                    Map<String, List<Notification>> copyToSend = subscriberNotifications;
                    subscriberNotifications = new ConcurrentHashMap<>();
                    copyToSend.forEach((recipient, notifications) ->
                            CompletableFuture.supplyAsync(() -> createEmail(recipient, notifications))
                                    .thenAccept(this::sendEmails));
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Error while try to get SendGridNotificationService lock", e);
        }
    }

    private void resetCounterIfNeeded(LocalDateTime currentDateTime) {
        if (currentDateTime.getDayOfMonth() != this.lastTimeTriggered.getDayOfMonth()) {
            log.warn("The counter has been reset");
            this.counter = new AtomicInteger(0);
        }
        this.lastTimeTriggered = currentDateTime;
    }
}
