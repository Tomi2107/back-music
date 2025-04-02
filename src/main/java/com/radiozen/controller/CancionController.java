package com.radiozen.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.radiozen.model.Cancion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/canciones") // Endpoint accesible desde el frontend
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
                logger.warn("No se encontraron canciones en la base de datos.");
                return ResponseEntity.noContent().build(); // Retorna 204 si no hay canciones
            }

            List<Cancion> canciones = snapshot.toObjects(Cancion.class);
            logger.info("Canciones obtenidas exitosamente. Total: {}", canciones.size());
            return ResponseEntity.ok(canciones);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error al obtener canciones: ", e);
            return ResponseEntity.internalServerError()
                    .body(null);
        }
    }

    @PostMapping("/storage")
    public ResponseEntity<String> guardarCancion(@RequestBody Cancion cancion) {
        try {
            db.collection("songs").add(cancion).get();
            logger.info("Canción guardada con éxito: {}", cancion);
            return ResponseEntity.ok("Canción guardada correctamente.");
        } catch (Exception e) {
            logger.error("Error al guardar la canción: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error al guardar la canción.");
        }
    }
}

@RestController
@RequestMapping("/") // Manejo de la ruta raíz
class HomeController {
    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("¡Bienvenido a RadioZen!");
    }
}
