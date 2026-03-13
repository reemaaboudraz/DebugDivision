package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateReservationRequest;
import com.example.ticketbackend.Model.Reservation;
import com.example.ticketbackend.Repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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

        try {
            return reservationRepository.createReservationAtomically(
                    req.getEventId(),
                    req.getNumberOfTickets(),
                    req.getUserName().trim(),
                    req.getUserEmail().trim()
            );
        } catch (ExecutionException e) {
            // Unwrap IllegalArgumentExceptions thrown inside the transaction
            // so the controller can return 400 instead of 500
            if (e.getCause() instanceof IllegalArgumentException)
                throw (IllegalArgumentException) e.getCause();
            throw e;
        }
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