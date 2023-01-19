package com.test.booking.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.JsonParsingException;
import com.test.booking.commons.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private String reservationId; // Available after saving in the database
    private String roomId;
    private String guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Override
    public String toString() {
        try {
            return MapperConfig.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(this.getClass().getName());
        }
    }

    public Reservation toEntity() {
        return Reservation.builder()
                    .roomId(UUID.fromString(this.roomId))
                    .guestId(UUID.fromString(this.guestId))
                    .checkInDate(this.checkInDate)
                    .checkOutDate(this.checkOutDate)
                    .build();
    }
}
