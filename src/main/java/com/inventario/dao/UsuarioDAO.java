package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object): Clase de Acceso a Datos para la entidad Usuario.
 * 
 * Implementa: RF-01 (Registrar Usuario), RF-02 (Iniciar Sesión), RF-03 (Gestionar Roles y Permisos), RF-28 (Dashboard Estadísticas)
 * Cumple: RNF-02 (Protección SQL Injection - usa PreparedStatement con parámetros ?)
 *         RNF-13 (Arquitectura MVC - Toda la lógica de BD está aislada en esta clase DAO)
 *         RNF-14 (Documentación del Código - comentarios Javadoc en métodos públicos)
 * 
 * Esta clase se encarga de TODAS las operaciones con la base de datos MySQL
 * relacionadas con usuarios: validar login, registrar, contar trabajadores.
 * NINGÚN Servlet hace consultas SQL directamente; todos delegan a esta clase.
 */
public class UsuarioDAO {

    /**
     * RF-02: Valida las credenciales de un usuario para permitir el acceso al sistema.
     * RF-03: Además carga los permisos del rol del usuario desde las tablas PERMISO y ROL_PERMISOS.
     * RNF-02: Usa PreparedStatement con parámetros (?) para prevenir SQL Injection.
     * 
     * @param email    Correo electrónico proporcionado por el usuario en el formulario de login.
     * @param password Contraseña proporcionada por el usuario en el formulario de login.
     * @return Objeto Usuario con todos sus datos si las credenciales son correctas, o null si falla.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null;        // Variable de tipo objeto Usuario, inicializada en null (vacía)
        Connection con = null;         // Variable para la conexión a la BD
        PreparedStatement ps = null;   // Variable para la consulta SQL preparada
        ResultSet rs = null;           // Variable para el resultado de la consulta

        try {
            // RF-02 PASO 1: Obtener conexión a la BD usando la clase utilitaria Conexion
            con = Conexion.getConexion();

            // RF-02 PASO 2: Preparar la consulta SQL con JOINs
            // INNER JOIN con CORREO_USUARIO: Para buscar por correo (el correo está en otra tabla)
            // LEFT JOIN con TELEFONO_USUARIO: Para traer el teléfono si existe (LEFT porque es opcional)
            // RNF-02: Los signos ? son parámetros seguros que previenen SQL Injection
            String sql = "SELECT u.id_usuario, u.id_rol, u.nombre, u.password, c.correo_electronico, t.numero_telefono " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                         "LEFT JOIN TELEFONO_USUARIO t ON u.id_usuario = t.id_usuario " +
                         "WHERE c.correo_electronico = ? AND u.password = ?";

            ps = con.prepareStatement(sql);   // Prepara la consulta SQL en la conexión
            
            // RF-02 PASO 3: Asignar valores a los parámetros (?)
            // RNF-02: setString escapa automáticamente caracteres peligrosos (', ", etc.)
            ps.setString(1, email);    // El primer ? se reemplaza con el email recibido
            ps.setString(2, password); // El segundo ? se reemplaza con la contraseña recibida

            // RF-02 PASO 4: Ejecutar la consulta SELECT en la BD
            rs = ps.executeQuery();    // Ejecuta y guarda el resultado (tabla de filas encontradas)

            // RF-02 PASO 5: Verificar si la BD devolvió alguna fila
            if (rs.next()) {
                // Si rs.next() es TRUE, significa que encontró un usuario con esas credenciales
                // Creamos un objeto Usuario y lo llenamos con los datos de la fila encontrada
                usuario = new Usuario();                                // Crear objeto vacío
                usuario.setIdUsuario(rs.getInt("id_usuario"));          // RF-02: Leer columna id_usuario de la fila
                usuario.setIdRol(rs.getInt("id_rol"));                  // RF-03: Leer el rol para control de acceso
                usuario.setNombre(rs.getString("nombre"));              // RF-02: Leer el nombre del usuario
                usuario.setPassword(rs.getString("password"));          // RF-02: Leer la contraseña almacenada
                
                // RF-02: Mapear campos adicionales de las tablas unidas (JOIN)
                usuario.setEmail(rs.getString("correo_electronico"));   // Viene de CORREO_USUARIO (INNER JOIN)
                usuario.setTelefono(rs.getString("numero_telefono"));   // Viene de TELEFONO_USUARIO (LEFT JOIN, puede ser null)
                
                // =====================================================================
                // RF-03: CARGAR PERMISOS DEL ROL DEL USUARIO
                // Consulta las tablas PERMISO y ROL_PERMISOS para saber qué puede hacer este usuario.
                // Ejemplo: Si es Admin (rol 1), puede tener permisos: CREAR_BAR, EDITAR_PRODUCTOS, etc.
                // RNF-04: Estos permisos se usan después en las vistas JSP para control de acceso.
                // =====================================================================
                PreparedStatement psPermisos = null;
                ResultSet rsPermisos = null;
                try {
                    // RF-03: Consulta que une PERMISO con ROL_PERMISOS para obtener nombres de permisos
                    String sqlPermisos = "SELECT p.nombre FROM PERMISO p " +
                                         "INNER JOIN ROL_PERMISOS rp ON p.id_permiso = rp.id_permiso " +
                                         "WHERE rp.id_rol = ?";
                    psPermisos = con.prepareStatement(sqlPermisos);           // Prepara segunda consulta
                    psPermisos.setInt(1, usuario.getIdRol());                 // RF-03: Busca permisos del rol del usuario
                    rsPermisos = psPermisos.executeQuery();                   // Ejecuta la consulta
                    
                    // RF-03: Recorrer todos los permisos encontrados y agregarlos a una lista
                    java.util.List<String> listaPermisos = new java.util.ArrayList<>();
                    while (rsPermisos.next()) {                              // Mientras haya filas de permisos
                        String nombrePermiso = rsPermisos.getString("nombre"); // Leer el nombre del permiso
                        listaPermisos.add(nombrePermiso);                    // Agregar a la lista
                        System.out.println("Permiso encontrado: " + nombrePermiso); // Log de depuración en consola
                    }
                    usuario.setPermisos(listaPermisos);                      // RF-03: Asignar la lista de permisos al objeto Usuario
                    
                } catch (Exception ex) {
                    ex.printStackTrace(); // Si falla la carga de permisos, no detener el login
                } finally {
                    if (rsPermisos != null) rsPermisos.close();  // Cerrar recurso
                    if (psPermisos != null) psPermisos.close();  // Cerrar recurso
                }
            }

        } catch (SQLException e) {
            // RNF-08: Loguear error descriptivo en consola del servidor
            System.err.println("Error en validarLogin: " + e.getMessage());
        } finally {
            // PASO 6: CERRAR CONEXIONES (Importante para no saturar la BD)
            // RNF-10: Si no cerramos las conexiones, eventualmente MySQL rechazará nuevas conexiones
            try {
                if (rs != null) rs.close();    // Cerrar el resultado
                if (ps != null) ps.close();    // Cerrar la consulta preparada
                if (con != null) con.close();  // Cerrar la conexión a la BD
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuario; // RF-02: Retorna el objeto Usuario lleno (login exitoso) o null (login fallido)
    }

    /**
     * RF-01: Registra un nuevo usuario en la base de datos.
     * Maneja una TRANSACCIÓN ATÓMICA para insertar en 3 tablas: USUARIO, CORREO_USUARIO y TELEFONO_USUARIO.
     * RF-01 Restricción 3: Si algo falla en cualquiera de las 3 inserciones, se hace ROLLBACK (se deshace todo).
     * RNF-02: Todas las consultas usan PreparedStatement con parámetros (?).
     * 
     * @param usuario   Objeto Usuario con nombre y password.
     * @param correo    String con el correo electrónico.
     * @param telefono  String con el número de teléfono (puede ser null si es opcional).
     * @param rolNombre String con el nombre del rol ("ADMIN" o "TRABAJADOR") para buscar su ID en la BD.
     * @return int El ID generado automáticamente para el nuevo usuario, o -1 si hubo error.
     */
    public int registrarUsuario(Usuario usuario, String correo, String telefono, String rolNombre) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idUsuarioGenerado = -1;   // Variable para guardar el ID nuevo. Empieza en -1 (error por defecto).

