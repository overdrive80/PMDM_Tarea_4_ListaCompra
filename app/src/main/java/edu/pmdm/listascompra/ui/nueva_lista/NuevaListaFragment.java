package edu.pmdm.listascompra.ui.nueva_lista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.FragmentNuevaListaBinding;

public class NuevaListaFragment extends Fragment {

    //1. Referencias vistas
    private FragmentNuevaListaBinding binding;
    private EditText etNombreLista;
    private Button btnCrearLista;
    private NuevaListaViewModel viewModel;
    private RepoListasCompras repo;
    private String nombreLista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //2. Inflar el layout y referenciarlo con el ViewBinding
        binding = FragmentNuevaListaBinding.inflate(inflater, container, false);

        //3. Obtener elemento raiz
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el repo correctamente
        repo = new RepoListasCompras(getContext());  // Asegúrate de pasar el contexto

        //4. Obtener vistas
        etNombreLista = binding.etNombreNuevaLista;
        btnCrearLista = binding.btnIniciarCreacion;

        //5. Obtener el ViewModel
        viewModel = new ViewModelProvider(this).get(NuevaListaViewModel.class);

        //6. Observar cambios en el ViewModel
        viewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String text) {
                etNombreLista.setText(text);
            }
        });

        //7. Asignar eventos
        btnCrearLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreLista = etNombreLista.getText().toString();
                viewModel.setText(nombreLista);

                comprobarNombreLista(existe -> {
                    if (existe) {
                        etNombreLista.setError("Ese nombre de lista ya existe, escoge otro");
                        //Toast.makeText(getActivity(), "Ese nombre de lista ya existe, escoge otro", Toast.LENGTH_LONG).show();
                    } else {
                        crearLista(v);
                    }
                });
            }
        });
    }

    private void comprobarNombreLista(Callback<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean existe = repo.existeNombreLista(nombreLista);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(existe);
                    }
                });
            }
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    private void crearLista(View view) {

        // Si el nombre definido es vacío
        if (nombreLista.isEmpty()) {
            etNombreLista.setError("El nombre de la lista no puede estar vacío");
        } else {
            // Actualizar el ViewModel con el texto del EditText
            viewModel.setText(nombreLista);

            // Definir intent explicito para abrir la actividad
            Intent intent = new Intent(view.getContext(), NuevaListaActivity.class);
            intent.putExtra("nombreLista", nombreLista);
            startActivity(intent);

        }
    }

    @Override
    public void onStop() {

        super.onStop();
        viewModel.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}