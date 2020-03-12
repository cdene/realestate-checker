package com.cdeneuve.realestate.core.source;

import com.cdeneuve.realestate.core.model.Apartment;

import java.util.*;

public interface ApartmentSource {

    Optional<Apartment> getById(String id);

    boolean existsById(String id);

    void save(Apartment apartment);

    Collection<Apartment> getAll();

}
