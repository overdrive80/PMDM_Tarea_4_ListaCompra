package edu.pmdm.listascompra;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.listascompra.basedatos.ConexionBBDD;
import edu.pmdm.listascompra.basedatos.RepoListasCompras;
import edu.pmdm.listascompra.databinding.MainActivityBinding;
import edu.pmdm.listascompra.datosexternos.DescargadorProductos;
import edu.pmdm.listascompra.modelo.Producto;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MainActivityBinding binding;
    private ConexionBBDD conexion;
    private RepoListasCompras repo;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bloqueamos la orientación vertical setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflamos el ViewBinding y lo establecemos con contenido
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuramos la interfaz
        configurarInterfaz();

        // Abrimos la instancia de la base de datos
        conexion = ConexionBBDD.getInstance(this);

        //***** elimina base de datos en fase pruebas ***/
        //conexion.eliminarBaseDeDatos();

        // Esto crea la base de datos si no existe
        conexion.getWritableDatabase();

        // Comprobamos si la tabla productos está vacía
        repo = new RepoListasCompras(this);
        cargarProductos();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void cargarProductos() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Comprobamos si la tabla productos está vacía
                if (!repo.hayProductos()) {
                    // Descargamos los productos
                    DescargadorProductos descargador = new DescargadorProductos();
                    List<Producto> productos = descargador.getProductos();

                    for (int i = 0; i < productos.size(); i++) {
                        Producto producto = productos.get(i);
                        repo.insertarProducto(producto);
                    }

                    // Mostrar el Toast en el hilo principal
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, "Productos descargados", Toast.LENGTH_SHORT).show();
                            View rootView = findViewById(android.R.id.content); // Obtiene la vista raíz
                            Snackbar.make(rootView, "Productos descargados", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void configurarInterfaz() {
        // Añadir soporte para la toolbar
        Toolbar toolbar = binding.appBarMain.barraMain.toolbar;
        setSupportActionBar(toolbar);

        // Configuramos el menú lateral
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Definimos los destinos de navegación
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_nueva_lista, R.id.nav_consultar_listas, R.id.nav_crear_producto, R.id.nav_compartir_lista)
                .setOpenableLayout(drawer)
                .build();

        // Se puede emplear si usamos <fragment> como nav_host_controller
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Modo correcto para obtener el navController usando FragmentContainerView como nav_host_controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        // Configurar el ActionBar con el NavController y
        // AppBarConfiguration para actualizar el título
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Conectar el NavigationView con el NavController del navHostFragment.
        // Indica qué fragmento debe abrir cada item del menú
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        //navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}