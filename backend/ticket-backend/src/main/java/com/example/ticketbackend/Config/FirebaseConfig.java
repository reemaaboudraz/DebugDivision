package com.example.ticketbackend.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        InputStream serviceAccount;

        try{
            //credentials for Integration Testing and Deployment
            String credentialsPath = System.getenv("FIREBASE_CREDENTIALS");
            serviceAccount = new FileInputStream(credentialsPath);
        } catch (Exception e){
            //credentials for local Environment
            serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
        }

        if (FirebaseApp.getApps().isEmpty()) {

            if (serviceAccount == null) {
                throw new IllegalStateException("firebase-service-account.json not found in resources!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully!");
            return app;
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}