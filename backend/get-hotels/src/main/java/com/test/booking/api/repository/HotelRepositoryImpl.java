package com.test.booking.api.repository;

import com.test.booking.api.model.Room;
import com.test.booking.commons.exception.DatabaseAccessException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class HotelRepositoryImpl implements HotelRepository {

    private static final String GET_HOTEL_ROOMS_QUERY = "SELECT r.hotel_id, r.id as room_id, name as hotel_name, country_code, city, latitude, longitude, type as room_type, number_of_guests, daily_rate, currency_code FROM booking.hotels h " +
            "LEFT JOIN booking.rooms r ON r.hotel_id = h.id;";

    @Override
    public List<Room> getHotelRooms(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_HOTEL_ROOMS_QUERY);
            ResultSet rs = preparedStatement.executeQuery();

            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                rooms.add(
                        Room.builder()
                                .hotelId(UUID.fromString(rs.getString("hotel_id")))
                                .roomId(UUID.fromString(rs.getString("room_id")))
                                .hotelName(rs.getString("hotel_name"))
                                .countryCode(rs.getString("country_code"))
                                .city(rs.getString("city"))
                                .latitude(Double.parseDouble(rs.getString("latitude")))
                                .longitude(Double.parseDouble(rs.getString("longitude")))
                                .roomType(rs.getString("room_type"))
                                .numberOfGuests(rs.getInt("number_of_guests"))
                                .dailyRate(Double.parseDouble(rs.getString("daily_rate")))
                                .currencyCode(rs.getString("currency_code"))
                                .build()
                );
            }

            log.info("Rooms retrieved from database: <{}>", rooms);
            return rooms;

        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", GET_HOTEL_ROOMS_QUERY, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(GET_HOTEL_ROOMS_QUERY);
        }

    }
}
