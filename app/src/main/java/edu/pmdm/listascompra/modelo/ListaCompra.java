package edu.pmdm.listascompra.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.pmdm.listascompra.gestores.GestorFechas;

public class ListaCompra {

    private int id;
    private String nombre;
    private Date fecha;
    private List<ItemLista> items;
    private final String patronEntrada = "yyyy-MM-dd HH:mm:ss";
    private final String patronSalida = "dd-MM-yyyy HH:mm";

    // Constructor para recuperar la lista de la base de datos
    public ListaCompra(int id, String nombre, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.items = new ArrayList<>();

        // Convertir la fecha de texto a java.util.Date
        this.fecha = GestorFechas.toLocalfromUTC(fecha);

    }

    public ListaCompra(int id, String nombre, String fecha, List<ItemLista> items) {
        this.id = id;
        this.nombre = nombre;
        this.items = items;
        this.fecha = GestorFechas.toLocalfromUTC(fecha);

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFechaString(String patronPerso) {

        if (patronPerso != null && !patronPerso.isEmpty()) {
            return GestorFechas.toString(fecha, patronPerso);
        }

        return GestorFechas.toString(fecha, patronSalida);
    }

    public List<ItemLista> getItems() {
        return items;
    }

    public void setItems(List<ItemLista> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ListaCompra{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", items=" + items +
                '}';
    }
}
