package edu.pmdm.listascompra.basedatos;

import android.provider.BaseColumns;

public final class ContratoCompra {
    public static final String NOMBRE_BBDD = "listascompra.db";
    public static final int VERSION_BBDD = 1;

    private ContratoCompra() {
    }

    /**
     * TABLA PRODUCTOS
     **/
    public static class Productos implements BaseColumns {
        //Definimos el nombre de la tabla
        public static final String NOMBRE_TABLA = "productos";

        //Definimos los campos de la tabla
        public static final String NOMBRE_PRODUCTO = "nombre";
        public static final String DESCRIPCION_PRODUCTO = "descripcion";
        public static final String IMAGEN_PRODUCTO = "imagen";
        public static final String PRECIO_PRODUCTO = "precio";
        public static final String VECES_COMPRADO = "pedido_veces";

        // Sentencias DDL para la creación de la tabla
        public static final String CREAR_TABLA =
                "CREATE TABLE " + NOMBRE_TABLA + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NOMBRE_PRODUCTO + " TEXT NOT NULL, " +
                        DESCRIPCION_PRODUCTO + " TEXT, " +
                        IMAGEN_PRODUCTO + " BLOB, " +
                        PRECIO_PRODUCTO + " REAL, " +
                        VECES_COMPRADO + " INTEGER DEFAULT 0)";

        public static final String BORRAR_TABLA =
                "DROP TABLE IF EXISTS " + Productos.NOMBRE_TABLA;
    }

    /**
     * TABLA LISTAS_COMPRA
     **/
    public static class ListasCompra implements BaseColumns {
        //Definimos el nombre de la tabla
        public static final String NOMBRE_TABLA = "listascompra";

        //Definimos los campos de la tabla
        public static final String NOMBRE_LISTA = "nombre";
        public static final String FECHA_LISTA = "fecha";

        // Sentencias DDL para la creación de la tabla
        public static final String CREAR_TABLA =
                "CREATE TABLE " + NOMBRE_TABLA + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NOMBRE_LISTA + " TEXT NOT NULL, " +
                        FECHA_LISTA + " TEXT DEFAULT CURRENT_TIMESTAMP)";

        public static final String BORRAR_TABLA =
                "DROP TABLE IF EXISTS " + ListasCompra.NOMBRE_TABLA;

    }

    /**
     * TABLA DETALLES LISTA
     **/
    public static class DetalleLista implements BaseColumns {
        //Definimos el nombre de la tabla
        public static final String NOMBRE_TABLA = "detallelista";

        //Definimos los campos de la tabla
        public static final String ID_LISTA = "id_lista";
        public static final String ID_PRODUCTO = "id_producto";
        public static final String CANTIDAD = "cantidad";
        public static final String UNIDAD = "unidad";

        // Sentencias DDL para la creación de la tabla
        public static final String CREAR_TABLA =
                "CREATE TABLE " + NOMBRE_TABLA + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ID_LISTA + " INTEGER NOT NULL, " +
                        ID_PRODUCTO + " INTEGER NOT NULL, " +
                        CANTIDAD + " REAL, " +
                        UNIDAD + " TEXT, " +
                        "CONSTRAINT fk_det_lista FOREIGN KEY(" + ID_LISTA + ") REFERENCES " + ListasCompra.NOMBRE_TABLA + "(" + ListasCompra._ID + ")," +
                        "CONSTRAINT fk_det_prod FOREIGN KEY(" + ID_PRODUCTO + ") REFERENCES " + Productos.NOMBRE_TABLA + "(" + Productos._ID + ")," +
                        "CONSTRAINT uni_lista_prod UNIQUE(" + ID_LISTA + "," + ID_PRODUCTO + ")" +
                        ")";

        public static final String BORRAR_TABLA =
                "DROP TABLE IF EXISTS " + DetalleLista.NOMBRE_TABLA;

    }
}
