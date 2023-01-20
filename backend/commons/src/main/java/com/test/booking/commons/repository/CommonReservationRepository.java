package com.test.booking.commons.repository;

import com.test.booking.commons.model.Reservation;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface CommonReservationRepository {

    List<Reservation> getValidReservationsByRoomId(Connection connection, UUID roomId);
}
