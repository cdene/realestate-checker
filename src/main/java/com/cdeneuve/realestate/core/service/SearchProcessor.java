package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.*;
import com.cdeneuve.realestate.core.notification.NotificationManager;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class SearchProcessor {
    private final ExecutorService executorService = Executors.newFixedThreadPool(200);

    private final ApartmentParser apartmentParser;

    private final ApartmentSource apartmentSource;

    private final NotificationManager notificationManager;

    public SearchProcessor(ApartmentParser apartmentParser, ApartmentSource apartmentSource, NotificationManager notificationManager) {
        this.apartmentParser = apartmentParser;
        this.apartmentSource = apartmentSource;
        this.notificationManager = notificationManager;
    }

    public void processSearchResults(String htmlResultPage) {
        List<Apartment> apartments = apartmentParser.parse(htmlResultPage);
        List<Apartment> newApartments = apartments.stream()
                .filter(apartment -> !apartmentSource.existsByExtId(apartment.getExtId()))
                .collect(Collectors.toList());

        newApartments.forEach(apartment -> executorService.submit(() -> {
            apartmentSource.save(apartment);
            notificationManager.sendNotification(ApartmentNotification.newApartmentCreated(apartment));
        }));

        log.info("Received apartments: {}, new apartments: {}", apartments.size(), newApartments.stream()
                .map(Apartment::getExtId).collect(toSet()));
    }

}
