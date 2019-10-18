package com.cdeneuve.realestate.infrastructure.notification;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.service.NotificationService;
import com.sendgrid.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SendGridNotificationService implements NotificationService {
    private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
    private static final String RECIPIENT_EMAIL = "RECIPIENT_EMAIL";
    private static final String CONTENT_TYPE = "text/plain";

    private List<Apartment> apartmentsToSend = new LinkedList();
    private LocalDateTime silentTime = LocalDateTime.now().plusMinutes(3);
    private LocalDateTime nextEmailTime = silentTime.plusMinutes(5);
    private AtomicInteger counter = new AtomicInteger(0);

    private int emailInterval = 5; // min

    private Lock lock = new ReentrantLock();

    @Override
    public void newApartmentCreated(Apartment apartment) {
        if (LocalDateTime.now().isAfter(silentTime)) {
            try {
                if (lock.tryLock(1, TimeUnit.MINUTES)) {
                    try {
                        apartmentsToSend.add(apartment);
                        log.info("New apartment {} added to email queue", apartment.getId());
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while try to get SendGridNotificationService lock", e);
            }
        }
    }

    @Scheduled(fixedRate = 180000)
    public void flush() {
        log.info("Mail service triggered. Apartments in the queue = {}", apartmentsToSend.size());

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(nextEmailTime)) {
            try {
                if (lock.tryLock(1, TimeUnit.MINUTES)) {
                    try {
                        if (now.getDayOfMonth() != this.nextEmailTime.getDayOfMonth()) {
                            log.warn("The counter has been reset");
                            this.counter = new AtomicInteger(0);
                        }
                        this.nextEmailTime = now.plusMinutes(emailInterval);
                        List<Apartment> toSend = apartmentsToSend;
                        apartmentsToSend = new LinkedList();
                        CompletableFuture.runAsync(() -> this.sendEmail(toSend));
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while try to get SendGridNotificationService lock", e);
            }
        }
    }

    private void sendEmail(List<Apartment> apartments) {
        if (apartments.size() > 0 && counter.get() < 100) {
            Mail email = createEmail(apartments);
            SendGrid sg = new SendGrid(System.getenv(SENDGRID_API_KEY));
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
        }
    }

    private Mail createEmail(List<Apartment> apartments) {
        Email from = new Email("real-estate@check.com");
        String subject = "Apartment news: " + apartments.size() + " new apartments added";
        Email to = new Email(System.getenv(RECIPIENT_EMAIL));

        StringBuilder contentBuilder = new StringBuilder();
        apartments.forEach(apartment -> {
            contentBuilder.append(apartment.getId())
                    .append(" [")
                    .append(apartment.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME))
                    .append("]")
                    .append("\n")
                    .append(apartment.getTitle())
                    .append("\nAddress: ")
                    .append(apartment.getAddress())
                    .append("\nPrice: ")
                    .append(apartment.getPrice())
                    .append("\nRooms: ")
                    .append(apartment.getRooms())
                    .append("\nArea: ")
                    .append(apartment.getArea())
                    .append("\nTags")
                    .append(String.join(", ", apartment.getTags()))
                    .append("\nLink: ").append("https://www.immobilienscout24.de/expose/").append(apartment.getId());
            contentBuilder.append("\n\n\n");
        });

        Content content = new Content(CONTENT_TYPE, contentBuilder.toString());
        log.info("Email body: {}", contentBuilder.toString());

        return new Mail(from, subject, to, content);
    }
}
