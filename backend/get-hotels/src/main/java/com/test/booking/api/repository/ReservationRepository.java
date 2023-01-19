package com.test.booking.api.repository;

import com.test.booking.api.model.Room;
import com.test.booking.commons.model.Reservation;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository {

    List<Reservation> getReservationsByRoomId(Connection connection, UUID roomId);
}
