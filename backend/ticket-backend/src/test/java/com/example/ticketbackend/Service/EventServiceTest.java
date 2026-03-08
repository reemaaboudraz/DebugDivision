package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Repository.EventRepository;
import com.google.cloud.Timestamp;
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
import static org.mockito.ArgumentMatchers.argThat;
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
        testEvent.setCategory("concert");
        testEvent.setArtist("Test Artist");
        testEvent.setOrganization("Test Org");
        testEvent.setCity("Montreal");
        testEvent.setCountry("Canada");
        testEvent.setOverview("Overview");
        testEvent.setVenue("Bell Centre");
        testEvent.setImageUrl("https://example.com/image.jpg");
        testEvent.setBuyTicketsUrl("https://example.com/tickets");

        validRequest = buildValidRequest();

    }

    private CreateEventRequest buildValidRequest() {
        CreateEventRequest req = new CreateEventRequest();
        req.setName("Test Event");
        req.setLocation("Campus Hall");
        req.setAvailableTickets(50);
        req.setEventDateMillis(1893456000000L);
        req.setOrganizerId("organizer-123");
        req.setCategory("concert");
        req.setArtist("Test Artist");
        req.setOrganization("Test Org");
        req.setCity("Montreal");
        req.setCountry("Canada");
        req.setOverview("Overview");
        req.setVenue("Bell Centre");
        req.setImageUrl("https://example.com/image.jpg");
        req.setBuyTicketsUrl("https://example.com/tickets");
        return req;
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
        when(eventRepository.saveEvent(any(Event.class))).thenReturn("test-event-id");

        eventService.createEvent(validRequest);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).saveEvent(captor.capture());

        Event saved = captor.getValue();
        assertEquals("Test Event", saved.getName());
        assertEquals("Campus Hall", saved.getLocation());
        assertEquals(50, saved.getAvailableTickets());
        assertEquals("organizer-123", saved.getOrganizerId());
        assertEquals("concert", saved.getCategory());
        assertEquals("Test Artist", saved.getArtist());
        assertEquals("Test Org", saved.getOrganization());
        assertEquals("Montreal", saved.getCity());
        assertEquals("Canada", saved.getCountry());
        assertEquals("Overview", saved.getOverview());
        assertEquals("Bell Centre", saved.getVenue());
        assertEquals("https://example.com/image.jpg", saved.getImageUrl());
        assertEquals("https://example.com/tickets", saved.getBuyTicketsUrl());
        assertNotNull(saved.getEventDate());
    }


