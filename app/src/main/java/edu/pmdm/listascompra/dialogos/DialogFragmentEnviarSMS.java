package edu.pmdm.listascompra.dialogos;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import edu.pmdm.listascompra.R;
import edu.pmdm.listascompra.gestores.GestorSMS;
import edu.pmdm.listascompra.modelo.Contacto;
import edu.pmdm.listascompra.modelo.ItemLista;
import edu.pmdm.listascompra.modelo.ListaCompra;
import edu.pmdm.listascompra.notificaciones.SmsBroadcastReceiver;


public class DialogFragmentEnviarSMS extends DialogFragment {

    private static final String ARG_LISTA = "lista";
    private ListaCompra lista;
    private Contacto contacto;
    private CheckBox chkProgramar;
    private EditText etFechaSMS, etHoraSMS;
    private Button btnEnviarSMS;
    private ImageButton ibCalendar, ibHora;
    private OnSmsEnviadoListener listener;
    private String mensajeSMS;
    private Context contexto;
    private GregorianCalendar fechaElegida, horaElegida;
    private View dialogView;
    private AlarmManager alarmManager;
    private boolean alarmaProgramada = false;
    private Calendar fechaProgramacion;
    private PendingIntent pendingIntent;

    public static DialogFragmentEnviarSMS newInstance(Contacto contacto, ListaCompra lista) {
        DialogFragmentEnviarSMS fragment = new DialogFragmentEnviarSMS();

        fragment.setLista(lista);
        fragment.setContacto(contacto);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmaProgramada == false) {
                return;
            }

