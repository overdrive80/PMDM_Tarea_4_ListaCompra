package edu.pmdm.listascompra.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class CanalNotificacionSMS {

    public static final String CHANNEL_ID = "ch01_SMS";
    public static final String CHANNEL_NAME = "CanalSMS";

    public static void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creamos un canal especificando ID, nombre e importancia
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            // Añadimos una descripción del canal
            channel.setDescription("Canal de notificaciones SMS");

            NotificationManager notificationManager = context.getSystemService(
                    NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
