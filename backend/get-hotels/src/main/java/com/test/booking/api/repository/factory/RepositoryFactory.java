package com.test.booking.api.repository.factory;

import com.test.booking.api.repository.HotelRepository;
import com.test.booking.api.repository.HotelRepositoryImpl;
import com.test.booking.api.repository.ReservationRepository;
import com.test.booking.api.repository.ReservationRepositoryImpl;

public class RepositoryFactory {

    private static HotelRepository hotelRepository;
    private static ReservationRepository reservationRepository;

    public static HotelRepository getHotelRepository() {
        if(hotelRepository == null) {
            hotelRepository = new HotelRepositoryImpl();
        }
        return hotelRepository;
    }

    public static ReservationRepository getReservationRepository() {
        if(reservationRepository == null) {
            reservationRepository = new ReservationRepositoryImpl();
        }
        return reservationRepository;
    }
}
