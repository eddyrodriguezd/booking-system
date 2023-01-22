package com.test.booking.api.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class ReservationNotFoundException extends ApiException {

    public ReservationNotFoundException(String reservationId) {
        super("005", HttpStatusCode.BAD_REQUEST, "Reservation with id=<{}> could not been found");
    }
}
