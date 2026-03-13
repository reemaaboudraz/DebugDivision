package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.Reservation;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ReservationRepository {

    private final Firestore db;

    public ReservationRepository(Firestore db) {
        this.db = db;
    }

    /**
     * Atomically checks ticket availability, decrements the event's ticket count,
     * and saves the reservation — all in a single Firestore transaction.
     * Throws IllegalArgumentException (wrapped in ExecutionException) if the event
     * is not found or does not have enough tickets.
     */
    public Reservation createReservationAtomically(
            String eventId, int numberOfTickets, String userName, String userEmail)
            throws ExecutionException, InterruptedException {

        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentReference reservationRef = db.collection("reservations").document();

        ApiFuture<Reservation> future = db.runTransaction(transaction -> {
            DocumentSnapshot eventSnap = transaction.get(eventRef).get();

            if (!eventSnap.exists())
                throw new IllegalArgumentException("Event not found.");

            long available = eventSnap.getLong("availableTickets");
            if (available < numberOfTickets)
                throw new IllegalArgumentException("Not enough tickets available.");

            transaction.update(eventRef, "availableTickets", available - numberOfTickets);

            Reservation reservation = new Reservation();
            reservation.setId(reservationRef.getId());
            reservation.setEventId(eventId);
            reservation.setEventName(eventSnap.getString("name"));
            reservation.setUserName(userName);
            reservation.setUserEmail(userEmail);
            reservation.setNumberOfTickets(numberOfTickets);
            reservation.setStatus("CONFIRMED");
            reservation.setCreatedAt(Timestamp.now());

            transaction.set(reservationRef, reservation);
            return reservation;
        });

        return future.get();
    }

    public String saveReservation(Reservation reservation) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = db.collection("reservations").add(reservation);
        String generatedId = future.get().getId();
        reservation.setId(generatedId);
        return generatedId;
    }

    public Reservation getReservationById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection("reservations").document(id).get().get();
        if (!doc.exists()) return null;
        Reservation reservation = doc.toObject(Reservation.class);
        if (reservation != null) reservation.setId(doc.getId());
        return reservation;
    }

    public List<Reservation> getReservationsByEventId(String eventId) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection("reservations")
                .whereEqualTo("eventId", eventId).get().get();
        List<Reservation> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Reservation r = doc.toObject(Reservation.class);
            if (r != null) { r.setId(doc.getId()); result.add(r); }
        }
        return result;
    }

    public List<Reservation> getReservationsByUserEmail(String userEmail) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection("reservations")
                .whereEqualTo("userEmail", userEmail).get().get();
        List<Reservation> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Reservation r = doc.toObject(Reservation.class);
            if (r != null) { r.setId(doc.getId()); result.add(r); }
        }
        return result;
    }
}