package com.test.booking.api.repository.factory;

import com.test.booking.api.repository.HotelRepository;
import com.test.booking.api.repository.HotelRepositoryImpl;

public class RepositoryFactory {

    private static HotelRepository hotelRepository;

    public static HotelRepository getHotelRepository() {
        if(hotelRepository == null) {
            hotelRepository = new HotelRepositoryImpl();
        }
        return hotelRepository;
    }
}
