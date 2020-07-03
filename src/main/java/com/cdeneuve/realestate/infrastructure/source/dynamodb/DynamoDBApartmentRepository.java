package com.cdeneuve.realestate.infrastructure.source.dynamodb;

import com.amazonaws.services.dynamodbv2.document.*;
import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
//@Repository
public class DynamoDBApartmentRepository implements ApartmentSource {
    private final static String APARTMENT_TABLE_NAME = "realestate";
    private final static String PRIMARY_PARTITION_KEY = "id";
    private final static String PRIMARY_SORT_KEY = "timestamp";

    private final Table table;
    private final ObjectMapper mapper;
    private final ZoneId zoneId;
    private final ZoneOffset zoneOffSet;

    public DynamoDBApartmentRepository(DynamoDB dynamoDB, ObjectMapper objectMapper) {
        this.table = dynamoDB.getTable(APARTMENT_TABLE_NAME);
        this.mapper = objectMapper;
        this.zoneId = ZoneId.of("Europe/Berlin");
        this.zoneOffSet = zoneId.getRules().getOffset(LocalDateTime.now(zoneId));
    }

    @Override
    public Optional<Apartment> getById(String id) {
        Item item = table.getItem(new PrimaryKey(PRIMARY_PARTITION_KEY, id));
        if (item != null) {
            return Optional.ofNullable(mapToModel(item));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByExtId(String id) {
        return table.getItem(new PrimaryKey(PRIMARY_PARTITION_KEY, id)) != null;
    }

    @Override
    public void save(Apartment apartment) {
        try {
            table.putItem(mapFromModel(apartment));
        } catch (Exception e) {
            log.error("Error on ty to save the apartment {}", apartment.getId());
        }

    }

    @Override
    public Collection<Apartment> getAll() {
        long oneWeekAgo = LocalDateTime.now(zoneId)
                .minusDays(7)
                .toEpochSecond(zoneOffSet);

        ScanFilter scanFilter = new ScanFilter(PRIMARY_SORT_KEY).ge(oneWeekAgo);

        ItemCollection<ScanOutcome> items = table.scan(scanFilter);

        Collection<Apartment> result = new ArrayList<>();

        items.forEach(item -> result.add(mapToModel(item)));

        return result.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Item mapFromModel(Apartment apartment) throws JsonProcessingException {
        PrimaryKey primaryKey = new PrimaryKey(PRIMARY_PARTITION_KEY, apartment.getId());

        Item item = new Item()
                .withPrimaryKey(primaryKey)
                .withNumber(PRIMARY_SORT_KEY, apartment.getTimestamp().toEpochSecond(zoneOffSet))
                .withJSON("payload", mapper.writeValueAsString(apartment));
        return item;
    }

    private Apartment mapToModel(Item item) {
        String payload = item.getJSON("payload");
        try {
            return mapper.readValue(payload, Apartment.class);
        } catch (Exception e) {
            log.error("Saved item couldn't be parsed: {}", payload);
            return null;
        }
    }
}
