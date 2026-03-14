package com.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de seguridad para el cifrado de contraseñas.
 * Implementa el algoritmo SHA-256 (Secure Hash Algorithm 256-bit).
 * RNF-01: Seguridad - Las contraseñas deben almacenarse de forma segura.
 */
public class Cifrado {

    /**
     * Convierte una cadena de texto plano en su representación hash SHA-256 (hexadecimal).
     * 
     * @param input La cadena de texto (contraseña) a cifrar.
     * @return El hash hexadecimal de 64 caracteres.
     */
    public static String sha256(String input) {
        if (input == null) return null;
        try {
            // Obtener instancia del algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Calcular el resumen (hash) de los bytes de la entrada
            byte[] hashBytes = md.digest(input.getBytes());
            
            // Convertir el array de bytes a formato hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // El formato %02x asegura que cada byte sea de 2 caracteres (con ceros a la izquierda si es necesario)
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 es un algoritmo estándar en Java, este error es poco probable
            System.err.println("Error: Algoritmo de cifrado no encontrado: " + e.getMessage());
            return null;
        }
    }
}
