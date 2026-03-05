package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class EventService {

    private final Firestore firestore;

    public EventService(Firestore firestore) {
        this.firestore = firestore;
    }

    public String createEvent(CreateEventRequest req) throws Exception {
        if (req == null) throw new IllegalArgumentException("Request body is required.");
        if (req.getName() == null || req.getName().trim().isEmpty())
            throw new IllegalArgumentException("Event name is required.");
        if (req.getAvailableTickets() < 0)
            throw new IllegalArgumentException("availableTickets must be >= 0.");
        if (req.getEventDateMillis() <= 0)
            throw new IllegalArgumentException("eventDateMillis is required.");

        Timestamp eventDate = Timestamp.ofTimeSecondsAndNanos(
                req.getEventDateMillis() / 1000,
                (int) ((req.getEventDateMillis() % 1000) * 1_000_000)
        );

        Map<String, Object> doc = new HashMap<>();
        doc.put("name", req.getName().trim());
        doc.put("location", req.getLocation() == null ? "" : req.getLocation().trim());
        doc.put("availableTickets", req.getAvailableTickets());
        doc.put("eventDate", eventDate);
        doc.put("organizerId", req.getOrganizerId() == null ? "" : req.getOrganizerId().trim());
        doc.put("createdAt", Instant.now().toString());

        ApiFuture<DocumentReference> added = firestore.collection("events").add(doc);
        return added.get().getId();
    }
}