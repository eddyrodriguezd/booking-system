package com.test.booking.commons.repository.factory;


import com.test.booking.commons.repository.CommonReservationRepository;
import com.test.booking.commons.repository.CommonReservationRepositoryImpl;

public class CommonRepositoryFactory {

    private static CommonReservationRepository reservationRepository;

    public static CommonReservationRepository getReservationRepository() {
        if(reservationRepository == null) {
            reservationRepository = new CommonReservationRepositoryImpl();
        }
        return reservationRepository;
    }
}
