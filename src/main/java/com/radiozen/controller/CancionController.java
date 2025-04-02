import com.google.cloud.storage.*;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/songs/storage")  // Evita conflicto con MusicController
@CrossOrigin(origins = "*")
public class CancionController {

    private static final List<String> AUDIO_FORMATS = Arrays.asList("mp3", "wav", "ogg", "aac", "flac");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final String bucketName = "tu-proyecto.appspot.com"; // Cambia por tu bucket
    private final Firestore db;

    @Autowired
    public CancionController() {
        this.db = FirestoreClient.getFirestore();  // Inicialización correcta de Firestore
    }

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

            // Guardar en Firestore
            Map<String, Object> cancion = new HashMap<>();
            String songId = UUID.randomUUID().toString();
            cancion.put("id", songId);
            cancion.put("title", title);
            cancion.put("url", fileUrl);

            ApiFuture<DocumentReference> future = db.collection("songs").add(cancion);

            return ResponseEntity.ok(cancion);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al subir la canción."));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerCanciones() {
        try {
            List<Map<String, Object>> canciones = db.collection("songs").get().get().toObjects(Map.class);
            return ResponseEntity.ok(canciones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCancion(@PathVariable String id) {
        try {
            db.collection("songs").document(id).delete();
            return ResponseEntity.ok("Canción eliminada.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la canción.");
        }
    }
}
