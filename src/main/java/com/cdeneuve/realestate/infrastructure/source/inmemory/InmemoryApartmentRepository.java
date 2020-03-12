package com.cdeneuve.realestate.infrastructure.source.inmemory;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO not sure if still needed
//@Service
public class InmemoryApartmentRepository implements ApartmentSource {
    private Map<String, Apartment> apartments = new ConcurrentHashMap<>();

    @Override
    public Optional<Apartment> getById(String id) {
        return Optional.ofNullable(apartments.get(id));
    }

    @Override
    public void save(Apartment apartment) {
        if (!apartments.containsKey(apartment.getId())) {
            apartments.put(apartment.getId(), apartment);
            showAll();
        }
    }

    @Override
    public Collection<Apartment> getAll() {
        return apartments.values();
    }

    private void showAll() {
        List<Apartment> sorted = apartments.values().stream()
                .sorted(Comparator.comparing(Apartment::getTimestamp).reversed())
                .collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

    @Override
    public boolean existsById(String id) {
        return apartments.containsKey(id);
    }
}
