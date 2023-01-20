package com.test.booking.commons.service;

import com.test.booking.commons.Constants;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.repository.factory.CommonRepositoryFactory;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CommonReservationService {

    public static List<LocalDate> getAvailabilityByRoomId(Connection connection, UUID roomId) {
        List<Reservation> reservations = CommonRepositoryFactory.getReservationRepository().getValidReservationsByRoomId(connection, roomId);

        LocalDate firstAvailableDate = LocalDate.now().plus(Period.ofDays(1)); // The next day
        LocalDate lastAvailableDate = firstAvailableDate.plus(Constants.RESERVATION_AVAILABLE_PERIOD);

        List<LocalDate> availableDates = firstAvailableDate.datesUntil(lastAvailableDate).collect(Collectors.toList());

        for(Reservation reservation : reservations) {
            availableDates.removeAll(reservation.getStay());
        }
        log.info("Available dates after filtering reservations: <{}>", availableDates);

        return availableDates;
    }
}
