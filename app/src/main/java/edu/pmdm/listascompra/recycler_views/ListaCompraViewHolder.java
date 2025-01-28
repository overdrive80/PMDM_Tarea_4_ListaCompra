package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ItemLista;
import edu.pmdm.listascompra.modelo.Producto;


/**
 * Cada instancia representa un objeto del RecyclerView
 */
public class ListaCompraViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //1. Crear referencias elementos de la vista
    // Campos de fila de la vista
    private TextView tvNombre;
    private TextView tvDescripcion;
    private TextView tvPrecio;
    private ImageView ivImagen;
    private TextView tvCantidad;
    private TextView tvUnidad;
    private Context contexto;
    private ListaCompraAdapter.onItemListaClickListener listener;

    public ListaCompraViewHolder(Context contexto, View itemView, ListaCompraAdapter.onItemListaClickListener listener) {
        super(itemView);

        this.contexto = contexto;
        this.listener = listener;

        itemView.setOnClickListener(this);
    }

    public void bind(ItemLista itemLista) {

        // Vincular instancias
        tvNombre = itemView.findViewById(R.id.tvNombre);
        tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);
        ivImagen = itemView.findViewById(R.id.ivImagenProd);
        tvCantidad = itemView.findViewById(R.id.tvCantidad);
        tvUnidad = itemView.findViewById(R.id.tvUnidades);

        //Recuperamos los campos del item
        Producto producto = itemLista.getProducto();
        Double cantidad = itemLista.getCantidad();
        String unidad = itemLista.getUnidad();

        // CANTIDAD Y UNIDAD
        tvCantidad.setText(String.valueOf(cantidad));
        tvUnidad.setText(unidad);

        // NOMBRE Y DESCRIPCION
        tvNombre.setText(producto.getNombre());
        tvDescripcion.setText(producto.getDescripcion());

        // PRECIO
        String sPrecio = itemView.getContext().getString(R.string.precio, producto.getPrecio());
        tvPrecio.setText(sPrecio);

        // IMAGEN
        byte[] byteImagen = producto.getImagenBlob();
        if (byteImagen != null) {
            //Decodificamos el byte array a bitmap
            Bitmap imagen = BitmapFactory.decodeByteArray(byteImagen, 0, byteImagen.length);

            ivImagen.setImageBitmap(imagen);
        }

    }

    @Override
    public void onClick(View v) {
        //Snackbar.make(v, "Producto seleccionado", Snackbar.LENGTH_LONG).show();
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Verifica que la posición sea válida
                listener.onItemListaClick(position);
            }
        }
    }
}
