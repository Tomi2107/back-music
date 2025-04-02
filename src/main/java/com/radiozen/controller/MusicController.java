import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;

import com.google.firebase.cloud.FirestoreClient; // 🔹 Importación corregida

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*") // CORS para permitir acceso desde el frontend
public class MusicController {

    private final Firestore db;

    @Autowired
    public MusicController() {
        this.db = FirestoreClient.getFirestore(); // 🔹 Inicialización correcta
    }

    @GetMapping
    public List<Map<String, Object>> getSongs() throws Exception {
        ApiFuture<QuerySnapshot> query = db.collection("songs").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        return documents.stream()
                .map(QueryDocumentSnapshot::getData)
                .collect(Collectors.toList());
    }

    @PostMapping
    public String addSong(@RequestBody Map<String, Object> song) throws Exception {
        db.collection("songs").add(song);
        return "Song added";
    }

    @DeleteMapping("/{id}")
    public String deleteSong(@PathVariable String id) throws Exception {
        db.collection("songs").document(id).delete();
        return "Song deleted";
    }
}
