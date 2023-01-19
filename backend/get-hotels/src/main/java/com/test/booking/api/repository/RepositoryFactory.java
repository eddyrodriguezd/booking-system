package com.test.booking.api.repository;

public class RepositoryFactory {

    private static HotelRepository hotelRepository;

    public static HotelRepository getHotelRepository() {
        if(hotelRepository == null) {
            hotelRepository = new HotelRepositoryImpl();
        }
        return hotelRepository;
    }
}
