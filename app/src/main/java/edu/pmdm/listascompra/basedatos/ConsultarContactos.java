package edu.pmdm.listascompra.basedatos;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.modelo.Contacto;

public class ConsultarContactos {
    private Context contexto;
    private static ContentResolver contentResolver;
    private static List<Contacto> listaContactos = new ArrayList<>();

    private ConsultarContactos(Context contexto) {
        this.contexto = contexto;
        this.contentResolver = contexto.getContentResolver();
    }

    public static List<Contacto> getListaContactos(Context contexto) {
        List<Contacto> listaContactos = new ArrayList<>(); // Inicializa la lista
        ContentResolver contentResolver = contexto.getContentResolver();

        // Definimos el URI del ContentProvider
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // Definimos la consulta. Nombre y telefono
        String[] select = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // No es necesario el where, ya que solo se obtendrán contactos con números de teléfono
        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        try (Cursor cursor = contentResolver.query(uri, select, null, null, orderBy)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Recuperamos los id de las columnas
                int idxID = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int idxNombre = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int idxTelefono = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                // Recorremos los resultados
                do {
                    int id = cursor.getInt(idxID);
                    String nombre = cursor.getString(idxNombre);
                    String telefono = cursor.getString(idxTelefono);

                    // Construimos la URI de la foto usando el CONTACT_ID
                    Uri photoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));

                    // Creamos el objeto Contacto con la URI de la foto
                    Contacto contacto = new Contacto(id, nombre, telefono, photoUri);
                    listaContactos.add(contacto);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Considera registrar el error en un log
        }

        return listaContactos;
    }

    public static Contacto getContacto(Context contexto, int idContacto) {
        ContentResolver contentResolver = contexto.getContentResolver();

        // Definimos el URI del ContentProvider
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // Definimos la consulta. Nombre y telefono
        String[] select = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // Cambiamos la condición para buscar por CONTACT_ID
        String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idContacto)};
        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Contacto contacto = null;

        try (Cursor cursor = contentResolver.query(uri, select, where, whereArgs, orderBy)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Recuperamos los id de las columnas
                int idxID = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int idxNombre = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int idxTelefono = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                int id = cursor.getInt(idxID);
                String nombre = cursor.getString(idxNombre);
                String telefono = cursor.getString(idxTelefono);

                // Construimos la URI de la foto usando el CONTACT_ID
                Uri photoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));

                // Creamos el objeto Contacto con la URI de la foto
                contacto = new Contacto(id, nombre, telefono, photoUri);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Considera registrar el error en un log
        }

        return contacto;
    }
}
