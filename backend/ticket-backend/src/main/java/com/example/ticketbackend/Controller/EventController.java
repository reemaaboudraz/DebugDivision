package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ticketbackend.Model.Event;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequest req) throws Exception {
        try {
            String id = eventService.createEvent(req);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() throws Exception {
        try{
            return ResponseEntity.ok(eventService.getAllEvents());
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) throws Exception {
        try{
            Event event = eventService.getEventById(id);
            if (event == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(event);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) throws Exception {
        try{
            eventService.deleteEvent(id);
            return ResponseEntity.ok(Map.of("deleted", id));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody CreateEventRequest req) throws Exception {
        try{
            eventService.updateEvent(id, req);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            if ("Event not found.".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }
}