            // Si tiene permiso para alarmas exactas
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, fechaProgramacion.getTimeInMillis(), pendingIntent);
                listener.onSmsProgramadoExito("Mensaje programado correctamente");
            } else {
                //Si no tiene permisos para alarmas exactas se establece una inexacta
                long windowLength = 1000;  // Duración de la ventana en milisegundos
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, fechaProgramacion.getTimeInMillis(), windowLength, pendingIntent);
                listener.onSmsProgramadoExito("Mensaje programado a hora inexacta.");
            }

            dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        contexto = requireContext();

        // Usamos la clase estatica AlertDialog.Builder para construir el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        // Asignamos el layout
        dialogView = LayoutInflater.from(contexto).inflate(R.layout.alertdialog_enviar_sms, null);
        builder.setView(dialogView);

        // Referencias vistas
        chkProgramar = dialogView.findViewById(R.id.chkProgramar);
        etFechaSMS = dialogView.findViewById(R.id.etFechaSMS);
        etHoraSMS = dialogView.findViewById(R.id.etHoraSMS);
        btnEnviarSMS = dialogView.findViewById(R.id.btnEnviarSMS);
        ibCalendar = dialogView.findViewById(R.id.ibCalendar);
        ibHora = dialogView.findViewById(R.id.ibHora);

        //Configuramos las vistas
        configurarVistas();

        // Definimos título y mensaje
        builder.setTitle(getString(R.string.enviar_sms));

        // Crear el AlertDialog y devolverlo
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    private void configurarVistas() {
        // Valores por defecto
        chkProgramar.setChecked(false);
        etFechaSMS.setEnabled(false);
        etHoraSMS.setEnabled(false);
        ibCalendar.setEnabled(false);
        ibHora.setEnabled(false);

        // Configurar listeners
        configurarCheckBoxProgramar();
        configurarBotonFecha();
        configurarBotonHora();
        configurarBotonEnviar();
    }

    private boolean fechaCorrecta(Calendar fechaProgramacion) {
        long tiempoProgramado = this.fechaProgramacion.getTimeInMillis();
        long tiempoActual = System.currentTimeMillis();

        if (tiempoProgramado <= tiempoActual) {
            return false;
        }

        return true;
    }

    private void programarEnvioSMS() throws SecurityException, Exception {

        alarmaProgramada = true;

        // Recuperamos valores de los campos de fecha y hora
        String sFecha = etFechaSMS.getText().toString();
        String sHora = etHoraSMS.getText().toString();

        if (fechaElegida == null || horaElegida == null || sFecha.isEmpty() || sHora.isEmpty()) {
            Snackbar.make(dialogView, "Debes seleccionar una fecha y una hora", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Recuperamos los valores de configuración del SMS
        String telefono = contacto.getTelefono();
        String mensaje = contruirMensaje();

        // Recuperar la fecha y hora de programación
        int year = fechaElegida.get(GregorianCalendar.YEAR);
        int month = fechaElegida.get(GregorianCalendar.MONTH);
        int day = fechaElegida.get(GregorianCalendar.DAY_OF_MONTH);
        int hour = horaElegida.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = horaElegida.get(GregorianCalendar.MINUTE);

        // 1. CREAR LA FECHA DE LA ALARMA
        fechaProgramacion = new GregorianCalendar(year, month, day, hour, minute, 0);

        if (!fechaCorrecta(fechaProgramacion)) {
            Snackbar.make(dialogView, "La fecha y hora deben ser futuras", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Log.d("ProgramarEnvioSMS", "Fecha programada: " + fechaProgramacion.getTime().toString());

        // 2. CREAR EL PENDINGINTENT
        Intent intent = new Intent(getActivity(), SmsBroadcastReceiver.class);
        intent.putExtra("telefono", telefono);
        intent.putExtra("mensaje", mensaje);

        pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Obtener instancia del AlarmManager
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.d("ProgramarEnvioSMS", "Error al acceder al servicio de alarmas");
            return;
        }

        // 3. ESTABLECEMOS LA ALARMA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, fechaProgramacion.getTimeInMillis(), pendingIntent);
            } else {
                Intent intentAlarm = new Intent();
                intentAlarm.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intentAlarm);
                throw new SecurityException("Permiso NO concedido");
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, fechaProgramacion.getTimeInMillis(), pendingIntent);
        }
    }

    private void enviarSMS(String mensaje) throws Exception {

        // Solo mandamos 1 SMS (160 caracteres)
        String mensajeCorto = GestorSMS.dividirMensaje(mensaje).get(0);

        String telefono = contacto.getTelefono();

        // Verifica si se tiene el permiso antes de intentar enviar el SMS
        if (ContextCompat.checkSelfPermission(contexto, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Permiso SEND_SMS no concedido");
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(telefono, null, mensajeCorto, null, null);

        Log.d("DialogFragmentEnviarSMS", "SMS enviado a: " + telefono);
    }

    private String contruirMensaje() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Lista de compra: ").append(lista.getNombre()).append("\n\n");

        for (ItemLista item : lista.getItems()) {
            mensaje.append("  - ").append(item.getProducto().getNombre()).append(": ")
                    .append(item.getCantidad())
                    .append(" (").append(item.getUnidad()).append(")")
                    .append("\n");
        }

        mensaje.append("\n").append("Enviado a: ")
                .append(contacto.getNombre())
                .append(", con teléfono: ").append(contacto.getTelefono());
        return mensaje.toString();
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public ListaCompra getLista() {
        return lista;
    }

    public void setLista(ListaCompra lista) {
        this.lista = lista;
    }

    //********************************* LISTENERS ***********************************//
    private void configurarCheckBoxProgramar() {
        chkProgramar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ibCalendar.setEnabled(true);
                    ibHora.setEnabled(true);
                    btnEnviarSMS.setText(DialogFragmentEnviarSMS.this.getString(R.string.programar));
                } else {
                    ibCalendar.setEnabled(false);
                    ibHora.setEnabled(false);
                    etFechaSMS.setText("");
                    etHoraSMS.setText("");
                    btnEnviarSMS.setText(DialogFragmentEnviarSMS.this.getString(R.string.enviar_sms));
                }
            }
        });
    }

    private void configurarBotonFecha() {
        ibCalendar.setOnClickListener(v -> {
            DialogoFecha d = new DialogoFecha();
            d.setOnFechaSeleccionada(getImplListenerFecha());

            d.show(getChildFragmentManager(), "Mi diálogo de fecha");
        });
    }

    private void configurarBotonHora() {
        ibHora.setOnClickListener(v -> {

            DialogoHora d = new DialogoHora();
            d.setOnHoraSeleccionada(getImplListenerHora());

            d.show(getChildFragmentManager(), "Mi diálogo de hora");
        });
    }

    private void configurarBotonEnviar() {
        btnEnviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el listener es nulo, no hacemos nada
                if (listener == null) {
                    return;
                }

                // Si el checkbox está marcado, programamos el envío
                if (chkProgramar.isChecked()) {

                    try {
                        programarEnvioSMS();
                        listener.onSmsProgramadoExito("Mensaje programado correctamente");
                        dismiss();
                    } catch (SecurityException e) {
                        //listener.onSmsErrorProgramado("Permiso NO concedido");
                        return;   // salir del método
                    } catch (Exception e) {
                        listener.onSmsErrorProgramado("Error al programar el envío del SMS");
                        Log.e("ProgramarEnvioSMS", e.getMessage());
                        dismiss(); // Cerrar el dialogfragment
                        return;   // salir del método
                    }

                    //Si el checkbox no está marcado, enviamos el SMS
                } else {
                    mensajeSMS = DialogFragmentEnviarSMS.this.contruirMensaje();

                    //Acciones de envio de SMS
                    try {
                        DialogFragmentEnviarSMS.this.enviarSMS(mensajeSMS);

                        listener.onSmsEnviadoExito(mensajeSMS);
                        dismiss();
                    } catch (Exception e) {
                        listener.onSmsErrorEnvio("Error al enviar el SMS");
                        dismiss(); // Cerrar el dialogfragment
                        return;   // salir del método
                    }

                }
            }
        });
    }

    private DialogoHora.OnHoraSeleccionada getImplListenerHora() {
        DialogoHora.OnHoraSeleccionada listener = new DialogoHora.OnHoraSeleccionada() {
            @Override
            public void onResultadoHora(GregorianCalendar hora) {
                //Formatear resultado
                String sHora = String.format(Locale.getDefault(), "%02d", hora.get(GregorianCalendar.HOUR_OF_DAY));
                String sMin = String.format(Locale.getDefault(), "%02d", hora.get(GregorianCalendar.MINUTE));
                String sHoraFormateada = String.format("%s:%s", sHora, sMin);

                etHoraSMS.setText(sHoraFormateada);
                horaElegida = hora;
            }
        };

        return listener;
    }

    private DialogoFecha.OnFechaSeleccionada getImplListenerFecha() {
        DialogoFecha.OnFechaSeleccionada listener = new DialogoFecha.OnFechaSeleccionada() {
            @Override
            public void onResultadoFecha(GregorianCalendar fecha) {
                //Formatear resultado
                String sAnnio = String.format(Locale.getDefault(), "%04d", fecha.get(GregorianCalendar.YEAR));
                String sMes = String.format(Locale.getDefault(), "%02d", fecha.get(GregorianCalendar.MONTH) + 1);
                String sDia = String.format(Locale.getDefault(), "%02d", fecha.get(GregorianCalendar.DAY_OF_MONTH));

                String sFecha = String.format("%s/%s/%s", sDia, sMes, sAnnio);

                etFechaSMS.setText(sFecha);
                fechaElegida = fecha;
            }
        };

        return listener;
    }

    public void setOnSmsEnviadoListener(OnSmsEnviadoListener listener) {
        this.listener = listener;

    }

    public interface OnSmsEnviadoListener {
        void onSmsEnviadoExito(String mensaje);

        void onSmsErrorEnvio(String mensaje);

        void onSmsProgramadoExito(String mensaje);

        void onSmsErrorProgramado(String mensaje);
    }
}