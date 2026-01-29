package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase Data Access Object (DAO) para la entidad Usuario.
 * Se encarga de realizar todas las operaciones CRUD (Create, Read, Update, Delete)
 * y consultas relacionadas con los usuarios en la base de datos mysql.
 */
public class UsuarioDAO {

    /**
     * Valida las credenciales de un usuario para permitir el acceso al sistema.
     * Realiza un JOIN con las tablas de correo y teléfono para recuperar toda la información.
     * 
     * @param email Correo electrónico proporcionado por el usuario.
     * @param password Contraseña proporcionada por el usuario.
     * @return Objeto Usuario con todos sus datos si las credenciales son correctas, o null si falla.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. OBTENER CONEXIÓN
            con = Conexion.getConexion();

            // 2. PREPARAR LA CONSULTA SQL
            // Unimos USUARIO con CORREO_USUARIO y ahora también con TELEFONO_USUARIO (Left Join por si no tiene telf)
            String sql = "SELECT u.id_usuario, u.id_rol, u.nombre, u.password, c.correo_electronico, t.numero_telefono " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                         "LEFT JOIN TELEFONO_USUARIO t ON u.id_usuario = t.id_usuario " +
                         "WHERE c.correo_electronico = ? AND u.password = ?";

            ps = con.prepareStatement(sql);
            
            // 3. ASIGNAR VALORES A LOS SIGNOS DE INTERROGACIÓN (?)
            ps.setString(1, email);    // El primer ? es el email
            ps.setString(2, password); // El segundo ? es el password

            // 4. EJECUTAR CONSULTA
            rs = ps.executeQuery();

            // 5. VERIFICAR SI HUBO RESULTADOS
            if (rs.next()) {
                // Si entra aquí, es que encontró al usuario
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPassword(rs.getString("password"));
                
                // Mapear nuevos campos
                usuario.setEmail(rs.getString("correo_electronico"));
                usuario.setTelefono(rs.getString("numero_telefono"));
                
                // --- NUEVO: CARGAR PERMISOS ---
                // Cerramos el ResultSet anterior para reusar variables o usamos nuevas
                // Es mejor usar un nuevo PreparedStatement para evitar conflictos
                PreparedStatement psPermisos = null;
                ResultSet rsPermisos = null;
                try {
                    // Seleccionar el NOMBRE del permiso basado en el rol del usuario
                    String sqlPermisos = "SELECT p.nombre FROM PERMISO p " +
                                         "INNER JOIN ROL_PERMISOS rp ON p.id_permiso = rp.id_permiso " +
                                         "WHERE rp.id_rol = ?";
                    psPermisos = con.prepareStatement(sqlPermisos);
                    psPermisos.setInt(1, usuario.getIdRol());
                    rsPermisos = psPermisos.executeQuery();
                    
                    java.util.List<String> listaPermisos = new java.util.ArrayList<>();
                    while (rsPermisos.next()) {
                        String nombrePermiso = rsPermisos.getString("nombre");
                        listaPermisos.add(nombrePermiso);
                        System.out.println("Permiso encontrado: " + nombrePermiso);
                    }
                    usuario.setPermisos(listaPermisos);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (rsPermisos != null) rsPermisos.close();
                    if (psPermisos != null) psPermisos.close();
                }
                // ------------------------------
            }

        } catch (SQLException e) {
            System.err.println("Error en validarLogin: " + e.getMessage());
        } finally {
            // 6. CERRAR CONEXIONES (Importante para no saturar la BD)
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuario; // Retorna el usuario si lo encontró, o null si no.
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Este método maneja una TRANSACCIÓN para asegurar que se guarden correctamente
     * los datos en tres tablas: USUARIO, CORREO_USUARIO y TELEFONO_USUARIO.
     * 
     * @param usuario Objeto Usuario con los datos básicos (nombre, password).
     * @param correo String con el correo electrónico.
     * @param telefono String con el número de teléfono.
     * @param rolNombre String con el nombre del rol (Administrador/Trabajador) para buscar su ID.
     * @return int El ID generado para el nuevo usuario, o -1 si hubo un error.
     */
    public int registrarUsuario(Usuario usuario, String correo, String telefono, String rolNombre) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idUsuarioGenerado = -1;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);

            // PASO 0: Averiguar el ID del ROL
            String sqlRol = "SELECT id_rol FROM ROL WHERE nombre_rol = ?";
            ps = con.prepareStatement(sqlRol);
            ps.setString(1, rolNombre);
            rs = ps.executeQuery();
            
            int idRol = 2; // Por defecto rol 2 (Trabajador)
            if (rs.next()) {
                idRol = rs.getInt("id_rol");
            }
            ps.close();

            // PASO 1: Insertar en la tabla USUARIO
            String sqlUsuario = "INSERT INTO USUARIO (id_rol, nombre, password) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idRol);
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getPassword());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1);
                    // IMPORTANTE: Actualizar el objeto Usuario con el ID generado
                    usuario.setIdUsuario(idUsuarioGenerado);
                    usuario.setIdRol(idRol);
                    usuario.setEmail(correo);
                    usuario.setTelefono(telefono);
                }
                ps.close();

                // PASO 2: Insertar en CORREO_USUARIO
                if (correo != null && !correo.isEmpty()) {
                    String sqlCorreo = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlCorreo);
                    ps.setInt(1, idUsuarioGenerado);
                    ps.setString(2, correo);
                    ps.executeUpdate();
                    ps.close();
                }

                // PASO 3: Insertar en TELEFONO_USUARIO
                if (telefono != null && !telefono.isEmpty()) {
                    String sqlTel = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlTel);
                    ps.setInt(1, idUsuarioGenerado);
                    ps.setString(2, telefono);
                    ps.executeUpdate();
                    ps.close();
                }

                con.commit();
                System.out.println("DAO: Usuario registrado con ID: " + idUsuarioGenerado);
            } else {
                con.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Error en registrarUsuario: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            idUsuarioGenerado = -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idUsuarioGenerado;
    }
    // CONTAR TRABAJADORES (Para estadísticas del admin)
    // Asumimos que id_rol = 2 es Trabajador
    public int contarTrabajadores() {
        int cantidad = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // Contamos usuarios con rol 2 (Trabajador)
            String sql = "SELECT COUNT(*) FROM USUARIO WHERE id_rol = 2";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1);
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
}
