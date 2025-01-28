package edu.pmdm.listascompra.modelo;

import android.net.Uri;

public class Contacto {
    private int id;
    private String nombre;
    private String telefono;
    private Uri fotoUri;

    public Contacto(int id, String nombre, String telefono, Uri fotoUri) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fotoUri = fotoUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Uri getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(Uri fotoUri) {
        this.fotoUri = fotoUri;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
