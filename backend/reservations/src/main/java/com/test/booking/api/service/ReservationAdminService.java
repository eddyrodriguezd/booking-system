package com.test.booking.api.service;

import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;

@Slf4j
public class ReservationAdminService {

    public static List<Reservation> getReservations(Connection connection) {
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getReservations(connection);
        log.info("Reservations retrieved: <{}>", reservations);
        return reservations;
    }
}
