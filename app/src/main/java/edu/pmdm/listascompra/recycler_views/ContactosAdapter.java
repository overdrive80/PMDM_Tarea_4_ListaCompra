package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.Contacto;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosViewHolder> {

    private List<Contacto> listaContactos = new ArrayList<>();
    private Context contexto;
    private onContactoLongClickListener listener;

    public ContactosAdapter(List<Contacto> listaContactos, Context contexto) {
        this.listaContactos = listaContactos;
        this.contexto = contexto;
    }

    @NonNull
    @Override
    public ContactosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(contexto).inflate(R.layout.fragment_compartir_lista_item, parent, false);

        return new ContactosViewHolder(view, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactosViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);

        holder.bind(contacto);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Nos aseguramos de pasar la posicion correcta
                int adapterPosition = holder.getAdapterPosition();

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onContactoLongClick(adapterPosition, listaContactos.get(adapterPosition));
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {

        if (listaContactos != null) {
            return listaContactos.size();
        }

        return 0;
    }

    public void setOnLongClickListener(onContactoLongClickListener listener) {
        this.listener = listener;
    }

    public interface onContactoLongClickListener {
        void onContactoLongClick(int idItem, Contacto contacto);
    }
}
