package edu.pmdm.listascompra.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.Producto;
import edu.pmdm.listascompra.recycler_views.ProductosAdapter;

public class DialogFragmentProductos extends DialogFragment {

    private List<Producto> productos;
    private Set<Integer> productosSeleccionados;
    private ProductosAdapter.OnProductoSeleccionadoListener listener;

    // Constructor modificado para aceptar la lista de productos cargados
    public DialogFragmentProductos(Set<Integer> productosSeleccionados, List<Producto> productos) {
        this.productosSeleccionados = productosSeleccionados;
        this.productos = productos;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_productos, null);
        builder.setView(dialogView);

        RecyclerView rvProductos = dialogView.findViewById(R.id.rvProductos);
        ProductosAdapter productosAdapter = new ProductosAdapter(productos, productosSeleccionados, listener);
        rvProductos.setAdapter(productosAdapter);

        Button btnSalir = dialogView.findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(v -> dismiss());

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        return dialog;
    }

    // Configuración del listener
    public void setOnProductoSeleccionadoListener(ProductosAdapter.OnProductoSeleccionadoListener listener) {
        this.listener = listener;
    }
}
