package com.radiozen.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.radiozen.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CancionService {

    private static final Logger logger = LoggerFactory.getLogger(CancionService.class);
    private final Firestore db = FirestoreClient.getFirestore();

    public List<Cancion> obtenerTodas() throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = db.collection("songs").get();
        List<Cancion> canciones = future.get().toObjects(Cancion.class);
        logger.info("🎵 Canciones recuperadas: {}", canciones.size());
        return canciones;
    }

    public void guardar(Cancion cancion) throws InterruptedException, ExecutionException {
        db.collection("songs").add(cancion).get();
        logger.info("✅ Canción guardada: {}", cancion);
    }

    public void eliminarPorId(String id) throws InterruptedException, ExecutionException {
        db.collection("songs").document(id).delete().get();
        logger.info("🗑️ Canción eliminada con ID: {}", id);
    }
}
