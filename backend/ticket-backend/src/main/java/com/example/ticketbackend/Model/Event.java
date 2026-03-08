package com.example.ticketbackend.Model;

import com.google.cloud.Timestamp;

public class Event {
    private String id;
    private String name;
    private String location;
    private int availableTickets;
    private Timestamp eventDate;
    private String organizerId;

    private String category;
    private String artist;
    private String organization;
    private String city;
    private String country;
    private String overview;
    private String venue;
    private String imageUrl;
    private String buyTicketsUrl;

    public Event() {}

    public Event(String id, String name, String location, int availableTickets, Timestamp eventDate, String organizerId, String category, String artist, String organization, String city, String country, String overview, String venue, String imageUrl, String buyTicketsUrl) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.availableTickets = availableTickets;
        this.eventDate = eventDate;
        this.organizerId = organizerId;
        this.category = category;
        this.artist = artist;
        this.organization = organization;
        this.city = city;
        this.country = country;
        this.overview = overview;
        this.venue = venue;
        this.imageUrl = imageUrl;
        this.buyTicketsUrl = buyTicketsUrl;
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

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBuyTicketsUrl() { return buyTicketsUrl; }
    public void setBuyTicketsUrl(String buyTicketsUrl) { this.buyTicketsUrl = buyTicketsUrl; }
}