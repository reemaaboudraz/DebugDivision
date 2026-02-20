package com.example.ticketbackend.Controller;

import com.example.ticketbackend.DTO.Request.RegisterRequestDTO;
import com.example.ticketbackend.Model.User;
import com.example.ticketbackend.Service.AuthService;
import tools.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.mockito.ArgumentMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.security.user.name=user",
        "spring.security.user.password=password"
})
@WithMockUser
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private User testUser;
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        // build MockMvc from WebApplicationContext with Spring Security (slower than using WebMvcTest)
        // wanted to use @WebMvcTest but wasnt able to make it work with spring boot 4.0.2
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity()) // to make it behave like real requests with security
                .build();

        // Setup test user
        testUser = new User();
        testUser.setUid("test-uid-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(User.UserRole.CUSTOMER);
        testUser.setEmailVerified(false);
        testUser.setCreatedAt(Timestamp.now());
        testUser.setUpdatedAt(Timestamp.now());

        // Setup register request
        registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        registerRequest.setRole(User.UserRole.CUSTOMER);
    }

    /**
     * Registering endpoint tests
     */

    @Test
    void testRegister_Success() throws Exception {
        when(authService.registerUser(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getRole()
        )).thenReturn(testUser);

        when(authService.generateCustomToken("test-uid-123"))
                .thenReturn("custom-token-xyz");

        // act and assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid").value("test-uid-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.customToken").value("custom-token-xyz"));

        // verify service methods were called
        verify(authService).registerUser(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getRole()
        );
        verify(authService).generateCustomToken("test-uid-123");
    }

    @Test
    void testRegister_FirebaseAuthException() throws Exception {
        // simulate duplicate email
        when(authService.registerUser(
                anyString(),
                anyString(),
                anyString(),
                ArgumentMatchers.any(User.UserRole.class)
        )).thenThrow(new FirebaseAuthException(
                new FirebaseException(ErrorCode.ALREADY_EXISTS, "Email already exists", null)
        ));

        // act and assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Registration failed")));

        // check registerUser was called but not generateCustomToken
        verify(authService).registerUser(anyString(), anyString(), anyString(), ArgumentMatchers.any(User.UserRole.class));
        verify(authService, never()).generateCustomToken(anyString());
    }

    @Test
    void testRegister_ExecutionException() throws Exception {
        // simulate database error
        when(authService.registerUser(
                anyString(),
                anyString(),
                anyString(),
                ArgumentMatchers.any(User.UserRole.class)
        )).thenThrow(new ExecutionException(new RuntimeException("Database error")));

        // act and assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).registerUser(anyString(), anyString(), anyString(), ArgumentMatchers.any(User.UserRole.class));
        verify(authService, never()).generateCustomToken(anyString());
    }

    @Test
    void testRegister_InterruptedException() throws Exception {
        when(authService.registerUser(
                anyString(),
                anyString(),
                anyString(),
                ArgumentMatchers.any(User.UserRole.class)
        )).thenThrow(new InterruptedException("Thread interrupted"));

        // act and assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).registerUser(anyString(), anyString(), anyString(), ArgumentMatchers.any(User.UserRole.class));
    }

    @Test
    void testRegister_InvalidContentType() throws Exception {
        // act and assert
        // wrong content type (just text instead of json)
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(authService, never()).registerUser(anyString(), anyString(), anyString(), ArgumentMatchers.any());
    }

    /**
     * Get profile endpoint tests
     */

    @Test
    void testGetProfile_Success() throws Exception {
        when(authService.getUserByUid("test-uid-123")).thenReturn(testUser);

        // act and assert
        mockMvc.perform(get("/api/auth/profile")
                        .param("uid", "test-uid-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("test-uid-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(authService).getUserByUid("test-uid-123");
    }

    @Test
    void testGetProfile_UserNotFound() throws Exception {
        when(authService.getUserByUid("nonexistent-uid")).thenReturn(null);

        // act and assert
        mockMvc.perform(get("/api/auth/profile")
                        .param("uid", "nonexistent-uid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(authService).getUserByUid("nonexistent-uid");
    }

    @Test
    void testGetProfile_ExecutionException() throws Exception {
        when(authService.getUserByUid("test-uid-123"))
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        // act and assert
        mockMvc.perform(get("/api/auth/profile")
                        .param("uid", "test-uid-123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).getUserByUid("test-uid-123");
    }

    @Test
    void testGetProfile_MissingUidParameter() throws Exception {
        // simulate missing required parameter
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).getUserByUid(anyString());
    }

    /**
     * Update profile endpoint tests
     */

    @Test
    void testUpdateProfile_Success() throws Exception {
        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("phoneNumber", "+15141234567");

        User updatedUser = new User();
        updatedUser.setUid("test-uid-123");
        updatedUser.setEmail("test@example.com");
        updatedUser.setName("Updated Name");
        updatedUser.setPhoneNumber("+15141234567");
        updatedUser.setRole(User.UserRole.CUSTOMER);

        when(authService.updateUserProfile("test-uid-123", "Updated Name", "+15141234567"))
                .thenReturn(updatedUser);

        //  act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("test-uid-123"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+15141234567"));

        verify(authService).updateUserProfile("test-uid-123", "Updated Name", "+15141234567");
    }

    @Test
    void testUpdateProfile_UserNotFound() throws Exception {
        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(authService.updateUserProfile(anyString(), eq("Updated Name"), isNull()))
                .thenReturn(null);

        // act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "nonexistent-uid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(authService).updateUserProfile(anyString(), eq("Updated Name"), isNull());
    }

    @Test
    void testUpdateProfile_FirebaseAuthException() throws Exception {
        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(authService.updateUserProfile(anyString(), anyString(), isNull()))
                .thenThrow(new FirebaseAuthException(
                        new FirebaseException(ErrorCode.INVALID_ARGUMENT, "Invalid phone number", null)
                ));

        // act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Update failed")));

        verify(authService).updateUserProfile(anyString(), anyString(), isNull());
    }

    @Test
    void testUpdateProfile_ExecutionException() throws Exception {
        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(authService.updateUserProfile(anyString(), anyString(), isNull()))
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        // act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).updateUserProfile(anyString(), anyString(), isNull());
    }

    @Test
    void testUpdateProfile_NullValues() throws Exception {
        Map<String, String> updates = new HashMap<>(); // both name and phoneNumber null

        when(authService.updateUserProfile("test-uid-123", null, null))
                .thenReturn(testUser);

        // act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

        verify(authService).updateUserProfile("test-uid-123", null, null);
    }

    @Test
    void testUpdateProfile_EmptyRequestBody() throws Exception {
        when(authService.updateUserProfile("test-uid-123", null, null))
                .thenReturn(testUser);

        // act and assert
        mockMvc.perform(put("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        // should still call service with null values
        verify(authService).updateUserProfile("test-uid-123", null, null);
    }

    /**
     * Delete profile endpoint tests
     */

    @Test
    void testDeleteProfile_Success() throws Exception {
        doNothing().when(authService).deleteUser("test-uid-123");

        // act and assert
        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(authService).deleteUser("test-uid-123");
    }

    @Test
    void testDeleteProfile_FirebaseAuthException() throws Exception {
        doThrow(new FirebaseAuthException(
                new FirebaseException(ErrorCode.NOT_FOUND, "User not found in Firebase Auth", null)
        )).when(authService).deleteUser("nonexistent-uid");

        // act and assert
        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "nonexistent-uid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Delete failed")));

        verify(authService).deleteUser("nonexistent-uid");
    }

    @Test
    void testDeleteProfile_ExecutionException() throws Exception {
        doThrow(new ExecutionException(new RuntimeException("Database error")))
                .when(authService).deleteUser("test-uid-123");

        // act and assert
        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf())
                        .param("uid", "test-uid-123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).deleteUser("test-uid-123");
    }

    @Test
    void testDeleteProfile_MissingUidParameter() throws Exception {
        // act and assert
        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(authService, never()).deleteUser(anyString());
    }

    /**
     * Get user by email endpoint tests
     */

    @Test
    void testGetUserByEmail_Success() throws Exception {
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);

        // act and assert
        mockMvc.perform(get("/api/auth/user/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("test-uid-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(authService).getUserByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_UserNotFound() throws Exception {
        when(authService.getUserByEmail("nonexistent@example.com")).thenReturn(null);

        // act and assert
        mockMvc.perform(get("/api/auth/user/email")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(authService).getUserByEmail("nonexistent@example.com");
    }

    @Test
    void testGetUserByEmail_ExecutionException() throws Exception {
        when(authService.getUserByEmail("test@example.com"))
                .thenThrow(new ExecutionException(new RuntimeException("Database error")));

        // act and assert
        mockMvc.perform(get("/api/auth/user/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Server error")));

        verify(authService).getUserByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_MissingEmailParameter() throws Exception {
        // act and assert
        mockMvc.perform(get("/api/auth/user/email"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).getUserByEmail(anyString());
    }

    @Test
    void testGetUserByEmail_InvalidEmailFormat() throws Exception {
        when(authService.getUserByEmail("invalid-email")).thenReturn(null);

        // act and assert
        mockMvc.perform(get("/api/auth/user/email")
                        .param("email", "invalid-email"))
                .andExpect(status().isNotFound());

        verify(authService).getUserByEmail("invalid-email");
    }

    /**
     * Health checkpoint endpoint test
     */

    @Test
    void testHealth_Success() throws Exception {
        // act and assert
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication service is running"));

        // check that no service methods was called (since health check is independent)
        verifyNoInteractions(authService);
    }
}