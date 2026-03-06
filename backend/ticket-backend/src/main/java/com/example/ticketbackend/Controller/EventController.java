package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ticketbackend.Model.Event;

import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequest req) throws Exception {
        String id = eventService.createEvent(req);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() throws Exception {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) throws Exception {
        Event event = eventService.getEventById(id);
        if (event == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) throws Exception {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody CreateEventRequest req) throws Exception {
        eventService.updateEvent(id, req);
        return ResponseEntity.ok(Map.of("id", id));
    }
}