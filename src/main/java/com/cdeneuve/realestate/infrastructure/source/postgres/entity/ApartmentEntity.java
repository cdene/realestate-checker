package com.cdeneuve.realestate.infrastructure.source.postgres.entity;

import com.cdeneuve.realestate.core.model.Apartment;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "apartments", schema = "realestate")
public class ApartmentEntity {
    private static String LINK_PREFIX = "https://www.immobilienscout24.de/expose/";

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "apartments_id_seq", sequenceName = "realestate.apartments_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartments_id_seq")
    private Long id;
    @Column(name = "ext_id")
    private String extId;
    @Column(name = "title")
    private String title;
    @Column(name = "address")
    private String address;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "street")
    private String street;
    @Column(name = "district")
    private String district;
    @Column(name = "coord_lat")
    private Double coordLat;
    @Column(name = "coord_lon")
    private Double coordLon;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "area")
    private BigDecimal area;
    @Column(name = "rooms")
    private BigDecimal rooms;
    @Column(name = "tags")
    private String tags;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name = "extra_info")
    private String info;

    public static ApartmentEntity mapToEntity(Apartment apartment) {
        ApartmentEntity entity = new ApartmentEntity();
        entity.setId(apartment.getId());
        entity.setExtId(apartment.getExtId());
        entity.setTitle(apartment.getTitle());
        entity.setZipCode(apartment.getZipCode());
        entity.setStreet(apartment.getStreet());
        entity.setDistrict(apartment.getDistrict());
        entity.setCoordLat(apartment.getCoordLat());
        entity.setCoordLon(apartment.getCoordLon());
        entity.setAddress(apartment.getAddress());
        entity.setPrice(apartment.getPrice());
        entity.setArea(apartment.getArea());
        entity.setRooms(apartment.getRooms());
        entity.setInfo(apartment.getInfo());
        entity.setTimestamp(apartment.getTimestamp());
        return entity;
    }

    public Apartment mapFromEntity() {
        return Apartment.builder()
                .id(id)
                .extId(extId)
                .title(title)
                .zipCode(zipCode)
                .street(street)
                .district(district)
                .coordLat(coordLat)
                .coordLon(coordLon)
                .address(address)
                .price(price)
                .area(area)
                .rooms(rooms)
                .timestamp(timestamp)
                .info(getInfo())
                .build();
    }
}
