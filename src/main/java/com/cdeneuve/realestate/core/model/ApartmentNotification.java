package com.cdeneuve.realestate.core.model;

import lombok.*;

import java.time.format.DateTimeFormatter;

@Builder(access = AccessLevel.PRIVATE)
public class ApartmentNotification implements Notification {
    private final String title;
    private final String payload;

    public static Notification newApartmentCreated(Apartment apartment) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(apartment.getExtId())
                .append(" [")
                .append(apartment.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME))
                .append("]")
                .append("\n ")
                .append(apartment.getTitle())
                .append("\n Address: ")
                .append(apartment.getAddress())
                .append("\n Price: ")
                .append(apartment.getPrice())
                .append("\n Rooms: ")
                .append(apartment.getRooms())
                .append("\n Area: ")
                .append(apartment.getArea())
                .append("\n Link: ").append("https://www.immobilienscout24.de/expose/").append(apartment.getExtId());
        contentBuilder.append("\n\n\n");
        return ApartmentNotification.builder()
                .title("1 new apartments added")
                .payload(contentBuilder.toString())
                .build();

    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPayload() {
        return payload;
    }
}
