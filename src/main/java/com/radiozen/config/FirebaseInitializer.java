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

    @PostConstruct
    public void initFirebase() {
        try {
            // 🔄 Leer la variable de entorno desde Render
            String firebaseConfigJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

            if (firebaseConfigJson == null || firebaseConfigJson.isEmpty()) {
                throw new IllegalStateException("La variable de entorno GOOGLE_APPLICATION_CREDENTIALS_JSON no está definida.");
            }

            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            System.err.println("❌ Error al inicializar Firebase:");
            e.printStackTrace();
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
