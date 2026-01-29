package com.inventario.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión con la Base de Datos MySQL.
 * Implementa el patrón Singleton (o estático) para proporcionar una única vía de acceso a los datos.
 * IMPORTANTE: Aquí se deben configurar las credenciales del servidor de BD.
 */
public class Conexion {

    // 1. CONFIGURACIÓN: Aquí defines a qué base de datos te vas a conectar
    // "jdbc:mysql" es el protocolo
    // "localhost:3306" es tu computadora y el puerto de MySQL
    // "proyecto_inventario_bar" es el NOMBRE DE TU BASE DE DATOS
    private static final String URL = "jdbc:mysql://localhost:3306/proyecto_inventario_bar?serverTimezone=UTC";
    
    // 2. CREDENCIALES: Usuario y contraseña de tu MySQL Workbench/Instalación
    // CAMBIAR ESTO SEGÚN EL COMPUTADOR DONDE SE EJECUTE
    private static final String USUARIO = "root";
    private static final String PASSWORD = "14296625Maicol"; // ¡Cambia esto si la instructora tiene otra clave!

    // Método estático para obtener la conexión
    public static Connection getConexion() {
        Connection con = null;
        try {
            // 3. CARGAR DRIVER: Esto "enciende" el conector de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 4. CONECTAR: Intenta abrir el túnel hacia la base de datos
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: No se pudo conectar a la BD. Revisa usuario/password o el nombre de la BD.");
            System.out.println("Detalles: " + e.getMessage());
        }
        return con;
    }

    // 5. TEST DE CONEXIÓN: Este método main te permite probar ESTE archivo clic derecho -> Run File
    public static void main(String[] args) {
        getConexion();
    }
}
