package com.test.booking.api.service;

import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.exception.InvalidReservationDatesException;
import com.test.booking.api.exception.ReservationNotFoundException;
import com.test.booking.api.repository.ReservationRepositoryImpl;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.service.CommonReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.test.booking.api.service.UtilTest.getReservationDto;
import static com.test.booking.api.service.UtilTest.getValidReservation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationValidationServiceTest {

    @Mock
    public ReservationRepositoryImpl reservationRepository;

    private ReservationValidationService reservationValidationService;

    @BeforeEach
    void setUp() {
        Connection connection = null;
        reservationValidationService = new ReservationValidationService(connection, reservationRepository);
    }

    @Test
    void validateUserIsNotExtendingStayByCreatingANewOne_UserHasPreviousReservation() {
        List<Reservation> reservationsList = List.of(getValidReservation(null, "2023-01-22", "2023-01-23"));

        when(reservationRepository.getValidReservationsByUserAndCheckInOrCheckOutDate(any(), any(), any(), any()))
                .thenReturn(reservationsList);

        ReservationDto reservationDto = getReservationDto("2023-01-19", "2023-01-21");
        assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateUserIsNotExtendingStayByCreatingANewOne(reservationDto));
    }

    @Test
    void validateUserIsNotExtendingStayByCreatingANewOne_Correct_NoPreviousReservation() {
        List<Reservation> reservationsEmptyList = new ArrayList<>();

        when(reservationRepository.getValidReservationsByUserAndCheckInOrCheckOutDate(any(), any(), any(), any()))
                .thenReturn(reservationsEmptyList);
        ReservationDto reservationDto = getReservationDto("2023-01-19", "2023-01-21");
        assertDoesNotThrow(() -> reservationValidationService.validateUserIsNotExtendingStayByCreatingANewOne(reservationDto));
    }

    @Test
    void validateUserIsNotMergingStaysByModifyingAPreviousOne_UserHasPreviousReservation() {
        Reservation reservation = getValidReservation(null, "2023-01-22", "2023-01-23");
        ReservationDto reservationDto = getReservationDto("2023-01-22", "2023-01-23");
        UUID reservationId = UUID.randomUUID(); // Current reservation Id is different from the one in "database"

        when(reservationRepository.getValidReservationsByUserAndCheckInOrCheckOutDate(any(), any(), any(), any()))
                .thenReturn(List.of(reservation));

        assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateUserIsNotMergingStaysByModifyingAPreviousOne(reservationId, reservationDto));
    }

    @Test
    void validateUserIsNotMergingStaysByModifyingAPreviousOne_Correct_OnlyCurrentReservation() {
        Reservation reservation = getValidReservation(null, "2023-01-22", "2023-01-23");
        ReservationDto reservationDto = getReservationDto("2023-01-22", "2023-01-23");
        UUID reservationId = reservation.getReservationId();

        when(reservationRepository.getValidReservationsByUserAndCheckInOrCheckOutDate(any(), any(), any(), any()))
                .thenReturn(List.of(reservation));

        assertDoesNotThrow(() -> reservationValidationService.validateUserIsNotMergingStaysByModifyingAPreviousOne(reservationId, reservationDto));
    }

    @Test
    void validateReservationCheckInAndCheckOutDates_PastCheckIn() {
        ReservationDto reservationDto = getReservationDto("2023-01-19", "2023-01-21");
        assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto));
    }

    @Test
    void validateReservationCheckInAndCheckOutDates_CheckInAfterCheckOut() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-18");
        assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto));
    }

    @Test
    void validateReservationCheckInAndCheckOutDates_StayTooLong() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-24");
        assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto));
    }

    @Test
    void validateReservationCheckInAndCheckOutDates_Correct_DatesAreFine() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-20");
        assertDoesNotThrow(() -> reservationValidationService.validateReservationCheckInAndCheckOutDates(reservationDto));
    }

    @Test
    void validateReservationExistsAndBelongsToUser_ReservationBelongsToSomeoneElse() {
        Reservation reservation = getValidReservation(null, "2023-01-22", "2023-01-23");
        UUID reservationId = reservation.getReservationId();
        UUID userId = reservation.getGuestId();

        reservation.setGuestId(UUID.randomUUID()); // Reservation belongs to someone else

        when(reservationRepository.getReservationById(any(), eq(reservationId))).thenReturn(reservation);

        assertThrows(ReservationNotFoundException.class, () -> reservationValidationService.validateReservationExistsAndBelongsToUser(reservationId, userId));
    }

    @Test
    void validateReservationExistsAndBelongsToUser_Correct_ReservationsBelongsToUser() {
        Reservation reservation = getValidReservation(null, "2023-01-22", "2023-01-23");
        UUID reservationId = reservation.getReservationId();
        UUID userId = reservation.getGuestId();

        when(reservationRepository.getReservationById(any(), eq(reservationId))).thenReturn(reservation);

        assertDoesNotThrow(() -> reservationValidationService.validateReservationExistsAndBelongsToUser(reservationId, userId));
    }

    @Test
    void validateReservationDatesAvailability_CreatingReservation_UnavailableDates() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-20");
        String roomId = reservationDto.getRoomId();

        try(MockedStatic<CommonReservationService> utilities = Mockito.mockStatic(CommonReservationService.class)) {
            utilities.when(() -> CommonReservationService.getAvailabilityByRoomId(any(), eq(UUID.fromString(roomId))))
                    .thenReturn(List.of(
                            LocalDate.parse("2023-02-17"), LocalDate.parse("2023-02-18"), LocalDate.parse("2023-02-19")
                    ));


            assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateReservationDatesAvailability(reservationDto, null));
        }
    }

    @Test
    void validateReservationDatesAvailability_CreatingReservation_Correct_AvailableDates() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-20");
        String roomId = reservationDto.getRoomId();

        try(MockedStatic<CommonReservationService> utilities = Mockito.mockStatic(CommonReservationService.class)) {
            utilities.when(() -> CommonReservationService.getAvailabilityByRoomId(any(), eq(UUID.fromString(roomId))))
                    .thenReturn(List.of(
                            LocalDate.parse("2023-02-17"), LocalDate.parse("2023-02-18"), LocalDate.parse("2023-02-19"),
                            LocalDate.parse("2023-02-20"), LocalDate.parse("2023-02-21"), LocalDate.parse("2023-02-22")
                    ));


            assertDoesNotThrow(() -> reservationValidationService.validateReservationDatesAvailability(reservationDto, null));
        }
    }

    @Test
    void validateReservationDatesAvailability_ModifyingReservation_UnavailableDates() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-20");
        String roomId = reservationDto.getRoomId();

        try(MockedStatic<CommonReservationService> utilities = Mockito.mockStatic(CommonReservationService.class)) {
            utilities.when(() -> CommonReservationService.getAvailabilityByRoomId(any(), eq(UUID.fromString(roomId))))
                    .thenReturn(List.of(
                            LocalDate.parse("2023-02-18"), LocalDate.parse("2023-02-19"),
                            LocalDate.parse("2023-02-21"), LocalDate.parse("2023-02-22")
                    ));


            assertThrows(InvalidReservationDatesException.class, () -> reservationValidationService.validateReservationDatesAvailability(reservationDto, List.of(LocalDate.parse("2023-02-21"), LocalDate.parse("2023-02-22"))));
        }
    }

    @Test
    void validateReservationDatesAvailability_ModifyingReservation_Correct_AvailableDates() {
        ReservationDto reservationDto = getReservationDto("2023-02-19", "2023-02-20");
        String roomId = reservationDto.getRoomId();

        try(MockedStatic<CommonReservationService> utilities = Mockito.mockStatic(CommonReservationService.class)) {
            utilities.when(() -> CommonReservationService.getAvailabilityByRoomId(any(), eq(UUID.fromString(roomId))))
                    .thenReturn(List.of(
                            LocalDate.parse("2023-02-17"), LocalDate.parse("2023-02-18"),
                            LocalDate.parse("2023-02-19"), LocalDate.parse("2023-02-20")
                    ));


            assertDoesNotThrow(() -> reservationValidationService.validateReservationDatesAvailability(reservationDto, List.of(LocalDate.parse("2023-02-21"), LocalDate.parse("2023-02-22"))));
        }
    }
}