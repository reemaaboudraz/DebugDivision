package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.RegisterRequestDTO;
import com.example.ticketbackend.DTO.Response.AuthResponseDTO;
import com.example.ticketbackend.Model.User;
import com.example.ticketbackend.Service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            // Register user
            User user = authService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getRole()
            );

            // Generate custom token for client
            String customToken = authService.generateCustomToken(user.getUid());

            // Return response
            AuthResponseDTO response = new AuthResponseDTO(
                    user.getUid(),
                    user.getEmail(),
                    user.getName(),
                    customToken
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Registration failed: " + e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * create a new user in firestore
     * POST /api/auth/register
     */
    @PostMapping("/register-phone")
    public ResponseEntity<?> registerWithPhone(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RegisterRequestDTO request) {
        try {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println(authHeader);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponseDTO("Missing or invalid Authorization header"));
            }

            String idToken = authHeader.substring(7);
            FirebaseToken decodedToken = authService.verifyIdToken(idToken);

            String uid = decodedToken.getUid();

            // Register user
            User user = authService.registerUserPhone(
                    uid,
                    request.getPhoneNumber(),
                    request.getName(),
                    request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Registration failed: " + e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * Get user profile by UID
     * Later when we do frontend authentication, we will use token
     * GET /api/auth/profile?uid=xxx
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String uid) {
        try {
            User user = authService.getUserByUid(uid);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AuthResponseDTO("User not found"));
            }

            return ResponseEntity.ok(user);

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * Update user profile
     * PUT /api/auth/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam String uid,
            @RequestBody Map<String, String> updates) {
        try {
            String name = updates.get("name");
            String phoneNumber = updates.get("phoneNumber");

            User updatedUser = authService.updateUserProfile(uid, name, phoneNumber);

            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AuthResponseDTO("User not found"));
            }

            return ResponseEntity.ok(updatedUser);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Update failed: " + e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * Delete user account
     * DELETE /api/auth/profile
     */
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(@RequestParam String uid) {
        try {
            authService.deleteUser(uid);
            return ResponseEntity.ok(new AuthResponseDTO("User deleted successfully"));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Delete failed: " + e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * Get user by email
     * GET /api/auth/user/email?email=xxx
     */
    @GetMapping("/user/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        try {
            User user = authService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AuthResponseDTO("User not found"));
            }

            return ResponseEntity.ok(user);

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Server error: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint (public)
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}