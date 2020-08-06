package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;

import java.util.List;

public interface ApartmentParser {

    List<Apartment> parse(String source);
}
