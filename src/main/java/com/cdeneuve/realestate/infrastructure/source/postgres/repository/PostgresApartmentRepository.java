package com.cdeneuve.realestate.infrastructure.source.postgres.repository;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import com.cdeneuve.realestate.infrastructure.source.postgres.entity.ApartmentEntity;
import com.cdeneuve.realestate.infrastructure.source.postgres.jpa.ApartmentEntityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostgresApartmentRepository implements ApartmentSource {
    private final ApartmentEntityJpaRepository jpaRepository;

    @Override
    public Optional<Apartment> getById(String externalId) {
        return jpaRepository.findByExtIdIgnoreCase(externalId)
                .map(ApartmentEntity::mapFromEntity);
    }

    @Override
    public boolean existsByExtId(String externalId) {
        return jpaRepository.existsByExtIdIgnoreCase(externalId);
    }

    @Override
    public void save(Apartment apartment) {
        jpaRepository.save(ApartmentEntity.mapToEntity(apartment));
    }

    @Override
    public Collection<Apartment> getAll() {
        return jpaRepository.findAll()
                .stream()
                .map(ApartmentEntity::mapFromEntity)
                .collect(Collectors.toList());
    }
}
