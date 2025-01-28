package edu.pmdm.listascompra.dialogos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DialogoFecha extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    OnFechaSeleccionada listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, anio, mes, dia);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar g = new GregorianCalendar(year, month, dayOfMonth);
        listener.onResultadoFecha(g);
    }

    public void setOnFechaSeleccionada(OnFechaSeleccionada listener) {
        this.listener = listener;
    }

    public interface OnFechaSeleccionada {
        public void onResultadoFecha(GregorianCalendar fecha);
    }
}

