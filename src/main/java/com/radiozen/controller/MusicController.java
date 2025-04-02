@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")  // CORS para permitir el acceso desde el frontend
public class MusicController {

    private final Firestore db = FirestoreClient.getFirestore();

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
