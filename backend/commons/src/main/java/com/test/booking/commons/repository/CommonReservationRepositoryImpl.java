package com.test.booking.commons.repository;

import com.test.booking.commons.exception.DatabaseAccessException;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.model.enums.ReservationStatus;
import com.test.booking.commons.util.db.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class CommonReservationRepositoryImpl implements CommonReservationRepository {

    private static final String GET_RESERVATIONS_DATES_BY_ROOM_ID_QUERY = "SELECT check_in_date, check_out_date FROM booking.reservations WHERE room_id = ? AND status = 'VALID' ORDER BY checkin_date";

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
                LocalDate checkInDate = LocalDate.parse(rs.getString("check_in_date"));
                LocalDate checkOutDate = LocalDate.parse(rs.getString("check_out_date"));

                reservations.add(
                        Reservation.builder()
                                .checkInDate(checkInDate)
                                .checkOutDate(checkOutDate)
                                //.stay(checkInDate.datesUntil(checkOutDate.plus(Period.ofDays(1))).collect(Collectors.toList()))
                                .status(ReservationStatus.VALID)
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
