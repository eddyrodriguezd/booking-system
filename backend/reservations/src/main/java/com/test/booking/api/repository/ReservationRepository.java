package com.test.booking.api.repository;

import com.test.booking.commons.model.Reservation;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository {

    Reservation getReservationById(Connection connection, UUID reservationId);

    List<Reservation> getReservations(Connection connection);

    List<Reservation> getReservationsByUser(Connection connection, UUID guestId);

    List<Reservation> getValidReservationsByUserAndCheckInOrCheckOutDate(Connection connection, UUID guestId, LocalDate checkInDate, LocalDate checkOutDate);

    Reservation createReservation(Connection connection, Reservation reservation);

    Reservation modifyReservation(Connection connection, Reservation reservation);

    void cancelReservation(Connection connection, UUID reservationId);
}
