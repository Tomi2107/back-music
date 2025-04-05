package com.radiozen.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseInitializer {

    // 🔥 Este método se ejecuta automáticamente cuando arranca la app
    @PostConstruct
    public void initFirebase() {
        try {
            // ✅ Leé las credenciales desde una variable de entorno
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");

            if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                throw new IllegalStateException("La variable de entorno FIREBASE_CONFIG no está definida.");
            }

            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔧 Bean para inyectar Firestore donde lo necesites
    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
