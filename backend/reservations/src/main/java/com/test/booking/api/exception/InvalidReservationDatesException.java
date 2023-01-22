package com.test.booking.api.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.temporal.ChronoUnit;

import static com.test.booking.commons.Constants.RESERVATION_MAXIMUM_STAY;

public class InvalidReservationDatesException extends ApiException {

    public enum Type {
        PAST_CHECK_IN("006", "Check-in date must start at least the next day of booking"),
        CHECK_IN_GREATER_THAN_CHECK_OUT("007", "Check-out date must be equal or greater than the check-in date"),
        STAY_TOO_LONG("008", "The stay can't be longer than " + RESERVATION_MAXIMUM_STAY.get(ChronoUnit.DAYS) + " days"),
        ALREADY_SELECTED_DATES_BY_OTHER_USERS("009", "The chosen dates have already been selected"),
        CREATING_RESERVATION_EXTENDS_PREVIOUS_ONE("010","By creating this new reservation, you are extending (a) previously created reservation(s) (%s). Please modify it if you want to extend your stay, but keep in mind it can't be longer than " + RESERVATION_MAXIMUM_STAY.get(ChronoUnit.DAYS) + " days"),
        MODIFYING_RESERVATION_MERGES_PREVIOUS_ONE("011", "By modifying this reservation, you are merging a previously created reservation (%s) with this one.");

        private String code;
        private String message;

        Type(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public InvalidReservationDatesException(Type type, String extraInfo) {
        super(type.getCode(), HttpStatusCode.BAD_REQUEST, "Reservation's selected dates are invalid. " +
                (extraInfo != null ? String.format(type.getMessage(), extraInfo) : type.getMessage()));
    }
}
