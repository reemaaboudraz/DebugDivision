package com.example.ticketbackend.DTO.Request;

public class CreateEventRequest {
    private String name;
    private String location;
    private int availableTickets;
    private long eventDateMillis;
    // who created it
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