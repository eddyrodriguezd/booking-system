package com.test.booking.api.service;

import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.Constants;
import com.test.booking.commons.model.Reservation;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class ReservationService {

    public static List<LocalDate> getAvailabilityByRoomId(Connection connection, UUID roomId) {
        List<Reservation> reservations = RepositoryFactory.getReservationRepository().getReservationsByRoomId(connection, roomId);

        LocalDate firstAvailableDate = LocalDate.now().plus(Duration.ofDays(1)); // The next day
        LocalDate lastAvailableDate = firstAvailableDate.plus(Constants.RESERVATION_VALIDITY);

        List<LocalDate> availableDates = firstAvailableDate.datesUntil(lastAvailableDate).collect(Collectors.toList());
        log.info("Available dates before filtering reservations: <{}>", availableDates);

        for(Reservation reservation : reservations) {
            availableDates.removeAll(reservation.getStay());
        }
        log.info("Available dates after filtering reservations: <{}>", availableDates);

        return availableDates;
    }
}
