package com.test.booking.api.repository;

import com.test.booking.commons.model.Reservation;

import java.sql.Connection;
import java.util.UUID;

public interface ReservationRepository {

    Reservation createReservation(Connection connection, Reservation reservation);

    Reservation modifyReservation(Connection connection, Reservation reservation);

    boolean cancelReservation(Connection connection, UUID reservationId);
}
