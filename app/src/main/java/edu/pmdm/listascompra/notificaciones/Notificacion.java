package edu.pmdm.listascompra.notificaciones;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.pmdm.listascompra.R;

public class Notificacion {
    public static final int NOTIFICATION_ID = 1;
    public static final int PERMISSION_REQUEST_CODE = 44;

    public static void mostrarNotificacion(Context context, String title, String message) {
        if (context == null) {
            Log.e("Notificacion", "Contexto nulo");
            return;
        }

        CanalNotificacionSMS.crearCanalNotificacion(context);

        //Creamos el builder de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                CanalNotificacionSMS.CHANNEL_ID);

        // Configuración del builder
        builder.setContentTitle(title)
                .setContentText(message)
                .setSubText("SMS enviado")
                .setSmallIcon(R.drawable.baseline_sms_24)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.mipmap.ic_nav_lista))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        ;

        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context); //El contexto sería la actividad que se ejecuta

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            return;
        }

        notificationManager.notify("A", NOTIFICATION_ID, notification);
    }
}