@Test
void testCreateEvent_TrimsAndLowercasesFields() throws ExecutionException, InterruptedException {
    validRequest.setName("  Test Event  ");
    validRequest.setLocation("  Campus Hall  ");
    validRequest.setOrganizerId("  organizer-123  ");
    validRequest.setCategory("  Concert  ");
    validRequest.setArtist("  Test Artist  ");
    validRequest.setOrganization("  Test Org  ");
    validRequest.setCity("  Montreal  ");
    validRequest.setCountry("  Canada  ");
    validRequest.setOverview("  Overview  ");
    validRequest.setVenue("  Bell Centre  ");
    validRequest.setImageUrl("  https://example.com/image.jpg  ");
    validRequest.setBuyTicketsUrl("  https://example.com/tickets  ");

    when(eventRepository.saveEvent(any(Event.class))).thenReturn("test-event-id");

    eventService.createEvent(validRequest);

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
    verify(eventRepository).saveEvent(captor.capture());

    Event saved = captor.getValue();
    assertEquals("Test Event", saved.getName());
    assertEquals("Campus Hall", saved.getLocation());
    assertEquals("organizer-123", saved.getOrganizerId());
    assertEquals("concert", saved.getCategory());
    assertEquals("Test Artist", saved.getArtist());
    assertEquals("Test Org", saved.getOrganization());
    assertEquals("Montreal", saved.getCity());
    assertEquals("Canada", saved.getCountry());
    assertEquals("Overview", saved.getOverview());
    assertEquals("Bell Centre", saved.getVenue());
    assertEquals("https://example.com/image.jpg", saved.getImageUrl());
    assertEquals("https://example.com/tickets", saved.getBuyTicketsUrl());
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
    void testCreateEvent_NullCategory_Throws() throws ExecutionException, InterruptedException {
        validRequest.setCategory(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        assertEquals("category is required.", ex.getMessage());
        verify(eventRepository, never()).saveEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_BlankCategory_Throws() throws ExecutionException, InterruptedException {
        validRequest.setCategory("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(validRequest);
        });

        assertEquals("category is required.", ex.getMessage());
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
void testCreateEvent_NewFieldsDefaultToEmptyWhenNull() throws ExecutionException, InterruptedException {
    validRequest.setArtist(null);
    validRequest.setOrganization(null);
    validRequest.setCity(null);
    validRequest.setCountry(null);
    validRequest.setOverview(null);
    validRequest.setVenue(null);
    validRequest.setImageUrl(null);
    validRequest.setBuyTicketsUrl(null);

    when(eventRepository.saveEvent(any(Event.class))).thenReturn("test-event-id");

    eventService.createEvent(validRequest);

    verify(eventRepository).saveEvent(argThat(saved ->
            "".equals(saved.getArtist()) &&
                    "".equals(saved.getOrganization()) &&
                    "".equals(saved.getCity()) &&
                    "".equals(saved.getCountry()) &&
                    "".equals(saved.getOverview()) &&
                    "".equals(saved.getVenue()) &&
                    "".equals(saved.getImageUrl()) &&
                    "".equals(saved.getBuyTicketsUrl())
    ));
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
        validRequest.setCategory("sports");
        validRequest.setArtist("Updated Artist");
        validRequest.setOrganization("Updated Org");
        validRequest.setCity("Toronto");
        validRequest.setCountry("Canada");
        validRequest.setOverview("Updated Overview");
        validRequest.setVenue("Updated Venue");
        validRequest.setImageUrl("https://example.com/updated.jpg");
        validRequest.setBuyTicketsUrl("https://example.com/updated-tickets");

        eventService.updateEvent("test-event-id", validRequest);

        verify(eventRepository).updateEvent(argThat(event ->
                "Updated Name".equals(event.getName()) &&
                        "New Location".equals(event.getLocation()) &&
                        event.getAvailableTickets() == 100 &&
                        "sports".equals(event.getCategory()) &&
                        "Updated Artist".equals(event.getArtist()) &&
                        "Updated Org".equals(event.getOrganization()) &&
                        "Toronto".equals(event.getCity()) &&
                        "Canada".equals(event.getCountry()) &&
                        "Updated Overview".equals(event.getOverview()) &&
                        "Updated Venue".equals(event.getVenue()) &&
                        "https://example.com/updated.jpg".equals(event.getImageUrl()) &&
                        "https://example.com/updated-tickets".equals(event.getBuyTicketsUrl())
        ));
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
void testUpdateEvent_NullNewFieldsDefaultToEmpty() throws ExecutionException, InterruptedException {
    when(eventRepository.getEventById("test-event-id")).thenReturn(testEvent);
    doNothing().when(eventRepository).updateEvent(any(Event.class));

    validRequest.setLocation(null);
    validRequest.setCategory("concert");
    validRequest.setArtist(null);
    validRequest.setOrganization(null);
    validRequest.setCity(null);
    validRequest.setCountry(null);
    validRequest.setOverview(null);
    validRequest.setVenue(null);
    validRequest.setImageUrl(null);
    validRequest.setBuyTicketsUrl(null);

    eventService.updateEvent("test-event-id", validRequest);

    verify(eventRepository).updateEvent(argThat(event ->
            "".equals(event.getLocation()) &&
                    "".equals(event.getArtist()) &&
                    "".equals(event.getOrganization()) &&
                    "".equals(event.getCity()) &&
                    "".equals(event.getCountry()) &&
                    "".equals(event.getOverview()) &&
                    "".equals(event.getVenue()) &&
                    "".equals(event.getImageUrl()) &&
                    "".equals(event.getBuyTicketsUrl())
    ));
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