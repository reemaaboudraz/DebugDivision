package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateReservationRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Model.Reservation;
import com.example.ticketbackend.Repository.EventRepository;
import com.example.ticketbackend.Repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Event testEvent;
    private CreateReservationRequest validRequest;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        testEvent = new Event();
        testEvent.setId("event-1");
        testEvent.setName("Test Concert");
        testEvent.setAvailableTickets(10);

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
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        Reservation result = reservationService.createReservation(validRequest);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(reservationRepository).saveReservation(any(Reservation.class));
    }

    @Test
    void testCreateReservation_SetsFieldsCorrectly() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        reservationService.createReservation(validRequest);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveReservation(captor.capture());

        Reservation saved = captor.getValue();
        assertEquals("event-1", saved.getEventId());
        assertEquals("Test Concert", saved.getEventName());
        assertEquals("John Doe", saved.getUserName());
        assertEquals("john@example.com", saved.getUserEmail());
        assertEquals(2, saved.getNumberOfTickets());
        assertEquals("CONFIRMED", saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testCreateReservation_DecrementsAvailableTickets() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        reservationService.createReservation(validRequest); // numberOfTickets = 2, available = 10

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).updateEvent(captor.capture());
        assertEquals(8, captor.getValue().getAvailableTickets());
    }

    @Test
    void testCreateReservation_ExactlyEnoughTickets_Success() throws ExecutionException, InterruptedException {
        testEvent.setAvailableTickets(2);
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        Reservation result = reservationService.createReservation(validRequest); // numberOfTickets = 2

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void testCreateReservation_TrimsNameAndEmail() throws ExecutionException, InterruptedException {
        validRequest.setUserName("  John Doe  ");
        validRequest.setUserEmail("  john@example.com  ");
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        reservationService.createReservation(validRequest);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveReservation(captor.capture());
        assertEquals("John Doe", captor.getValue().getUserName());
        assertEquals("john@example.com", captor.getValue().getUserEmail());
    }

    @Test
    void testCreateReservation_NullEventId_Throws() {
        validRequest.setEventId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("eventId is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_BlankEventId_Throws() {
        validRequest.setEventId("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("eventId is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_NullUserName_Throws() {
        validRequest.setUserName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("userName is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_BlankUserName_Throws() {
        validRequest.setUserName("   ");

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_NullUserEmail_Throws() {
        validRequest.setUserEmail(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("userEmail is required.", ex.getMessage());
        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_BlankUserEmail_Throws() {
        validRequest.setUserEmail("   ");

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_ZeroTickets_Throws() {
        validRequest.setNumberOfTickets(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("numberOfTickets must be at least 1.", ex.getMessage());
        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_NegativeTickets_Throws() {
        validRequest.setNumberOfTickets(-5);

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        verifyNoInteractions(reservationRepository, eventRepository);
    }

    @Test
    void testCreateReservation_EventNotFound_Throws() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("event-1")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("Event not found.", ex.getMessage());
        verify(eventRepository).getEventById("event-1");
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_NotEnoughTickets_Throws() throws ExecutionException, InterruptedException {
        testEvent.setAvailableTickets(1);
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);

        validRequest.setNumberOfTickets(5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(validRequest));

        assertEquals("Not enough tickets available.", ex.getMessage());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_RepositoryFailure() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("event-1")).thenReturn(testEvent);
        doThrow(new ExecutionException(new RuntimeException("Firestore error")))
                .when(reservationRepository).saveReservation(any(Reservation.class));

        assertThrows(ExecutionException.class, () ->
                reservationService.createReservation(validRequest));

        verify(reservationRepository).saveReservation(any(Reservation.class));
    }

    /**
     * getReservationById tests
     */

    @Test
    void testGetReservationById_Success() throws ExecutionException, InterruptedException {
        Reservation reservation = new Reservation();
        reservation.setId("res-1");
        when(reservationRepository.getReservationById("res-1")).thenReturn(reservation);

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
        verify(reservationRepository).getReservationsByEventId("event-1");
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
        Reservation r = new Reservation();
        r.setUserEmail("john@example.com");
        when(reservationRepository.getReservationsByUserEmail("john@example.com"))
                .thenReturn(List.of(r));

        List<Reservation> result = reservationService.getReservationsByUserEmail("john@example.com");

        assertEquals(1, result.size());
        verify(reservationRepository).getReservationsByUserEmail("john@example.com");
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
