import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class CancionController {

    private static final String UPLOAD_DIR = "uploads/";
    private List<Map<String, String>> canciones = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Map<String, String>> subirCancion(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "No se subió ningún archivo."));
        }

        try {
            Path ruta = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.createDirectories(ruta.getParent());
            Files.write(ruta, file.getBytes());

            String url = "/uploads/" + file.getOriginalFilename();

            Map<String, String> cancion = new HashMap<>();
            cancion.put("id", UUID.randomUUID().toString());
            cancion.put("title", title);
            cancion.put("url", url);
            canciones.add(cancion);

            return ResponseEntity.ok(cancion);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Error al subir la canción."));
        }
    }

    @GetMapping
    public List<Map<String, String>> obtenerCanciones() {
        return canciones;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCancion(@PathVariable String id) {
        canciones.removeIf(c -> c.get("id").equals(id));
        return ResponseEntity.ok("Canción eliminada con éxito.");
    }
}
