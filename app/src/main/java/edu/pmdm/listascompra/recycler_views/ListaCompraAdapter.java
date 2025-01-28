package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ItemLista;

/**
 * Esta clase interconecta tipos de elementos para pasar datos
 * Por eso recibe toda la cantidad de datos que se necesitan
 */
public class ListaCompraAdapter extends RecyclerView.Adapter<ListaCompraViewHolder> {

    // Representa una lista de productos ampliada con la cantidad y unidades
    private List<ItemLista> listaCompra;
    private Context contexto;
    private onItemListaClickListener listener;

    public ListaCompraAdapter(Context contexto, List<ItemLista> listaCompra, onItemListaClickListener listener) {
        this.listaCompra = listaCompra;
        this.contexto = contexto;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListaCompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // El parent representa el RecyclerView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.activity_nueva_lista_item, parent, false);

        return new ListaCompraViewHolder(contexto, itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCompraViewHolder holder, int position) {
        holder.bind(listaCompra.get(position));
    }

    @Override
    public int getItemCount() {

        return listaCompra == null ? 0 : listaCompra.size();
    }

    public interface onItemListaClickListener {
        void onItemListaClick(int posicion);
    }

}
