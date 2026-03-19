package com.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase Utilitaria de Seguridad: Cifrado.
 * 
 * Componente abstracto estático diseñado para la manipulación y transformación 
 * de cadenas de texto (Strings). Implementa el algoritmo de hash unidireccional SHA-256 
 * para garantizar la integridad y confidencialidad de las propiedades del modelo (Contraseñas).
 */
public class Cifrado {

    /**
     * Módulo Factory Transformador Estático (String Mutator Method).
     * 
     * Subrutina funcional pura que recibe una instancia de String (texto plano) 
     * y computa una nueva instancia de String (hash hexadecimal) sin alterar el estado 
     * interno de la clase (Stateless Component).
     * 
     * @param input Parámetro de entrada constante que representa la cadena base a transformar.
     * @return Una nueva instancia de String con formato hexadecimal representando el digest computado, o null en caso de error.
     */
    public static String sha256(String input) {
        if (input == null) return null; 
        try {
            // Getter de Instancia de Algoritmo:
            // Solicita al Java Security Provider un objeto MessageDigest configurado para SHA-256.
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            
            // Método Transformador de Flujo de Bytes:
            // Convierte el String input en un arreglo de bytes primitivo (byte[]) y calcula su resumen criptográfico.
            byte[] hashBytes = md.digest(input.getBytes()); 
            
            // Constructor Dinámico de Cadenas (String Builder Object):
            // Inicializa un objeto mutable diseñado para la concatenación eficiente en memoria.
            StringBuilder hexString = new StringBuilder(); 
            
            // Iterador Abstracto:
            // Recorre secuencialmente cada índice del arreglo de bytes primitivo.
            for (byte b : hashBytes) { 
                // Mapeo Hexadecimal Computado:
                // Convierte el valor del byte actual a su representación entera positiva y luego a String hexadecimal.
                String hex = Integer.toHexString(0xff & b); 
                
                // Condicional de Formato:
                // Garantiza que cada bloque hexadecimal tenga longitud mínima de 2 caracteres (padding).
                if (hex.length() == 1) { 
                    hexString.append('0'); 
                }
                // Setter de Adición:
                // Concatena el sub-string actual al objeto acumulador StringBuilder.
                hexString.append(hex); 
            }
            // Retorno Inmutable:
            // Llama al método toString() del objeto StringBuilder para retornar la cadena final consolidada.
            return hexString.toString(); 
            
        } catch (NoSuchAlgorithmException e) { 
            // Manejador de Excepciones de Entorno:
            // Captura instancias de error lanzadas si el algoritmo solicitado no existe en la JVM.
            System.err.println("Error de Entorno: Algoritmo instanciador (SHA-256) no soportado: " + e.getMessage());
            return null; 
        }
    }
}
