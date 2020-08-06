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
@JsonDeserialize(builder = Apartment.ApartmentBuilder.class)
public class Apartment {
    private final Long id;
    private final String extId;
    private final String title;
    private final String address;
    private final BigDecimal price;
    private final BigDecimal area;
    private final BigDecimal rooms;
    private final List<String> tags;
    private final String info;
    private final LocalDateTime timestamp;

    Apartment(Long id, String extId, String title, String address, BigDecimal price, BigDecimal area,
              BigDecimal rooms, List<String> tags, String info, LocalDateTime timestamp) {
        this.id = id;
        this.extId = extId;
        this.title = title;
        this.address = address;
        this.price = price;
        this.area = area;
        this.rooms = rooms;
        this.tags = tags;
        this.info = info;
        this.timestamp = timestamp;
    }

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
