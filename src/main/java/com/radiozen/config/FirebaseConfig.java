package com.radiozen.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Leer la variable de entorno con el JSON de Firebase
        String firebaseConfigJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

        if (firebaseConfigJson == null || firebaseConfigJson.isEmpty()) {
            throw new IllegalStateException("❌ ERROR: La variable GOOGLE_APPLICATION_CREDENTIALS_JSON no está definida.");
        }

        // Convertir el JSON a InputStream
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore();
    }
}
