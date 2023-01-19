package com.test.booking.commons;

import java.time.Period;
import java.time.temporal.TemporalAmount;

public class Constants {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final TemporalAmount RESERVATION_VALIDITY = Period.ofMonths(3);
}
