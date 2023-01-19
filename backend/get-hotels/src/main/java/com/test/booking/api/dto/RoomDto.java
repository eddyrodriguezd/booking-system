package com.test.booking.api.dto;

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
public class RoomDto implements Serializable {
    private UUID id;
    private String type;
    private int numberOfGuests;
    private PriceDto price;

    @Override
    public String toString() {
        try {
            return MapperConfig.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(this.getClass().getName());
        }
    }
}
