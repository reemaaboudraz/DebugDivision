package com.example.ticketbackend.Model;

import com.google.cloud.Timestamp;

public class Event {
    private String id;
    private String name;
    private String location;
    private int availableTickets;

    private Timestamp eventDate;

    private String organizerId;

    public Event() {}

    public Event(String id, String name, String location, int availableTickets, Timestamp eventDate, String organizerId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.availableTickets = availableTickets;
        this.eventDate = eventDate;
        this.organizerId = organizerId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public Timestamp getEventDate() { return eventDate; }
    public void setEventDate(Timestamp eventDate) { this.eventDate = eventDate; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
}