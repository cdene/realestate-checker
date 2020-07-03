package com.cdeneuve.realestate.web;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import com.cdeneuve.realestate.web.dto.ApartmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentSource apartmentSource;

    private Comparator<Apartment> newestFirst = Comparator.comparing(Apartment::getTimestamp).reversed();


    @GetMapping(value = "/all")
    public List<ApartmentDto> getAll() {
        return apartmentSource.getAll().stream()
                .sorted(newestFirst)
                .map(apartment ->
                             ApartmentDto.builder()
                                     .id(apartment.getExtId())
                                     .title(apartment.getTitle())
                                     .address(apartment.getAddress())
                                     .price(apartment.getPrice())
                                     .rooms(apartment.getRooms())
                                     .area(apartment.getArea())
                                     .tags(apartment.getTags())
                                     .link(ApartmentDto.template + apartment.getId())
                                     .timestamp(apartment.getTimestamp())
                                     .build())
                .collect(Collectors.toList());
    }

}
