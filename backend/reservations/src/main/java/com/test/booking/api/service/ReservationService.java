package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.exception.InvalidReservationDatesException;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.service.CommonReservationService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ReservationService {

    public static ReservationDto placeReservation(Connection connection, ReservationDto reservationDto) {
        List<LocalDate> availableDates = CommonReservationService.getAvailabilityByRoomId(connection, UUID.fromString(reservationDto.getRoomId()));
        Reservation reservation = reservationDto.toEntity();

        if(!areReservationDatesValid(availableDates, reservation.getStay())) throw new InvalidReservationDatesException();

        reservation = RepositoryFactory.getReservationRepository().createReservation(connection, reservation);
        log.info("Reservation <{}> was successfully created", reservation);

        reservationDto.setReservationId(reservation.getReservationId().toString());
        return reservationDto;
    }

    public static ReservationDto modifyReservation(Connection connection, ReservationDto reservationDto) {
        List<LocalDate> availableDates = CommonReservationService.getAvailabilityByRoomId(connection, UUID.fromString(reservationDto.getRoomId()));
        Reservation reservation = reservationDto.toEntity();

        if(!areReservationDatesValid(availableDates, reservation.getStay())) throw new InvalidReservationDatesException();

        Reservation newReservation = RepositoryFactory.getReservationRepository().modifyReservation(connection, reservation);
        log.info("Reservation was successfully updated from <{}> to <{}>", reservation, newReservation);

        return reservationDto;
    }

    public static void cancelReservation(Connection connection, String reservationId) {
        RepositoryFactory.getReservationRepository().cancelReservation(connection, UUID.fromString(reservationId));
        log.info("Reservation with id=<{}> was successfully canceled", reservationId);
    }



    protected static boolean areReservationDatesValid(List<LocalDate> availableDates, List<LocalDate> stay) {
        return availableDates.containsAll(stay);
    }
}