        try {
            con = Conexion.getConexion();      // Obtener conexión a la BD
            
            // RF-01 Restricción 3: INICIO DE TRANSACCIÓN
            // setAutoCommit(false) le dice a MySQL: "No guardes nada automáticamente. Espera a que yo te diga."
            // Esto permite hacer ROLLBACK (deshacer) si algún paso falla.
            con.setAutoCommit(false);

            // =====================================================================
            // RF-01, RF-03: PASO 0 - Buscar el ID del ROL en la tabla ROL
            // El formulario envía "ADMIN" o "TRABAJADOR" como texto.
            // Necesitamos convertirlo al número (ID) que usa la BD.
            // RF-01 Restricción 2: Si no encuentra el rol, usa 2 (Trabajador) por defecto.
            // =====================================================================
            String sqlRol = "SELECT id_rol FROM ROL WHERE nombre_rol = ?";
            ps = con.prepareStatement(sqlRol);
            ps.setString(1, rolNombre);        // Busca el rol por nombre ("ADMIN" o "TRABAJADOR")
            rs = ps.executeQuery();
            
            int idRol = 2;                     // RF-01 Restricción 2: Valor por defecto = Trabajador (id_rol=2)
            if (rs.next()) {
                idRol = rs.getInt("id_rol");   // Si lo encuentra, usa el ID real de la BD
            }
            ps.close();                        // Cerrar para reutilizar la variable ps

            // =====================================================================
            // RF-01: PASO 1 - Insertar en la tabla USUARIO
            // RETURN_GENERATED_KEYS le dice a MySQL: "Después de insertar, devuélveme el ID que generaste"
            // =====================================================================
            String sqlUsuario = "INSERT INTO USUARIO (id_rol, nombre, password) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idRol);                       // Columna id_rol = el ID del rol encontrado
            ps.setString(2, usuario.getNombre());      // Columna nombre = nombre del objeto Usuario
            ps.setString(3, usuario.getPassword());    // Columna password = contraseña del objeto. RNF-01: PENDIENTE cifrado BCrypt
            
            int filasAfectadas = ps.executeUpdate();   // Ejecuta el INSERT. Retorna cuántas filas se insertaron.
            
            if (filasAfectadas > 0) {
                // RF-01: El INSERT fue exitoso, ahora obtenemos el ID generado
                rs = ps.getGeneratedKeys();            // Pedir las claves generadas
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1);  // Leer el ID generado (ej: 45)
                    // RF-01: Actualizar el objeto Usuario con los datos generados por la BD
                    usuario.setIdUsuario(idUsuarioGenerado);  // Asignar el ID generado
                    usuario.setIdRol(idRol);                  // Asignar el rol
                    usuario.setEmail(correo);                 // Asignar el correo
                    usuario.setTelefono(telefono);            // Asignar el teléfono
                }
                ps.close();

