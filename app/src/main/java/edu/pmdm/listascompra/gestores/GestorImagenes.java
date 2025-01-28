package edu.pmdm.listascompra.gestores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GestorImagenes {

    private static final String TAG = "GestorImagenes";

    public static Bitmap obtenerImagenDesdeUri(Context context, Uri uri) {

        if (uri == null) {
            Log.e(TAG, "URI es nulo");
            return null;
        }

        Bitmap bitmap = null;
        InputStream inputStream = null;

        try {
            // Opcion 1. Empleando streams
            inputStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);

            // Opcion 2. Empleando ImageDecoder
            //bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));

            // Opcion 3. Empleando MediaStore. Deprecado
            //bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        } catch (IOException e) {
            Log.e(TAG, "Error al obtener la imagen desde URI", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }

            return bitmap;
        }
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap) {

        if (bitmap == null) {
            Log.e(TAG, "Bitmap es nulo");
            return null;
        }

        byte[] imagenBytes;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            imagenBytes = outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return imagenBytes;

    }
}
