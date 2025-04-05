package com.radiozen.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.radiozen.model.Cancion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "https://frontmusic.netlify.app") // 🔒 Solo permite peticiones del frontend productivo
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private final Firestore db;

    public SongController() {
        this.db = FirestoreClient.getFirestore();
    }

    // GET /api/songs → lista todas las canciones
    @GetMapping
    public ResponseEntity<List<Cancion>> getSongs() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("songs").get();
            List<Cancion> songs = future.get().toObjects(Cancion.class);

            if (songs.isEmpty()) {
                logger.warn("🎵 No se encontraron canciones en Firestore.");
                return ResponseEntity.noContent().build();
            }

            logger.info("✅ Canciones obtenidas: {}", songs.size());
            return ResponseEntity.ok(songs);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("❌ Error al obtener canciones:", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // POST /api/songs → agrega una nueva canción
    @PostMapping
    public ResponseEntity<String> addSong(@RequestBody Cancion cancion) {
        try {
            db.collection("songs").add(cancion).get();
            logger.info("🎶 Canción guardada: {}", cancion);
            return ResponseEntity.ok("Canción guardada exitosamente.");
        } catch (Exception e) {
            logger.error("❌ Error al guardar canción:", e);
            return ResponseEntity.internalServerError().body("Error al guardar la canción.");
        }
    }

    // DELETE /api/songs/{id} → elimina canción por ID del documento Firestore
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable String id) {
        try {
            db.collection("songs").document(id).delete();
            logger.info("🗑️ Canción eliminada con ID: {}", id);
            return ResponseEntity.ok("Canción eliminada.");
        } catch (Exception e) {
            logger.error("❌ Error al eliminar canción:", e);
            return ResponseEntity.internalServerError().body("Error al eliminar la canción.");
        }
    }

    // BONUS: GET de prueba
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("🎧 API RadioZen funcionando.");
    }
}
