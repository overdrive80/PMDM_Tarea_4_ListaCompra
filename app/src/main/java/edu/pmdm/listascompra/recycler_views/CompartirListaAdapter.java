package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ListaCompra;

public class CompartirListaAdapter extends RecyclerView.Adapter<CompartirListasViewHolder> {
    private Context contexto;
    private List<ListaCompra> listas;
    private OnClickItemListener listener;

    public CompartirListaAdapter(Context contexto, List<ListaCompra> listas) {
        this.contexto = contexto;
        this.listas = listas;
    }

    @NonNull
    @Override
    public CompartirListasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(contexto).inflate(R.layout.activity_compartir_lista_item, parent, false);
        return new CompartirListasViewHolder(contexto, view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompartirListasViewHolder holder, int position) {
        holder.bind(listas.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listas != null ? listas.size() : 0;
    }

    public interface OnClickItemListener {
        void onItemClick(int position, ListaCompra lista);
    }

    public void setOnItemClickListener(OnClickItemListener listener) {
        this.listener = listener;
    }
}
