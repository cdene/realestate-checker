package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.model.ApartmentNotification;
import com.cdeneuve.realestate.core.notification.NotificationManager;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchProcessor {

    private final ApartmentParser apartmentParser;

    private final ApartmentSource apartmentSource;

    private final NotificationManager notificationManager;

    public SearchProcessor(ApartmentParser apartmentParser, ApartmentSource apartmentSource, NotificationManager notificationManager) {
        this.apartmentParser = apartmentParser;
        this.apartmentSource = apartmentSource;
        this.notificationManager = notificationManager;
    }

    public void processSearchResults(String htmlResultPage) {
        List<Apartment> apartments = apartmentParser.parseApartmentIdsFromHtml(htmlResultPage);
        List<Apartment> newApartments = apartments.stream()
                .filter(apartment -> !apartmentSource.getById(apartment.getId()).isPresent())
                .collect(Collectors.toList());
        newApartments.forEach(apartment -> {
            apartmentSource.save(apartment);
            notificationManager.sendNotification(ApartmentNotification.newApartmentCreated(apartment));
        });
        log.info("Received apartments: {}, new apartments: {}", apartments.size(), newApartments.size());
    }

}
