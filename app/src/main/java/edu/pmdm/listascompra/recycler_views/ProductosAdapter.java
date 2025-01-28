package edu.pmdm.listascompra.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.Producto;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosViewHolder> {
    private List<Producto> productos;
    private Set<Integer> productosSeleccionados;
    private OnProductoSeleccionadoListener listener;

    public ProductosAdapter(List<Producto> productos, Set<Integer> productosSeleccionados, OnProductoSeleccionadoListener listener) {
        this.productos = productos;
        this.listener = listener;
        this.productosSeleccionados = productosSeleccionados;
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductosViewHolder pvh = null;

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.alertdialog_productos_item, parent, false);

        pvh = new ProductosViewHolder(itemView);
        return pvh;

    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {

        Producto producto = productos.get(position);
        boolean estaElegido = productosSeleccionados.contains(producto.getId());

        holder.bind(producto, estaElegido, listener);
    }

    @Override
    public int getItemCount() {
        return productos == null ? 0 : productos.size();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public interface OnProductoSeleccionadoListener {
        void onProductoSeleccionado(Producto producto, boolean seleccionado);
    }
}
