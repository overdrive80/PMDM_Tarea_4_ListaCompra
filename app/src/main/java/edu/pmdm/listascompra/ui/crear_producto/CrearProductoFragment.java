package edu.pmdm.listascompra.ui.crear_producto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.FragmentCrearProductoBinding;
import edu.pmdm.listascompra.gestores.GestorImagenes;
import edu.pmdm.listascompra.gestores.GestorNumeros;
import edu.pmdm.listascompra.modelo.Producto;

public class CrearProductoFragment extends Fragment {

    private CrearProductoViewModel viewModel;
    private FragmentCrearProductoBinding binding;
    private Bitmap imagenSeleccionada;
    private ActivityResultLauncher<Intent> eleccionImagen; // Definimos el lanzador para la selección de imagen
    private RepoListasCompras repo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflamos el layout del fragmento utilizando ViewBinding
        binding = FragmentCrearProductoBinding.inflate(inflater, container, false);

        // Obtenemos la vista raíz del fragmento
        View root = binding.getRoot();

        // Inicializamos el ViewModel asociado al fragmento
        viewModel = new ViewModelProvider(this).get(CrearProductoViewModel.class);

        // Inicializamos el repositorio de acceso a la base de datos
        repo = new RepoListasCompras(requireContext());

        // Devolvemos la vista raíz para que el fragmento se muestre en la interfaz de usuario
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configuramos un listener en el ImageButton (botón de imagen)
        // para que abra la galería cuando el usuario haga clic en él
        binding.ibImgProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen(); // Método para iniciar el proceso de selección de imagen
            }
        });

        // Configuramos el lanzador para manejar los resultados de la actividad de selección de imagen
        eleccionImagen = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Verificamos que el resultado sea exitoso y que contenga datos
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri uriImagen = result.getData().getData(); // Obtenemos el URI de la imagen seleccionada
                            //Snackbar.make(getView().getRootView(), "Imagen seleccionada", Snackbar.LENGTH_SHORT).show();

                            try {
                                imagenSeleccionada = GestorImagenes.obtenerImagenDesdeUri(getActivity(), uriImagen);

                                binding.ibImgProducto.setImageBitmap(imagenSeleccionada);
                            } catch (Exception e) {
                                e.printStackTrace(); // Registramos errores en caso de que ocurra un problema al procesar la imagen
                            }
                        }
                    }
                }
        );

        // Configuramos el listener del botón para crear producto
        binding.btnGuardarProd.setOnClickListener(v -> guardarProducto(v));

    }

    private void guardarProducto(View view) {
        // Recuperamos los los datos de los campos
        String nombre = binding.etNombre.getText().toString();
        String descripcion = binding.etDescripcion.getText().toString();
        String precio = binding.etPrecio.getText().toString();

        // Comprobar que estan los campos obligatorios.
        // Solo consideraremos obligatorios el nombre y el precio.
        if (nombre.isEmpty() || precio.isEmpty()) {
            Snackbar.make(view, "Nombre y precio son campos obligatorios", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Si los campos obligatorios tienen valor.
        // Comprobamos que no exista el producto con el mismo nombre
        boolean existe = repo.existeProductoNombre(nombre);

        if (existe) {
            Snackbar.make(view, "Ya existe un producto con ese nombre", Snackbar.LENGTH_SHORT).show();
            return;
        }

        //Guardamos el producto
        byte[] imagen = GestorImagenes.convertBitmapToBytes(imagenSeleccionada);
        double precioDouble = GestorNumeros.stringADouble(precio);
        Producto producto = new Producto(nombre, descripcion, precioDouble, imagen);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            long id = repo.insertarProducto(producto); // Operación en hilo secundario

            // Las operaciones en la IU deben hacerse en el hilo principal
            requireActivity().runOnUiThread(() -> {
                if (id > 0) {
                    Snackbar.make(view, "Producto creado correctamente", Snackbar.LENGTH_SHORT).show();
                    resetearCampos();
                } else {
                    Snackbar.make(view, "Error al crear el producto", Snackbar.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Método para abrir la galería del dispositivo y permitir al usuario seleccionar una imagen
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        eleccionImagen.launch(intent); // Iniciamos la actividad de selección de imagen
    }

    private void resetearCampos() {
        binding.etNombre.setText("");
        binding.etDescripcion.setText("");
        binding.etPrecio.setText("");
        binding.ibImgProducto.setImageResource(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Liberamos el binding para evitar pérdidas de memoria
        binding = null;
    }
}
