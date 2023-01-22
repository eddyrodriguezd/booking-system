package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.UUID;

public class UtilTest {

    private static final String ROOM_ID = "e9c7e7ff-839f-4b72-9d1c-7c422cf484c5";
    private static final String GUEST_ID = "21f37701-eb48-421a-878f-04a220ce7201";

    private static final String RESERVATION_ID = "1f4203b3-c834-4a4b-b79e-41366a7eb525";

    public static ReservationDto getReservationDto(String checkInDate, String checkoutDate) {
        return ReservationDto.builder()
                .roomId(ROOM_ID)
                .guestId(GUEST_ID)
                .checkInDate(LocalDate.parse(checkInDate))
                .checkOutDate(LocalDate.parse(checkoutDate))
                .build();
    }

    public static Reservation getValidReservation(String reservationId, String checkInDate, String checkoutDate) {
        return Reservation.builder()
                .reservationId(reservationId == null ? UUID.fromString(RESERVATION_ID) : UUID.fromString(reservationId))
                .roomId(UUID.fromString(ROOM_ID))
                .guestId(UUID.fromString(GUEST_ID))
                .checkInDate(LocalDate.parse(checkInDate))
                .checkOutDate(LocalDate.parse(checkoutDate))
                .status(ReservationStatus.VALID)
                .build();
    }
}
