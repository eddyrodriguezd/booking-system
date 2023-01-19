package com.test.booking.api.repository;

import com.test.booking.commons.exception.DatabaseAccessException;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.util.db.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class ReservationRepositoryImpl implements ReservationRepository {

    private static final String GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY = "SELECT checkin_date, checkout_date FROM booking.reservations WHERE room_id = ? ORDER BY checkin_date";

    @Override
    public List<Reservation> getReservationsByRoomId(Connection connection, UUID roomId) {
        try {
            log.info("Query that will be executed: <{}>", GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY);
            PreparedStatement preparedStatement = connection.prepareStatement(GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY);
            PGobject pgObject = DBUtil.buildPostgresUUIDObject(roomId);
            preparedStatement.setObject(1, pgObject);
            ResultSet rs = preparedStatement.executeQuery();

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                LocalDate checkinDate = LocalDate.parse(rs.getString("checkin_date"));
                LocalDate checkoutDate = LocalDate.parse(rs.getString("checkout_date"));
                reservations.add(
                        Reservation.builder()
                                //.checkinDate(checkinDate)
                                //.checkoutDate(checkoutDate)
                                .stay(checkinDate.datesUntil(checkoutDate.plus(Period.ofDays(1))).collect(Collectors.toList()))
                                .build()
                );
            }

            log.info("Reservations retrieved from database: <{}>", reservations);
            return reservations;

        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY);
        }
    }
}
