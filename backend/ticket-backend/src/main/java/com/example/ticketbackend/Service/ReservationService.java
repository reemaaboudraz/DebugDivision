package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateReservationRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Model.Reservation;
import com.example.ticketbackend.Repository.EventRepository;
import com.example.ticketbackend.Repository.ReservationRepository;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public ReservationService(ReservationRepository reservationRepository, EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
    }

    public Reservation createReservation(CreateReservationRequest req) throws ExecutionException, InterruptedException {
        if (req.getEventId() == null || req.getEventId().isBlank())
            throw new IllegalArgumentException("eventId is required.");
        if (req.getUserName() == null || req.getUserName().isBlank())
            throw new IllegalArgumentException("userName is required.");
        if (req.getUserEmail() == null || req.getUserEmail().isBlank())
            throw new IllegalArgumentException("userEmail is required.");
        if (req.getNumberOfTickets() <= 0)
            throw new IllegalArgumentException("numberOfTickets must be at least 1.");

        Event event = eventRepository.getEventById(req.getEventId());
        if (event == null) throw new IllegalArgumentException("Event not found.");
        if (event.getAvailableTickets() < req.getNumberOfTickets())
            throw new IllegalArgumentException("Not enough tickets available.");

        // Decrement available tickets
        event.setAvailableTickets(event.getAvailableTickets() - req.getNumberOfTickets());
        eventRepository.updateEvent(event);

        Reservation reservation = new Reservation();
        reservation.setEventId(req.getEventId());
        reservation.setEventName(event.getName());
        reservation.setUserName(req.getUserName().trim());
        reservation.setUserEmail(req.getUserEmail().trim());
        reservation.setNumberOfTickets(req.getNumberOfTickets());
        reservation.setStatus("CONFIRMED");
        reservation.setCreatedAt(Timestamp.now());

        reservationRepository.saveReservation(reservation);
        return reservation;
    }

    public Reservation getReservationById(String id) throws ExecutionException, InterruptedException {
        return reservationRepository.getReservationById(id);
    }

    public List<Reservation> getReservationsByEventId(String eventId) throws ExecutionException, InterruptedException {
        return reservationRepository.getReservationsByEventId(eventId);
    }

    public List<Reservation> getReservationsByUserEmail(String email) throws ExecutionException, InterruptedException {
        return reservationRepository.getReservationsByUserEmail(email);
    }
}