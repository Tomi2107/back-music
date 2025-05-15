package com.radiozen.model;

public class Cancion {
    private String id;         // ID en Firestore (asignado manualmente desde el controller)
    private String titulo;
    private String artista;
    private String album;
    private String anio;
    private String duracion;
    private String genero;
    private String url;        // URL del archivo en Cloudinary
    private String public_id;  // 🔸 Para poder borrar la canción de Cloudinary

    // 🔸 Constructor vacío requerido por Firestore
    public Cancion() {}

    // 🔸 Constructor sin ID ni public_id (para crear nueva canción)
    public Cancion(String titulo, String artista, String album, String anio, String duracion, String genero, String url) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.anio = anio;
        this.duracion = duracion;
        this.genero = genero;
        this.url = url;
    }

    // 🔸 Constructor completo (útil para debugging o respuesta completa)
    public Cancion(String id, String titulo, String artista, String album, String anio, String duracion, String genero, String url, String public_id) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.anio = anio;
        this.duracion = duracion;
        this.genero = genero;
        this.url = url;
        this.public_id = public_id;
    }

    // 🔸 Getters y Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublic_id() { return public_id; }
    public void setPublic_id(String public_id) { this.public_id = public_id; }
}
