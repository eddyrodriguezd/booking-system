package com.test.booking.api.repository;

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
public class ReservationRepositoryImpl implements ReservationRepository {

    private static final String GET_ALL_RESERVATIONS = "SELECT id as reservation_id, room_id, checkin_date, checkout_date, guest_id, status FROM booking.reservations;";
    private static final String GET_GUEST_RESERVATIONS = "SELECT id as reservation_id, room_id, checkin_date, checkout_date, guest_id, status FROM booking.reservations WHERE guest_id = ?;";
    private static final String CREATE_RESERVATION = "INSERT INTO booking.reservations (room_id, checkin_date, checkout_date, guest_id, status) VALUES (?, ?, ?, ?, ?);";
    private static final String MODIFY_RESERVATION = "UPDATE booking.reservations SET check_in_date = ?, check_out_date = ? where id = ?;";
    private static final String CANCEL_RESERVATION = "UPDATE booking.reservations SET status = 'CANCELED' where id = ?;";

    @Override
    public List<Reservation> getReservations(Connection connection) {
        try {
            log.info("Query that will be executed: <{}>", GET_ALL_RESERVATIONS);
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_RESERVATIONS);
            ResultSet rs = preparedStatement.executeQuery();

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                reservations.add(
                        Reservation.builder()
                                .reservationId(UUID.fromString(rs.getString("reservation_id")))
                                .roomId(UUID.fromString(rs.getString("room_id")))
                                .checkInDate(LocalDate.parse(rs.getString("check_in_date")))
                                .checkOutDate(LocalDate.parse(rs.getString("check_out_date")))
                                .guestId(UUID.fromString(rs.getString("guest_id")))
                                .status(ReservationStatus.valueOf(rs.getString("status")))
                                .build()
                );
            }
            return reservations;
        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", GET_ALL_RESERVATIONS, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(GET_ALL_RESERVATIONS);
        }
    }

    @Override
    public List<Reservation> getGuestReservations(Connection connection, UUID guestId) {
        try {
            log.info("Query that will be executed: <{}>", GET_GUEST_RESERVATIONS);
            PreparedStatement preparedStatement = connection.prepareStatement(GET_GUEST_RESERVATIONS);

            PGobject guestIdPgObject = DBUtil.buildPostgresUUIDObject(guestId);
            preparedStatement.setObject(1, guestIdPgObject);
            ResultSet rs = preparedStatement.executeQuery();

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                reservations.add(
                        Reservation.builder()
                                .reservationId(UUID.fromString(rs.getString("reservation_id")))
                                .roomId(UUID.fromString(rs.getString("room_id")))
                                .checkInDate(LocalDate.parse(rs.getString("check_in_date")))
                                .checkOutDate(LocalDate.parse(rs.getString("check_out_date")))
                                .guestId(UUID.fromString(rs.getString("guest_id")))
                                .status(ReservationStatus.valueOf(rs.getString("status")))
                                .build()
                );
            }
            return reservations;
        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", GET_GUEST_RESERVATIONS, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(GET_GUEST_RESERVATIONS);
        }
    }

    @Override
    public Reservation createReservation(Connection connection, Reservation reservation) {
        try {
            log.info("Query that will be executed: <{}>", CREATE_RESERVATION);
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_RESERVATION);

            reservation.setStatus(ReservationStatus.VALID);
            PGobject roomIdPgObject = DBUtil.buildPostgresUUIDObject(reservation.getRoomId());
            PGobject guestIdPgObject = DBUtil.buildPostgresUUIDObject(reservation.getGuestId());
            preparedStatement.setObject(1, roomIdPgObject);
            preparedStatement.setObject(2, reservation.getCheckInDate());
            preparedStatement.setObject(3, reservation.getCheckOutDate());
            preparedStatement.setObject(4, guestIdPgObject);
            preparedStatement.setString(5, reservation.getStatus().name());

            int result = preparedStatement.executeUpdate();

            if(result == 0) {
                log.error("SQL query <{}> failed. It didn't create any record.", CREATE_RESERVATION);
                throw new DatabaseAccessException(CREATE_RESERVATION);
            }
            return reservation;
        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", CREATE_RESERVATION, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(CREATE_RESERVATION);
        }
    }

    @Override
    public Reservation modifyReservation(Connection connection, Reservation reservation) {
        try {
            log.info("Query that will be executed: <{}>", MODIFY_RESERVATION);
            PreparedStatement preparedStatement = connection.prepareStatement(MODIFY_RESERVATION);

            PGobject reservationIdPgObject = DBUtil.buildPostgresUUIDObject(reservation.getReservationId());
            preparedStatement.setObject(1, reservation.getCheckInDate());
            preparedStatement.setObject(2, reservation.getCheckOutDate());
            preparedStatement.setObject(3, reservationIdPgObject);

            int result = preparedStatement.executeUpdate();

            if(result == 0) {
                log.error("SQL query <{}> failed. It didn't update any record.", MODIFY_RESERVATION);
                throw new DatabaseAccessException(MODIFY_RESERVATION);
            }
            return reservation;
        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", MODIFY_RESERVATION, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(MODIFY_RESERVATION);
        }
    }

    @Override
    public boolean cancelReservation(Connection connection, UUID reservationId) {
        try {
            log.info("Query that will be executed: <{}>", CANCEL_RESERVATION);
            PreparedStatement preparedStatement = connection.prepareStatement(CANCEL_RESERVATION);

            PGobject reservationIdPgObject = DBUtil.buildPostgresUUIDObject(reservationId);
            preparedStatement.setObject(3, reservationIdPgObject);

            int result = preparedStatement.executeUpdate();

            if(result == 0) {
                log.error("SQL query <{}> failed. It didn't update any record.", CANCEL_RESERVATION);
                throw new DatabaseAccessException(CANCEL_RESERVATION);
            }
            return true;
        }
        catch (SQLException e) {
            log.error("SQL query <{}> failed. Error: <{}>. Stack Trace: <{}>.", CANCEL_RESERVATION, e.getMessage(), e.getStackTrace());
            throw new DatabaseAccessException(CANCEL_RESERVATION);
        }
    }
}
