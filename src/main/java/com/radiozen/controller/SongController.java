package com.radiozen.controller;

import java.util.ArrayList;
import java.util.List;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.radiozen.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "https://frontmusic.netlify.app")
@RequestMapping("/api/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private static final String UPLOAD_DIR = "uploads";

    private final Firestore db;

    public SongController(Firestore db) {
        this.db = db;
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
                c.setId(doc.getId()); // 🔥 Guardamos el ID
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

    // 🔹 Subida con archivo local
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirCancionConArchivo(
            @RequestParam("titulo") String titulo,
            @RequestParam("artista") String artista,
            @RequestParam("album") String album,
            @RequestParam("anio") String anio,
            @RequestParam("duracion") String duracion,
            @RequestParam("genero") String genero,
            @RequestPart("archivo") MultipartFile archivo) {

        logger.info("📥 Petición recibida para subir canción local:");
        try {
            if (archivo == null || archivo.isEmpty()) {
                logger.warn("⚠️ El archivo es nulo o está vacío");
                return ResponseEntity.badRequest().body("Archivo no enviado o vacío.");
            }

            String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR, filename);

            Files.copy(archivo.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            String baseUrl = "https://back-music-3izh.onrender.com";  // O sacalo de una variable de entorno
            String localUrl = baseUrl + "/api/songs/audio/" + filename;
            Cancion cancion = new Cancion(titulo, artista, album, anio, duracion, genero, localUrl);

            db.collection("songs").add(cancion).get();

            logger.info("✅ Canción guardada local con URL: {}", localUrl);
            return ResponseEntity.ok("Canción subida exitosamente. URL: " + localUrl);

        } catch (Exception e) {
            logger.error("❌ Error al subir canción:", e);
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // 🔹 Servir archivos
    @GetMapping("/audio/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                logger.warn("🛑 Archivo no encontrado: {}", filename);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (MalformedURLException e) {
            logger.error("❌ Error al servir archivo:", e);
            return ResponseEntity.internalServerError().build();
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
