package edu.pmdm.listascompra.gestores;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Info {

    public static void mostrarNombre(Context context, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup myViewGroup = (ViewGroup) view;
            int viewId = myViewGroup.getId();

            if (viewId != View.NO_ID) { // Verifica si la vista tiene un id asignado
                String viewName = context.getResources().getResourceEntryName(viewId);
                Log.i("NombreClase", "getView: ViewGroup ID = " + viewId +
                        ", Name = " + viewName +
                        ", Class Name = " + myViewGroup.getClass().getSimpleName());
            } else {
                Log.i("nombreclase", "getView: ViewGroup has no ID assigned.");
            }
        }
    }
}
