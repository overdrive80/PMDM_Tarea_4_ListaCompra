package edu.pmdm.listascompra.recycler_views;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ListaCompra;

public class TodasListasViewHolder extends RecyclerView.ViewHolder {
    // Referencias java de los elementos
    private EditText etNombreLista;
    private TextView tvFechaCreacion;
    private ImageButton ibEditarNombre;
    private ImageButton ibMenuOpciones;
    private Context contexto;
    private String nombreOriginal;
    private ListaCompra listaCompraItem;
    private OnCambiarNombreListener listenerNombreValido;
    private OnMenuListasListener listenerMenu;


    public TodasListasViewHolder(Context contexto, @NonNull View itemView) {
        super(itemView);

        this.contexto = contexto;
        this.etNombreLista = itemView.findViewById(R.id.tvNombreLista);
        this.tvFechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
        this.ibEditarNombre = itemView.findViewById(R.id.ibEditarNombre);
        this.ibMenuOpciones = itemView.findViewById(R.id.ibMenuOpciones);

        // Detectar clics en el elemento para eliminar el foco del EditText
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNombreLista.clearFocus(); // Eliminar el foco
                // Al eliminar el foco se ejecutará el evento de perder el foco de más abajo
                // De esta manera, se restaurará el nombre original
            }
        });
    }

    public void bind(ListaCompra lista) {
        listaCompraItem = lista;

        this.etNombreLista.setText(lista.getNombre());
        nombreOriginal = etNombreLista.getText().toString(); // Guardamos el nombre original

        String sFecha = lista.getFechaString(null);
        this.tvFechaCreacion.setText(contexto.getString(R.string.fecha_creacion, sFecha));

        // Al editar el nombre de la lista
        this.ibEditarNombre.setOnClickListener(getImplOnClickListener());

        // Al perder el foco el edittext del nombre de la lista
        etNombreLista.setOnFocusChangeListener(getImplOnFocusChangeListener());

        // Configuramos el botón de opciones
        ibMenuOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), ibMenuOpciones);
                popupMenu.inflate(R.menu.listacompra_menu);//Toast.makeText(contexto, "Opción
                //escogida: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.action_edit) {
                        listenerMenu.onBotonEditar(listaCompraItem.getId(), getAdapterPosition());
                        return true;
                    } else if (menuItem.getItemId() == R.id.action_delete) {
                        listenerMenu.onBotonBorrar(listaCompraItem.getId(), getAdapterPosition());
                        return true;
                    }
                    return false;
                });

                popupMenu.show();
            }
        });
    }

    /**
     * Restaura el nombre del EditText original
     */
    private void restaurarNombre() {
        etNombreLista.setText(nombreOriginal); // Restaurar el texto original
        etNombreLista.setEnabled(false); // Deshabilitar edición
        ibEditarNombre.setImageResource(R.drawable.baseline_edit_24); // Cambiar ícono a editar
    }

    /**
     * Implementación del evento click para editar el nombre de la lista
     *
     * @return
     */
    public View.OnClickListener getImplOnClickListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean esEditable = etNombreLista.isEnabled();

                if (esEditable) {
                    //Validamos los cambios
                    String nuevoNombre = etNombreLista.getText().toString();

                    // Si el nombre está vacío
                    if (nuevoNombre.isEmpty()) {
                        Snackbar.make(view, "El nombre de la lista no puede estar vacío", Snackbar.LENGTH_SHORT).show();
                        restaurarNombre();
                        return;
                    }

                    // Si el nombre es igual al original. No hace falta validar
                    if (nuevoNombre.equals(nombreOriginal)) {
                        restaurarNombre();
                        return;
                    }

                    // Si no está vacío validamos que no exista
                    boolean esValido = listenerNombreValido.esCambioCorrecto(listaCompraItem.getId(), nuevoNombre);

                    // Si el nombre ya existe
                    if (!esValido) {
                        restaurarNombre();
                        return;
                    }

                    // Actualiza el nombre de la listaCompraItem
                    listaCompraItem.setNombre(nuevoNombre);
                    nombreOriginal = nuevoNombre;
                    //itemView.notifyItemChanged(getAdapterPosition());

                    etNombreLista.setEnabled(false);
                    ibEditarNombre.setImageResource(R.drawable.baseline_edit_24);

                } else {
                    //Snackbar.make(view, "Editar nombre", Snackbar.LENGTH_SHORT).show();
                    etNombreLista.setEnabled(true);
                    etNombreLista.requestFocus();  // Dar foco al EditText
                    ibEditarNombre.setImageResource(R.drawable.baseline_save_alt_24);
                }
            }
        };
    }

    /**
     * Implementación del evento de perder el foco del EditText
     *
     * @return
     */
    private View.OnFocusChangeListener getImplOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Si pierde el foco antes de validar cambios, se deshace y vuelve a la original
                if (hasFocus == false) {
                    restaurarNombre();
                }
            }
        };
    }

    //***** SECCION LISTENERS ****///
    public void setOnCambiarNombreListener(OnCambiarNombreListener listenerNombreValido) {
        this.listenerNombreValido = listenerNombreValido;
    }

    public void setOnMenuListasListener(OnMenuListasListener listenerMenu) {
        this.listenerMenu = listenerMenu;
    }

    public interface OnCambiarNombreListener {
        boolean esCambioCorrecto(int idLista, String nuevoNombre);
    }

    public interface OnMenuListasListener {
        void onBotonEditar(int idLista, int posicion);

        void onBotonBorrar(int idLista, int posicion);
    }

}
