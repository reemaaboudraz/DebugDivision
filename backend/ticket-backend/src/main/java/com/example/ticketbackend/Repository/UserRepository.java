package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private final Firestore db;

    public UserRepository(Firestore db) {
        this.db = db;
    }

    public void saveUser(User user) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = db.collection("users").document(user.getUid()).set(user);
        future.get();
    }

    public User getUserByUid(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(uid);
        DocumentSnapshot doc = docRef.get().get();
        return doc.exists() ? doc.toObject(User.class) : null;
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .whereEqualTo("email", email)
                .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            return doc.toObject(User.class);
        }
        return null;
    }

    public void updateUser(User user) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = db.collection("users").document(user.getUid()).set(user);
        future.get();
    }

    public void deleteUser(String uid) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = db.collection("users").document(uid).delete();
        future.get();
    }
}
