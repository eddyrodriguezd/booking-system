package com.test.booking.api.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.LocalDate;
import java.util.List;

public class InvalidReservationDatesException extends ApiException {

    public InvalidReservationDatesException() {
        super("005", HttpStatusCode.BAD_REQUEST, "Reservation's selected dates have already been chosen");
    }
}
