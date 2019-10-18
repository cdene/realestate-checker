package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;


public interface NotificationService {

    void newApartmentCreated(Apartment apartment);

}
