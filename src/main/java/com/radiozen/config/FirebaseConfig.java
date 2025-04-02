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
        if (!FirebaseApp.getApps().isEmpty()) {
            System.out.println("✅ Firebase ya estaba inicializado.");
            return FirebaseApp.getInstance();
        }

        String firebaseConfigJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

        if (firebaseConfigJson == null || firebaseConfigJson.isEmpty()) {
            throw new IllegalStateException("❌ ERROR: La variable GOOGLE_APPLICATION_CREDENTIALS_JSON no está definida.");
        }

        System.out.println("🔥 JSON de Firebase encontrado.");

        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        System.out.println("🚀 Inicializando Firebase...");
        FirebaseApp app = FirebaseApp.initializeApp(options);
        System.out.println("✅ Firebase inicializado correctamente.");
        return app;
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        System.out.println("🔍 Firebase Apps registradas: " + FirebaseApp.getApps());

        return FirestoreClient.getFirestore(firebaseApp);
    }
}
