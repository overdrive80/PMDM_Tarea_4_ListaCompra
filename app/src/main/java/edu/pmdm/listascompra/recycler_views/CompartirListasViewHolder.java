package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ListaCompra;

public class CompartirListasViewHolder extends RecyclerView.ViewHolder {
    private Context contexto;
    private TextView tvNombreLista;
    private TextView tvFechaCreacion;
    private CompartirListaAdapter.OnClickItemListener listener;
    private ListaCompra listaCompra;


    public CompartirListasViewHolder(Context contexto, @NonNull View itemView) {
        super(itemView);

        this.contexto = contexto;
        tvNombreLista = itemView.findViewById(R.id.tvNombreLista);
        tvFechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);

    }

    public void bind(ListaCompra listaCompra, CompartirListaAdapter.OnClickItemListener listener) {
        // Asignar los datos a las vistas
        tvNombreLista.setText(listaCompra.getNombre());
        tvFechaCreacion.setText(listaCompra.getFechaString(null));

        // Configurar el listener en el itemView
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(getAdapterPosition(), listaCompra);
            }
        });
    }

    public void setOnItemClickListener(CompartirListaAdapter.OnClickItemListener listener) {
        this.listener = listener;
    }
}