package com.example.ticketbackend.Service;

import com.example.ticketbackend.Model.User;
import com.example.ticketbackend.Repository.UserRepository;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserRecord mockUserRecord;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUid("test-uid-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(User.UserRole.CUSTOMER);
        testUser.setEmailVerified(false);
        testUser.setCreatedAt(Timestamp.now());
        testUser.setUpdatedAt(Timestamp.now());

        mockUserRecord = mock(UserRecord.class);
    }

    @Test
    void testRegisterUser_Success() throws FirebaseAuthException, ExecutionException, InterruptedException {
        // "Mock" the methods
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            when(mockUserRecord.getUid()).thenReturn("test-uid-123");

            when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class))).thenReturn(mockUserRecord);
            doNothing().when(userRepository).saveUser(any(User.class));

            // Call the actual register method
            User result = authService.registerUser(
                    "test@example.com",
                    "password123",
                    "Test User",
                    User.UserRole.CUSTOMER
            );

            // Check values
            assertNotNull(result);
            assertEquals("test-uid-123", result.getUid());
            assertEquals("test@example.com", result.getEmail());
            assertEquals("Test User", result.getName());
            assertEquals(User.UserRole.CUSTOMER, result.getRole());
            assertFalse(result.isEmailVerified());

            // Make sure createUser and saveUser were called
            verify(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
            verify(userRepository).saveUser(any(User.class));
        }
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws FirebaseAuthException, ExecutionException, InterruptedException {
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            // Throwing the exception (bc when user registers with same email, exception is thrown)
            when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                    .thenThrow(FirebaseAuthException.class);

            // Make sure exception is thrown
            assertThrows(FirebaseAuthException.class, () -> {
                authService.registerUser(
                        "test@example.com",
                        "password123",
                        "Test User",
                        User.UserRole.CUSTOMER
                );
            });

            // Make sure createUser is called but not saveUser (bc exception is thrown so code doesn't reach saveUser)
            verify(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
            verify(userRepository, never()).saveUser(any(User.class));
        }
    }

    @Test
    void testGenerateCustomToken_Success() throws FirebaseAuthException {
        String expectedToken = "custom-token-xyz";
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(firebaseAuth.createCustomToken("test-uid-123")).thenReturn(expectedToken);

            // Act
            String result = authService.generateCustomToken("test-uid-123");

            // Assert
            assertEquals(expectedToken, result);
            verify(firebaseAuth).createCustomToken("test-uid-123");
        }
    }

    @Test
    void testGetUserByUid_Success() throws ExecutionException, InterruptedException {
        when(userRepository.getUserByUid("test-uid-123")).thenReturn(testUser);

        // Act
        User result = authService.getUserByUid("test-uid-123");

        // Assert
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).getUserByUid("test-uid-123");
    }

    @Test
    void testGetUserByUid_NotFound() throws ExecutionException, InterruptedException {
        when(userRepository.getUserByUid("nonexistent-uid")).thenReturn(null);

        // Act
        User result = authService.getUserByUid("nonexistent-uid");

        // Assert
        assertNull(result);
        verify(userRepository).getUserByUid("nonexistent-uid");
    }

    @Test
    void testGetUserByEmail_Success() throws ExecutionException, InterruptedException {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(testUser);

        // Act
        User result = authService.getUserByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository).getUserByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() throws ExecutionException, InterruptedException {
        when(userRepository.getUserByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        User result = authService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
        verify(userRepository).getUserByEmail("nonexistent@example.com");
    }

    @Test
    void testUpdateUserProfile_Success() throws ExecutionException, InterruptedException, FirebaseAuthException {
        String newName = "Updated Name";
        String newPhone = "+15141234567";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(userRepository.getUserByUid("test-uid-123")).thenReturn(testUser);
            when(firebaseAuth.updateUser(any(UserRecord.UpdateRequest.class))).thenReturn(mockUserRecord);
            doNothing().when(userRepository).updateUser(any(User.class));

            // Act
            User result = authService.updateUserProfile("test-uid-123", newName, newPhone);

            // Assert
            assertNotNull(result);
            assertEquals(newName, result.getName());
            assertEquals(newPhone, result.getPhoneNumber());

            verify(firebaseAuth).updateUser(any(UserRecord.UpdateRequest.class));
            verify(userRepository).getUserByUid("test-uid-123");
            verify(userRepository).updateUser(any(User.class));
        }
    }

    @Test
    void testUpdateUserProfile_UserNotFound() throws ExecutionException, InterruptedException, FirebaseAuthException {
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(userRepository.getUserByUid("nonexistent-uid")).thenReturn(null);
            when(firebaseAuth.updateUser(any(UserRecord.UpdateRequest.class))).thenReturn(mockUserRecord);

            // Act
            User result = authService.updateUserProfile("nonexistent-uid", "New Name", "+15141234567");

            // Assert
            assertNull(result);
            verify(firebaseAuth).updateUser(any(UserRecord.UpdateRequest.class));
            verify(userRepository).getUserByUid("nonexistent-uid");
            verify(userRepository, never()).updateUser(any(User.class));
        }
    }

    @Test
    void testDeleteUser_Success() throws FirebaseAuthException, ExecutionException, InterruptedException {
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            doNothing().when(firebaseAuth).deleteUser("test-uid-123");
            doNothing().when(userRepository).deleteUser("test-uid-123");

            // Act
            authService.deleteUser("test-uid-123");

            // Assert
            verify(firebaseAuth).deleteUser("test-uid-123");
            verify(userRepository).deleteUser("test-uid-123");
        }
    }

    @Test
    void testDeleteUser_FirebaseAuthException() throws FirebaseAuthException, ExecutionException, InterruptedException {
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            // Just throw the exception directly
            doThrow(FirebaseAuthException.class).when(firebaseAuth).deleteUser("nonexistent-uid");

            // Act & Assert
            assertThrows(FirebaseAuthException.class, () -> {
                authService.deleteUser("nonexistent-uid");
            });

            verify(firebaseAuth).deleteUser("nonexistent-uid");
            verify(userRepository, never()).deleteUser(anyString());
        }
    }
}