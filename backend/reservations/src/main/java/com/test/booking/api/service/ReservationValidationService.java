package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.exception.InvalidReservationDatesException;
import com.test.booking.api.exception.ReservationNotFoundException;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.service.CommonReservationService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.test.booking.commons.Constants.RESERVATION_MAXIMUM_STAY;
import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
public class ReservationValidationService {

    protected static void validateUserIsNotExtendingStayByCreatingANewOne(Connection connection, ReservationDto reservationDto) {
        log.info("Validating if the creation of this reservation doesn't extend a previous one...");
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getValidReservationsByUserAndCheckInOrCheckOutDate(
                connection,
                UUID.fromString(reservationDto.getGuestId()),
                reservationDto.getCheckInDate().plus(Period.ofDays(1)),
                reservationDto.getCheckOutDate().minus(Period.ofDays(1))
        );

        if(reservations.size() > 0) {
            String message = reservations.stream().map(r -> "from " + r.getCheckInDate() + " to " + r.getCheckOutDate()).collect(Collectors.joining(", "));
            throw new InvalidReservationDatesException(
                    InvalidReservationDatesException.Type.CREATING_RESERVATION_EXTENDS_PREVIOUS_ONE, message
            );
        }
    }

    protected static void validateUserIsNotMergingStaysByModifyingAPreviousOne(Connection connection, UUID reservationId, ReservationDto reservationDto) {
        log.info("Validating if the modification of reservation <{}> doesn't merge with a previous reservation...", reservationId);
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getValidReservationsByUserAndCheckInOrCheckOutDate(
                connection,
                UUID.fromString(reservationDto.getGuestId()),
                reservationDto.getCheckInDate().plus(Period.ofDays(1)),
                reservationDto.getCheckOutDate().minus(Period.ofDays(1))
        );
        log.info("Reservations with a similar date including current reservation: <{}>", reservations);
        reservations.removeIf(reservation -> reservation.getReservationId().equals(reservationId));
        log.info("Reservations with a similar date excluding current reservation: <{}>", reservations);

        if(reservations.size() > 0) {
            throw new InvalidReservationDatesException(
                    InvalidReservationDatesException.Type.MODIFYING_RESERVATION_MERGES_PREVIOUS_ONE,
                    "from " + reservations.get(0).getCheckInDate() + " to " + reservations.get(0).getCheckOutDate()
            );
        }
    }

    protected static void validateReservationCheckInAndCheckOutDates(ReservationDto reservationDto) {
        log.info("Validating check-in <{}> and check-out <{}> dates...", reservationDto.getCheckInDate(), reservationDto.getCheckOutDate());
        if(!reservationDto.getCheckInDate().isAfter(LocalDate.now()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.Type.PAST_CHECK_IN, null);

        if(reservationDto.getCheckInDate().isAfter(reservationDto.getCheckOutDate()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.Type.CHECK_IN_GREATER_THAN_CHECK_OUT, null);

        if(DAYS.between(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate()) >= RESERVATION_MAXIMUM_STAY.get(ChronoUnit.DAYS))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.Type.STAY_TOO_LONG, null);
    }

    protected static void validateReservationExistsAndBelongsToUser(Connection connection, UUID reservationId, UUID guestId) {
        log.info("Validating reservation <{}> exists and belongs to user <{}>...", reservationId, guestId);
        Reservation reservation = RepositoryFactory.getReservationRepository().getReservationById(connection, reservationId);
        if (reservation == null || !reservation.getGuestId().equals(guestId))
            throw new ReservationNotFoundException(reservationId.toString());
    }

    protected static void validateReservationDatesAvailability(Connection connection, ReservationDto reservationDto, boolean modifyingReservation) {
        log.info("Validating stay <[{}, {}]> is available for room <{}>...", reservationDto.getCheckInDate(), reservationDto.getCheckOutDate(), reservationDto.getRoomId());
        List<LocalDate> availableDates = CommonReservationService.getAvailabilityByRoomId(connection, UUID.fromString(reservationDto.getRoomId()));

        // If reservation is being modified, the stay it already has should be considered as available
        if(modifyingReservation) {
            availableDates.addAll(reservationDto.getStay());
        }

        if (!availableDates.containsAll(reservationDto.getStay()))
            throw new InvalidReservationDatesException(InvalidReservationDatesException.Type.ALREADY_SELECTED_DATES_BY_OTHER_USERS, null);
    }
}
