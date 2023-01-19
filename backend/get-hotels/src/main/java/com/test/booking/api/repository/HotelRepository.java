package com.test.booking.api.repository;

import com.test.booking.api.model.Room;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface HotelRepository {

    List<Room> getHotelRooms(Connection connection);
}
