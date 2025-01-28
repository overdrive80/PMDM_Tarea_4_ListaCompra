package edu.pmdm.listascompra.dialogos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.modelo.ItemLista;

public class DialogFragmentCantidad extends DialogFragment {
    private EditText etCantidad, etUnidad;
    private ImageButton ibMenos, ibMas;
    private ChipGroup chipGroup;
    private Chip cpLitros, cpKilos, cpMili, cpGramos;
    private View dialogView;
    private ItemLista item;
    private int posicion;
    private onItemChangedListener listener;

    public DialogFragmentCantidad(ItemLista item, int posicion, onItemChangedListener listener) {
        this.item = item;
        this.posicion = posicion;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflar el layout personalizado para el diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.alertdialog_cantidad, null);

        // Configurar el layout en el AlertDialog
        builder.setView(dialogView);

        // Obtener referencias a las vistas dentro del layout
        setupReferencias();

        // Configurar eventos o lógica en las vistas
        setupListeners();

        // Establecer los valores iniciales en los EditText
        etCantidad.setText(String.valueOf(item.getCantidad()));
        etUnidad.setText(item.getUnidad());

        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,  // Ancho del diálogo
                ViewGroup.LayoutParams.WRAP_CONTENT   // Alto del diálogo
        );

        return dialog; // Retorna el diálogo
    }

    private void setupReferencias() {
        etCantidad = dialogView.findViewById(R.id.etCantidad);
        etUnidad = dialogView.findViewById(R.id.etUnidad);
        ibMenos = dialogView.findViewById(R.id.ibMenos);
        ibMas = dialogView.findViewById(R.id.ibMas);
        chipGroup = dialogView.findViewById(R.id.chipGroup);
        cpLitros = dialogView.findViewById(R.id.cpLitros);
        cpKilos = dialogView.findViewById(R.id.cpKilos);
        cpMili = dialogView.findViewById(R.id.cpMili);
        cpGramos = dialogView.findViewById(R.id.cpGramos);
    }

    private void setupListeners() {
        // Reducir cantidad
        ibMenos.setOnClickListener(v -> disminuirCantidad());
        // Lógica para aumentar cantidad
        ibMas.setOnClickListener(v -> aumentarCantidad());

        // Establecer el onClickListener para cada chip
        cpLitros.setOnClickListener(v -> updateUnidad(cpLitros));
        cpKilos.setOnClickListener(v -> updateUnidad(cpKilos));
        cpMili.setOnClickListener(v -> updateUnidad(cpMili));
        cpGramos.setOnClickListener(v -> updateUnidad(cpGramos));

    }

    private void updateUnidad(Chip chip) {
        // Actualiza el EditText con el texto del chip seleccionado
        String chipText = chip.getText().toString();
        etUnidad.setText(chipText);
    }

    private void disminuirCantidad() {
        // Ejemplo de lógica para disminuir cantidad
        String cantidadStr = etCantidad.getText().toString();
        if (!cantidadStr.isEmpty()) {
            double cantidad = Double.parseDouble(cantidadStr);
            if (cantidad > 1.0) {
                etCantidad.setText(String.valueOf(cantidad - 1.0));
            }
        }
    }

    private void aumentarCantidad() {
        // Ejemplo de lógica para aumentar cantidad
        String cantidadStr = etCantidad.getText().toString();
        double cantidad = cantidadStr.isEmpty() ? 0 : Double.parseDouble(cantidadStr);
        etCantidad.setText(String.valueOf(cantidad + 1.0));
    }

    public interface onItemChangedListener {
        public void onItemChanged(ItemLista item, int posicion);
    }

    // Al cerrar el dialogo sea cual sea el motivo
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        // Actualizar la cantidad y unidad en el item
        String sCantidad = etCantidad.getText().toString();
        String sUnidad = etUnidad.getText().toString();

        item.setCantidad(Double.parseDouble(sCantidad));
        item.setUnidad(sUnidad);

        //Devolvemos el item modificado y la posición del adapter
        listener.onItemChanged(item, posicion);

    }
}
