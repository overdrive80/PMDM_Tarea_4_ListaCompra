package edu.pmdm.listascompra.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.modelo.ItemLista;
import edu.pmdm.listascompra.modelo.ListaCompra;
import edu.pmdm.listascompra.modelo.Producto;

public class RepoListasCompras {

    private ConexionBBDD conexion;
    private Context contexto;
    private static final String TAG = "RepoListasCompras";

    public RepoListasCompras(Context contexto) {
        // Obtenemos una instancia de la conexion a la BBDD (SQLiteOpenHelper)
        conexion = ConexionBBDD.getInstance(contexto);
        this.contexto = contexto;
    }

    //----------------------- METODOS TABLA PRODUCTOS -----------------------//

    /**
     * Inserta un producto en la tabla productos
     *
     * @param producto
     * @return
     */
    public long insertarProducto(Producto producto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        db.beginTransaction();

        //Encapsulamos los valores
        ContentValues valores = new ContentValues();
        valores.put(ContratoCompra.Productos.NOMBRE_PRODUCTO, producto.getNombre());
        valores.put(ContratoCompra.Productos.DESCRIPCION_PRODUCTO, producto.getDescripcion());
        valores.put(ContratoCompra.Productos.IMAGEN_PRODUCTO, producto.getImagenBlob());
        valores.put(ContratoCompra.Productos.PRECIO_PRODUCTO, producto.getPrecio());

        // Insertamos los valores
        long nuevo = -1;

        try {
            nuevo = db.insert(ContratoCompra.Productos.NOMBRE_TABLA, null, valores);

            // Si sale bien confirmamos la transacción
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, "Error al añadir productos: " + e.getMessage(), e);
        } finally {
            // Finaliza la transacción
            db.endTransaction();  // Si no se llamó a setTransactionSuccessful(), hace un rollback
        }

