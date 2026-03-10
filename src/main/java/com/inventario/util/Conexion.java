package com.inventario.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * UTILIDAD: Clase de Conexión a la Base de Datos MySQL.
 * 
 * Implementa: Soporte transversal para TODOS los requisitos funcionales (RF-01 a RF-28)
 *             ya que todos los módulos necesitan conectarse a la BD para funcionar.
 * Cumple: RNF-10 (Usuarios Concurrentes - cada llamada crea una conexión independiente)
 *         RNF-13 (Arquitectura MVC - Capa de utilidad separada)
 * 
 * Esta clase provee un método estático para obtener una conexión a MySQL.
 * Es utilizada por TODOS los DAOs del sistema (UsuarioDAO, ProductoDAO, VentaDAO, etc.).
 * IMPORTANTE: Las credenciales deben cambiarse según el servidor donde se despliegue.
 */
public class Conexion {

    // =====================================================================
    // CONSTANTES DE CONFIGURACIÓN (private static final)
    // private = Solo accesibles dentro de esta clase (seguridad)
    // static = Pertenecen a la clase, no a un objeto específico (eficiencia)
    // final = No pueden cambiar su valor después de ser asignadas (constantes)
    // =====================================================================

    /**
     * CONSTANTE: URL de conexión a la base de datos.
     * Formato: jdbc:mysql://[host]:[puerto]/[nombre_base_datos]?[parámetros]
     * - "jdbc:mysql://" = Protocolo de conexión (le dice a Java que va a hablar con MySQL)
     * - "localhost:3306" = Dirección del servidor (localhost = tu PC, 3306 = puerto por defecto de MySQL)
     * - "proyecto_inventario_bar" = Nombre exacto de tu base de datos en MySQL Workbench
     * - "?serverTimezone=UTC" = Parámetro para sincronizar la zona horaria y evitar errores
     */
    private static final String URL = "jdbc:mysql://localhost:3306/proyecto_inventario_bar?serverTimezone=UTC";
    
    /**
     * CONSTANTE: Nombre de usuario de MySQL.
     * "root" es el usuario administrador por defecto de MySQL.
     */
    private static final String USUARIO = "root";
    
    /**
     * CONSTANTE: Contraseña de MySQL.
     * RNF-01: NOTA DE SEGURIDAD - En un entorno de producción real, esta contraseña
     * no debería estar en el código fuente. Se debería usar variables de entorno o un archivo de configuración externo.
     * CAMBIAR ESTO según el computador donde se ejecute.
     */
    private static final String PASSWORD = "14296625Maicol";

    /**
     * MÉTODO ESTÁTICO: Obtener una nueva conexión a la base de datos.
     * 
     * public = Puede ser llamado desde cualquier otra clase (los DAOs lo necesitan)
     * static = No necesitas crear un objeto "new Conexion()" para usarlo. Se llama directamente: Conexion.getConexion()
     * Connection = Tipo de retorno. Devuelve un objeto Connection que permite ejecutar consultas SQL.
     * 
     * @return Objeto Connection activo, o null si la conexión falló
     */
    public static Connection getConexion() {
        Connection con = null; // Variable de tipo Connection, inicializada en null (vacía, sin conexión aún)
        try {
            // PASO 1: CARGAR EL DRIVER DE MYSQL EN MEMORIA
            // Class.forName() busca y carga la clase del driver (mysql-connector-j) que Maven descargó.
            // Es como "encender" el traductor que permite a Java hablar con MySQL.
            // Si el .jar no está en el proyecto, lanza ClassNotFoundException.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // PASO 2: ESTABLECER LA CONEXIÓN CON LA BASE DE DATOS
            // DriverManager es el "jefe" que administra los drivers cargados.
            // getConnection() usa la URL, usuario y contraseña para abrir un túnel hacia MySQL.
            // Si las credenciales son correctas y MySQL está encendido, devuelve un objeto Connection.
            // RNF-02: Esta conexión permite usar PreparedStatement para proteger contra SQL Injection.
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos!"); // Mensaje de confirmación en consola
            
        } catch (ClassNotFoundException e) {
            // ERROR: No se encontró el driver de MySQL
            // Causa probable: Falta la dependencia mysql-connector-j en el pom.xml
            System.out.println("Error: No se encontró el driver de MySQL.");
            e.printStackTrace(); // Imprime detalles técnicos del error en consola
        } catch (SQLException e) {
            // ERROR: No se pudo conectar a la base de datos
            // Causas probables: MySQL apagado, usuario/password incorrectos, nombre de BD incorrecto
            // RNF-08: Se imprime mensaje descriptivo del error
            System.out.println("Error: No se pudo conectar a la BD. Revisa usuario/password o el nombre de la BD.");
            System.out.println("Detalles: " + e.getMessage());
        }
        return con; // Devuelve la conexión activa (o null si falló algún paso)
    }

    /**
     * MÉTODO MAIN: Para pruebas independientes.
     * Permite ejecutar SOLO este archivo (Clic Derecho → Run File)
     * para verificar que la conexión a MySQL funciona correctamente
     * sin necesidad de iniciar Tomcat ni abrir el navegador.
     */
    public static void main(String[] args) {
        getConexion(); // Llama al método y verás en consola si funcionó o no
    }
}
