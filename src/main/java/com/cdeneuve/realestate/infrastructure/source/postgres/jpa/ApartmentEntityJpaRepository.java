package com.cdeneuve.realestate.infrastructure.source.postgres.jpa;

import com.cdeneuve.realestate.infrastructure.source.postgres.entity.ApartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApartmentEntityJpaRepository extends JpaRepository<ApartmentEntity, Long> {

    Optional<ApartmentEntity> findByExtIdIgnoreCase(String externalId);

    boolean existsByExtIdIgnoreCase(String externalId);

}
