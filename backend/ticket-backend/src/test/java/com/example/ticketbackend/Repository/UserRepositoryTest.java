package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    private Query query;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @InjectMocks
    private UserRepository userRepository;

    private User testUser;

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
    }

    /**
     * Saving user tests
     */

    @Test
    void testSaveUser_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.set(testUser)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        userRepository.saveUser(testUser);

        // Assert
        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).set(testUser);
        verify(writeResultFuture).get();
    }

    @Test
    void testSaveUser_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.set(testUser)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        // Make sure exception is thrown
        assertThrows(ExecutionException.class, () -> {
            userRepository.saveUser(testUser);
        });

        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).set(testUser);
    }

    /**
     * Getting user by UID tests
     */

    @Test
    void testGetUserByUid_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(User.class)).thenReturn(testUser);

        // Act
        User result = userRepository.getUserByUid("test-uid-123");

        // Assert
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());

        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).get();
        verify(documentSnapshot).exists();
        verify(documentSnapshot).toObject(User.class);
    }

    @Test
    void testGetUserByUid_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("nonexistent-uid")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act
        User result = userRepository.getUserByUid("nonexistent-uid");

        // Assert
        assertNull(result);

        verify(firestore).collection("users");
        verify(collectionReference).document("nonexistent-uid");
        verify(documentReference).get();
        verify(documentSnapshot).exists();
        verify(documentSnapshot, never()).toObject(User.class);
    }

    @Test
    void testGetUserByUid_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);

        // make the future.get() throw an exception to simulate Firestore failure
        when(documentSnapshotFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        // Act and assert (verify the exception is thrown)
        assertThrows(ExecutionException.class, () -> {
            userRepository.getUserByUid("test-uid-123");
        });

        // check that the methods were called (even though it failed)
        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).get();
    }

    /**
     * Getting users by email tests
     */

    @Test
    void testGetUserByEmail_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot));  // Changed
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(testUser);  // Changed

        // Act
        User result = userRepository.getUserByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());

        verify(firestore).collection("users");
        verify(collectionReference).whereEqualTo("email", "test@example.com");
        verify(query).get();
        verify(querySnapshot).getDocuments();
        verify(queryDocumentSnapshot).toObject(User.class);  // Changed
    }

    @Test
    void testGetUserByEmail_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "nonexistent@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        User result = userRepository.getUserByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);

        verify(firestore).collection("users");
        verify(collectionReference).whereEqualTo("email", "nonexistent@example.com");
        verify(query).get();
        verify(querySnapshot).getDocuments();
    }

    @Test
    void testGetUserByEmail_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);

        // make the future.get() throw an exception to simulate Firestore query failure
        when(querySnapshotFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        // Act and ssert (verify the exception is thrown)
        assertThrows(ExecutionException.class, () -> {
            userRepository.getUserByEmail("test@example.com");
        });

        // check the methods were called up to the failure point
        verify(firestore).collection("users");
        verify(collectionReference).whereEqualTo("email", "test@example.com");
        verify(query).get();
    }

    @Test
    void testGetUserByEmail_MultipleResults() throws ExecutionException, InterruptedException {
        // this shouldnt happen (firebase auth prevents duplicate emails), but testing for defense
        User secondUser = new User();
        secondUser.setUid("test-uid-456");
        secondUser.setEmail("test@example.com");
        secondUser.setName("Second User");

        QueryDocumentSnapshot queryDoc2 = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        // Return 2 documents
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, queryDoc2));

        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(testUser);

        // Act
        User result = userRepository.getUserByEmail("test@example.com");

        // Assert (should return the first user)
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        assertEquals("Test User", result.getName());

        verify(firestore).collection("users");
        verify(collectionReference).whereEqualTo("email", "test@example.com");
        verify(query).get();
        verify(querySnapshot).getDocuments();
    }

    /**
     * Updating user tests
     */

    @Test
    void testUpdateUser_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.set(testUser)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        userRepository.updateUser(testUser);

        // Assert
        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).set(testUser);
        verify(writeResultFuture).get();
    }

    @Test
    void testUpdateUser_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.set(testUser)).thenReturn(writeResultFuture);

        // make the future.get() throw an exception to simulate firestore update failure
        when(writeResultFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        // act and assert (verify the exception is thrown)
        assertThrows(ExecutionException.class, () -> {
            userRepository.updateUser(testUser);
        });

        // check the methods were called up to the failure point
        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).set(testUser);
    }

    /**
     * Deleting user tests
     */

    @Test
    void testDeleteUser_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        userRepository.deleteUser("test-uid-123");

        // Assert
        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).delete();
        verify(writeResultFuture).get();
    }

    @Test
    void testDeleteUser_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document("test-uid-123")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            userRepository.deleteUser("test-uid-123");
        });

        verify(firestore).collection("users");
        verify(collectionReference).document("test-uid-123");
        verify(documentReference).delete();
    }
}