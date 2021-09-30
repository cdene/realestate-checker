package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonApartmentParser implements ApartmentParser {
    private final ObjectMapper objectMapper;

    @Override
    public List<Apartment> parse(String source) {
        Optional<ArrayNode> resultList = findResultList(source);
        return resultList
                .map(it -> StreamSupport.stream(resultList.get().spliterator(), false)
                        .map(ApartmentParser::getApartment)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @SneakyThrows
    private Optional<ArrayNode> findResultList(String source) {
        var tree = objectMapper.readTree(source);
        //drop huge string
        source = "";
        var resultNode = tree.findValue("resultlist.resultlist");
        if (resultNode.isEmpty()) {
            log.warn("Result Node is empty (why?)");
            log.warn("Here is list of keys: {}", tree.fieldNames());
            return Optional.empty();
        } else {
            var resultList = resultNode.findValue("resultlistEntry");
            if (resultList.isEmpty() || !resultList.isArray()) {
                log.warn("Result list is array={} and is empty ={}", resultList.isArray(), resultList.isEmpty());
                return Optional.empty();
            } else {
                return Optional.of((ArrayNode) resultList);
            }
        }
    }

    private static class ApartmentParser {
        private static final String APARTMENT_DETAILS_NODE_NAME = "resultlist.realEstate";
        private static final String REAL_ESTATE_ID = "realEstateId";
        private static final String TITLE = "title";
        private static final String LIVING_SPACE = "livingSpace";
        private static final String CALCULATED_TOTAL_RENT = "calculatedTotalRent";
        private static final String TOTAL_RENT = "totalRent";
        private static final String VALUE = "value";
        private static final String NUMBER_OF_ROOMS = "numberOfRooms";
        private static final String ADDRESS = "address";
        private static final String POSTCODE = "postcode";
        private static final String STREET = "street";
        private static final String QUARTER = "quarter";
        private static final String COORDINATE = "wgs84Coordinate";
        private static final String LATITUDE = "latitude";
        private static final String LONGITUDE = "longitude";
        private static final String DESCRIPTION = "description";
        private static final String TEXT = "text";
        private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Berlin");

        private static Apartment getApartment(JsonNode entry) {
            JsonNode details = entry.get(APARTMENT_DETAILS_NODE_NAME);
            return Apartment.builder()
                    .extId(entry.get(REAL_ESTATE_ID).asText())
                    .title(details.get(TITLE).asText())
                    .area(new BigDecimal(details.get(LIVING_SPACE).asText()))
                    .price(new BigDecimal(details.path(CALCULATED_TOTAL_RENT).path(TOTAL_RENT).path(VALUE).asText()))
                    .rooms(new BigDecimal(details.get(NUMBER_OF_ROOMS).asText()))
                    .zipCode(details.path(ADDRESS).path(POSTCODE).asText())
                    .street(details.path(ADDRESS).path(STREET).asText())
                    .district(details.path(ADDRESS).path(QUARTER).asText())
                    .coordLat(details.path(ADDRESS).path(COORDINATE).path(LATITUDE).asDouble())
                    .coordLon(details.path(ADDRESS).path(COORDINATE).path(LONGITUDE).asDouble())
                    .address(details.path(ADDRESS).path(DESCRIPTION).path(TEXT).asText())
                    .timestamp(LocalDateTime.now(DEFAULT_ZONE_ID))
                    .info(entry.toPrettyString())
                    .build();
        }
    }
}
