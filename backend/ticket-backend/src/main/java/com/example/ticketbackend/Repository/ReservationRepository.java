package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.Reservation;
import com.google.api.core.ApiFuture;
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