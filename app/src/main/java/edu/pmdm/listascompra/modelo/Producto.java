package edu.pmdm.listascompra.modelo;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private String nombreImagen;
    private double precio;
    private byte[] imagenBlob;
    private int vecesComprado;

    public Producto() {
    }

    // Constructor para cuando recuperamos los datos de la web por el JSON
    public Producto(String nombre, String descripcion, String nombreImagen, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombreImagen = nombreImagen;
        this.precio = precio;
    }

    // Constructor para cuando recuperamos los datos de la base de datos
    public Producto(int id, String nombre, String descripcion, double precio, byte[] imagenBlob, int vecesComprado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenBlob = imagenBlob;
        this.vecesComprado = vecesComprado;
    }

    // Constructor para cuando creamos un producto nuevo
    public Producto(String nombre, String descripcion, double precio, byte[] imagenBlob) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenBlob = imagenBlob;
    }

    public int getVecesComprado() {
        return vecesComprado;
    }

    public void setVecesComprado(int vecesComprado) {
        this.vecesComprado = vecesComprado;
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

    public String getDescripcion() {

        return descripcion;
    }

    public void setDescripcion(String descripcion) {

        this.descripcion = descripcion;
    }

    public String getNombreImagen() {

        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {

        this.nombreImagen = nombreImagen;
    }

    public double getPrecio() {

        return precio;
    }

    public void setPrecio(double precio) {

        this.precio = precio;
    }

    public byte[] getImagenBlob() {

        return imagenBlob;
    }

    public void setImagenBlob(byte[] imagenBlob) {

        this.imagenBlob = imagenBlob;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nombreImagen='" + nombreImagen + '\'' +
                ", precio=" + precio +
                ", vecesComprado=" + vecesComprado +
                '}';
    }
}
