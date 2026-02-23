package com.example.ticketbackend.Model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;           // Firebase UID
    private String email;
    private String name;
    private String phoneNumber;
    private UserRole role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean emailVerified;

    // Enum for user roles
    public enum UserRole {
        CUSTOMER,
        ORGANIZER
    }

    // Custom constructor for basic user creation
    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.role = UserRole.CUSTOMER;
    }

    public User(String uid, String email, String name, UserRole role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Helper methods to check roles
    public boolean isCustomer() {
        return this.role == UserRole.CUSTOMER;
    }

    public boolean isOrganizer() {
        return this.role == UserRole.ORGANIZER;
    }
}