package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonApartmentParser implements ApartmentParser {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public List<Apartment> parse(String source) {
        ArrayNode resultList = (ArrayNode) objectMapper.readTree(source).findValue("resultlistEntry");
        //drop huge string
        source = "";

        List<Apartment> apartments = StreamSupport.stream(resultList.spliterator(), false)
                .map(entry -> {
                    JsonNode details = entry.get("resultlist.realEstate");
                    return Apartment.builder()
                            .extId(entry.get("realEstateId").asText())
                            .title(details.get("title").asText())
                            .area(new BigDecimal(details.get("livingSpace").asText()))
                            .price(new BigDecimal(details.get("calculatedPrice").get("value").asText()))
                            .rooms(new BigDecimal(details.get("numberOfRooms").asText()))
                            .zipCode(details.findPath("address.postcode").asText())
                            .street(details.findPath("address.street").asText())
                            .district(details.findPath("address.quarter").asText())
                            .coordLat(details.findPath("address.wgs84Coordinate.latitude").asDouble())
                            .coordLon(details.findPath("address.wgs84Coordinate.longitude").asDouble())
                            .address(details.get("address").get("description").get("text").asText())
                            .timestamp(LocalDateTime.now(ZoneId.of("Europe/Berlin")))
                            .info(entry.toPrettyString())
                            .build();
                })
                .collect(Collectors.toList());


        return apartments;
    }
}
