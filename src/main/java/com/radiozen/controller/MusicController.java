package com.radiozen.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs/db") // Evita conflictos con /api/canciones
@CrossOrigin(origins = {
        "https://frontmusic.netlify.app",
        "http://localhost:3000"
}) // ⚠️ Cors específico para seguridad en producción
public class MusicController {

    private final Firestore db;

    @Autowired
    public MusicController() {
        this.db = FirestoreClient.getFirestore();
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getSongs() {
        try {
            ApiFuture<QuerySnapshot> query = db.collection("songs").get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            List<Map<String, Object>> songs = documents.stream()
                    .map(doc -> {
                        Map<String, Object> song = doc.getData();
                        song.put("id", doc.getId()); // Agrega ID del documento
                        return song;
                    })
                    .collect(Collectors.toList());

            if (songs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(songs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addSong(@RequestBody Map<String, Object> song) {
        try {
            db.collection("songs").add(song);
            return ResponseEntity.ok("🎵 Canción añadida correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error al añadir canción.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable String id) {
        try {
            db.collection("songs").document(id).delete();
            return ResponseEntity.ok("🗑️ Canción eliminada con ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error al eliminar la canción.");
        }
    }
}
