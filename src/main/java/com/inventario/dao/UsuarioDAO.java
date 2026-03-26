package com.inventario.dao; // Paquete que agrupa los objetos de acceso a datos (DAOs)

import com.inventario.util.Conexion; // Importamos la clase para conectar con la base de datos
import com.inventario.util.Cifrado; // Importamos la herramienta para encriptar contraseñas
import com.inventario.model.Usuario; // Importamos el molde (modelo) del objeto Usuario
import java.sql.Connection; // Clase de Java para la conexión física
import java.sql.PreparedStatement; // Clase para preparar consultas SQL seguras
import java.sql.ResultSet; // Clase para recibir los resultados de la base de datos
import java.sql.SQLException; // Clase para manejar errores de SQL

/**
 * Clase UsuarioDAO (Data Access Object).
 * 
 * Esta clase funciona como el "bibliotecario" de los usuarios.
 * Se encarga de buscar, guardar, editar y borrar personas (Cajeros/Admins) en
 * la base de datos.
 * Utiliza el objeto 'Connection' para hablar con MySQL y el objeto 'Usuario'
 * para mover la data en Java.
 */
public class UsuarioDAO { // Declaración de la clase pública

    /**
     * MÉTODOS DE LA CLASE:
     * Son las funciones o acciones que este objeto puede realizar.
     */

