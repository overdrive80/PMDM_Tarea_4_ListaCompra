package edu.pmdm.listascompra.recycler_views;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.Producto;

/**
 * Cada instancia representa un objeto del RecyclerView
 */
public class ProductosViewHolder extends RecyclerView.ViewHolder {
    //1. Crear referencias elementos de la vista
    // Campos de fila de la vista
    private TextView tvNombre;
    private CheckBox chkProducto;

    public ProductosViewHolder(@NonNull View itemView) {
        super(itemView);

        tvNombre = itemView.findViewById(R.id.tvNombre);
        chkProducto = itemView.findViewById(R.id.chkProducto);

    }

    public void bind(Producto producto, Boolean estaElegido, ProductosAdapter.OnProductoSeleccionadoListener listener) {
        //2. Vincular instancias
        tvNombre.setText(producto.getNombre());
        chkProducto.setChecked(estaElegido); // Establecer el estado del CheckBox

        chkProducto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onProductoSeleccionado(producto, isChecked);
            }
        });
    }

}
