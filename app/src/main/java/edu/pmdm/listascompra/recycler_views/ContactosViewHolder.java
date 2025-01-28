package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.Contacto;

public class ContactosViewHolder extends RecyclerView.ViewHolder {
    private Context contexto;
    private int idContacto;
    private TextView tvNombreContacto;
    private TextView tvTelefono;
    private ImageView ivFotoContacto;

    public ContactosViewHolder(@NonNull View itemView, Context contexto) {
        super(itemView);
        this.contexto = contexto;

        tvNombreContacto = itemView.findViewById(R.id.tvNombreContacto);
        tvTelefono = itemView.findViewById(R.id.tvTelefono);
        ivFotoContacto = itemView.findViewById(R.id.ivFotoContacto);
    }

    public void bind(Contacto contacto) {
        tvNombreContacto.setText(contacto.getNombre());
        tvTelefono.setText(contacto.getTelefono());

        // Configura la imagen
        Uri fotoUri = contacto.getFotoUri(); // Foto URI obtenida
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contexto.getContentResolver(), fotoUri, true);

        // Si no se pudo obtener la foto (inputStream es null), asignamos la imagen predeterminada
        if (inputStream == null) {
            ivFotoContacto.setImageResource(R.drawable.ic_contacto);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ivFotoContacto.setImageBitmap(bitmap);

    }
}
