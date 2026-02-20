package com.example.ticketbackend.DTO.Request;

import com.example.ticketbackend.Model.User.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String phoneNumber;
    private String password;     // only required for email, not used for phone
    private String name;
    private UserRole role;
}