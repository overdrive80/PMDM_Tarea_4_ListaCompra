package edu.pmdm.listascompra.basedatos;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class ConexionBBDD extends SQLiteOpenHelper {
    private Context contexto;
    private static ConexionBBDD conexion;

    private ConexionBBDD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.contexto = context;
    }

    public static final ConexionBBDD getInstance(Context context) {
        if (conexion == null) {
            conexion = new ConexionBBDD(context, ContratoCompra.NOMBRE_BBDD, null, ContratoCompra.VERSION_BBDD);
        }
        return conexion;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContratoCompra.Productos.CREAR_TABLA);
        db.execSQL(ContratoCompra.ListasCompra.CREAR_TABLA);
        db.execSQL(ContratoCompra.DetalleLista.CREAR_TABLA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ContratoCompra.Productos.BORRAR_TABLA);
        db.execSQL(ContratoCompra.ListasCompra.CREAR_TABLA);
        db.execSQL(ContratoCompra.DetalleLista.CREAR_TABLA);

        // Crear las tablas nuevamente
        onCreate(db);
    }

    public void borrarTablas() {

        if (conexion != null) {
            SQLiteDatabase db;

            try {
                db = conexion.getWritableDatabase();

                db.execSQL("DROP TABLE IF EXISTS " + ContratoCompra.Productos.NOMBRE_TABLA);
                db.execSQL("DROP TABLE IF EXISTS " + ContratoCompra.ListasCompra.NOMBRE_TABLA);
                db.execSQL("DROP TABLE IF EXISTS " + ContratoCompra.DetalleLista.NOMBRE_TABLA);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void eliminarBaseDeDatos() {
        if (contexto == null) {
            throw new IllegalArgumentException("El contexto no puede ser nulo");
        }

        boolean eliminada = contexto.deleteDatabase(ContratoCompra.NOMBRE_BBDD);
        if (eliminada) {
            Log.d("BBDD", "La base de datos se ha eliminado correctamente.");
        } else {
            Log.d("BBDD", "No se pudo eliminar la base de datos o no existía.");
        }
    }


}
