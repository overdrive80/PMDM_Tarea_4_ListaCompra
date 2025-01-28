package edu.pmdm.listascompra.gestores;

public class GestorNumeros {

    // Convertir String a int
    public static int stringAInt(String valor) throws NumberFormatException {
        if (valor == null || valor.isEmpty()) {
            throw new NumberFormatException("El string no puede ser nulo o vacío");
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error al convertir el string a int: " + e.getMessage());
        }
    }

    // Convertir String a long
    public static long stringALong(String valor) throws NumberFormatException {
        if (valor == null || valor.isEmpty()) {
            throw new NumberFormatException("El string no puede ser nulo o vacío");
        }
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error al convertir el string a long: " + e.getMessage());
        }
    }

    // Convertir String a double
    public static double stringADouble(String valor) throws NumberFormatException {
        if (valor == null || valor.isEmpty()) {
            throw new NumberFormatException("El string no puede ser nulo o vacío");
        }
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error al convertir el string a double: " + e.getMessage());
        }
    }

    // Convertir int a String
    public static String intAString(int valor) {
        return Integer.toString(valor);
    }

    // Convertir long a String
    public static String longAString(long valor) {
        return Long.toString(valor);
    }

    // Convertir double a String
    public static String doubleAString(double valor) {
        return Double.toString(valor);
    }

    // Métodos de prueba
    public static void main(String[] args) {
        try {
            // Pruebas de conversión de String a números
            System.out.println("String a int: " + stringAInt("123"));
            System.out.println("String a long: " + stringALong("123456789"));
            System.out.println("String a double: " + stringADouble("123.45"));

            // Pruebas de conversión de números a String
            System.out.println("int a String: " + intAString(123));
            System.out.println("long a String: " + longAString(123456789L));
            System.out.println("double a String: " + doubleAString(123.45));
        } catch (NumberFormatException e) {
            System.err.println("Error en la conversión: " + e.getMessage());
        }
    }
}