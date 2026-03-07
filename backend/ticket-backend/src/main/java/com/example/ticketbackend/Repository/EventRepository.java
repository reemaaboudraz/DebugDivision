package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.Event;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class EventRepository {

    private final Firestore db;

    public EventRepository(Firestore db) {
        this.db = db;
    }

    public String saveEvent(Event event) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = db.collection("events").add(event);
        String generatedId = future.get().getId();
        event.setId(generatedId);
        return generatedId;
    }

    public Event getEventById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection("events").document(id).get().get();
        if (!doc.exists()) return null;
        Event event = doc.toObject(Event.class);
        if (event != null) event.setId(doc.getId());
        return event;
    }

    public List<Event> getAllEvents() throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection("events").get().get();
        List<Event> events = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Event event = doc.toObject(Event.class);
            if (event != null) {
                event.setId(doc.getId());
                events.add(event);
            }
        }
        return events;
    }

    public void deleteEvent(String id) throws ExecutionException, InterruptedException {
        db.collection("events").document(id).delete().get();
    }

    public void updateEvent(Event event) throws ExecutionException, InterruptedException {
        db.collection("events").document(event.getId()).set(event).get();
    }
}