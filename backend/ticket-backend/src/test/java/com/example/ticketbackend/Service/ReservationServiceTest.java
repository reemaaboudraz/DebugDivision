package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateReservationRequest;
import com.example.ticketbackend.Model.Reservation;
import com.example.ticketbackend.Repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private CreateReservationRequest validRequest;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setId("res-1");
        testReservation.setEventId("event-1");
        testReservation.setEventName("Test Concert");
        testReservation.setUserName("John Doe");
        testReservation.setUserEmail("john@example.com");
        testReservation.setNumberOfTickets(2);
        testReservation.setStatus("CONFIRMED");

        validRequest = new CreateReservationRequest();
        validRequest.setEventId("event-1");
        validRequest.setUserName("John Doe");
        validRequest.setUserEmail("john@example.com");
        validRequest.setNumberOfTickets(2);
    }

    /**
     * createReservation tests
     */

    @Test
    void testCreateReservation_Success() throws ExecutionException, InterruptedException {
        when(reservationRepository.createReservationAtomically("event-1", 2, "John Doe", "john@example.com"))
                .thenReturn(testReservation);

        Reservation result = reservationService.createReservation(validRequest);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(reservationRepository).createReservationAtomically("event-1", 2, "John Doe", "john@example.com");
    }

    @Test
    void testCreateReservation_PassesCorrectArgsToRepository() throws ExecutionException, InterruptedException {
        when(reservationRepository.createReservationAtomically(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(testReservation);

        reservationService.createReservation(validRequest);

        verify(reservationRepository).createReservationAtomically("event-1", 2, "John Doe", "john@example.com");
    }

    @Test
    void testCreateReservation_TrimsNameAndEmail() throws ExecutionException, InterruptedException {
        validRequest.setUserName("  John Doe  ");
        validRequest.setUserEmail("  john@example.com  ");
        when(reservationRepository.createReservationAtomically(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(testReservation);

        reservationService.createReservation(validRequest);

        verify(reservationRepository).createReservationAtomically("event-1", 2, "John Doe", "john@example.com");
    }

    @Test
    void testCreateReservation_NullEventId_Throws() {
        validRequest.setEventId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("eventId is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_BlankEventId_Throws() {
        validRequest.setEventId("   ");

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_NullUserName_Throws() {
        validRequest.setUserName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("userName is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_BlankUserName_Throws() {
        validRequest.setUserName("   ");

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_NullUserEmail_Throws() {
        validRequest.setUserEmail(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("userEmail is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_BlankUserEmail_Throws() {
        validRequest.setUserEmail("   ");

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_ZeroTickets_Throws() {
        validRequest.setNumberOfTickets(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("numberOfTickets must be at least 1.", ex.getMessage());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_NegativeTickets_Throws() {
        validRequest.setNumberOfTickets(-5);

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_EventNotFound_Throws() throws ExecutionException, InterruptedException {
        when(reservationRepository.createReservationAtomically(anyString(), anyInt(), anyString(), anyString()))
                .thenThrow(new ExecutionException(new IllegalArgumentException("Event not found.")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("Event not found.", ex.getMessage());
    }

    @Test
    void testCreateReservation_NotEnoughTickets_Throws() throws ExecutionException, InterruptedException {
        when(reservationRepository.createReservationAtomically(anyString(), anyInt(), anyString(), anyString()))
                .thenThrow(new ExecutionException(new IllegalArgumentException("Not enough tickets available.")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("Not enough tickets available.", ex.getMessage());
    }

    @Test
    void testCreateReservation_FirestoreFailure_Rethrows() throws ExecutionException, InterruptedException {
        when(reservationRepository.createReservationAtomically(anyString(), anyInt(), anyString(), anyString()))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () ->
                reservationService.createReservation(validRequest));
    }

    /**
     * getReservationById tests
     */

    @Test
    void testGetReservationById_Success() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationById("res-1")).thenReturn(testReservation);

        Reservation result = reservationService.getReservationById("res-1");

        assertNotNull(result);
        assertEquals("res-1", result.getId());
        verify(reservationRepository).getReservationById("res-1");
    }

    @Test
    void testGetReservationById_NotFound() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationById("nonexistent")).thenReturn(null);

        Reservation result = reservationService.getReservationById("nonexistent");

        assertNull(result);
    }

    @Test
    void testGetReservationById_RepositoryFailure() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationById("res-1"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () ->
                reservationService.getReservationById("res-1"));
    }

    /**
     * getReservationsByEventId tests
     */

    @Test
    void testGetReservationsByEventId_Success() throws ExecutionException, InterruptedException {
        Reservation r1 = new Reservation();
        r1.setId("res-1");
        Reservation r2 = new Reservation();
        r2.setId("res-2");
        when(reservationRepository.getReservationsByEventId("event-1"))
                .thenReturn(Arrays.asList(r1, r2));

        List<Reservation> result = reservationService.getReservationsByEventId("event-1");

        assertEquals(2, result.size());
    }

    @Test
    void testGetReservationsByEventId_Empty() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationsByEventId("event-1"))
                .thenReturn(Collections.emptyList());

        List<Reservation> result = reservationService.getReservationsByEventId("event-1");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * getReservationsByUserEmail tests
     */

    @Test
    void testGetReservationsByUserEmail_Success() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationsByUserEmail("john@example.com"))
                .thenReturn(List.of(testReservation));

        List<Reservation> result = reservationService.getReservationsByUserEmail("john@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void testGetReservationsByUserEmail_Empty() throws ExecutionException, InterruptedException {
        when(reservationRepository.getReservationsByUserEmail("nobody@example.com"))
                .thenReturn(Collections.emptyList());

        List<Reservation> result = reservationService.getReservationsByUserEmail("nobody@example.com");

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}