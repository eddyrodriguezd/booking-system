package com.test.booking.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation implements Serializable {

    private UUID reservation_id;
    private UUID room_id;
    //private LocalDate checkinDate;
    //private LocalDate checkoutDate;
    private UUID guest_id;
    private List<LocalDate> stay;
}
