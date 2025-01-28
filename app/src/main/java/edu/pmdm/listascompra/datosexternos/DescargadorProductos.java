package edu.pmdm.listascompra.datosexternos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.modelo.Producto;

public class DescargadorProductos {

    private static final String JSON_URL = "https://fp.cloud.riberadeltajo.es/listacompra/listaproductos.json";
    private static final String IMAGE_URL_BASE = "https://fp.cloud.riberadeltajo.es/listacompra/images/";

    public DescargadorProductos() {
    }

    public List<Producto> getProductos() {

        List<Producto> productos = new ArrayList<>();
        InputStream is;

        try {
            // Definimos el objeto URL y la conexion
            URL url = new URL(JSON_URL);
            URLConnection urlConnection = url.openConnection();

            // Obtenemos el InputStream del JSON
            is = urlConnection.getInputStream();

            //Lo asignamos a un InputStreamReader para poder leer el JSON.
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);

            // Reader es la clase padre de InputStreamReader y BufferedReader, así que puedo usarlo
            // como parametro del BufferedReader para leer por lineas
            BufferedReader reader = new BufferedReader(isr);

            // Creamos un StringBuilder para almacenar el contenido del JSON
            StringBuilder jsonBuilder = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                jsonBuilder.append(linea);
            }

            //Cerramos objetos de lectura
            reader.close();
            isr.close();
            is.close();

            // Convertir el contenido a String
            JSONArray productosArray = getJsonArray(jsonBuilder);

            // Procesar cada producto
            for (int i = 0; i < productosArray.length(); i++) {
                // Obtener el objeto JSON correspondiente a cada item
                JSONObject productoObj = productosArray.getJSONObject(i);

                // Obtener los valores de los atributos
                String nombre = productoObj.getString("nombre");
                String descripcion = productoObj.getString("descripcion");
                String nombreImagen = productoObj.getString("imagen");
                double precio = productoObj.getDouble("precio");

                // Pasamos el nombre de la imagen para obtener un array de bytes
                byte[] imagenBlob = getImagenes(nombreImagen);

                // Crear objeto Producto
                Producto producto = new Producto(nombre, descripcion, nombreImagen, precio);
                producto.setImagenBlob(imagenBlob);
                productos.add(producto);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return productos;
    }

    private static @NonNull JSONArray getJsonArray(StringBuilder jsonBuilder) throws JSONException {
        String json = jsonBuilder.toString();

        // Validar el JSON descargado
        if (json.isEmpty()) {
            throw new RuntimeException("El JSON descargado está vacío o no es válido.");
        }

        // Convertir el JSON a un objeto JSONObject
        JSONObject jsonObject = new JSONObject(json);

        // Obtener el array de productos
        return jsonObject.getJSONArray("productos");
    }

    private byte[] getImagenes(String nombreImagen) {

        try {
            // Creamos la url de acceso de la imagen
            URL url = new URL(IMAGE_URL_BASE + nombreImagen);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            // Obtenemos el InputStream de la imagen
            InputStream is = connection.getInputStream();

            // Convertir InputStream a byte[] y cerramos el InputStream
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            // Convertir Bitmap a byte[]
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputBytes);
            return outputBytes.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

