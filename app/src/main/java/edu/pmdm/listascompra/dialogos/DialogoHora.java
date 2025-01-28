package edu.pmdm.listascompra.dialogos;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DialogoHora extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    OnHoraSeleccionada listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR);
        int minutos = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hora, minutos, true); // true formato 24H
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        GregorianCalendar g = new GregorianCalendar();
        g.set(Calendar.HOUR_OF_DAY, hourOfDay); // 24h, para 12h usar g.set(Calendar.HOUR,hourOfDay);
        g.set(Calendar.MINUTE, minute);
        listener.onResultadoHora(g);
    }

    public interface OnHoraSeleccionada {
        public void onResultadoHora(GregorianCalendar hora);
    }

    public void setOnHoraSeleccionada(OnHoraSeleccionada listener) {
        this.listener = listener;
    }
}

