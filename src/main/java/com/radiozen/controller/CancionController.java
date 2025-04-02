import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class CancionController {

    private static final String UPLOAD_DIR = "uploads/";
    private static final List<String> AUDIO_FORMATS = Arrays.asList("mp3", "wav", "ogg", "aac", "flac");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private List<Map<String, String>> canciones = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> subirCancion(@RequestParam("title") String title, @RequestParam("file") MultipartFile file) {

        // 1️⃣ Validar si el archivo está vacío
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El archivo está vacío."));
        }

        // 2️⃣ Validar tamaño del archivo (máx. 10MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Collections.singletonMap("error", "El archivo es demasiado grande (Máx: 10MB)."));
        }

        // 3️⃣ Obtener la extensión del archivo y validarla
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if (!AUDIO_FORMATS.contains(extension)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Formato de archivo no válido. Solo se permiten: " + AUDIO_FORMATS));
        }

        try {
            // 4️⃣ Guardar el archivo en el servidor
            Path ruta = Paths.get(UPLOAD_DIR + originalFilename);
            Files.createDirectories(ruta.getParent()); // Crear la carpeta si no existe
            Files.write(ruta, file.getBytes());

            String url = "/uploads/" + originalFilename;

            // 5️⃣ Guardar información de la canción
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
