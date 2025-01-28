package edu.pmdm.listascompra.ui.compartir_lista;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.ActivityCompartirListaBinding;
import edu.pmdm.listascompra.dialogos.DialogFragmentEnviarSMS;
import edu.pmdm.listascompra.modelo.Contacto;
import edu.pmdm.listascompra.modelo.ItemLista;
import edu.pmdm.listascompra.modelo.ListaCompra;
import edu.pmdm.listascompra.recycler_views.CompartirListaAdapter;

public class CompartirListaActivity extends AppCompatActivity {
    private RecyclerView rvListasCompra;
    private List<ListaCompra> listas = new ArrayList<>();
    private RepoListasCompras repo = new RepoListasCompras(this);
    private ActivityCompartirListaBinding binding;
    private CompartirListaViewModel viewModel;
    private ListaCompra listaSeleccionada;
    private Contacto contactoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el binding
        binding = ActivityCompartirListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gestionar el Toolbar
        gestionarToolbar();

        // Recuperamos el contacto seleccionado a partir del Bundle
        getContactoSeleccionado();

        // Instancia el ViewModel
        viewModel = new ViewModelProvider(this).get(CompartirListaViewModel.class);

        // Configurar RecyclerView y Adapter
        rvListasCompra = binding.rvListaCompartir;
        CompartirListaAdapter adapter = new CompartirListaAdapter(this, listas);
        rvListasCompra.setAdapter(adapter);

        // Imprescindible configurar el observador
        // El 'observer' observa el LiveData que contiene la lista, y cuando se produce un cambio en la lista
        // (p.e. carga datos), el observer se activa y ejecuta el metodo onChanged, solicitando la actualización
        // del adaptador
        viewModel.getListasCompra().observe(this, new Observer<List<ListaCompra>>() {
            @Override
            public void onChanged(List<ListaCompra> listasModificadas) {
                if (listasModificadas == null || listasModificadas.isEmpty()) {
                    rvListasCompra.setVisibility(View.GONE);
                } else {
                    rvListasCompra.setVisibility(View.VISIBLE);

                    listas.clear();
                    listas.addAll(listasModificadas);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Delegacion del clic del item
        CompartirListaAdapter.OnClickItemListener listener = getImplListenerClick();
        adapter.setOnItemClickListener(listener);

        // Cargamos la lista de datos desde una BBDD y la vinculamos al ViewModel
        getListaCompra();
    }

    // Implementamos el listener del clic del item
    private CompartirListaAdapter.OnClickItemListener getImplListenerClick() {
        return new CompartirListaAdapter.OnClickItemListener() {

            @Override
            public void onItemClick(int position, ListaCompra lista) {
                listaSeleccionada = lista;
                //Snackbar.make(binding.getRoot(), "Lista seleccionada: " + lista.getNombre(), Snackbar.LENGTH_SHORT).show();

                // Mostrar el cuadro de diálogo para enviar SMS
                DialogFragmentEnviarSMS dialog = DialogFragmentEnviarSMS.newInstance(contactoSeleccionado, listaSeleccionada);

                // Implementamos la devolución
                DialogFragmentEnviarSMS.OnSmsEnviadoListener listener = new DialogFragmentEnviarSMS.OnSmsEnviadoListener() {
                    @Override
                    public void onSmsEnviadoExito(String mensaje) {
                        mostrarDialogExitoSMS(mensaje);
                    }

                    @Override
                    public void onSmsErrorEnvio(String mensaje) {
                        Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSmsProgramadoExito(String mensaje) {
                        Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSmsErrorProgramado(String mensaje) {
                        Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_SHORT).show();

                    }
                };

                dialog.setOnSmsEnviadoListener(listener);
                dialog.show(getSupportFragmentManager(), "EnviarSMSDialog");
            }
        };
    }

    private void mostrarDialogExitoSMS(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Mensaje Enviado!")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void getContactoSeleccionado() {
        Bundle bundle = getIntent().getExtras();
        String nombreContacto = bundle.getString(CompartirListaFragment.NOMBRE_CONTACTO);
        String telefonoContacto = bundle.getString(CompartirListaFragment.TELEFONO_CONTACTO);
        int idContacto = bundle.getInt(CompartirListaFragment.ID_CONTACTO);
        contactoSeleccionado = new Contacto(idContacto, nombreContacto, telefonoContacto, null);
    }

    private void getListaCompra() {
        new Thread(() -> {
            List<ListaCompra> listasRecuperadas = repo.getListas();

            // Recuperar contenido listas
            for (ListaCompra lista : listasRecuperadas) {
                List<ItemLista> detallesLista = repo.getDetalleLista(lista.getId());
                lista.setItems(detallesLista);
            }

            // Actualizar el LiveData en el hilo principal
            runOnUiThread(() -> viewModel.setListasCompra(listasRecuperadas));
        }).start();
    }

    private void gestionarToolbar() {
        // La toolbar con el título
        setSupportActionBar(binding.barraCompartirlista.toolbar);
        // Habilitar la flecha de retroceso (Up button)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Configurar el titulo
        getSupportActionBar().setTitle(getString(R.string.seleccion_lista_compartir));
    }

    // Control de las opciones de la toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Si el usuario hace clic en la flecha de retroceso
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}