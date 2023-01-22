package com.test.booking.api.service;

import com.test.booking.api.repository.ReservationRepository;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;

@Slf4j
public class ReservationAdminService {

    private final Connection connection;
    private final ReservationRepository reservationRepository;

    public ReservationAdminService(Connection connection, ReservationRepository reservationRepository) {
        this.connection = connection;
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservations() {
        List<Reservation> reservations = reservationRepository.getReservations(connection);
        log.info("Reservations retrieved: <{}>", reservations);
        return reservations;
    }
}