    /**
     * Función para validar el ingreso (Login).
     * Recibe el email y la contraseña, y busca si existe una instancia (objeto) que
     * coincida.
     * 
     * @param email    Correo electrónico del usuario
     * @param password Clave en texto plano (se cifra antes de comparar)
     * @return Un objeto Usuario lleno de datos si tuvo éxito, o null si falló.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null; // Declaramos la variable que guardará al objeto, empezamos en vacío (null)
        Connection con = null; // Variable para la conexión
        PreparedStatement ps = null; // Variable para la consulta
        ResultSet rs = null; // Variable para el resultado

        try {
            con = Conexion.getConexion(); // Abrimos la puerta a la base de datos

            // CONSULTA SQL: Une 3 tablas para sacar la ficha completa:
            // 1. USUARIO (u): Datos base como nombre e ID.
            // 2. CORREO_USUARIO (c): Su dirección de email.
            // 3. TELEFONO_USUARIO (t): Su número de teléfono (si tiene).
            String sql = "SELECT u.id_usuario, u.id_rol, u.nombre, u.password, c.correo_electronico, t.numero_telefono "
                    +
                    "FROM USUARIO u " +
                    "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                    "LEFT JOIN TELEFONO_USUARIO t ON u.id_usuario = t.id_usuario " +
                    "WHERE c.correo_electronico = ? AND u.password = ?"; // Los '?' son huecos que llenaremos luego

            ps = con.prepareStatement(sql); // Preparamos la consulta en el servidor
            ps.setString(1, email); // Llenamos el primer '?' con el correo
            ps.setString(2, Cifrado.sha256(password)); // Llenamos el segundo '?' con la clave ya encriptada

            rs = ps.executeQuery(); // Ejecutamos la búsqueda en las tablas

            if (rs.next()) { // Si la base de datos encontró una fila que coincida...
                usuario = new Usuario(); // Instanciamos (creamos) un nuevo objeto Usuario
                // Llenamos los atributos del objeto con la información de las columnas de la
                // tabla:
                usuario.setIdUsuario(rs.getInt("id_usuario")); // Guardamos el ID
                usuario.setIdRol(rs.getInt("id_rol")); // Guardamos el Rol (1=Admin, 2=Cajero)
                usuario.setNombre(rs.getString("nombre")); // Guardamos el nombre real
                usuario.setPassword(rs.getString("password")); // Guardamos la clave (el hash)
                usuario.setEmail(rs.getString("correo_electronico")); // Guardamos el email hallado
                usuario.setTelefono(rs.getString("numero_telefono")); // Guardamos el teléfono hallado

                // --- BUSCAR PERMISOS (Poderes del usuario) ---
                PreparedStatement psPermisos = null; // Otra consulta para los permisos
                ResultSet rsPermisos = null; // Resultado de los permisos
                try {
                    // Consulta SQL: Busca en la tabla PERMISO cuáles están unidos al Rol de este
                    // usuario
                    String sqlPermisos = "SELECT p.nombre FROM PERMISO p " +
                            "INNER JOIN ROL_PERMISOS rp ON p.id_permiso = rp.id_permiso " +
                            "WHERE rp.id_rol = ?";
                    psPermisos = con.prepareStatement(sqlPermisos);
                    psPermisos.setInt(1, usuario.getIdRol()); // Le pasamos el ID del rol que sacamos antes
                    rsPermisos = psPermisos.executeQuery(); // Buscamos los permisos

                    java.util.List<String> listaPermisos = new java.util.ArrayList<>(); // Creamos una lista (bolsa)
                    while (rsPermisos.next()) { // Mientras haya más permisos en la tabla...
                        listaPermisos.add(rsPermisos.getString("nombre")); // Los metemos a la lista
                    }
                    usuario.setPermisos(listaPermisos); // Le entregamos la lista de permisos al objeto Usuario

                } catch (Exception ex) {
                    ex.printStackTrace(); // Si falla buscando permisos, mostramos el error en consola
                } finally {
                    if (rsPermisos != null)
                        rsPermisos.close(); // Cerramos el resultado de permisos
                    if (psPermisos != null)
                        psPermisos.close(); // Cerramos la consulta de permisos
                }
            }

        } catch (SQLException e) { // Si ocurre un error de base de datos...
            System.err.println(
                    "Falla crítica en control de inicio de sesión de Credenciales Usuario BD: " + e.getMessage());
        } finally {
            // BLOQUE FINALLY: Pase lo que pase, debemos cerrar las conexiones para no
            // gastar memoria
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuario; // Devolvemos el objeto Usuario lleno o vacío (null)
    }

    /**
     * Función para registrar un nuevo Cajero/Vendedor.
     * Es una operación transaccional: registra en tablas de Usuario, Correo y
     * Telefono.
     * 
     * @return El ID generado para el nuevo usuario, o -1 si hubo error.
     */
    public int registrarUsuario(Usuario usuario, String correo, String telefono, String rolNombre) {
        Connection con = null; // Conexión
        PreparedStatement ps = null; // Consulta
        ResultSet rs = null; // Resultado
        int idUsuarioGenerado = -1; // Empezamos asumiendo que el registro falló (-1)

        try {
            con = Conexion.getConexion(); // Conectar con MySQL
            con.setAutoCommit(false); // HERENCIA DE TRANSACCIÓN: No guardes nada hasta que yo te diga "commit"

            // PASO 1: Buscar qué número de ID tiene el Rol (ej: buscar "Cajero" para saber
            // que es el ID 2)
            String sqlRol = "SELECT id_rol FROM ROL WHERE nombre_rol = ?";
            ps = con.prepareStatement(sqlRol);
            ps.setString(1, rolNombre); // Le pasamos el nombre del rol (Cajero/Administrador)
            rs = ps.executeQuery(); // Buscamos en la tabla ROL

            int idRol = 2; // Por defecto asumimos 2 (Trabajador)
            if (rs.next()) {
                idRol = rs.getInt("id_rol"); // Si lo encontramos, tomamos el ID real de la base de datos
            }
            ps.close(); // Cerramos esta pequeña consulta del rol

            // PASO 2: Insertar en la tabla USUARIO los datos básicos
            String sqlUsuario = "INSERT INTO USUARIO (id_rol, nombre, password) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS); // Pedimos que nos devuelva
                                                                                            // el ID creado
            ps.setInt(1, idRol); // Primer '?' : el Rol
            ps.setString(2, usuario.getNombre()); // Segundo '?' : su nombre
            ps.setString(3, Cifrado.sha256(usuario.getPassword())); // Tercer '?' : su contraseña ya encriptada

            int filasAfectadas = ps.executeUpdate(); // Ejecutamos la inserción (Guardamos)

            if (filasAfectadas > 0) { // Si se logró guardar el usuario...
                rs = ps.getGeneratedKeys(); // Obtenemos el ID que el servidor le asignó automáticamente
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1); // Guardamos ese ID (ej: el usuario nro 50)
                    // Sincronizamos el objeto usuario con los datos de la base de datos:
                    usuario.setIdUsuario(idUsuarioGenerado);
                    usuario.setIdRol(idRol);
                    usuario.setEmail(correo);
                    usuario.setTelefono(telefono);
                }
                ps.close(); // Cerramos consulta

                // PASO 3: Insertar el Correo en la tabla CORREO_USUARIO
                if (correo != null && !correo.isEmpty()) {
                    String sqlCorreo = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlCorreo);
                    ps.setInt(1, idUsuarioGenerado); // ID del usuario que acabamos de crear
                    ps.setString(2, correo); // Su dirección de correo
                    ps.executeUpdate(); // Guardamos correo
                    ps.close();
                }

