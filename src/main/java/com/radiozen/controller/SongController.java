package com.radiozen.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "https://frontmusic.netlify.app") // Permitir CORS en este controlador
public class SongController {

    @GetMapping("/storage")
    public ResponseEntity<String> getStorage() {
        return ResponseEntity.ok("Ruta de almacenamiento de canciones");
    }
}
