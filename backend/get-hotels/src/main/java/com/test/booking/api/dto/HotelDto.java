package com.test.booking.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.JsonParsingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto implements Serializable {
    private UUID id;
    private String name;
    private LocationDto location;
    private List<RoomDto> rooms;

    @Override
    public String toString() {
        try {
            return MapperConfig.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(this.getClass().getName());
        }
    }
}
