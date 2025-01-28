package edu.pmdm.listascompra.gestores;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestorPermisos {

    private final Context context;
    private final Activity activity;
    private final Fragment fragment;
    private PermisosCallback callback;

    private ActivityResultLauncher<String[]> permisosLauncher;

    // Constructor para Activity
    public GestorPermisos(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.fragment = null;
        setupLauncher();
    }

    // Constructor para Fragment
    public GestorPermisos(Fragment fragment) {
        this.context = fragment.requireContext().getApplicationContext();
        this.activity = fragment.requireActivity();
        this.fragment = fragment;
        setupLauncher();
    }

    public interface PermisosCallback {
        void onPermisosConcedidos();

        void onPermisosDenegados(String[] permisosDenegados);
    }

    // Configura el launcher para solicitar permisos
    private void setupLauncher() {
        if (fragment != null) {
            permisosLauncher = fragment.registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            GestorPermisos.this.manejarResultadosPermisos(result);
                        }
                    }
            );
        } else if (activity != null) {
            // En el caso de Activity, usamos el ActivityResultRegistry
            permisosLauncher = ((AppCompatActivity) activity).registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> permisosResultados) {
                            GestorPermisos.this.manejarResultadosPermisos(permisosResultados);
                        }
                    });
        }
    }

    /**
     * Comprueba y solicita permisos si es necesario.
     *
     * @param permissions Permisos que se quieren solicitar.
     * @param callback    Callback para manejar los resultados.
     */
    public void comprobarResultadosPermisos(String[] permissions, @NonNull PermisosCallback callback) {
        this.callback = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permisosDenegados = getPermisosDenegados(permissions);

            if (permisosDenegados.length > 0) {
                permisosLauncher.launch(permisosDenegados);
            } else {
                callback.onPermisosConcedidos(); // Todos los permisos ya están concedidos
            }
        } else {
            callback.onPermisosConcedidos(); // Los permisos se conceden automáticamente en versiones anteriores
        }
    }

    /**
     * Maneja los resultados de la solicitud de permisos.
     *
     * @param permisosResultados Mapa de permisos y su estado.
     */
    private void manejarResultadosPermisos(@NonNull Map<String, Boolean> permisosResultados) {
        List<String> permisosDenegados = new ArrayList<>();
        for (String permiso : permisosResultados.keySet()) {
            if (!permisosResultados.get(permiso)) {
                permisosDenegados.add(permiso);
            }
        }

        if (permisosDenegados.isEmpty()) {
            callback.onPermisosConcedidos();
        } else {
            callback.onPermisosDenegados(permisosDenegados.toArray(new String[0]));
        }
    }

    /**
     * Devuelve los permisos que aún no han sido concedidos.
     *
     * @param permissions Lista de permisos.
     * @return Array de permisos denegados.
     */
    private String[] getPermisosDenegados(String[] permissions) {
        List<String> permisosDenegados = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permisosDenegados.add(permission);
            }
        }
        return permisosDenegados.toArray(new String[0]);
    }
}
