package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Repository.EventRepository;
import com.google.cloud.Timestamp;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private CreateEventRequest validRequest;

    @BeforeEach
    void setUp() {
        // Fake event
        testEvent = new Event();
        testEvent.setId("test-event-id");
        testEvent.setName("Test Event");
        testEvent.setLocation("Campus Hall");
        testEvent.setAvailableTickets(50);
        testEvent.setEventDate(Timestamp.ofTimeSecondsAndNanos(1893456000L, 0));
        testEvent.setOrganizerId("organizer-123");

        // Setup create request
        validRequest = new CreateEventRequest();
        validRequest.setName("Test Event");
        validRequest.setLocation("Campus Hall");
        validRequest.setAvailableTickets(50);
        validRequest.setEventDateMillis(1893456000000L);
        validRequest.setOrganizerId("organizer-123");
    }

    /**
     * createEvent tests
     */

    @Test
    void testCreateEvent_Success() throws ExecutionException, InterruptedException {
        when(eventRepository.saveEvent(any(Event.class))).thenReturn("test-event-id");

        String result = eventService.createEvent(validRequest);

        assertEquals("test-event-id", result);
        verify(eventRepository).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_SetsFieldsCorrectly() throws ExecutionException, InterruptedException {
        when(eventRepository.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            assertEquals("Test Event", saved.getName());
            assertEquals("Campus Hall", saved.getLocation());
            assertEquals(50, saved.getAvailableTickets());
            assertEquals("organizer-123", saved.getOrganizerId());
            assertNotNull(saved.getEventDate());
            return "test-event-id";
        });

        eventService.createEvent(validRequest);

        verify(eventRepository).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_NullRequest() throws ExecutionException, InterruptedException{
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(null);
        });

        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_NullName() throws ExecutionException, InterruptedException{
        validRequest.setName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_EmptyName() throws ExecutionException, InterruptedException{
        validRequest.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_NegativeTickets() throws ExecutionException, InterruptedException{
        validRequest.setAvailableTickets(-1);

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_ZeroTicketsAllowed() throws ExecutionException, InterruptedException {
        validRequest.setAvailableTickets(0); //rn we're accepting 0 available tickets (should we change this?)
        when(eventRepository.saveEvent(any(Event.class))).thenReturn("test-event-id");

        String result = eventService.createEvent(validRequest);

        assertEquals("test-event-id", result);
        verify(eventRepository).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_InvalidDate() throws ExecutionException, InterruptedException{
        validRequest.setEventDateMillis(0);

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_NullLocation_DefaultsToEmpty() throws ExecutionException, InterruptedException {
        validRequest.setLocation(null);
        when(eventRepository.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            assertEquals("", saved.getLocation());
            return "test-event-id";
        });

        eventService.createEvent(validRequest);

        verify(eventRepository).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_NullOrganizerId_DefaultsToEmpty() throws ExecutionException, InterruptedException {
        validRequest.setOrganizerId(null);
        when(eventRepository.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            assertEquals("", saved.getOrganizerId());
            return "test-event-id";
        });

        eventService.createEvent(validRequest);

        verify(eventRepository).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_RepositoryFailure() throws ExecutionException, InterruptedException {
        when(eventRepository.saveEvent(any(Event.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () -> {
            eventService.createEvent(validRequest);
        });

        verify(eventRepository).saveEvent(any(Event.class));
    }

    /**
     * getEventById tests
     */

    @Test
    void testGetEventById_Success() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id")).thenReturn(testEvent);

        Event result = eventService.getEventById("test-event-id");

        assertNotNull(result);
        assertEquals("test-event-id", result.getId());
        assertEquals("Test Event", result.getName());
        verify(eventRepository).getEventById("test-event-id");
    }

    @Test
    void testGetEventById_NotFound() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("nonexistent-id")).thenReturn(null);

        Event result = eventService.getEventById("nonexistent-id");

        assertNull(result);
        verify(eventRepository).getEventById("nonexistent-id");
    }

    @Test
    void testGetEventById_RepositoryFailure() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () -> {
            eventService.getEventById("test-event-id");
        });

        verify(eventRepository).getEventById("test-event-id");
    }

    /**
     * getAllEvents tests
     */

    @Test
    void testGetAllEvents_Success() throws ExecutionException, InterruptedException {
        Event secondEvent = new Event();
        secondEvent.setId("test-event-id-2");
        secondEvent.setName("Second Event");

        when(eventRepository.getAllEvents()).thenReturn(Arrays.asList(testEvent, secondEvent));

        List<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test-event-id", result.get(0).getId());
        assertEquals("test-event-id-2", result.get(1).getId());
        verify(eventRepository).getAllEvents();
    }

    @Test
    void testGetAllEvents_Empty() throws ExecutionException, InterruptedException {
        when(eventRepository.getAllEvents()).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(eventRepository).getAllEvents();
    }

    @Test
    void testGetAllEvents_RepositoryFailure() throws ExecutionException, InterruptedException {
        when(eventRepository.getAllEvents())
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () -> {
            eventService.getAllEvents();
        });

        verify(eventRepository).getAllEvents();
    }

    /**
     * deleteEvent tests
     */

    @Test
    void testDeleteEvent_Success() throws ExecutionException, InterruptedException {
        doNothing().when(eventRepository).deleteEvent("test-event-id");

        eventService.deleteEvent("test-event-id");

        verify(eventRepository).deleteEvent("test-event-id");
    }

    @Test
    void testDeleteEvent_RepositoryFailure() throws ExecutionException, InterruptedException {
        doThrow(new ExecutionException(new RuntimeException("Firestore error")))
                .when(eventRepository).deleteEvent("test-event-id");

        assertThrows(ExecutionException.class, () -> {
            eventService.deleteEvent("test-event-id");
        });

        verify(eventRepository).deleteEvent("test-event-id");
    }

    /**
     * updateEvent tests
     */

    @Test
    void testUpdateEvent_Success() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id")).thenReturn(testEvent);
        doNothing().when(eventRepository).updateEvent(any(Event.class));

        eventService.updateEvent("test-event-id", validRequest);

        verify(eventRepository).getEventById("test-event-id");
        verify(eventRepository).updateEvent(any(Event.class));
    }

    @Test
    void testUpdateEvent_UpdatesFieldsCorrectly() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id")).thenReturn(testEvent);
        doNothing().when(eventRepository).updateEvent(any(Event.class));

        validRequest.setName("Updated Name");
        validRequest.setLocation("New Location");
        validRequest.setAvailableTickets(100);

        eventService.updateEvent("test-event-id", validRequest);

        verify(eventRepository).updateEvent(argThat(event -> {
            assertEquals("Updated Name", event.getName());
            assertEquals("New Location", event.getLocation());
            assertEquals(100, event.getAvailableTickets());
            return true;
        }));
    }

    @Test
    void testUpdateEvent_EventNotFound() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("nonexistent-id")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent("nonexistent-id", validRequest);
        });

        assertEquals("Event not found.", exception.getMessage());

        verify(eventRepository).getEventById("nonexistent-id");
        verify(eventRepository, never()).updateEvent(any(Event.class));
    }

    @Test
    void testUpdateEvent_RepositoryFailureOnFetch() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        assertThrows(ExecutionException.class, () -> {
            eventService.updateEvent("test-event-id", validRequest);
        });

        verify(eventRepository).getEventById("test-event-id");
        verify(eventRepository, never()).updateEvent(any(Event.class));
    }

    @Test
    void testUpdateEvent_RepositoryFailureOnSave() throws ExecutionException, InterruptedException {
        when(eventRepository.getEventById("test-event-id")).thenReturn(testEvent);
        doThrow(new ExecutionException(new RuntimeException("Firestore error")))
                .when(eventRepository).updateEvent(any(Event.class));

        assertThrows(ExecutionException.class, () -> {
            eventService.updateEvent("test-event-id", validRequest);
        });

        verify(eventRepository).getEventById("test-event-id");
        verify(eventRepository).updateEvent(any(Event.class));
    }
}