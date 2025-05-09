package com.radiozen.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.radiozen.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "https://frontmusic.netlify.app")
@RequestMapping("/api/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private static final String UPLOAD_DIR = "uploads";

    private final Firestore db;
    private final Cloudinary cloudinary;

    public SongController(Firestore db) {
        this.db = db;
        this.cloudinary = new Cloudinary(System.getenv("CLOUDINARY_URL"));

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();
    }

    @GetMapping
    public ResponseEntity<List<Cancion>> getSongs() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("songs").get();
            List<Cancion> songs = new ArrayList<>();

            for (var doc : future.get().getDocuments()) {
                Cancion c = doc.toObject(Cancion.class);
                c.setId(doc.getId());
                songs.add(c);
            }

            if (songs.isEmpty()) {
                logger.warn("🎵 No se encontraron canciones.");
                return ResponseEntity.noContent().build();
            }

            logger.info("✅ Canciones encontradas: {}", songs.size());
            return ResponseEntity.ok(songs);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("❌ Error al obtener canciones:", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirCancionConArchivo(
            @RequestParam("titulo") String titulo,
            @RequestParam("artista") String artista,
            @RequestParam("album") String album,
            @RequestParam("anio") String anio,
            @RequestParam("duracion") String duracion,
            @RequestParam("genero") String genero,
            @RequestPart("archivo") MultipartFile archivo) {

        logger.info("📥 Petición recibida para subir canción a Cloudinary:");

        try {
            if (archivo == null || archivo.isEmpty()) {
                logger.warn("⚠️ El archivo es nulo o está vacío");
                return ResponseEntity.badRequest().body("Archivo no enviado o vacío.");
            }

            // Subir a Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(archivo.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "songs"
                    ));

            String cloudinaryUrl = (String) uploadResult.get("secure_url");

            Cancion cancion = new Cancion(titulo, artista, album, anio, duracion, genero, cloudinaryUrl);
            db.collection("songs").add(cancion).get();

            logger.info("✅ Canción subida a Cloudinary: {}", cloudinaryUrl);
            return ResponseEntity.ok("Canción subida exitosamente. URL: " + cloudinaryUrl);

        } catch (Exception e) {
            logger.error("❌ Error al subir canción:", e);
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable String id) {
        try {
            db.collection("songs").document(id).delete();
            logger.info("🗑️ Canción eliminada: {}", id);
            return ResponseEntity.ok("Canción eliminada.");
        } catch (Exception e) {
            logger.error("❌ Error al eliminar canción:", e);
            return ResponseEntity.internalServerError().body("Error al eliminar la canción.");
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("🎧 API RadioZen funcionando.");
    }
}
