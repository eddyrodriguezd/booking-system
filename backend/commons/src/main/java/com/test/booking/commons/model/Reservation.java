package com.test.booking.commons.model;

import com.test.booking.commons.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation implements Serializable {

    private UUID reservationId;
    private UUID roomId;
    private UUID guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;

    // Stay = [Check-in date inclusive; Check-out date inclusive]
    public List<LocalDate> getStay() {
        return this.checkInDate.datesUntil(
                this.checkOutDate.plus(Period.ofDays(1))
        ).collect(Collectors.toList());
    }
}
