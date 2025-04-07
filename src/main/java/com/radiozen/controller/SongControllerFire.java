package com.radiozen.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.radiozen.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "https://frontmusic.netlify.app") // Ajustá esto si usás otro frontend
@RequestMapping("/api/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private final Firestore db;

    public SongController(Firestore db) {
        this.db = db;
    }

    // 🔹 Obtener todas las canciones
    @GetMapping
    public ResponseEntity<List<Cancion>> getSongs() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("songs").get();
            List<Cancion> songs = future.get().toObjects(Cancion.class);

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

    // 🔹 Guardar canción sin archivo
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

    // 🔹 Subir canción con archivo
   @PostMapping(value = "/upload", consumes = "multipart/form-data")
public ResponseEntity<String> subirCancionConArchivo(
        @RequestParam("titulo") String titulo,
        @RequestParam("artista") String artista,
        @RequestParam("album") String album,
        @RequestParam("anio") String anio,
        @RequestParam("duracion") String duracion,
        @RequestParam("genero") String genero,
        @RequestPart("archivo") MultipartFile archivo) {

    logger.info("📥 Petición recibida para subir canción:");
    logger.info("➡️ Título: {}", titulo);
    logger.info("➡️ Artista: {}", artista);
    logger.info("➡️ Álbum: {}", album);
    logger.info("➡️ Año: {}", anio);
    logger.info("➡️ Duración: {}", duracion);
    logger.info("➡️ Género: {}", genero);
    logger.info("➡️ Archivo recibido: {}", archivo != null ? archivo.getOriginalFilename() : "null");

    try {
        if (archivo == null || archivo.isEmpty()) {
            logger.warn("⚠️ El archivo es nulo o está vacío");
            return ResponseEntity.badRequest().body("Archivo no enviado o vacío.");
        }

        // Subir archivo a Firebase
        String nombreArchivo = archivo.getOriginalFilename();
        logger.info("📤 Subiendo archivo: {}", nombreArchivo);

        Bucket bucket = StorageClient.getInstance().bucket();
        logger.info("🪣 Bucket: {}", bucket.getName());

        Blob blob = bucket.create("songs/" + nombreArchivo, archivo.getBytes(), archivo.getContentType());
        String url = String.format("https://storage.googleapis.com/%s/%s", bucket.getName(), blob.getName());

        logger.info("✅ Archivo subido correctamente. URL: {}", url);

        // Guardar metadatos
        Cancion cancion = new Cancion();
        cancion.setTitulo(titulo);
        cancion.setArtista(artista);
        cancion.setAlbum(album);
        cancion.setAnio(anio);
        cancion.setDuracion(duracion);
        cancion.setGenero(genero);
        cancion.setUrl(url);

        db.collection("songs").add(cancion).get();

        logger.info("✅ Canción guardada con metadatos: {}", cancion);
        return ResponseEntity.ok("Canción subida exitosamente con archivo. URL: " + url);

    } catch (Exception e) {
        logger.error("❌ Error al subir canción con archivo:", e);
        return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
}


    // 🔹 Eliminar canción
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

    // 🔹 Prueba de conexión
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("🎧 API RadioZen funcionando.");
    }
}
