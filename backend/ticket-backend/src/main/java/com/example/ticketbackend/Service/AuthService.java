package com.example.ticketbackend.Service;

import com.example.ticketbackend.Model.User;
import com.example.ticketbackend.Repository.UserRepository;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user with Firebase Authentication and save to Firestore
     */
    public User registerUser(String email, String password, String name, User.UserRole role)
            throws FirebaseAuthException, ExecutionException, InterruptedException {

        // Create user in Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(name)
                .setEmailVerified(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        // Create user in Firestore
        User user = new User();
        user.setUid(userRecord.getUid());
        user.setEmail(email);
        user.setName(name);
        user.setRole(role);
        user.setEmailVerified(false);
        user.setCreatedAt(Timestamp.now());
        user.setUpdatedAt(Timestamp.now());

        userRepository.saveUser(user);

        return user;
    }
    /**
     * save a user who registered via phone to Firestore
     */
    public User registerUserPhone(String uid, String phoneNumber, String name, User.UserRole role)
            throws ExecutionException, InterruptedException {

        User existingUser = userRepository.getUserByUid(uid);
        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();
        user.setUid(uid);
        user.setPhoneNumber(phoneNumber);
        user.setName(name);
        user.setRole(role);
        user.setEmailVerified(false);
        user.setCreatedAt(Timestamp.now());
        user.setUpdatedAt(Timestamp.now());

        userRepository.saveUser(user);
        return user;
    }

    /**
     * Generate a custom token for a user
     */
    public String generateCustomToken(String uid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    /**
     * Get user by UID
     */
    public User getUserByUid(String uid) throws ExecutionException, InterruptedException {
        return userRepository.getUserByUid(uid);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        return userRepository.getUserByEmail(email);
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(String uid, String name, String phoneNumber)
            throws ExecutionException, InterruptedException, FirebaseAuthException {

        // Update in Firebase Auth
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid);

        if (name != null) {
            request.setDisplayName(name);
        }
        if (phoneNumber != null) {
            request.setPhoneNumber(phoneNumber);
        }

        FirebaseAuth.getInstance().updateUser(request);

        // Update in Firestore
        User user = userRepository.getUserByUid(uid);
        if (user != null) {
            if (name != null) {
                user.setName(name);
            }
            if (phoneNumber != null) {
                user.setPhoneNumber(phoneNumber);
            }
            user.setUpdatedAt(Timestamp.now());
            userRepository.updateUser(user);
        }
        return user;
    }

    /**
     * Delete user
     */
    public void deleteUser(String uid) throws FirebaseAuthException, ExecutionException, InterruptedException {
        // Delete from Firebase Auth
        FirebaseAuth.getInstance().deleteUser(uid);

        // Delete from Firestore
        userRepository.deleteUser(uid);
    }
}