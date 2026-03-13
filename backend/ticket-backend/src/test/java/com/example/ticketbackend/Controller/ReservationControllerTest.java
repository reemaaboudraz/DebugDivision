package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.CreateReservationRequest;
import com.example.ticketbackend.Model.Reservation;
import com.example.ticketbackend.Service.ReservationService;
import tools.jackson.databind.ObjectMapper;
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
import java.util.List;
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
class ReservationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    private Reservation testReservation;
    private Reservation secondReservation;
    private CreateReservationRequest createReservationRequest;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();

        testReservation = new Reservation();
        testReservation.setId("res-1");
        testReservation.setEventId("event-1");
        testReservation.setEventName("Test Concert");
        testReservation.setUserName("John Doe");
        testReservation.setUserEmail("john@example.com");
        testReservation.setNumberOfTickets(2);
        testReservation.setStatus("CONFIRMED");

        secondReservation = new Reservation();
        secondReservation.setId("res-2");
        secondReservation.setEventId("event-1");
        secondReservation.setEventName("Test Concert");
        secondReservation.setUserName("Jane Smith");
        secondReservation.setUserEmail("jane@example.com");
        secondReservation.setNumberOfTickets(1);
        secondReservation.setStatus("CONFIRMED");

        createReservationRequest = new CreateReservationRequest();
        createReservationRequest.setEventId("event-1");
        createReservationRequest.setUserName("John Doe");
        createReservationRequest.setUserEmail("john@example.com");
        createReservationRequest.setNumberOfTickets(2);
    }

    /**
     * POST /reservations tests
     */

    @Test
    void testCreateReservation_Success() throws Exception {
        when(reservationService.createReservation(any(CreateReservationRequest.class)))
                .thenReturn(testReservation);

        mockMvc.perform(post("/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"))
                .andExpect(jsonPath("$.eventId").value("event-1"))
                .andExpect(jsonPath("$.userName").value("John Doe"))
                .andExpect(jsonPath("$.userEmail").value("john@example.com"))
                .andExpect(jsonPath("$.numberOfTickets").value(2))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    void testCreateReservation_BadRequest_MissingField() throws Exception {
        when(reservationService.createReservation(any(CreateReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("eventId is required."));

        createReservationRequest.setEventId(null);

        mockMvc.perform(post("/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("eventId is required."));

        verify(reservationService).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    void testCreateReservation_BadRequest_NotEnoughTickets() throws Exception {
        when(reservationService.createReservation(any(CreateReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("Not enough tickets available."));

        mockMvc.perform(post("/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not enough tickets available."));
    }

    @Test
    void testCreateReservation_ServerError() throws Exception {
        when(reservationService.createReservation(any(CreateReservationRequest.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        mockMvc.perform(post("/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationRequest)))
                .andExpect(status().isInternalServerError());

        verify(reservationService).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    void testCreateReservation_InvalidContentType() throws Exception {
        mockMvc.perform(post("/reservations")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(reservationService, never()).createReservation(any());
    }

    /**
     * GET /reservations/{id} tests
     */

    @Test
    void testGetReservationById_Success() throws Exception {
        when(reservationService.getReservationById("res-1")).thenReturn(testReservation);

        mockMvc.perform(get("/reservations/res-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"))
                .andExpect(jsonPath("$.userName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService).getReservationById("res-1");
    }

    @Test
    void testGetReservationById_NotFound() throws Exception {
        when(reservationService.getReservationById("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/reservations/nonexistent"))
                .andExpect(status().isNotFound());

        verify(reservationService).getReservationById("nonexistent");
    }

    @Test
    void testGetReservationById_ServerError() throws Exception {
        when(reservationService.getReservationById("res-1"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        mockMvc.perform(get("/reservations/res-1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * GET /reservations/event/{eventId} tests
     */

    @Test
    void testGetReservationsByEvent_Success() throws Exception {
        when(reservationService.getReservationsByEventId("event-1"))
                .thenReturn(Arrays.asList(testReservation, secondReservation));

        mockMvc.perform(get("/reservations/event/event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("res-1"))
                .andExpect(jsonPath("$[1].id").value("res-2"));

        verify(reservationService).getReservationsByEventId("event-1");
    }

    @Test
    void testGetReservationsByEvent_Empty() throws Exception {
        when(reservationService.getReservationsByEventId("event-1"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reservations/event/event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetReservationsByEvent_ServerError() throws Exception {
        when(reservationService.getReservationsByEventId("event-1"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        mockMvc.perform(get("/reservations/event/event-1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * GET /reservations/user/{email} tests
     */

    @Test
    void testGetReservationsByUser_Success() throws Exception {
        when(reservationService.getReservationsByUserEmail("john@example.com"))
                .thenReturn(List.of(testReservation));

        mockMvc.perform(get("/reservations/user/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userEmail").value("john@example.com"));

        verify(reservationService).getReservationsByUserEmail("john@example.com");
    }

    @Test
    void testGetReservationsByUser_Empty() throws Exception {
        when(reservationService.getReservationsByUserEmail("nobody@example.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reservations/user/nobody@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetReservationsByUser_ServerError() throws Exception {
        when(reservationService.getReservationsByUserEmail("john@example.com"))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore error")));

        mockMvc.perform(get("/reservations/user/john@example.com"))
                .andExpect(status().isInternalServerError());
    }
}