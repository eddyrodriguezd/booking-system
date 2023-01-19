package com.test.booking.commons;

import java.time.Period;
import java.time.temporal.TemporalAmount;

public class Constants {
    // Reservation maximum stay
    public static final TemporalAmount RESERVATION_MAXIMUM_STAY = Period.ofDays(3);
    // Reservations can't be done more than 30 days in advance
    public static final TemporalAmount RESERVATION_AVAILABLE_PERIOD = Period.ofDays(30);
}
