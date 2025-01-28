package edu.pmdm.listascompra.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import edu.pmdm.listascompra.gestores.GestorSMS;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String TELEFONO = "telefono";
    public static final String MENSAJE = "mensaje";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SmsBroadcastReceiver", "Alarma disparada!");

        String telefono = intent.getStringExtra(TELEFONO);
        String mensaje = intent.getStringExtra(MENSAJE);

        // Enviar el SMS
        // Solo mandamos 1 SMS (160 caracteres)
        String mensajeCorto = GestorSMS.dividirMensaje(mensaje).get(0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(telefono, null, mensajeCorto, null, null);

        // Mostrar notificación
        Notificacion.mostrarNotificacion(context, telefono, mensaje);
    }
}
