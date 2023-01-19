package com.test.booking.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.JsonParsingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room implements Serializable {
    private UUID hotelId;
    private UUID roomId;
    private String hotelName;
    private String countryCode;
    private String city;
    private double latitude;
    private double longitude;
    private String roomType;
    private int numberOfGuests;
    private double dailyRate;
    private String currencyCode;

    @Override
    public String toString() {
        try {
            return MapperConfig.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(this.getClass().getName());
        }
    }
}
