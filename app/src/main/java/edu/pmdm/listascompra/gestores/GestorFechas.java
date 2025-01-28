package edu.pmdm.listascompra.gestores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/*
 * G: Era (designación de era, como "AD")
 * y: Año (año completo)
 * M: Mes en año (como "julio" o "07")
 * d: Día en mes
 * k: Hora en día (1-24)
 * H: Hora en día (0-23)
 * m: Minutos en hora
 * s: Segundos en minuto
 * S: Milisegundos
 * E: Día en semana (texto completo, como "domingo" o "lunes")
 * D: Día en año (número de día del año)
 * F: Semana en mes
 * w: Semana en año
 * W: Semana en mes
 * a: AM/PM (indicador de mañana o tarde)
 * h: Hora en AM/PM (1-12)
 * K: Hora en AM/PM (0-11)
 * z: Zona horaria (por ejemplo, "PST" para Hora Estándar del Pacífico)
 * Z: Desplazamiento de zona horaria (en formato ±HHmm)
 */

/***
 * Clase de herramienta para conversion de fechas
 *
 * @author: Israel Lucas Torrijos
 *
 * SimpleDateFormat:
 * - format: fecha a texto
 * - parse: texto a fecha
 **/

public class GestorFechas {

    public static final String PATRON_UTC = "yyyy-MM-dd HH:mm:ss";

    private GestorFechas() {
    }

    /**
     * Convierte una fecha de tipo java.sql.Date a java.util.Date
     *
     * @param fecha Fecha procedente de base de datos
     * @return java.util.Date Fecha para manejar en JAVA
     */
    public static java.util.Date toDate(java.sql.Date fecha) {

        java.util.Date nuevaFecha = null;

        if (fecha != null) {
            nuevaFecha = new java.util.Date(fecha.getTime());
        }

        return nuevaFecha;
    }

    /**
     * Convierte una fecha de tipo java.util.Date a java.sql.Date
     *
     * @param fecha Fecha procedente de JAVA
     * @return java.sql.Date Fecha para manejar en SQL
     */
    public static java.sql.Date toSQLDate(java.util.Date fecha) {

        java.sql.Date nuevaFecha = null;

        if (fecha != null) {
            nuevaFecha = new java.sql.Date(fecha.getTime());
        }

        return nuevaFecha;
    }

    /**
     * Convierte: java.util.Date a Texto.
     *
     * @param fecha  Fecha de tipo java.util.Date
     * @param patron Patrón para formatear la fecha a texto
     * @return String Fecha formateada en texto
     */
    public static String toString(java.util.Date fecha, String patron) {
        SimpleDateFormat sdf = null;
        String fechaFormateada = "";
        String patronDefecto = "dd-MM-yyyy";

        try {
            sdf = new SimpleDateFormat(patron);
            fechaFormateada = sdf.format(fecha);

        } catch (NullPointerException | IllegalArgumentException e) {

            // Si hay un problema con el patron dado devuelve la fecha en formato por defecto
            if (fecha != null) {
                sdf = new SimpleDateFormat(patronDefecto);
                fechaFormateada = sdf.format(fecha);
            }

        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());

        }

        return fechaFormateada;

    }

    /**
     * Convierte: Texto a java.util.Date
     *
     * @param sFecha Texto que representa una fecha. DEBE AJUSTARSE AL PATRON
     * @param patron Patrón de salida de la fecha
     * @return java.util.Date Fecha en formato java.util.Date
     */
    public static java.util.Date toDate(String sFecha, String patron) {
        java.util.Date fecha = null;
        SimpleDateFormat sdf = null;

        try {
            sdf = new SimpleDateFormat(patron);

            // Verificar fecha correcta
            sdf.setLenient(false);
            fecha = sdf.parse(sFecha);

        } catch (NullPointerException e) {
            System.err.println("El patrón no puede ser un valor nulo.");
        } catch (ParseException e) {
            System.err.println("La fecha en texto: " + sFecha + ", no se ajusta al patrón: " + patron);
        }

        return fecha;
    }

    /**
     * Convierte una fecha de tipo UTC a la zona horaria local del dispositivo.
     *
     * @param fechaUtc Fecha en UTC
     * @return Fecha convertida a la zona horaria local
     */
    public static String toLocalTimeString(java.util.Date fechaUtc, String patron) {
        if (fechaUtc == null) {
            return "";
        }
        // Obtener la zona horaria local del dispositivo
        SimpleDateFormat sdf = new SimpleDateFormat(patron);
        TimeZone zonaHorariaLocal = TimeZone.getDefault();
        sdf.setTimeZone(zonaHorariaLocal); // Establecer la zona horaria local del dispositivo
        return sdf.format(fechaUtc);
    }

    public static java.util.Date toLocalfromUTC(String fechaUtc) {
        if (fechaUtc == null) {
            return null;
        }

        java.util.Date fechaLocal = null;

        // Obtener la zona horaria local
        TimeZone zonaHorariaLocal = TimeZone.getDefault();

        // Crear un SimpleDateFormat en UTC para analizar la fecha UTC correctamente
        SimpleDateFormat sdfUtc = new SimpleDateFormat(PATRON_UTC);
        sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));  // Asegurarse de que la fecha de entrada está en UTC

        try {
            // Parsear la fecha UTC
            java.util.Date fechaUtcParsed = sdfUtc.parse(fechaUtc);

            // Ahora convertir a la zona horaria local
            SimpleDateFormat sdfLocal = new SimpleDateFormat(PATRON_UTC);
            sdfLocal.setTimeZone(zonaHorariaLocal);  // Configurar la zona horaria local

            // Formatear la fecha UTC convertida a hora local
            String fechaLocalString = sdfLocal.format(fechaUtcParsed);

            // Convertir la fecha formateada nuevamente a Date
            fechaLocal = sdfLocal.parse(fechaLocalString);
        } catch (ParseException e) {
            throw new RuntimeException("Error al convertir fecha UTC a hora local", e);
        }

        return fechaLocal;
    }


}

