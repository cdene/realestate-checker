package com.cdeneuve.realestate.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ApartmentDto {
    public static final String template = "https://www.immobilienscout24.de/expose/";

    private final String id;
    private final String title;
    private final String address;
    private final BigDecimal price;
    private final BigDecimal area;
    private final BigDecimal rooms;
    private final List<String> tags;
    private final String link;
    private final LocalDateTime timestamp;
}
