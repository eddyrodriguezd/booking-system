package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.exception.InvalidReservationDatesException;
import com.test.booking.api.exception.ReservationNotFoundException;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.dto.Message;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.service.CommonReservationService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.test.booking.commons.Constants.RESERVATION_MAXIMUM_STAY;

@Slf4j
public class ReservationService {

    public static List<Reservation> getReservations(Connection connection, UUID guestId) {
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getReservationsByUser(connection, guestId);
        log.info("Reservations retrieved for guest=<{}>: <{}>", guestId, reservations);
        return reservations;
    }

    public static Reservation placeReservation(Connection connection, ReservationDto reservationDto) {
        validateUserIsNotExtendingStayByCreatingANewOne(connection, reservationDto);
        validateReservationCheckInAndCheckOutDates(reservationDto);
        validateReservationDatesUnavailable(connection, reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation = RepositoryFactory.getReservationRepository().createReservation(connection, reservation);
        log.info("Reservation <{}> was successfully created", reservation);
        return reservation;
    }

    public static Reservation modifyReservation(Connection connection, UUID reservationId, UUID guestId, ReservationDto reservationDto) {

        validateReservationExistsAndBelongsToUser(connection, reservationId, guestId);
        validateReservationCheckInAndCheckOutDates(reservationDto);
        validateReservationDatesUnavailable(connection, reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation.setReservationId(reservationId);
        Reservation newReservation = RepositoryFactory.getReservationRepository().modifyReservation(connection, reservation);
        reservation.setReservationId(reservationId);
        log.info("Reservation was successfully updated from <{}> to <{}>", reservation, newReservation);

        return reservation;
    }

    public static Message cancelReservation(Connection connection, UUID reservationId, UUID guestId) {
        validateReservationExistsAndBelongsToUser(connection, reservationId, guestId);
        
        RepositoryFactory.getReservationRepository().cancelReservation(connection, reservationId);
        log.info("Reservation with id=<{}> was successfully canceled", reservationId);
        return Message.builder().message("Reservation with id=<" + reservationId + "> has been canceled").build();
    }
    
    /*
     * VALIDATION METHODS
     */
    protected static void validateUserIsNotExtendingStayByCreatingANewOne(Connection connection, ReservationDto reservationDto) {
        //TODO: Implement method
        // It should throw InvalidReservationDatesException.InvalidReservationDatesType.EXTENDING_EXISTING_RESERVATION
        // in case user is trying to extend its stay by creating a new one
    }
    
    protected static void validateReservationCheckInAndCheckOutDates(ReservationDto reservationDto) {
        if(!reservationDto.getCheckInDate().isAfter(LocalDate.now()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.InvalidReservationDatesType.PAST_CHECK_IN);

        if(reservationDto.getCheckInDate().isAfter(reservationDto.getCheckOutDate()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.InvalidReservationDatesType.CHECK_IN_GREATER_THAN_CHECK_OUT);

        if(reservationDto.getCheckInDate().plus(RESERVATION_MAXIMUM_STAY).isAfter(reservationDto.getCheckOutDate()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.InvalidReservationDatesType.STAY_TOO_LONG);
    }

    protected static void validateReservationExistsAndBelongsToUser(Connection connection, UUID reservationId, UUID guestId) {
        Reservation reservation = RepositoryFactory.getReservationRepository().getReservationById(connection, reservationId);
        if (reservation == null || !reservation.getGuestId().equals(guestId))
            throw new ReservationNotFoundException(reservationId.toString());
    }

    protected static void validateReservationDatesUnavailable(Connection connection, ReservationDto reservationDto) {
        List<LocalDate> availableDates = CommonReservationService.getAvailabilityByRoomId(connection, UUID.fromString(reservationDto.getRoomId()));
        if (!availableDates.containsAll(reservationDto.getStay()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.InvalidReservationDatesType.ALREADY_SELECTED_DATES_BY_OTHER_USERS);
    }
}