                // =====================================================================
                // RF-01: PASO 2 - Insertar en la tabla CORREO_USUARIO
                // Vincula el correo electrónico con el usuario recién creado.
                // RF-32: PENDIENTE - Aquí se debería validar que el correo no exista ya.
                // =====================================================================
                if (correo != null && !correo.isEmpty()) {  // RF-30: Solo inserta si el correo no está vacío
                    String sqlCorreo = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlCorreo);
                    ps.setInt(1, idUsuarioGenerado);        // FK: ID del usuario recién creado
                    ps.setString(2, correo);                // El correo electrónico
                    ps.executeUpdate();                     // Ejecuta el INSERT
                    ps.close();
                }

                // =====================================================================
                // RF-01: PASO 3 - Insertar en la tabla TELEFONO_USUARIO
                // Vincula el teléfono con el usuario recién creado.
                // El teléfono es OPCIONAL según RF-01, por eso validamos que no sea null/vacío.
                // =====================================================================
                if (telefono != null && !telefono.isEmpty()) {  // Solo inserta si proporcionó teléfono
                    String sqlTel = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlTel);
                    ps.setInt(1, idUsuarioGenerado);            // FK: ID del usuario recién creado
                    ps.setString(2, telefono);                  // El número de teléfono
                    ps.executeUpdate();                         // Ejecuta el INSERT
                    ps.close();
                }

                // RF-01 Restricción 3: CONFIRMAR TRANSACCIÓN (COMMIT)
                // Le decimos a MySQL: "Todo salió perfecto. ¡Guarda los cambios de las 3 tablas DEFINITIVAMENTE!"
                con.commit();
                System.out.println("DAO: Usuario registrado con ID: " + idUsuarioGenerado);
            } else {
                // RF-01: Si el primer INSERT falló, deshacemos todo
                con.rollback();
            }

        } catch (SQLException e) {
            // ERROR: Algo falló durante el proceso
            // RNF-08: Loguear error descriptivo
            System.err.println("Error en registrarUsuario: " + e.getMessage());
            try {
                // RF-01 Restricción 3: ROLLBACK - Deshacer TODAS las inserciones parciales
                // Si insertó el usuario pero falló el correo, se BORRA el usuario también
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            idUsuarioGenerado = -1;  // Marcar como error
        } finally {
            // CERRAR TODOS LOS RECURSOS (conexiones, consultas, resultados)
            // RNF-10: Liberar recursos para que otros usuarios puedan conectarse
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);  // Restaurar el comportamiento automático
                    con.close();              // Cerrar la conexión a la BD
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idUsuarioGenerado;  // RF-01: Retorna el ID generado (>0 = éxito, -1 = error)
    }

    /**
     * RF-28: Cuenta la cantidad total de trabajadores (id_rol = 2) en el sistema.
     * Se usa en LoginServlet para cargar estadísticas del dashboard/perfil.
     * RNF-02: Usa PreparedStatement (aunque sin parámetros en este caso).
     * 
     * @return int Cantidad de usuarios con rol Trabajador en la BD
     */
    public int contarTrabajadores() {
        int cantidad = 0;              // Variable para el resultado, empieza en 0
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // RF-28: Consulta SQL que cuenta usuarios con id_rol = 2 (Trabajador)
            String sql = "SELECT COUNT(*) FROM USUARIO WHERE id_rol = 2";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1);  // RF-28: Leer el resultado del COUNT
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
        return cantidad;  // RF-28: Retorna la cantidad de trabajadores
    }

    /**
     * Lista todos los trabajadores (idRol=2) con su negocio asignado (si tienen).
     */
    public java.util.List<Usuario> listarTrabajadores() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT u.id_usuario, u.nombre, c.correo_electronico, " +
                         "n.id_negocio, n.nombre AS nombre_negocio " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                         "LEFT JOIN USUARIO_NEGOCIO un ON u.id_usuario = un.id_usuario " +
                         "LEFT JOIN NEGOCIO n ON un.id_negocio = n.id_negocio " +
                         "WHERE u.id_rol = 2 " +
                         "ORDER BY u.nombre";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("correo_electronico"));
                // Guardamos el nombre del negocio temporalmente en el campo telefono
                // para no crear un nuevo modelo (truco sencillo)
                String nombreNeg = rs.getString("nombre_negocio");
                u.setTelefono(nombreNeg != null ? nombreNeg : "Sin asignar");
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar trabajadores: " + e.getMessage());
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
     * Asigna un negocio a un trabajador.
     * Si ya tiene un negocio asignado, lo reemplaza.
     */
    public boolean asignarNegocio(int idUsuario, int idNegocio) {
        Connection con = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        boolean exito = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);

            // 1. Borrar asignación anterior (si existe)
            String sqlDel = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            psDelete = con.prepareStatement(sqlDel);
            psDelete.setInt(1, idUsuario);
            psDelete.executeUpdate();

            // 2. Insertar nueva asignación
            String sqlIns = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
            psInsert = con.prepareStatement(sqlIns);
            psInsert.setInt(1, idUsuario);
            psInsert.setInt(2, idNegocio);
            int filas = psInsert.executeUpdate();

            if (filas > 0) {
                con.commit();
                exito = true;
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Error al asignar negocio: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (psDelete != null) psDelete.close();
                if (psInsert != null) psInsert.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    /**
     * Obtiene el negocio asignado a un trabajador.
     * Retorna null si no tiene negocio asignado.
     */
    public com.inventario.model.Negocio obtenerNegocioAsignado(int idUsuario) {
        com.inventario.model.Negocio negocio = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT n.id_negocio, n.nombre, n.direccion, n.estado " +
                         "FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                         "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                negocio = new com.inventario.model.Negocio();
                negocio.setIdNegocio(rs.getInt("id_negocio"));
                negocio.setNombre(rs.getString("nombre"));
                negocio.setDireccion(rs.getString("direccion"));
                negocio.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener negocio asignado: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return negocio;
    }
}
