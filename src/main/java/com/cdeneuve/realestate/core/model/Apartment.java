package com.cdeneuve.realestate.core.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Getter
public class Apartment {
    private final String id;
    private final String title;
    private final String address;
    private final BigDecimal price;
    private final BigDecimal area;
    private final BigDecimal rooms;
    private final List<String> tags;
    private final LocalDateTime timestamp;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("========================\n");
        sb.append(getId())
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
                .append("\nTags")
                .append(String.join(", ", getTags()))
                .append("\nLink: ").append("https://www.immobilienscout24.de/expose/").append(getId());
        return sb.toString();
    }
}
