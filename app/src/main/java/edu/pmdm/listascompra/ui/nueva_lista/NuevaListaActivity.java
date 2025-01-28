package edu.pmdm.listascompra.ui.nueva_lista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.ActivityNuevaListaBinding;
import edu.pmdm.listascompra.dialogos.DialogFragmentCantidad;
import edu.pmdm.listascompra.dialogos.DialogFragmentProductos;
import edu.pmdm.listascompra.modelo.ItemLista;
import edu.pmdm.listascompra.modelo.Producto;
import edu.pmdm.listascompra.recycler_views.ListaCompraAdapter;
import edu.pmdm.listascompra.recycler_views.ProductosAdapter;

public class NuevaListaActivity extends AppCompatActivity {

    private String nombreLista;
    private RecyclerView rvListaCompra;
    private List<Producto> productos = new ArrayList<>();
    private List<ItemLista> listaCompra = new ArrayList<>();
    private List<ItemLista> listaOriginal = new ArrayList<>();
    private Set<Integer> productosSeleccionados = new HashSet<>(); // Para mantener el estado de selección
    private RepoListasCompras repo = new RepoListasCompras(this);
    private FloatingActionButton fab;
    private ListaCompraAdapter listaCompraAdapter;
    private ActivityNuevaListaBinding binding;
    private boolean modoEdicion = false;
    private int idLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el binding
        binding = ActivityNuevaListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Analizamos los intents
        Intent intentRecibido = getIntent();
        nombreLista = intentRecibido.getStringExtra("nombreLista");

        // Definimos el Reciclerview de la lista de la compra y lo asignamos al adaptador
        rvListaCompra = findViewById(R.id.rvListaCompra);
        rvListaCompra.setLayoutManager(new LinearLayoutManager(this));

        // Mostramos un dialogo para modificar las unidades y la cantidad del producto
        ListaCompraAdapter.onItemListaClickListener listener = new ListaCompraAdapter.onItemListaClickListener() {
            @Override
            public void onItemListaClick(int posicion) {
                // Obtenemos el item con la posición del adapter
                ItemLista item = listaCompra.get(posicion);

                //Invocamos el dialogo para modificar la cantidad y unidades
                mostrarDialogCantidad(item, posicion);
            }
        };
        listaCompraAdapter = new ListaCompraAdapter(this, listaCompra, listener);
        rvListaCompra.setAdapter(listaCompraAdapter);

        // Modo edición: Carga de datos
        modoEdicion = intentRecibido.hasExtra("idLista");
        if (modoEdicion) {
            idLista = intentRecibido.getIntExtra("idLista", -1);

            cargarDetallesLista(idLista);

        }

        // Cargar los productos solo una vez al crear la actividad
        cargarProductos();

        // Recuperamos el boton flotante
        fab = findViewById(R.id.fabProductos);
        fab.setOnClickListener(v -> {
            // Cargar los productos de forma asincrónica
            mostrarDialogoProductos();
        });

        // La toolbar con el título
        setSupportActionBar(binding.barraNuevalista.toolbar);
        // Habilitar la flecha de retroceso (Up button)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Configurar el titulo
        getSupportActionBar().setTitle(getString(R.string.nombre_nueva_lista, nombreLista));

        // Registrar la captura del botón retroceso de android
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void cargarProductos() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            productos = repo.getProductos(); // Cargar los productos solo una vez

