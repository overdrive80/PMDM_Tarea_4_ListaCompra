package edu.pmdm.listascompra.gestores;

import java.util.ArrayList;
import java.util.List;

public class GestorSMS {

    public static List<String> dividirMensaje(String mensajeOriginal) {
        int maxCaracteres = 160; // Longitud máxima estándar para SMS.
        List<String> partes = new ArrayList<>();

        // Verifica si el mensaje cabe en un único SMS
        if (mensajeOriginal.length() <= maxCaracteres) {
            partes.add(mensajeOriginal);
            return partes;
        }

        // Si el mensaje es más largo, dividirlo en partes
        int indice = 0;
        int totalPartes = (int) Math.ceil((double) mensajeOriginal.length() / maxCaracteres);

        while (indice < mensajeOriginal.length()) {
            // Calcula el final de la parte actual
            int finParte = Math.min(indice + maxCaracteres, mensajeOriginal.length());

            // Agrega el índice de la parte al mensaje
            String parte = String.format("(%d/%d) %s", (partes.size() + 1), totalPartes, mensajeOriginal.substring(indice, finParte));

            partes.add(parte);

            // Avanza al siguiente segmento
            indice = finParte;
        }

        return partes;
    }
}
