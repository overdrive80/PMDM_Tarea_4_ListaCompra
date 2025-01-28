package edu.pmdm.listascompra.ui.compartir_lista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.basedatos.ConsultarContactos;
import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.FragmentCompartirListaBinding;
import edu.pmdm.listascompra.modelo.Contacto;
import edu.pmdm.listascompra.recycler_views.ContactosAdapter;

public class CompartirListaFragment extends Fragment {

    private FragmentCompartirListaBinding binding;
    private RepoListasCompras repo;
    private List<Contacto> listaContactos = new ArrayList<>();
    private RecyclerView rvContactos;
    private ContactosAdapter adaptador;
    private static final String[] PERMISOS_COMPARTIR = new String[]{
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.POST_NOTIFICATIONS
    };
    public static final String ID_CONTACTO = "idContacto";
    public static final String NOMBRE_CONTACTO = "nombreContacto";
    public static final String TELEFONO_CONTACTO = "telefonoContacto";

    private boolean modoDegradadoCompartir = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCompartirListaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CompartirListaViewModel viewModel = new ViewModelProvider(this).get(CompartirListaViewModel.class);

        // Al iniciar el fragment solicitamos los permisos
        solicitarPermisos();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias al RecyclerView
        rvContactos = view.findViewById(R.id.rvContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configuracion del adaptador
        adaptador = new ContactosAdapter(listaContactos, getContext());
        ContactosAdapter.onContactoLongClickListener listener = new ContactosAdapter.onContactoLongClickListener() {
            @Override
            public void onContactoLongClick(int idItem, Contacto contacto) {
                if (modoDegradadoCompartir) {
                    // Mostrar mensaje indicando que la funcionalidad está deshabilitada
                    Snackbar.make(binding.getRoot(), "Esta acción no está disponible en modo degradado.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("idItem", idItem);
                bundle.putInt(ID_CONTACTO, contacto.getId());
                bundle.putString(NOMBRE_CONTACTO, contacto.getNombre());
                bundle.putString(TELEFONO_CONTACTO, contacto.getTelefono());

                Intent intent = new Intent(getContext(), CompartirListaActivity.class);
                intent.putExtras(bundle);
                startActivity(intent, bundle);

            }
        };

        // Establecemos el listener en el adaptador para controlar el clic largo
        adaptador.setOnLongClickListener(listener);

        //Establecemos el adaptador
        rvContactos.setAdapter(adaptador);

        // Notificar al adaptador que los datos han cambiado
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Invocar permisosLauncher
    private void solicitarPermisos() {

        permisosLauncher.launch(PERMISOS_COMPARTIR);

    }

    // Usando la API ActivityResult
    private ActivityResultLauncher<String[]> permisosLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> isGranted) {

                            boolean todosPermisos = true; // Bandera para controlar el modo degradado

                            for (Map.Entry<String, Boolean> entry : isGranted.entrySet()) {
                                String permission = entry.getKey();
                                boolean granted = entry.getValue();

                                if (granted) {
                                    // El permiso ha sido concedido
                                    if (permission.equals(android.Manifest.permission.READ_CONTACTS)) {
                                        obtenerContactos();
                                    }
                                } else {
                                    // Si algún permiso fue denegado
                                    Snackbar.make(binding.getRoot(), "Permiso denegado: " + permission, Toast.LENGTH_SHORT).show();
                                    todosPermisos = false;
                                }
                            }

                            modoDegradadoCompartir = !todosPermisos;
                            if (modoDegradadoCompartir) {
                                Snackbar.make(binding.getRoot(), "La opción de compartir listas no está disponible" +
                                        " hasta que se concedan los permisos necesarios.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


    private void obtenerContactos() {
        if (modoDegradadoCompartir) {
            return; // No hacer nada si estamos en modo degradado
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Obtener los contactos en un hilo secundario
                List<Contacto> nuevosContactos = ConsultarContactos.getListaContactos(requireContext());

                // Las notificaciones al adaptador se hacen en el hilo principal
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaContactos.addAll(nuevosContactos);

                        if (adaptador != null) {
                            adaptador.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
}
