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

public class TodasListasAdapter extends RecyclerView.Adapter<TodasListasViewHolder> {

    private List<ListaCompra> listas; // Inicializa la lista vacía
    private Context contexto;
    private TodasListasViewHolder.OnCambiarNombreListener listenerNombreValido;
    private TodasListasViewHolder.OnMenuListasListener listenerMenu;

    public TodasListasAdapter(Context contexto, List<ListaCompra> listas) {
        this.listas = listas;
        this.contexto = contexto;
    }

    @NonNull
    @Override
    public TodasListasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_consultar_listas_item, parent, false);

        return new TodasListasViewHolder(contexto, itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodasListasViewHolder holder, int position) {
        holder.bind(listas.get(position));

        // Pasamos las referencias a los listeners
        holder.setOnCambiarNombreListener(listenerNombreValido);
        holder.setOnMenuListasListener(listenerMenu);
    }

    @Override
    public int getItemCount() {
        return listas != null ? listas.size() : 0;
    }

    public void setOnCambiarNombreListener(TodasListasViewHolder.OnCambiarNombreListener listenerNombreValido) {
        this.listenerNombreValido = listenerNombreValido;
    }

    public void setOnMenuListasListener(TodasListasViewHolder.OnMenuListasListener listenerMenu) {
        this.listenerMenu = listenerMenu;
    }
}
