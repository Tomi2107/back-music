package com.radiozen.model;

public class Cancion {
    private String titulo;
    private String artista;
    private String album;
    private int año;
    private String duracion;
    private String genero;
    private String url;

    // Constructor vacío requerido por Firestore
    public Cancion() {}

    public Cancion(String titulo, String artista, String album, int año, String duracion, String genero, String url) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.año = año;
        this.duracion = duracion;
        this.genero = genero;
        this.url = url;
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public int getAño() { return año; }
    public void setAño(int año) { this.año = año; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
