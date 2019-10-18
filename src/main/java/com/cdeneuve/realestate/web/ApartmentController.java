package com.cdeneuve.realestate.web;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import com.cdeneuve.realestate.web.dto.ApartmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentSource apartmentSource;

    private Comparator<Apartment> newestFirst = Comparator.comparing(Apartment::getTimestamp).reversed();


    @GetMapping(value = "/all")
    public List<ApartmentDto> getAll() {
        List<ApartmentDto> sorted = apartmentSource.getAll().stream()
                .sorted(newestFirst)
                .map(apartment ->
                        ApartmentDto.builder()
                                .id(apartment.getId())
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
        return sorted;
    }

}
