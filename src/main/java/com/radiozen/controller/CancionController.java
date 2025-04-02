import com.google.cloud.storage.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class CancionController {

    private static final List<String> AUDIO_FORMATS = Arrays.asList("mp3", "wav", "ogg", "aac", "flac");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final String bucketName = "tu-proyecto.appspot.com"; // Cambia por tu bucket

    private List<Map<String, String>> canciones = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> subirCancion(@RequestParam("title") String title, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío."));
        if (file.getSize() > MAX_FILE_SIZE) return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("error", "Máximo 10MB."));
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!AUDIO_FORMATS.contains(extension)) return ResponseEntity.badRequest().body(Map.of("error", "Formato no válido."));

        try {
            // Subir archivo a Firebase Storage
            String fileName = UUID.randomUUID().toString() + "." + extension;
            BlobId blobId = BlobId.of(bucketName, "songs/" + fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());

            // URL de descarga pública
            String fileUrl = "https://storage.googleapis.com/" + bucketName + "/songs/" + fileName;

            // Guardar info de la canción
            Map<String, String> cancion = Map.of("id", UUID.randomUUID().toString(), "title", title, "url", fileUrl);
            canciones.add(cancion);

            return ResponseEntity.ok(cancion);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al subir la canción."));
        }
    }

    @GetMapping
    public List<Map<String, String>> obtenerCanciones() {
        return canciones;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCancion(@PathVariable String id) {
        canciones.removeIf(c -> c.get("id").equals(id));
        return ResponseEntity.ok("Canción eliminada.");
    }
}
