package edu.pmdm.listascompra.ui.consultar_listas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.FragmentConsultarListasBinding;
import edu.pmdm.listascompra.modelo.ListaCompra;
import edu.pmdm.listascompra.recycler_views.TodasListasAdapter;
import edu.pmdm.listascompra.recycler_views.TodasListasViewHolder;
import edu.pmdm.listascompra.ui.nueva_lista.NuevaListaActivity;

public class ConsultarListasFragment extends Fragment {

    // Referencia ViewBinding
    private FragmentConsultarListasBinding binding;
    private RecyclerView rvListasCompra;
    private RepoListasCompras repo;
    private List<ListaCompra> listas = new ArrayList<>();
    private TodasListasAdapter adapter;
    private ConsultarListasViewModel viewModel;

    // Sobreescribir métodos
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instancia el ViewModel
        viewModel = new ViewModelProvider(this).get(ConsultarListasViewModel.class);
        repo = new RepoListasCompras(getContext());

        // Cargamos la lista de datos desde una BBDD y la vinculamos al ViewModel
        new Thread(() -> {
            List<ListaCompra> listasRecuperadas = repo.getListas();
            viewModel.setListasCompra(listasRecuperadas); // Actualiza LiveData en el hilo principal
        }).start();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflar el layout y referenciarlo con el ViewBinding
        binding = FragmentConsultarListasBinding.inflate(inflater, container, false);

        // Obtener elemento raiz
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener el RecyclerView
        rvListasCompra = binding.rvListasCompra;

        // Establecer el adaptador
        Log.i("contexto", getContext().toString());
        adapter = new TodasListasAdapter(getContext(), listas);

        //Implementamos la interfaz de comunicación
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TodasListasViewHolder.OnCambiarNombreListener> future = executor.submit(() -> getImplOnCambiarNombreListener());

        try {
            // Establecemos el listener de cambiar nombre en el adaptador
            adapter.setOnCambiarNombreListener(future.get());
        } catch (ExecutionException e) {
            Log.e("ConsultarListasFragment", "Error al obtener el OnCambiarNombreListener", e);
        } catch (InterruptedException e) {
            Log.e("ConsultarListasFragment", "Error al obtener el OnCambiarNombreListener", e);
        }

        adapter.setOnMenuListasListener(new TodasListasViewHolder.OnMenuListasListener() {
            // La posición del adapter coincidirá con la del elemento en la lista
            @Override
            public void onBotonEditar(int idLista, int posicion) {
                String nombreLista = viewModel.getListasCompra().getValue().get(posicion).getNombre();

                Intent intent = new Intent(getContext(), NuevaListaActivity.class);
                intent.putExtra("idLista", idLista);
                intent.putExtra("nombreLista", nombreLista);
                startActivity(intent);
            }

            @Override
            public void onBotonBorrar(int idLista, int posicion) {
                int filas = repo.eliminarLista(idLista);

                if (filas < 0) {
                    Snackbar.make(getView(), "Error al eliminar la lista", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Elimina el elemento de la lista del adaptador
                    List<ListaCompra> listaActual = viewModel.getListasCompra().getValue();
                    listaActual.remove(posicion);

                    // Actualiza la lista en el ViewModel
                    viewModel.setListasCompra(listaActual);

                    // Notifica al adaptador
                    adapter.notifyItemRemoved(posicion);

                    Snackbar.make(getView(), "Lista eliminada", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Establecemos el adaptador en el recyclerview
        rvListasCompra.setAdapter(adapter);

        // Configuramos el observer sobre la lista vinculada al RecyclerView.
        viewModel.getListasCompra().observe(getViewLifecycleOwner(), new Observer<List<ListaCompra>>() {
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

    }

    private TodasListasViewHolder.OnCambiarNombreListener getImplOnCambiarNombreListener() {

        TodasListasViewHolder.OnCambiarNombreListener
                listener =
                new TodasListasViewHolder.OnCambiarNombreListener() {

                    @Override
                    public boolean esCambioCorrecto(int idLista, String nuevoNombre) {
                        boolean existeNombre = repo.existeNombreLista(nuevoNombre);

                        if (existeNombre) {
                            Snackbar.make(getView(), "Ya existe una lista con ese nombre", Snackbar.LENGTH_SHORT).show();
                            return false;
                        }

                        int i = repo.cambiarNombreLista(idLista, nuevoNombre);

                        if (i < 0) {
                            Snackbar.make(getView(), "Error al cambiar el nombre de la lista", Snackbar.LENGTH_SHORT).show();
                            return false;
                        }

                        return true;
                    }
                };

        return listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}