package edu.pmdm.listascompra.modelo;

public class ItemLista {
    private Producto producto;
    private Double cantidad;
    private String unidad;

    public ItemLista(Producto producto, Double cantidad, String unidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    public ItemLista(Producto producto, Double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    // Implementamos este método para detectar modificaciones.
    public boolean haSidoModificado(ItemLista otroItem) {
        return (this.cantidad != otroItem.getCantidad()) || !this.unidad.equals(otroItem.getUnidad());
    }

    @Override
    public String toString() {
        return "ItemLista{" +
                "producto=" + producto +
                ", cantidad='" + cantidad + '\'' +
                ", unidad='" + unidad + '\'' +
                '}';
    }
}
