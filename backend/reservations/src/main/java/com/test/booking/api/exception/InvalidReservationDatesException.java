package com.test.booking.api.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.temporal.ChronoUnit;

import static com.test.booking.commons.Constants.RESERVATION_MAXIMUM_STAY;

public class InvalidReservationDatesException extends ApiException {

    public enum InvalidReservationDatesType {
        PAST_CHECK_IN("Check-in date must start at least the next day of booking"),
        CHECK_IN_GREATER_THAN_CHECK_OUT("Check-out date must be equal or greater than the check-in date"),
        STAY_TOO_LONG("The stay can't be longer than " + RESERVATION_MAXIMUM_STAY.get(ChronoUnit.DAYS) + " days"),
        ALREADY_SELECTED_DATES_BY_OTHER_USERS("The chosen dates have already been selected"),
        EXTENDING_EXISTING_RESERVATION("By creating this new reservation, you are extending an already created reservation. Please modify it if you want to extend it, but keep in mind it can't be longer than " + RESERVATION_MAXIMUM_STAY.get(ChronoUnit.DAYS) + " days");

        private String message;

        InvalidReservationDatesType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public InvalidReservationDatesException(InvalidReservationDatesType type) {
        super("006", HttpStatusCode.BAD_REQUEST, "Reservation's selected dates are invalid. " + type.getMessage());
    }
}