            // Una vez que los productos se cargan, mostrar el diálogo en el hilo principal
            runOnUiThread(() -> {
                // No hace falta cargar los productos cada vez que se abre el diálogo, solo mostrarlos
            });
        });
    }

    private void cargarDetallesLista(int idLista) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Recuperamos la lista de la base de datos
            List<ItemLista> listaDetallesBBDD = repo.getDetalleLista(idLista);
            Log.d("NuevaListaActivity", "Detalles de la lista obtenida de la BD: " + listaDetallesBBDD.size());

            // Crear una copia profunda de la lista obtenida de la base de datos para listaOriginal
            List<ItemLista> copiaListaOriginal = new ArrayList<>();
            for (ItemLista item : listaDetallesBBDD) {
                copiaListaOriginal.add(new ItemLista(item.getProducto(), item.getCantidad(), item.getUnidad()));
            }

            // Actualizar las listas en el hilo principal
            runOnUiThread(() -> {
                // Actualizar listaOriginal y listaCompra con los datos correctos
                listaOriginal.clear();
                listaOriginal.addAll(copiaListaOriginal);

                listaCompra.clear();
                listaCompra.addAll(listaDetallesBBDD);

                // Notificar al adaptador de los cambios
                listaCompraAdapter.notifyDataSetChanged();
            });
        });
    }


    private void mostrarDialogoProductos() {
        productosSeleccionados.clear();
        for (ItemLista item : listaCompra) {
            productosSeleccionados.add(item.getProducto().getId());
        }

        DialogFragmentProductos dialogFragment = new DialogFragmentProductos(productosSeleccionados, productos);

        ProductosAdapter.OnProductoSeleccionadoListener listener = (producto, seleccionado) -> {
            if (seleccionado) {
                productosSeleccionados.add(producto.getId());
                ItemLista itemLista = new ItemLista(producto, 1d, "Unidad");
                listaCompra.add(itemLista);
            } else {
                productosSeleccionados.remove(producto.getId());
                listaCompra.removeIf(item -> item.getProducto().getId() == producto.getId());
            }

            listaCompraAdapter.notifyDataSetChanged();
        };

        dialogFragment.setOnProductoSeleccionadoListener(listener);
        dialogFragment.show(getSupportFragmentManager(), "ProductosDialogFragment");
    }

    // Control de las opciones de la toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            guardarYsalir();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void guardarYsalir() {
        if (!modoEdicion) {
            guardarLista();
        } else {
            modificarLista();
        }

        finish();
    }

    private void modificarLista() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Set<Integer> idsItemsListaOriginal = new HashSet<>();
            Set<Integer> idsItemsNuevaLista = new HashSet<>();

            // Crear conjuntos de IDs para comparación
            for (ItemLista item : listaOriginal) {
                idsItemsListaOriginal.add(item.getProducto().getId());
            }
            for (ItemLista item : listaCompra) {
                idsItemsNuevaLista.add(item.getProducto().getId());
            }

            // 1. Eliminar productos que ya no están en la lista actual
            for (ItemLista itemOriginal : new ArrayList<>(listaOriginal)) {
                if (!idsItemsNuevaLista.contains(itemOriginal.getProducto().getId())) {
                    repo.eliminarDetalleLista(idLista, itemOriginal.getProducto().getId());
                }
            }

            // 2. Modificar productos existentes
            for (ItemLista itemModificado : listaCompra) {
                for (ItemLista itemOriginal : new ArrayList<>(listaOriginal)) {
                    if (itemModificado.getProducto().getId() == itemOriginal.getProducto().getId()
                            && !itemModificado.equals(itemOriginal)) {
                        repo.modificarDetalleLista(idLista, itemModificado.getProducto().getId(),
                                itemModificado.getCantidad(), itemModificado.getUnidad());
                    }
                }
            }

            // 3. Insertar nuevos productos que no estaban en la lista original
            for (ItemLista itemModificado : listaCompra) {
                if (!idsItemsListaOriginal.contains(itemModificado.getProducto().getId())) {
                    repo.insertarDetalleLista(idLista, itemModificado.getProducto().getId(),
                            itemModificado.getCantidad(), itemModificado.getUnidad());
                }
            }
        });

        // Apagar el executor después de completar la tarea
        executor.shutdown();

    }

    private void guardarLista() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Si la lista está vacía, creamos el nombre de la lista
            long idLista = repo.insertarLista(nombreLista);

            if (!listaCompra.isEmpty()) {
                for (ItemLista item : listaCompra) {
                    Producto producto = item.getProducto();
                    repo.insertarDetalleLista(idLista, producto.getId(), item.getCantidad(), item.getUnidad());
                }
            }
        });
    }

    public void mostrarDialogCantidad(ItemLista item, int posicion) {
        DialogFragmentCantidad.onItemChangedListener listener = new DialogFragmentCantidad.onItemChangedListener() {
            @Override
            public void onItemChanged(ItemLista nuevoItem, int posicion) {
                listaCompra.set(posicion, nuevoItem);
                listaCompraAdapter.notifyItemChanged(posicion);
            }
        };

        DialogFragmentCantidad dialog = new DialogFragmentCantidad(item, posicion, listener);
        dialog.show(getSupportFragmentManager(), "DialogFragmentCantidad");
    }

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            guardarYsalir();
        }
    };
}
