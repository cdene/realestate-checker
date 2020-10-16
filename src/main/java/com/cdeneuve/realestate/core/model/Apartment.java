package com.cdeneuve.realestate.core.model;

import com.fasterxml.jackson.databind.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@AllArgsConstructor
@JsonDeserialize(builder = Apartment.ApartmentBuilder.class)
public class Apartment {
    private final Long id;
    private final String extId;
    private final String title;
    private final String address;
    private final String zipCode;
    private final String street;
    private final String district;
    private final Double coordLat;
    private final Double coordLon;
    private final BigDecimal price;
    private final BigDecimal area;
    private final BigDecimal rooms;
    private final List<String> tags;
    private final String info;
    private final LocalDateTime timestamp;

    public static ApartmentBuilder builder() {
        return new ApartmentBuilder();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("========================\n");
        sb.append(getExtId())
                .append(" [").append(getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)).append("]")
                .append("\n")
                .append(title)
                .append("\nAddress: ")
                .append(getAddress())
                .append("\nPrice: ")
                .append(getPrice())
                .append("\nRooms: ")
                .append(getRooms())
                .append("\nArea: ")
                .append(getArea())
                .append("\nLink: ").append("https://www.immobilienscout24.de/expose/").append(getExtId());
        return sb.toString();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ApartmentBuilder {

    }
}
