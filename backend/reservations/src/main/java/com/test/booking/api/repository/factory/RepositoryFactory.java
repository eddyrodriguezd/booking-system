package com.test.booking.api.repository.factory;

import com.test.booking.api.repository.ReservationRepository;
import com.test.booking.api.repository.ReservationRepositoryImpl;

public class RepositoryFactory {

    private static ReservationRepository reservationRepository;

    public static ReservationRepository getReservationRepository() {
        if(reservationRepository == null) {
            reservationRepository = new ReservationRepositoryImpl();
        }
        return reservationRepository;
    }
}