                // PASO 4: Insertar el Teléfono en la tabla TELEFONO_USUARIO
                if (telefono != null && !telefono.isEmpty()) {
                    String sqlTel = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlTel);
                    ps.setInt(1, idUsuarioGenerado); // ID del usuario creado
                    ps.setString(2, telefono); // Su número telefónico
                    ps.executeUpdate(); // Guardamos teléfono
                    ps.close();
                }

                con.commit(); // CONFIRMACIÓN FINAL: Si todo salió bien, guardamos definitivamente en disco
            } else {
                con.rollback(); // DESHACER: Si el usuario no se guardó, cancelamos todo el proceso
            }

        } catch (SQLException e) { // Si estalla un error...
            System.err.println("Falla creando empleado: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback(); // DESHACER CAMBIOS por seguridad (Rollback)
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            idUsuarioGenerado = -1; // Marcamos error
        } finally {
            // Limpieza de recursos
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null) {
                    con.setAutoCommit(true); // Restauramos el comportamiento normal de la conexión
                    con.close(); // Cerramos la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idUsuarioGenerado; // Retornamos el ID o el -1 en caso de fallo
    }

    /**
     * Función que cuenta cuántos trabajadores (rol 2) existen en el sistema.
     * Utiliza la función COUNT de SQL sobre la tabla USUARIO.
     */
    public int contarTrabajadores() {
        int cantidad = 0; // Variable para el contador
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM USUARIO WHERE id_rol = 2"; // Consulta que suma filas
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1); // Obtenemos el número resultante del conteo
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cantidad;
    }

    /**
     * Función para listar todos los trabajadores y saber en qué negocio laboran.
     * Cruza (JOIN) las tablas de Usuario, Correo, Vínculo de Negocio y Negocio
     * propiamente dicho.
     * 
     * @return Una lista de objetos Usuario con sus datos de contacto y laboral.
     */
    public java.util.List<Usuario> listarTrabajadores() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>(); // Creamos la lista (colección) de resultados
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // CONSULTA SQL:
            // - u: Usuario (nombre)
            // - c: Correo electrónico
            // - un: Vínculo con el negocio (si tiene uno asignado)
            // - n: El nombre del negocio o bar donde trabaja
            String sql = "SELECT u.id_usuario, u.nombre, c.correo_electronico, " +
                    "n.id_negocio, n.nombre AS nombre_negocio " +
                    "FROM USUARIO u " +
                    "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                    "LEFT JOIN USUARIO_NEGOCIO un ON u.id_usuario = un.id_usuario " +
                    "LEFT JOIN NEGOCIO n ON un.id_negocio = n.id_negocio " +
                    "WHERE u.id_rol = 2 " +
                    "ORDER BY u.nombre"; // Ordenamos alfabéticamente por nombre
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) { // Recorremos fila por fila lo que devolvió la base de datos
                Usuario u = new Usuario(); // Instanciamos un nuevo molde para este trabajador
                u.setIdUsuario(rs.getInt("id_usuario")); // Ponemos ID
                u.setNombre(rs.getString("nombre")); // Ponemos Nombre
                u.setEmail(rs.getString("correo_electronico")); // Ponemos Email

                String nombreNeg = rs.getString("nombre_negocio"); // Sacamos el nombre del local comercial
                // REGLA DE NEGOCIO: Si no tiene local, mostramos "Sin asignar"
                u.setTelefono(nombreNeg != null ? nombreNeg : "Sin asignar");
                lista.add(u); // Añadimos este objeto a nuestra bolsa (lista) de trabajadores
            }
        } catch (SQLException e) {
            System.err.println("Falla listar empleados: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; // Devolvemos la lista llena
    }

    /**
     * Función para asignar (contratar) a un trabajador en un negocio específico.
     * Borra cualquier asignación previa y crea la nueva en la tabla
     * USUARIO_NEGOCIO.
     * 
     * @param idUsuario Cédula/ID del empleado
     * @param idNegocio ID del bar o negocio
     * @return true si funcionó, false si no.
     */
    public boolean asignarNegocio(int idUsuario, int idNegocio) {
        Connection con = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        boolean exito = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Operación segura (Transaccional)

            // PASO 1: Eliminar si ya estaba asignado a otro bar (para que solo trabaje en
            // uno al tiempo)
            String sqlDel = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            psDelete = con.prepareStatement(sqlDel);
            psDelete.setInt(1, idUsuario);
            psDelete.executeUpdate(); // Borramos rastro laboral anterior

            // PASO 2: Insertar el nuevo vínculo laboral
            String sqlIns = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
            psInsert = con.prepareStatement(sqlIns);
            psInsert.setInt(1, idUsuario); // '?' : Empleado
            psInsert.setInt(2, idNegocio); // '?' : Local

            int filas = psInsert.executeUpdate(); // Ejecutamos la unión
            if (filas > 0) { // Si se guardó correctamente...
                con.commit(); // Guardamos cambios (Confirmación)
                exito = true;
            } else {
                con.rollback(); // Deshacemos si algo falló
            }
        } catch (SQLException e) {
            System.err.println("Error rotando/asignando tienda de trabajador: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            try {
                if (con != null)
                    con.setAutoCommit(true);
                if (psDelete != null)
                    psDelete.close();
                if (psInsert != null)
                    psInsert.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Función para obtener el objeto Negocio donde trabaja un usuario.
     * 
     * @param idUsuario ID de la persona
     * @return Un objeto Negocio lleno con la info del bar donde labora.
     */
    public com.inventario.model.Negocio obtenerNegocioAsignado(int idUsuario) {
        com.inventario.model.Negocio negocio = null; // Empezamos en nulo (no tiene negocio)
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // CONSULTA SQL: Une NEGOCIO con la tabla puente de asignación filtrando por el
            // usuario
            String sql = "SELECT n.id_negocio, n.nombre, n.direccion, n.estado " +
                    "FROM NEGOCIO n " +
                    "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                    "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Le damos el ID del cajero
            rs = ps.executeQuery();

            if (rs.next()) { // Si encontramos dónde trabaja...
                negocio = new com.inventario.model.Negocio(); // Instanciamos el objeto Negocio
                // Llenamos sus características:
                negocio.setIdNegocio(rs.getInt("id_negocio"));
                negocio.setNombre(rs.getString("nombre"));
                negocio.setDireccion(rs.getString("direccion"));
                negocio.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Falla averiguando qué local lo tiene contratado: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return negocio; // Devolvemos el objeto con la tienda
    }

    /**
     * Función que chequea si un negocio específico tiene empleados asignados.
     * Útil antes de intentar borrar un negocio (para no borrar uno con gente
     * adentro).
     */
    public boolean negocioTieneTrabajador(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tiene = false;

        try {
            con = Conexion.getConexion();
            // Consulta SQL: Cuenta si hay personas con rol 2 unidas a ese ID de negocio
            String sql = "SELECT COUNT(*) FROM USUARIO_NEGOCIO un " +
                    "INNER JOIN USUARIO u ON un.id_usuario = u.id_usuario " +
                    "WHERE un.id_negocio = ? AND u.id_rol = 2";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) { // Si el conteo es mayor a cero...
                tiene = true; // Sí hay gente trabajando allí
            }
        } catch (SQLException e) {
            System.err.println("Falla chequeo nomina bar: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tiene;
    }

    /**
     * Función para desasignar (despedir) a un trabajador.
     * Borra la fila correspondiente en la tabla USUARIO_NEGOCIO.
     */
    public boolean desasignarNegocio(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            // SQL: Borra la relación laboral de la persona
            String sql = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            if (ps.executeUpdate() > 0) {
                exito = true; // Se logró quitar del negocio
            }
        } catch (SQLException e) {
            System.err.println("Error echando a alguien: " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Función drástica para ELIMINAR a un trabajador por completo del sistema.
     * Borra en cascada: Correos, Teléfonos, Puesto Laboral y finalmente su Usuario
     * base.
     * Utiliza una TRANSACCIÓN (Unit of Work) para asegurar que no queden datos
     * huérfanos.
     */
    public boolean eliminarTrabajador(int idUsuario) {
        Connection con = null;
        // Declaramos varias variables de consulta para cada paso de borrado
        PreparedStatement psCor = null, psTel = null, psAsig = null, psUsu = null;
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN INICIADA: O se borra todo o no se borra nada.

            // PASO 1: Exterminar sus correos asociados en CORREO_USUARIO
            psCor = con.prepareStatement("DELETE FROM CORREO_USUARIO WHERE id_usuario = ?");
            psCor.setInt(1, idUsuario);
            psCor.executeUpdate();

            // PASO 2: Exterminar sus teléfonos asociados en TELEFONO_USUARIO
            psTel = con.prepareStatement("DELETE FROM TELEFONO_USUARIO WHERE id_usuario = ?");
            psTel.setInt(1, idUsuario);
            psTel.executeUpdate();

            // PASO 3: Exterminar su puesto de trabajo en la tabla puente USUARIO_NEGOCIO
            psAsig = con.prepareStatement("DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?");
            psAsig.setInt(1, idUsuario);
            psAsig.executeUpdate();

            // PASO 4: Eliminar definitivamente su registro raíz de la tabla USUARIO
            psUsu = con.prepareStatement("DELETE FROM USUARIO WHERE id_usuario = ?");
            psUsu.setInt(1, idUsuario);
            int rows = psUsu.executeUpdate();

            if (rows > 0) { // Si logramos borrar el registro principal...
                con.commit(); // CONFIRMACIÓN: Grabamos los borrados en la base de datos
                exito = true;
            } else {
                con.rollback(); // DESHACER: Si no borró el usuario, deshacemos los pasos 1, 2 y 3.
            }
        } catch (SQLException e) {
            System.err.println("Error grave en exterminio del trabajador: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
            } // Deshacer por error crítico
        } finally {
            // Limpieza y cierre
            try {
                if (con != null)
                    con.setAutoCommit(true);
                if (psCor != null)
                    psCor.close();
                if (psTel != null)
                    psTel.close();
                if (psAsig != null)
                    psAsig.close();
                if (psUsu != null)
                    psUsu.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Función que consulta y devuelve todos los correos registrados para un
     * usuario.
     * 
     * @return Una lista de cadenas de texto (Strings) con los emails hallados.
     */
    public java.util.List<String> listarCorreos(int idUsuario) {
        java.util.List<String> correos = new java.util.ArrayList<>(); // Bolsa para los correos
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // SQL: Selecciona solo la columna correo de la tabla respectiva
            String sql = "SELECT correo_electronico FROM CORREO_USUARIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Cedula del usuario
            rs = ps.executeQuery();
            while (rs.next()) {
                correos.add(rs.getString("correo_electronico")); // Guardamos cada email en la lista
            }
        } catch (SQLException e) {
            System.err.println("Problema busqueda e-mails : " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return correos; // Retornamos la lista de correos
    }

    /**
     * Función que consulta y devuelve todos los teléfonos registrados para un
     * usuario.
     * 
     * @return Una lista de cadenas de texto (Strings) con los números hallados.
     */
    public java.util.List<String> listarTelefonos(int idUsuario) {
        java.util.List<String> telefonos = new java.util.ArrayList<>(); // Bolsa para teléfonos
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // SQL: Selecciona los números de la tabla de teléfonos
            String sql = "SELECT numero_telefono FROM TELEFONO_USUARIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(rs.getString("numero_telefono")); // Los metemos al arreglo
            }
        } catch (SQLException e) {
            System.err.println("Falla lista celus: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return telefonos;
    }

    /**
     * Función para agregar un CORREO EXTRA a un usuario desde su perfil.
     * Verifica primero si el usuario ya tenía ese correo para no duplicarlo.
     */
    public boolean agregarCorreo(int idUsuario, String correo) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            // PASO 1: Revisa si ese usuario ya tiene registrado ese correo exacto
            String sqlCheck = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE id_usuario = ? AND correo_electronico = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) { // Si ya el conteo dio positivo...
                rs.close();
                ps.close();
                return false; // Salimos de la función sin agregar (Evitamos duplicación)
            }
            rs.close();
            ps.close();

            // PASO 2: Como es nuevo, lo guardamos con un INSERT
            String sql = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            if (ps.executeUpdate() > 0) {
                agregado = true; // Éxito guardando el correo extra
            }
        } catch (SQLException e) {
            System.err.println("Imposible inyectar coreo nuevo: " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return agregado;
    }

    /**
     * Función para agregar un TELÉFONO EXTRA a un usuario desde su perfil.
     * Funciona igual que el método de correo (comprobando duplicados).
     */
    public boolean agregarTelefono(int idUsuario, String telefono) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            // Comprobar si ya existe el número para ese ID de persona
            String sqlCheck = "SELECT COUNT(*) FROM TELEFONO_USUARIO WHERE id_usuario = ? AND numero_telefono = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) { // Si ya existe...
                rs.close();
                ps.close();
                return false; // No duplicar, salimos
            }
            rs.close();
            ps.close();

            // Guardar número nuevo en la tabla de teléfonos
            String sql = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            if (ps.executeUpdate() > 0) {
                agregado = true; // Teléfono guardado
            }
        } catch (SQLException e) {
            System.err.println("Falla agregar cel a base de dato: " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return agregado;
    }

    /**
     * Función para cambiar la contraseña de un usuario.
     * Recibe la nueva clave, la encripta y actualiza la tabla USUARIO.
     */
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        try {
            con = Conexion.getConexion();
            // SQL: Actualiza el campo password buscando al usuario por su ID
            String sql = "UPDATE USUARIO SET password = ? WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, Cifrado.sha256(nuevaPassword)); // ENCAPSULAMIENTO/SEGURIDAD: Ciframos antes de guardar
            ps.setInt(2, idUsuario); // Quién es el dueño de la clave
            if (ps.executeUpdate() > 0) {
                actualizado = true; // Clave cambiada con éxito
            }
        } catch (SQLException e) {
            System.err.println("Crash editando Clave secreta personal : " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return actualizado;
    }

    /**
     * Función que verifica si un correo electrónico ya está registrado por alguien.
     * Se usa para validar que no haya dos cuentas con el mismo @mail.
     * 
     * @return true si ya existe, false si está libre para usar.
     */
    public boolean existeCorreo(String correo) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // SQL: Cuenta cuántas veces aparece ese correo en la tabla CORREO_USUARIO
            String sql = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE correo_electronico = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, correo); // Pasamos el correo que queremos validar
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) { // Si hay uno o más...
                existe = true; // El correo está ocupado
            }
        } catch (SQLException e) {
            System.err.println("Error detector de redundacias arrobas base: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return existe;
    }
}
