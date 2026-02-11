package com.example.ticketbackend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String uid;
    private String email;
    private String name;
    private String customToken;  // from firebase
    private String message;

    // Constructor for success response
    public AuthResponseDTO(String uid, String email, String name, String customToken) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.customToken = customToken;
    }

    // Constructor for error response
    public AuthResponseDTO(String message) {
        this.message = message;
    }
}