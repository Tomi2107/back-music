package com.radiozen.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.radiozen.model.Cancion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/canciones")
@CrossOrigin(origins = {
        "https://frontmusic.netlify.app",
        "http://localhost:3000"
}) // Backup por si CORS no se inyecta desde CorsConfig
public class CancionController {

    private static final Logger logger = LoggerFactory.getLogger(CancionController.class);
    private final Firestore db;

    public CancionController(Firestore db) {
        this.db = db;
    }

    @GetMapping
    public ResponseEntity<List<Cancion>> obtenerCanciones() {
        try {
            QuerySnapshot snapshot = db.collection("songs").get().get();

            if (snapshot.isEmpty()) {
                logger.warn("🎧 No se encontraron canciones en Firestore.");
                return ResponseEntity.noContent().build();
            }

            List<Cancion> canciones = snapshot.toObjects(Cancion.class);
            logger.info("✅ {} canciones recuperadas.", canciones.size());
            return ResponseEntity.ok(canciones);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("❌ Error al obtener canciones: ", e);
            Thread.currentThread().interrupt(); // buena práctica en interrupciones
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/storage")
    public ResponseEntity<String> guardarCancion(@RequestBody Cancion cancion) {
        try {
            db.collection("songs").add(cancion).get();
            logger.info("✅ Canción guardada: {}", cancion.getTitulo());
            return ResponseEntity.ok("Canción guardada correctamente.");
        } catch (Exception e) {
            logger.error("❌ Error al guardar canción: ", e);
            return ResponseEntity.internalServerError()
                    .body("Ocurrió un error al guardar la canción.");
        }
    }
}
