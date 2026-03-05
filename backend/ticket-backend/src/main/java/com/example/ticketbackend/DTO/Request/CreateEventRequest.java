package com.example.ticketbackend.DTO.Request;

public class CreateEventRequest {
    private String name;
    private String location;
    private int availableTickets;

    private long eventDateMillis;

    // who created it
    private String organizerId;

    public CreateEventRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public long getEventDateMillis() { return eventDateMillis; }
    public void setEventDateMillis(long eventDateMillis) { this.eventDateMillis = eventDateMillis; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
}