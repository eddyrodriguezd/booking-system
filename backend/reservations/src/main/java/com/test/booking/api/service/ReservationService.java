package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.repository.ReservationRepository;
import com.test.booking.api.repository.factory.RepositoryFactory;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.model.enums.ReservationStatus;
import com.test.booking.commons.util.Message;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ReservationService {

    private final Connection connection;
    private final ReservationValidationService reservationValidationService;
    private final ReservationRepository reservationRepository;

    public ReservationService(Connection connection, ReservationRepository reservationRepository) {
        this.connection = connection;
        this.reservationValidationService = new ReservationValidationService(connection, reservationRepository);
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservations(UUID guestId) {
        List<Reservation> reservations = reservationRepository.getReservationsByUser(connection, guestId);
        log.info("Reservations retrieved for guest=<{}>: <{}>", guestId, reservations);
        return reservations;
    }

    public Reservation placeReservation(ReservationDto reservationDto) {
        reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto);
        reservationValidationService.validateReservationDatesAvailability(reservationDto, null);
        reservationValidationService.validateUserIsNotExtendingStayByCreatingANewOne(reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation = reservationRepository.createReservation(connection, reservation);
        log.info("Reservation <{}> was successfully created", reservation);
        return reservation;
    }

    public Reservation modifyReservation(UUID reservationId, UUID guestId, ReservationDto reservationDto) {
        reservationValidationService.validateReservationExistsAndBelongsToUser(reservationId, guestId);
        reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto);

        Reservation existingReservation = reservationRepository.getReservationById(connection, reservationId);
        reservationDto.setRoomId(existingReservation.getRoomId().toString());
        reservationValidationService.validateReservationDatesAvailability(reservationDto, existingReservation.getStay());

        reservationDto.setGuestId(guestId.toString());
        reservationValidationService.validateUserIsNotMergingStaysByModifyingAPreviousOne(reservationId, reservationDto);

        Reservation reservation = reservationDto.toEntity();
        reservation.setReservationId(reservationId);
        Reservation newReservation = reservationRepository.modifyReservation(connection, reservation);
        reservation.setReservationId(reservationId);
        log.info("Reservation was successfully updated from <{}> to <{}>", reservation, newReservation);

        return reservation;
    }

    public Message cancelReservation(UUID reservationId, UUID guestId) {
        reservationValidationService.validateReservationExistsAndBelongsToUser(reservationId, guestId);

        Reservation existingReservation = reservationRepository.getReservationById(connection, reservationId);
        if(existingReservation.getStatus().equals(ReservationStatus.CANCELED))
            return Message.builder().message("Reservation with id=<" + reservationId + "> was already canceled").build();

        reservationRepository.cancelReservation(connection, reservationId);
        log.info("Reservation with id=<{}> was successfully canceled", reservationId);
        return Message.builder().message("Reservation with id=<" + reservationId + "> has been canceled").build();
    }
}
