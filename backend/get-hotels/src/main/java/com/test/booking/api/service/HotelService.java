package com.test.booking.api.service;

import com.test.booking.api.dto.CoordinatesDto;
import com.test.booking.api.dto.CountryDto;
import com.test.booking.api.dto.HotelDto;
import com.test.booking.api.dto.LocationDto;
import com.test.booking.api.dto.PriceDto;
import com.test.booking.api.dto.RoomDto;
import com.test.booking.api.model.Room;
import com.test.booking.api.repository.RepositoryFactory;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class HotelService {

    public static List<HotelDto> getHotels(Connection connection) {
        List<HotelDto> hotels = new ArrayList<>();

        List<Room> rooms = RepositoryFactory.getHotelRepository().getHotelRooms(connection);
        Map<UUID, List<Room>> roomsGroupedByHotel = rooms.stream().collect(Collectors.groupingBy(Room::getHotelId));

        for (Map.Entry<UUID, List<Room>> entry : roomsGroupedByHotel.entrySet()) {
            List<RoomDto> roomsDto = entry.getValue().stream().map(
                    room -> RoomDto.builder()
                            .id(room.getRoomId())
                            .type(room.getRoomType())
                            .numberOfGuests(room.getNumberOfGuests())
                            .price(PriceDto.builder().dailyRate(room.getDailyRate()).currencyCode(room.getCurrencyCode()).build())
                            .build()
            ).collect(Collectors.toList());

            hotels.add(
                    HotelDto.builder()
                            .id(entry.getKey())
                            .name(entry.getValue().get(0).getHotelName()) // Get the hotel name from the first room (same approach for location)
                            .location(LocationDto.builder()
                                    .country(CountryDto.builder().code(entry.getValue().get(0).getCountryCode()).build())
                                    .city(entry.getValue().get(0).getCity())
                                    .coordinates(CoordinatesDto.builder().latitude(entry.getValue().get(0).getLatitude()).longitude(entry.getValue().get(0).getLongitude()).build())
                                    .build())
                            .rooms(roomsDto)
                            .build()
            );
        }

        return hotels;
    }
}
