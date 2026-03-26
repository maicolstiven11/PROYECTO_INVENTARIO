package com.inventario.dao;

import com.inventario.util.Conexion;   
import com.inventario.model.Negocio;   
import java.sql.Connection;            
import java.sql.PreparedStatement;     
import java.sql.SQLException;         
import java.sql.ResultSet;            
import java.util.ArrayList;           
import java.util.List;                

/**
 * Clase NegocioDAO (Data Access Object).
 * 
 * Se encarga de todas las operaciones sobre la tabla NEGOCIO en la base de datos:
 * registrar bares nuevos, listar los que pertenecen a un dueño, inactivarlos
 * y eliminar solo los que estén vacíos (sin datos vinculados).
 */
public class NegocioDAO {

    /**
     * Registra un nuevo negocio en la base de datos y lo vincula con su dueño.
     * Inserta en la tabla NEGOCIO y luego en la tabla puente USUARIO_NEGOCIO.
     * Usa transacción para asegurar que ambas inserciones se hagan o ninguna.
     *
     * @param negocio Objeto con nombre y dirección del bar
     * @param idUsuario ID del administrador dueño
     * @return El ID generado para el nuevo negocio, o -1 si falló
     */
    /**
     * Registra un nuevo negocio en la base de datos y lo vincula con su dueño.
     * Inserta en la tabla NEGOCIO y luego en la tabla puente USUARIO_NEGOCIO.
     * Usa transacción para asegurar que ambas inserciones se hagan o ninguna.
     *
     * @param negocio Objeto con nombre y dirección del bar
     * @param idUsuario ID del administrador dueño
     * @return El ID generado para el nuevo negocio, o -1 si falló
     */
    public int registrarNegocio(Negocio negocio, int idUsuario) {
        Connection con = null; // Socket para hablar con MySQL
        PreparedStatement psNegocio = null; // Comando para la tabla de bares
        PreparedStatement psVinculo = null; // Comando para la tabla que une dueños con bares
        ResultSet rsKeys = null; // Cofre para recibir el ID autoincremental
        int idGenerado = -1; // Bandera de error
        
        try {
            con = Conexion.getConexion(); // Abrimos el canal
            
            // TRANSACCIÓN DE SEGURIDAD: Desactivamos el guardado automático.
            // Esto es porque un negocio SIN dueño no debe existir. O se crean ambos registros o ninguno.
            con.setAutoCommit(false); 

            // CONSULTA SQL 1 (Creación de Bar):
            // 1. INSERT INTO NEGOCIO: Metemos nombre y dirección.
            // 2. estado: Por defecto 'inactivo' porque aún no tiene inventario inicial.
            String sqlNegocio = "INSERT INTO NEGOCIO (nombre, direccion, estado) VALUES (?, ?, ?)";
            
            // Pedimos que nos devuelva las llaves (ID) generadas
            psNegocio = con.prepareStatement(sqlNegocio, PreparedStatement.RETURN_GENERATED_KEYS);
            psNegocio.setString(1, negocio.getNombre()); // Nombre del bar comercial
            psNegocio.setString(2, negocio.getDireccion()); // Ubicación física
            psNegocio.setString(3, "inactivo"); // Estado de "en espera"
            
            int filas = psNegocio.executeUpdate(); // Realizamos la inserción
            
            if (filas > 0) { // Si el bar se creó...
                rsKeys = psNegocio.getGeneratedKeys(); // Atrapamos el ID
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // Lo guardamos
                    negocio.setIdNegocio(idGenerado);
                }
                
                // CONSULTA SQL 2 (Vinculación):
                // 1. INSERT INTO USUARIO_NEGOCIO: Tabla puente vital para saber de quién es el bar.
                // 2. id_usuario, id_negocio: Son las llaves foráneas que cruzan la información.
                if (idUsuario > 0 && idGenerado > 0) {
                    String sqlVinculo = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
                    psVinculo = con.prepareStatement(sqlVinculo);
                    psVinculo.setInt(1, idUsuario); // Quién es el dueño
                    psVinculo.setInt(2, idGenerado); // Qué negocio compró/registró
                    psVinculo.executeUpdate(); // Ejecutamos la unión física
                    System.out.println("DAO: Negocio " + idGenerado + " vinculado con Usuario " + idUsuario);
                }
                
                // ÉXITO: Los dos pasos salieron bien, le decimos a MySQL que guarde todo definitivamente
                con.commit(); 
                System.out.println("DAO: Negocio registrado con ID: " + idGenerado);
            } else {
                // FALLO: Algo pasó en el primer paso, deshacemos todo
                con.rollback();
            }
            
        } catch (SQLException e) {
            System.out.println("Error al registrar negocio: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Crash preventivo
            }
            throw new RuntimeException("ErrorSQL: " + e.getMessage()); 
        } finally {
            try {
                // Restauramos configuración y soltamos cables
                if (con != null) con.setAutoCommit(true); 
                if (rsKeys != null) rsKeys.close();
                if (psVinculo != null) psVinculo.close();
                if (psNegocio != null) psNegocio.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; // Retornamos el ID o el error (-1)
    }

    /**
     * Lista todos los negocios que pertenecen a un usuario administrador.
     * Además calcula si cada negocio tiene un inventario activo (abierto).
     */
    public List<Negocio> listarNegocios(int idUsuario) {
        List<Negocio> lista = new ArrayList<>(); // Lista de bares del administrador
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); // Enlace seguro
            
            String sql = "SELECT n.*, " +   // Selecciona todos los campos de la tabla NEGOCIO (alias n)

                         "(SELECT COUNT(*) FROM INVENTARIO i WHERE i.id_negocio = n.id_negocio AND i.estado = 'activo') as tiene_inv " +   // Subconsulta: cuenta cuántos inventarios activos existen para cada negocio, y lo devuelve como la columna 'tiene_inv'

                         "FROM NEGOCIO n " +   // Tabla principal: NEGOCIO con alias "n"

                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +   // Une NEGOCIO con USUARIO_NEGOCIO, para saber qué negocios están asociados a un usuario específico

                         "WHERE un.id_usuario = ?";   // Filtra: solo devuelve los negocios asociados al usuario indicado (parámetro ?)

            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Filtro de seguridad (solo mis bares)
            rs = ps.executeQuery(); // Disparamos radar
            
            while(rs.next()){ // Convertimos cada fila de MySQL en un objeto Negocio de Java
                Negocio n = new Negocio();
                n.setIdNegocio(rs.getInt("id_negocio"));
                n.setNombre(rs.getString("nombre"));
                n.setDireccion(rs.getString("direccion"));
                n.setEstado(rs.getString("estado"));
                
                // Si la subconsulta contó 1 o más, marcamos que tiene inventario abierto
                boolean activo = rs.getInt("tiene_inv") > 0;
                n.setTieneInventarioActivo(activo); // Dato vital para habilitar/deshabilitar botones en la web
                
                lista.add(n); // Añadimos al listado final
            }
        } catch (SQLException e) {
            System.err.println("Error listar negocios: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    /**
     * Verifica si un negocio tiene datos vinculados en otras tablas.
     * Si tiene datos, NO se debe borrar sino inactivar para no perder la contabilidad histórica.
     */
    public boolean negocioTieneDatos(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tieneDatos = false;

        try {
            con = Conexion.getConexion();

          
            String sql = "SELECT " +   // Inicia la consulta principal SELECT

                         "(SELECT COUNT(*) FROM INVENTARIO WHERE id_negocio = ?) + " +   // Subconsulta 1: cuenta cuántos inventarios existen para el negocio indicado (primer parámetro ?)

                         "(SELECT COUNT(*) FROM USUARIO_NEGOCIO un " +   // Subconsulta 2: comienza contando usuarios asociados a un negocio en la tabla USUARIO_NEGOCIO

                         " INNER JOIN USUARIO u ON un.id_usuario = u.id_usuario " +   // Une USUARIO_NEGOCIO con USUARIO para obtener información del usuario

                         " WHERE un.id_negocio = ? AND u.id_rol = 2) " +   // Filtra: solo usuarios del negocio indicado (segundo parámetro ?) y cuyo rol sea 2 (ej. empleados)

                         "AS total_datos";   // El resultado final es la suma de ambas cuentas, con alias 'total_datos'

            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            ps.setInt(2, idNegocio);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt("total_datos") > 0) {
                tieneDatos = true; // No está vacío, precaución
            }
        } catch (SQLException e) {
            System.err.println("Error verificando datos del negocio: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return tieneDatos;
    }

    /**
     * Cambia el estado de un negocio de "activo" a "inactivo".
     * Se usa cuando el bar tiene datos históricos y borrarlo causaría errores o pérdida de información.
     */
    public boolean inactivarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;

        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // CONSULTA SQL (Edición de Estado):
            // Modificamos la columna 'estado' para que el sistema sepa que este bar ya no abre cajas.
            String sql = "UPDATE NEGOCIO SET estado = 'inactivo' WHERE id_negocio = ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // Bar objetivo

            if (ps.executeUpdate() > 0) { // Si MySQL confirmó el cambio
                actualizado = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al inactivar negocio: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado; // Éxito o Fracaso
    }

    /**
     * Elimina un negocio SOLO si no tiene datos vinculados (está vacío).
     * Borra primero de la tabla puente USUARIO_NEGOCIO y luego de NEGOCIO (Respetando FK).
     */
    public boolean eliminarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            
            // TRANSACCIÓN: O borramos del puente y de la tabla principal, o no tocamos nada.
            con.setAutoCommit(false); 

            // PASO 1: Eliminar el vínculo legal del dueño con este bar.
            String sqlVinculo = "DELETE FROM USUARIO_NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sqlVinculo);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();

            // PASO 2: Eliminar la existencia física del negocio de la tabla NEGOCIO.
            String sqlNegocio = "DELETE FROM NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sqlNegocio);
            ps.setInt(1, idNegocio);
            
            int filas = ps.executeUpdate();
            if (filas > 0) { // Si se borró de verdad...
                eliminado = true;
                con.commit(); // Confirmamos el borrado atómico
                System.out.println("Negocio " + idNegocio + " eliminado correctamente (estaba vacío).");
            } else {
                con.rollback(); // Algo falló, recuperamos el bar
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar negocio: " + e.getMessage());
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }

    /**
     * Cuenta cuántos negocios tiene un usuario (para las tarjetas del Dashboard).
     */
    public int contarNegocios(int idUsuario) {
        int cantidad = 0; // Iniciamos contador
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL (Conteo con Join):
            // Cuenta las filas de NEGOCIO cruzadas con USUARIO_ID por medio de la tabla puente.
            String sql = "SELECT COUNT(*) FROM NEGOCIO n " +   // Cuenta cuántos registros hay en la tabla NEGOCIO (alias n)

                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +   // Une NEGOCIO con USUARIO_NEGOCIO, solo si existe coincidencia en id_negocio (relación negocio-usuario)

                         "WHERE un.id_usuario = ?";   // Filtra: solo devuelve los negocios asociados al usuario indicado (parámetro ?)

            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Usuario logueado
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1); // Atrapamos el número final
            }
        } catch (SQLException e) { 
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); } 
        }
        return cantidad;
    }

    /**
     * Obtiene los datos de un negocio específico por su ID.
     */
    public Negocio obtenerNegocio(int idNegocio) {
        Negocio n = null; // Caja vacía
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion(); // Enlace
            
            // CONSULTA SQL: Búsqueda exacta por ID único.
            String sql = "SELECT * FROM NEGOCIO WHERE id_negocio = ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();
            
            if (rs.next()) { // Si el bar existe en el sistema
                n = new Negocio(); // Llenamos el objeto Java con sus datos:
                n.setIdNegocio(rs.getInt("id_negocio"));
                n.setNombre(rs.getString("nombre"));
                n.setDireccion(rs.getString("direccion"));
                n.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return n; // Retornamos el bar localizado
    }
}
