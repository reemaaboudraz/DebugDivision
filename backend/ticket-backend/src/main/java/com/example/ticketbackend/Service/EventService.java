package com.example.ticketbackend.Service;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Model.Event;
import com.example.ticketbackend.Repository.EventRepository;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public String createEvent(CreateEventRequest req) throws ExecutionException, InterruptedException {
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

        Event event = new Event();
        event.setName(req.getName().trim());
        event.setLocation(req.getLocation() == null ? "" : req.getLocation().trim());
        event.setAvailableTickets(req.getAvailableTickets());
        event.setEventDate(eventDate);
        event.setOrganizerId(req.getOrganizerId() == null ? "" : req.getOrganizerId().trim());

        return eventRepository.saveEvent(event);
    }

    public Event getEventById(String id) throws ExecutionException, InterruptedException {
        return eventRepository.getEventById(id);
    }

    public List<Event> getAllEvents() throws ExecutionException, InterruptedException {
        return eventRepository.getAllEvents();
    }

    public void deleteEvent(String id) throws ExecutionException, InterruptedException {
        eventRepository.deleteEvent(id);
    }

    public void updateEvent(String id, CreateEventRequest req) throws ExecutionException, InterruptedException {
        Event existing = eventRepository.getEventById(id);
        if (existing == null) throw new IllegalArgumentException("Event not found.");

        Timestamp eventDate = Timestamp.ofTimeSecondsAndNanos(
                req.getEventDateMillis() / 1000,
                (int) ((req.getEventDateMillis() % 1000) * 1_000_000)
        );

        existing.setName(req.getName().trim());
        existing.setLocation(req.getLocation() == null ? "" : req.getLocation().trim());
        existing.setAvailableTickets(req.getAvailableTickets());
        existing.setEventDate(eventDate);

        eventRepository.updateEvent(existing);
    }
}