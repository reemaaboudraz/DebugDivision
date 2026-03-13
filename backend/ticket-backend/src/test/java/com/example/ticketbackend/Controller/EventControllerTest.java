package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Service.EventService;
import tools.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.security.user.name=user",
        "spring.security.user.password=password"
})
@WithMockUser
class EventControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    private Event testEvent;
    private Event secondEvent;
    private CreateEventRequest createEventRequest;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();

        // Fake test event 1
        testEvent = new Event();
        testEvent.setId("test-event-id");
        testEvent.setName("Test Event");
        testEvent.setLocation("Campus Hall");
        testEvent.setAvailableTickets(50);
        testEvent.setEventDate(Timestamp.ofTimeSecondsAndNanos(1893456000L, 0));
        testEvent.setOrganizerId("organizer-123");

        // Fake test event 2
        secondEvent = new Event();
        secondEvent.setId("test-event-id-2");
        secondEvent.setName("Second Event");
        secondEvent.setLocation("Somewhere");
        secondEvent.setAvailableTickets(100);
        secondEvent.setEventDate(Timestamp.ofTimeSecondsAndNanos(1893456000L, 0));
        secondEvent.setOrganizerId("organizer-123");

        // Setup create request
        createEventRequest = new CreateEventRequest();
        createEventRequest.setName("Test Event");
        createEventRequest.setLocation("Campus Hall");
        createEventRequest.setAvailableTickets(50);
        createEventRequest.setEventDateMillis(1893456000000L);
        createEventRequest.setOrganizerId("organizer-123");
        createEventRequest.setCategory("concert");
    }

    /**
     * Creating event endpoint tests
     */

    @Test
    void testCreateEvent_Success() throws Exception {
        when(eventService.createEvent(any(CreateEventRequest.class)))
                .thenReturn("test-event-id");

        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-event-id"));

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void testCreateEvent_MissingName() throws Exception {
        // simulate service throwing for missing name
        when(eventService.createEvent(any(CreateEventRequest.class)))
                .thenThrow(new IllegalArgumentException("Event name is required."));

        createEventRequest.setName("");

        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void testCreateEvent_NegativeTickets() throws Exception {
        when(eventService.createEvent(any(CreateEventRequest.class)))
                .thenThrow(new IllegalArgumentException("availableTickets must be >= 0."));

        createEventRequest.setAvailableTickets(-1);

        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }
    @Test
    void testCreateEvent_MissingCategory() throws Exception {
        when(eventService.createEvent(any(CreateEventRequest.class)))
                .thenThrow(new IllegalArgumentException("category is required."));

        createEventRequest.setCategory("");

        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void testUpdateEvent_MissingCategory() throws Exception {
        doThrow(new IllegalArgumentException("category is required."))
                .when(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));

        createEventRequest.setCategory("");

        mockMvc.perform(put("/events/test-event-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));
    }
    @Test
    void testCreateEvent_ExecutionException() throws Exception {
        when(eventService.createEvent(any(CreateEventRequest.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isInternalServerError());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void testCreateEvent_InvalidContentType() throws Exception {
        mockMvc.perform(post("/events/create")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(eventService, never()).createEvent(any());
    }

    /**
     * Getting all events endpoint tests
     */

    @Test
    void testGetAllEvents_Success() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Arrays.asList(testEvent, secondEvent));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("test-event-id"))
                .andExpect(jsonPath("$[0].name").value("Test Event"))
                .andExpect(jsonPath("$[0].location").value("Campus Hall"))
                .andExpect(jsonPath("$[0].availableTickets").value(50))
                .andExpect(jsonPath("$[1].id").value("test-event-id-2"))
                .andExpect(jsonPath("$[1].name").value("Second Event"));

        verify(eventService).getAllEvents();
    }

    @Test
    void testGetAllEvents_Empty() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventService).getAllEvents();
    }

    @Test
    void testGetAllEvents_ExecutionException() throws Exception {
        when(eventService.getAllEvents())
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        mockMvc.perform(get("/events"))
                .andExpect(status().isInternalServerError());

        verify(eventService).getAllEvents();
    }

    /**
     * Getting event by id endpoint tests
     */

    @Test
    void testGetEventById_Success() throws Exception {
        when(eventService.getEventById("test-event-id")).thenReturn(testEvent);

        mockMvc.perform(get("/events/test-event-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-event-id"))
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.location").value("Campus Hall"))
                .andExpect(jsonPath("$.availableTickets").value(50))
                .andExpect(jsonPath("$.organizerId").value("organizer-123"));

        verify(eventService).getEventById("test-event-id");
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        when(eventService.getEventById("nonexistent-id")).thenReturn(null);

        mockMvc.perform(get("/events/nonexistent-id"))
                .andExpect(status().isNotFound());

        verify(eventService).getEventById("nonexistent-id");
    }

    @Test
    void testGetEventById_ExecutionException() throws Exception {
        when(eventService.getEventById("test-event-id"))
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        mockMvc.perform(get("/events/test-event-id"))
                .andExpect(status().isInternalServerError());

        verify(eventService).getEventById("test-event-id");
    }

    /**
     * Delete event endpoint tests
     */

    @Test
    void testDeleteEvent_Success() throws Exception {
        doNothing().when(eventService).deleteEvent("test-event-id");

        mockMvc.perform(delete("/events/test-event-id")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value("test-event-id"));

        verify(eventService).deleteEvent("test-event-id");
    }

    @Test
    void testDeleteEvent_ExecutionException() throws Exception {
        doThrow(new ExecutionException(new RuntimeException("Database error")))
                .when(eventService).deleteEvent("test-event-id");

        mockMvc.perform(delete("/events/test-event-id")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(eventService).deleteEvent("test-event-id");
    }

    /**
     * Update event endpoint tests
     */

    @Test
    void testUpdateEvent_Success() throws Exception {
        doNothing().when(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));

        mockMvc.perform(put("/events/test-event-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-event-id"));

        verify(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));
    }

    @Test
    void testUpdateEvent_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Event not found."))
                .when(eventService).updateEvent(eq("nonexistent-id"), any(CreateEventRequest.class));

        mockMvc.perform(put("/events/nonexistent-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isNotFound());

        verify(eventService).updateEvent(eq("nonexistent-id"), any(CreateEventRequest.class));
    }

    @Test
    void testUpdateEvent_ExecutionException() throws Exception {
        doThrow(new ExecutionException(new RuntimeException("Database error")))
                .when(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));

        mockMvc.perform(put("/events/test-event-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRequest)))
                .andExpect(status().isInternalServerError());

        verify(eventService).updateEvent(eq("test-event-id"), any(CreateEventRequest.class));
    }

    @Test
    void testUpdateEvent_InvalidContentType() throws Exception {
        mockMvc.perform(put("/events/test-event-id")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(eventService, never()).updateEvent(anyString(), any());
    }
}