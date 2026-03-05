package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.CreateEventRequest;
import com.example.ticketbackend.Service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}