package com.cdeneuve.realestate.infrastructure.source.mongodb;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.source.ApartmentSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class MongoDBRepository implements ApartmentSource {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Apartment> getById(String id) {
        Query query = new Query(where("id").is(id));
        return Optional.ofNullable(mongoTemplate.findOne(query, Apartment.class));
    }

    @Override
    public boolean existsById(String id) {
        Query query = new Query(where("id").is(id));
        return mongoTemplate.exists(query, Apartment.class);
    }

    @Override
    public void save(Apartment apartment) {
        mongoTemplate.save(apartment);
    }

    @Override
    public Collection<Apartment> getAll() {
        return mongoTemplate.findAll(Apartment.class);
    }
}
