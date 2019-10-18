package com.cdeneuve.realestate.core.source;

import com.cdeneuve.realestate.core.model.Apartment;

import java.util.Collection;
import java.util.Optional;

public interface ApartmentSource {

    Optional<Apartment> getById(String id);

    void save(Apartment apartment);

    Collection<Apartment> getAll();

}
