package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.model.enums.ReservationStatus;
import com.test.booking.commons.util.Message;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static com.test.booking.api.service.ReservationValidationService.validateReservationCheckInAndCheckOutDates;
import static com.test.booking.api.service.ReservationValidationService.validateReservationDatesAvailability;
import static com.test.booking.api.service.ReservationValidationService.validateReservationExistsAndBelongsToUser;
import static com.test.booking.api.service.ReservationValidationService.validateUserIsNotExtendingStayByCreatingANewOne;
import static com.test.booking.api.service.ReservationValidationService.validateUserIsNotMergingStaysByModifyingAPreviousOne;

@Slf4j
public class ReservationService {

    public static List<Reservation> getReservations(Connection connection, UUID guestId) {
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getReservationsByUser(connection, guestId);
        log.info("Reservations retrieved for guest=<{}>: <{}>", guestId, reservations);
        return reservations;
    }

    public static Reservation placeReservation(Connection connection, ReservationDto reservationDto) {
        validateReservationCheckInAndCheckOutDates(reservationDto);
        validateReservationDatesAvailability(connection, reservationDto, false);
        validateUserIsNotExtendingStayByCreatingANewOne(connection, reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation = RepositoryFactory.getReservationRepository().createReservation(connection, reservation);
        log.info("Reservation <{}> was successfully created", reservation);
        return reservation;
    }

    public static Reservation modifyReservation(Connection connection, UUID reservationId, UUID guestId, ReservationDto reservationDto) {
        validateReservationExistsAndBelongsToUser(connection, reservationId, guestId);
        validateReservationCheckInAndCheckOutDates(reservationDto);

        Reservation existingReservation = RepositoryFactory.getReservationRepository().getReservationById(connection, reservationId);
        reservationDto.setRoomId(existingReservation.getRoomId().toString());
        validateReservationDatesAvailability(connection, reservationDto, true);

        reservationDto.setGuestId(guestId.toString());
        validateUserIsNotMergingStaysByModifyingAPreviousOne(connection, reservationId, reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation.setReservationId(reservationId);
        Reservation newReservation = RepositoryFactory.getReservationRepository().modifyReservation(connection, reservation);
        reservation.setReservationId(reservationId);
        log.info("Reservation was successfully updated from <{}> to <{}>", reservation, newReservation);

        return reservation;
    }

    public static Message cancelReservation(Connection connection, UUID reservationId, UUID guestId) {
        validateReservationExistsAndBelongsToUser(connection, reservationId, guestId);

        Reservation existingReservation = RepositoryFactory.getReservationRepository().getReservationById(connection, reservationId);
        if(existingReservation.getStatus().equals(ReservationStatus.CANCELED))
            return Message.builder().message("Reservation with id=<" + reservationId + "> was already canceled").build();

        RepositoryFactory.getReservationRepository().cancelReservation(connection, reservationId);
        log.info("Reservation with id=<{}> was successfully canceled", reservationId);
        return Message.builder().message("Reservation with id=<" + reservationId + "> has been canceled").build();
    }
}
