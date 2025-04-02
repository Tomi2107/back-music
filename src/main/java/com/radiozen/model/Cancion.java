package com.radiozen.model;

public class Cancion {
    private String titulo;
    private String artista;
    private String url;

    // Constructor vacío requerido por Firestore
    public Cancion() {}

    public Cancion(String titulo, String artista, String url) {
        this.titulo = titulo;
        this.artista = artista;
        this.url = url;
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
