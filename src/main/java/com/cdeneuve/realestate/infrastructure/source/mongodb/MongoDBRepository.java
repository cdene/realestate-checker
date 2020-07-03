package com.cdeneuve.realestate.infrastructure.source.mongodb;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
//@Repository
@RequiredArgsConstructor
public class MongoDBRepository implements ApartmentSource {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Apartment> getById(String id) {
        Query query = new Query(where("id").is(id));
        return Optional.ofNullable(mongoTemplate.findOne(query, Apartment.class));
    }

    @Override
    public boolean existsByExtId(String id) {
        Query query = new Query(where("id").is(id));
        return mongoTemplate.exists(query, Apartment.class);
    }

    @Override
    public void save(Apartment apartment) {
        log.info("Saving apartment with  id={}", apartment.getId());
        mongoTemplate.save(apartment);
    }

    @Override
    public Collection<Apartment> getAll() {
        return mongoTemplate.findAll(Apartment.class);
    }
}
