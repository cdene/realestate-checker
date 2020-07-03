package com.cdeneuve.realestate.infrastructure.source.postgres.jpa;

import com.cdeneuve.realestate.infrastructure.source.postgres.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagEntityJpaRepository extends JpaRepository<TagEntity, Long> {

    Optional<TagEntity> findByTag(String tag);

}