        return nuevo;
    }

    /**
     * Comprueba si la tabla productos esta vacia
     *
     * @return
     */
    public boolean hayProductos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        boolean bHayProductos = false;

        String sSQL = "SELECT * FROM " + ContratoCompra.Productos.NOMBRE_TABLA;

        try (Cursor cursor = db.rawQuery(sSQL, null)) {
            bHayProductos = cursor.getCount() > 0;
        }

        return bHayProductos;
    }

    /**
     * Obtiene un producto específico por su ID.
     *
     * @param idProducto El ID del producto.
     * @return El producto encontrado o null si no existe.
     */
    public Producto getProducto(int idProducto) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Producto producto = null;

        String[] select = null;
        String where = ContratoCompra.Productos._ID + " = ?";
        String[] whereArgs = {String.valueOf(idProducto)};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        try (Cursor cursor = db.query(
                ContratoCompra.Productos.NOMBRE_TABLA,
                select, where, whereArgs, groupBy, having, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {
                // Recuperamos las posiciones de las columnas
                int idxId = cursor.getColumnIndexOrThrow(ContratoCompra.Productos._ID);
                int idxNombre = cursor.getColumnIndexOrThrow(ContratoCompra.Productos.NOMBRE_PRODUCTO);
                int idxDescripcion = cursor.getColumnIndexOrThrow(ContratoCompra.Productos.DESCRIPCION_PRODUCTO);
                int idxImagen = cursor.getColumnIndexOrThrow(ContratoCompra.Productos.IMAGEN_PRODUCTO);
                int idxPrecio = cursor.getColumnIndexOrThrow(ContratoCompra.Productos.PRECIO_PRODUCTO);
                int idxVecesComprado = cursor.getColumnIndexOrThrow(ContratoCompra.Productos.VECES_COMPRADO);

                // Crear el objeto Producto
                int id = cursor.getInt(idxId);
                String nombre = cursor.getString(idxNombre);
                String descripcion = cursor.getString(idxDescripcion);
                byte[] imagen = cursor.getBlob(idxImagen);
                double precio = cursor.getDouble(idxPrecio);
                int vecesComprado = cursor.getInt(idxVecesComprado);

                producto = new Producto(id, nombre, descripcion, precio, imagen, vecesComprado);
            }
        }

        return producto;
    }

    /**
     * Obtiene todos los productos de la tabla productos.
     *
     * @return Lista de productos.
     */
    public List<Producto> getProductos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        List<Producto> productos = new ArrayList<>();

        String[] columns = {ContratoCompra.Productos._ID};
        String where = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = ContratoCompra.Productos.VECES_COMPRADO + " DESC";

        // Primero obtenemos todos los IDs de los productos
        try (Cursor cursor = db.query(
                ContratoCompra.Productos.NOMBRE_TABLA,
                columns, where, whereArgs, groupBy, having, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {
                int idxId = cursor.getColumnIndexOrThrow(ContratoCompra.Productos._ID);

                do {
                    int id = cursor.getInt(idxId);
                    Producto producto = getProducto(id); // Reutilizamos getProducto
                    if (producto != null) {
                        productos.add(producto);
                    }
                } while (cursor.moveToNext());
            }
        }

        return productos;
    }


    public void incrementarVeces(long idProducto) {
        SQLiteDatabase db = conexion.getWritableDatabase();

        db.beginTransaction();

        try {
            String sql = "UPDATE " + ContratoCompra.Productos.NOMBRE_TABLA +
                    " SET " + ContratoCompra.Productos.VECES_COMPRADO + " = " +
                    ContratoCompra.Productos.VECES_COMPRADO + " + 1 " +
                    " WHERE " + ContratoCompra.Productos._ID + " = ?";

            db.execSQL(sql, new String[]{String.valueOf(idProducto)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error al incrementar las veces: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    public void decrementarVeces(long idProducto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        int vecesComprado = 0;

        // Primero consultamos que no sea cero para evitar numeros negativos
        String[] select = new String[]{ContratoCompra.Productos.VECES_COMPRADO};
        String where = ContratoCompra.Productos._ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idProducto)};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        try (Cursor cursor = db.query(ContratoCompra.Productos.NOMBRE_TABLA,
                select, where, whereArgs, groupBy, having, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {
                vecesComprado = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "DecrementarVeces: Error al consultar campo veces: " + e.getMessage(), e);
        }

        // Actualizamos el campo veces si es mayor de cero
        db.beginTransaction();
        try {
            if (vecesComprado > 0) {
                String sql = "UPDATE " + ContratoCompra.Productos.NOMBRE_TABLA +
                        " SET " + ContratoCompra.Productos.VECES_COMPRADO + " = " +
                        ContratoCompra.Productos.VECES_COMPRADO + " - 1 " +
                        " WHERE " + ContratoCompra.Productos._ID + " = ?";

                db.execSQL(sql, new String[]{String.valueOf(idProducto)});
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "DecrementarVeces: Error al actualizar campo veces: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

    }

    public boolean existeProductoNombre(String nombre) {
        boolean existe = false;

        SQLiteDatabase db = conexion.getReadableDatabase();

        String sSQL = "SELECT 1 FROM " + ContratoCompra.Productos.NOMBRE_TABLA +
                " WHERE " + ContratoCompra.Productos.NOMBRE_PRODUCTO + " = ?";

        try (Cursor cursor = db.rawQuery(sSQL, new String[]{nombre})) {
            existe = cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return existe;

    }

    //----------------------- METODOS TABLA LISTASCOMPRA -----------------------//
    public long insertarLista(String nombreLista) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        long nuevo = -1;

        //Encapsulamos los valores
        ContentValues valores = new ContentValues();
        valores.put(ContratoCompra.ListasCompra.NOMBRE_LISTA, nombreLista);

        db.beginTransaction();

        try {
            nuevo = db.insert(ContratoCompra.ListasCompra.NOMBRE_TABLA, null, valores);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar lista: " + e.getMessage(), e);
        } finally {
            // Finaliza la transacción
            db.endTransaction();  // Si no se llamó a setTransactionSuccessful(), hace un rollback
        }

        return nuevo;
    }

    public boolean existeNombreLista(String nombre) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        boolean bExisteLista = false;

        String sSQL = "SELECT * FROM " + ContratoCompra.ListasCompra.NOMBRE_TABLA +
                " WHERE " + ContratoCompra.ListasCompra.NOMBRE_LISTA + " = ?";

        try (Cursor cursor = db.rawQuery(sSQL, new String[]{nombre})) {
            bExisteLista = cursor.getCount() > 0;
        }

        return bExisteLista;
    }

    public int cambiarNombreLista(int idLista, String nuevoNombre) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        int filas = -1;

        db.beginTransaction();

        try {
            // Definimos el campo a modificar junto al nuevo valor
            ContentValues cv = new ContentValues();
            cv.put(ContratoCompra.ListasCompra.NOMBRE_LISTA, nuevoNombre);

            // Definimos la condición de búsqueda
            String where = ContratoCompra.ListasCompra._ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(idLista)};

            // Realizamos la actualización
            filas = db.update(ContratoCompra.ListasCompra.NOMBRE_TABLA, cv, where, whereArgs);

            // Solo marcamos la transacción como correcta si se actualizó al menos una fila
            if (filas > 0) {
                db.setTransactionSuccessful();
            } else {
                Log.w(TAG, "No se encontró ninguna lista con el ID: " + idLista);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cambiar el nombre de la lista: " + e.getMessage(), e);
        } finally {
            // Finaliza la transacción
            db.endTransaction();  // Si no se llamó a setTransactionSuccessful(), hace un rollback
        }

        return filas;
    }

    public List<ListaCompra> getListas() {
        SQLiteDatabase db = conexion.getReadableDatabase();

        List<ListaCompra> listas = new ArrayList<>();

        // Si ponemos null devuelve todos
        String[] select = null;
        String where = null;
        String[] whereArgs = null; //new String[]{campo1, campo2};
        String groupBy = null;
        String having = null;
        String orderBy = ContratoCompra.ListasCompra.FECHA_LISTA + " DESC";

        // Ejecutamos la consulta
        try (Cursor cursor = db.query(ContratoCompra.ListasCompra.NOMBRE_TABLA, select, where, whereArgs, groupBy, having, orderBy)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = cursor.getColumnIndexOrThrow(ContratoCompra.ListasCompra._ID);
                int idxNombre = cursor.getColumnIndexOrThrow(ContratoCompra.ListasCompra.NOMBRE_LISTA);
                int idxFecha = cursor.getColumnIndexOrThrow(ContratoCompra.ListasCompra.FECHA_LISTA);

                // Recorremos el cursor
                do {
                    ListaCompra listasCompra = new ListaCompra(cursor.getInt(idxId), cursor.getString(idxNombre), cursor.getString(idxFecha));

                    listas.add(listasCompra);
                } while (cursor.moveToNext());
            }
        }

        return listas;
    }

    public int eliminarLista(int idLista) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        db.beginTransaction();
        int filas = -1;

        try {
            // Eliminar registros relacionados en tabla "detallelista"
            String deleteDetalleLista = "DELETE FROM " + ContratoCompra.DetalleLista.NOMBRE_TABLA +
                    " WHERE " + ContratoCompra.DetalleLista.ID_LISTA + " = ?";
            db.execSQL(deleteDetalleLista, new String[]{String.valueOf(idLista)});

            // Elimina el registro de "listascompra"
            String deleteListasCompra = "DELETE FROM " + ContratoCompra.ListasCompra.NOMBRE_TABLA +
                    " WHERE " + ContratoCompra.ListasCompra._ID + " = ?";
            db.execSQL(deleteListasCompra, new String[]{String.valueOf(idLista)});

            db.setTransactionSuccessful();
            filas = 1; // Si se ejecuta correctamente, consideramos 1 fila eliminada
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar la lista: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return filas;
    }

    //----------------------- METODOS TABLA DETALLELISTA -----------------------//
    public long insertarDetalleLista(long idLista, long idProducto, Double cantidad, String unidad) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        long nuevo = -1;

        //Encapsulamos los valores
        ContentValues valores = new ContentValues();

        valores.put(ContratoCompra.DetalleLista.ID_LISTA, idLista);
        valores.put(ContratoCompra.DetalleLista.ID_PRODUCTO, idProducto);
        valores.put(ContratoCompra.DetalleLista.UNIDAD, unidad);

        // Convierte la cantidad a un número decimal
        try {
            double cantidadNumerica = cantidad;
            valores.put(ContratoCompra.DetalleLista.CANTIDAD, cantidadNumerica);
        } catch (NumberFormatException e) {
            // Si ocurre un error, logeamos y asignamos un valor por defecto
            Log.e(TAG, "Error al parsear cantidad: " + cantidad, e);
            valores.put(ContratoCompra.DetalleLista.CANTIDAD, 0.0);
        }

        db.beginTransaction();

        try {
            nuevo = db.insert(ContratoCompra.DetalleLista.NOMBRE_TABLA, null, valores);
            incrementarVeces(idProducto);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return nuevo;
    }


    public List<ItemLista> getDetalleLista(int idLista) {
        List<ItemLista> itemsLista = new ArrayList<>();

        SQLiteDatabase db = conexion.getReadableDatabase();

        String[] select = null;
        String where = ContratoCompra.DetalleLista.ID_LISTA + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idLista)};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        try (Cursor cursor = db.query(ContratoCompra.DetalleLista.NOMBRE_TABLA, select, where, whereArgs, groupBy, having, orderBy)) {

            if (cursor != null && cursor.moveToFirst()) {

                int idxProducto = cursor.getColumnIndexOrThrow(ContratoCompra.DetalleLista.ID_PRODUCTO);
                int idxCantidad = cursor.getColumnIndexOrThrow(ContratoCompra.DetalleLista.CANTIDAD);
                int idxUnidad = cursor.getColumnIndexOrThrow(ContratoCompra.DetalleLista.UNIDAD);

                do {

                    int idProducto = cursor.getInt(idxProducto);
                    double cantidad = cursor.getDouble(idxCantidad);
                    String unidad = cursor.getString(idxUnidad);

                    Producto producto = getProducto(idProducto);

                    ItemLista itemLista = new ItemLista(producto, cantidad, unidad);
                    itemsLista.add(itemLista);

                } while (cursor.moveToNext());
            }
        }

        return itemsLista;
    }

    public void eliminarDetalleLista(int idLista, int idProducto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        db.beginTransaction();

        String sSQL = "DELETE FROM " + ContratoCompra.DetalleLista.NOMBRE_TABLA +
                " WHERE " + ContratoCompra.DetalleLista.ID_LISTA + " = ? AND " +
                ContratoCompra.DetalleLista.ID_PRODUCTO + " = ?";

        try {
            db.execSQL(sSQL, new String[]{String.valueOf(idLista), String.valueOf(idProducto)});
            decrementarVeces(idProducto);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void modificarDetalleLista(int idLista, int id, Double cantidad, String unidad) {
        SQLiteDatabase db = conexion.getWritableDatabase();

        db.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(ContratoCompra.DetalleLista.CANTIDAD, cantidad);
        cv.put(ContratoCompra.DetalleLista.UNIDAD, unidad);

        String where = ContratoCompra.DetalleLista.ID_LISTA + " = ? AND " +
                ContratoCompra.DetalleLista.ID_PRODUCTO + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idLista), String.valueOf(id)};

        try {
            db.update(ContratoCompra.DetalleLista.NOMBRE_TABLA, cv, where, whereArgs);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
