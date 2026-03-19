package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.util.Cifrado;
import com.inventario.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase UsuarioDAO.
 * 
 * Se encarga de manejar a las personas del sistema: Cajeros y Administradores.
 * Valida ingresos, registra empleados nuevos y gestiona sus teléfonos o correos.
 */
public class UsuarioDAO {

    /**
     * Verifica que el correo y la contraseña coincidan para dejar entrar al sistema.
     * Busca en la tabla USUARIO, CORREO_USUARIO y PERMISO para darle los poderes necesarios (roles).
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null;        // Arranca asumiendo que el usuario no existe
        Connection con = null;         
        PreparedStatement ps = null;   
        ResultSet rs = null;           

        try {
            con = Conexion.getConexion(); // Conexion 

            // Esta consulta une 3 tablas: el USUARIO (la persona base), su CORREO, y su TELÉFONO principal
            String sql = "SELECT u.id_usuario, u.id_rol, u.nombre, u.password, c.correo_electronico, t.numero_telefono " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " + // Requiere sí o sí tener correo
                         "LEFT JOIN TELEFONO_USUARIO t ON u.id_usuario = t.id_usuario " + // Puede (o no) tener teléfono
                         "WHERE c.correo_electronico = ? AND u.password = ?"; // Revisa correo específico con clave específica

            ps = con.prepareStatement(sql);   
            ps.setString(1, email); // Inyectamos el correo que metieron en el formulario
            ps.setString(2, Cifrado.sha256(password)); // La contraseña la volvemos caracteres encriptados por seguridad (SHA256)

            rs = ps.executeQuery(); // Disparamos búsqueda

            if (rs.next()) { // Si logramos hallar esa combinación exacta (1 registro)...
                usuario = new Usuario(); // Creamos la representación del Java                                
                usuario.setIdUsuario(rs.getInt("id_usuario")); // Copiamos el ID del cajero/admin         
                usuario.setIdRol(rs.getInt("id_rol")); // 1 para Admin, 2 para Cajero (normalmente)                 
                usuario.setNombre(rs.getString("nombre")); // Juan Perez              
                usuario.setPassword(rs.getString("password")); // Hash          
                usuario.setEmail(rs.getString("correo_electronico")); // Juan@bar.com   
                usuario.setTelefono(rs.getString("numero_telefono")); // 3110000..   
                
                // Pero ahora, debemos buscar qué puede hacer en el sistema (sus PERMISOS)
                PreparedStatement psPermisos = null;
                ResultSet rsPermisos = null;
                try {
                    // Consultamos los permisos ligados al Rol del usuario
                    String sqlPermisos = "SELECT p.nombre FROM PERMISO p " +
                                         "INNER JOIN ROL_PERMISOS rp ON p.id_permiso = rp.id_permiso " +
                                         "WHERE rp.id_rol = ?";
                    psPermisos = con.prepareStatement(sqlPermisos);           
                    psPermisos.setInt(1, usuario.getIdRol()); // Damos su rol                 
                    rsPermisos = psPermisos.executeQuery();                   
                    
                    java.util.List<String> listaPermisos = new java.util.ArrayList<>();
                    while (rsPermisos.next()) { // Por cada permiso que la BD nos regrese...                             
                        String nombrePermiso = rsPermisos.getString("nombre"); // Ej: MÓDULO VENTAS
                        listaPermisos.add(nombrePermiso); // Lo guardamos en una lista "Ventas, Inventario, Configuraciones..."
                    }
                    usuario.setPermisos(listaPermisos); // Y se lo pegamos en el bolsillo al objeto Usuario                     
                    
                } catch (Exception ex) {
                    ex.printStackTrace(); // Falla extrayendo los permisos del rol general
                } finally {
                    if (rsPermisos != null) rsPermisos.close();  
                    if (psPermisos != null) psPermisos.close();  
                }
            }

        } catch (SQLException e) { // Falla de login base de datos
            System.err.println("Falla crítica en control de inicio de sesión de Credenciales Usuario BD: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();    
                if (ps != null) ps.close();    
                if (con != null) con.close();  
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return usuario; // Si no lo encontró, devuelve NULO (acceso denegado). Si lo halló, devuelve la ficha llena.
    }

    /**
     * Guarda a un nuevo empleado/Cajero en el sistema.
     * Como los datos del empleado están regados en 3 tablas distintas (Persona, Correo, Telefono),
     * este script efectúa 3 Inserciones seguidas usando una 'Transacción' de seguridad.
     */
    public int registrarUsuario(Usuario usuario, String correo, String telefono, String rolNombre) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idUsuarioGenerado = -1; // -1 asume fracaso de registro
        
        try {
            con = Conexion.getConexion();      
            con.setAutoCommit(false); // Seguro: no guarda a la persona si después falla guardar su correo. O Todo o Nada.

            // PASO 1. Averiguar qué número de Rol le corresponde a la palabra (EJ: buscar "Cajero" y darnos un id=2)
            String sqlRol = "SELECT id_rol FROM ROL WHERE nombre_rol = ?";
            ps = con.prepareStatement(sqlRol);
            ps.setString(1, rolNombre);        
            rs = ps.executeQuery();
            
            int idRol = 2; // Por si acaso las moscas, asume 2 (Trabajador)                     
            if (rs.next()) {
                idRol = rs.getInt("id_rol"); // Reemplaza si lo halló   
            }
            ps.close(); // Cierra etapa rol                       

            // PASO 2: Guardamos ahora sí en USUARIO a la persona, inyectando su rol, nombre y contraseña oculta.
            String sqlUsuario = "INSERT INTO USUARIO (id_rol, nombre, password) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS); // Pedir ID que nos dan
            ps.setInt(1, idRol);                       
            ps.setString(2, usuario.getNombre());      
            ps.setString(3, Cifrado.sha256(usuario.getPassword())); // ENCRIPTAR CLAVE AQUÍ 
            
            int filasAfectadas = ps.executeUpdate(); // Ejecuta 
            
            if (filasAfectadas > 0) { // Si guardó la persona
                rs = ps.getGeneratedKeys();            
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1); // Nos apoderamos del ID que dio BD
                    // Se lo pegamos a nuestro propio molde en Java también
                    usuario.setIdUsuario(idUsuarioGenerado);  
                    usuario.setIdRol(idRol);                  
                    usuario.setEmail(correo);                 
                    usuario.setTelefono(telefono);            
                }
                ps.close(); // Apagar query 

                // PASO 3: Con la persona creada y su ID en mano (ej: la per. Nro 43), vamos a CORREO a asociarlo.
                if (correo != null && !correo.isEmpty()) { // Validamos que SÍ digitó correo 
                    String sqlCorreo = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlCorreo);
                    ps.setInt(1, idUsuarioGenerado); // El número 43        
                    ps.setString(2, correo);         // Su arroba       
                    ps.executeUpdate();                     
                    ps.close(); // Apagar correo query
                }

                // PASO 4: Hacemos exactamente lo mismo para TELEFONO.
                if (telefono != null && !telefono.isEmpty()) {  
                    String sqlTel = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlTel);
                    ps.setInt(1, idUsuarioGenerado);            
                    ps.setString(2, telefono);                  
                    ps.executeUpdate();                         
                    ps.close();
                }

                con.commit(); // SI LLEGA HASTA ACÁ: Oficializamos el contrato en la base de datos (Todo se guardó)
            } else {
                con.rollback(); // Alguna tabla nos rebotó la data, abortar misión 
            }

        } catch (SQLException e) { // BD Estalló
            System.err.println("Falla creando empleado: " + e.getMessage());
            try {
                if (con != null) con.rollback(); // Anular Todo
            } catch (SQLException ex) { ex.printStackTrace(); }
            idUsuarioGenerado = -1;  // Informar rotundo fracaso a Java
        } finally { // Limpiemos variables
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true); // Termina bloque seguro Transaccional 
                    con.close();              
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idUsuarioGenerado;  // Devuelve ID para la tabla USUARIO_NEGOCIO posterior.
    }

    /**
     * Cuenta sencillamente a todos los empleados de Rol Nivel 2 (Trabajadores).
     */
    public int contarTrabajadores() {
        int cantidad = 0;              
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // Función COUNT de aquellos que son Trabajador (ID ROL = 2)
            String sql = "SELECT COUNT(*) FROM USUARIO WHERE id_rol = 2";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1); // Recibe el simple número
            }
        } catch (SQLException e) { e.printStackTrace(); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return cantidad;  
    }

    /**
     * Lista a todos los cajeros en el sistema, jalando además dónde trabajan actualmente (si trabajan).
     */
    public java.util.List<Usuario> listarTrabajadores() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // Trae datos basicos pero une a CORREO para su Mail, USUARIO_NEGOCIO para saber la relación laboral y a NEGOCIO para sacar el nombre del letrero comercial
            String sql = "SELECT u.id_usuario, u.nombre, c.correo_electronico, " +
                         "n.id_negocio, n.nombre AS nombre_negocio " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " + 
                         "LEFT JOIN USUARIO_NEGOCIO un ON u.id_usuario = un.id_usuario " + 
                         "LEFT JOIN NEGOCIO n ON un.id_negocio = n.id_negocio " + // Left Join porque a lo mejor lo despidieron y está flotando sin asignar local
                         "WHERE u.id_rol = 2 " +
                         "ORDER BY u.nombre"; // Organiza alfabéticamente
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario(); // Prepara ficha de empleado
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("correo_electronico"));
                
                String nombreNeg = rs.getString("nombre_negocio"); // Obtenemos el local (si existe)
                // Usamos momentáneamente el campo Telefono del objeto Java para guardar Dónde Trabaja (Hack temporal del modelo)
                u.setTelefono(nombreNeg != null ? nombreNeg : "Sin asignar"); 
                lista.add(u); // Anexa empleado
            }
        } catch (SQLException e) { System.err.println("Falla listar empleados: " + e.getMessage()); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    /**
     * Une a un trabajador a laborar en un NEGOCIO específico.
     */
    public boolean asignarNegocio(int idUsuario, int idNegocio) {
        Connection con = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        boolean exito = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Transacción para evitar duplicar el enlace

            // Elimina (Si existiése de antes) otro local donde laborara (Para no dejarlo en 2 tiendas a la vez si no queremos)
            String sqlDel = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            psDelete = con.prepareStatement(sqlDel);
            psDelete.setInt(1, idUsuario);
            psDelete.executeUpdate(); // Chau lazos anteriores

            // Ahora sí inyecta el nuevo contrato
            String sqlIns = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
            psInsert = con.prepareStatement(sqlIns);
            psInsert.setInt(1, idUsuario);
            psInsert.setInt(2, idNegocio);
            
            int filas = psInsert.executeUpdate(); // Asigna
            if (filas > 0) { // Si pegó con exito
                con.commit(); // Grabar en disco
                exito = true;
            } else {
                con.rollback(); // Falla
            }
        } catch (SQLException e) {
            System.err.println("Error rotando/asignando tienda de trabajador: " + e.getMessage());
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
     * Trae el local (Negocio) al que un trabajador fue afiliado laboralmente.
     */
    public com.inventario.model.Negocio obtenerNegocioAsignado(int idUsuario) {
        com.inventario.model.Negocio negocio = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // Unimos NEGOCIO y USUARIO_NEGOCIO filtrando por el id de la persona para sacar los datos comerciales de la heladería
            String sql = "SELECT n.id_negocio, n.nombre, n.direccion, n.estado " +
                         "FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                         "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Ingresa mi cedula cajero
            rs = ps.executeQuery();

            if (rs.next()) { // Si tiene lugar de trabajo
                negocio = new com.inventario.model.Negocio(); // Genero Negocio para darle sus atributos
                negocio.setIdNegocio(rs.getInt("id_negocio"));
                negocio.setNombre(rs.getString("nombre"));
                negocio.setDireccion(rs.getString("direccion"));
                negocio.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Falla averiguando qué local lo tiene contratado: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return negocio;
    }

    /**
     * Busca si un determinado establecimiento (Negocio) tiene cajeros o administradores asignados a él actualmente
     */
    public boolean negocioTieneTrabajador(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tiene = false;

        try {
            con = Conexion.getConexion();
            // Cuenta a cuántos hazañeros nivel 2 (trabajador) encontramos enchufados al ID negocio a la tabla Vinculo
            String sql = "SELECT COUNT(*) FROM USUARIO_NEGOCIO un " +
                         "INNER JOIN USUARIO u ON un.id_usuario = u.id_usuario " +
                         "WHERE un.id_negocio = ? AND u.id_rol = 2";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) { // Mayor a 0 significa sí hay plantilla activa contratada
                tiene = true;
            }
        } catch (SQLException e) {
            System.err.println("Falla chequeo nomina bar: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return tiene; 
    }

    /**
     * Despide o quita un empleado de su negocio asociado. (Rompe el vínculo USUARIO_NEGOCIO).
     */
    public boolean desasignarNegocio(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            // Simplemente destruye el registro de afiliación. La persona como tal (USUARIO) sigue existiendo y puede ver su correo aún.
            String sql = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) { System.err.println("Error echando a alguien: " + e.getMessage()); } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    /**
     * Elimina DEFINITIVAMENTE a la persona, trabajador, sus correos, sus telefonos, y su puesto de nuestra base de datos para siempre.
     * Es riesgoso pero se maneja en "Cascada" Transaccional.
     */
    public boolean eliminarTrabajador(int idUsuario) {
        Connection con = null;
        PreparedStatement psCor = null, psTel = null, psAsig = null, psUsu = null; 
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Transacción para evitar descabezados fantasma

            // Borra correos asociados al cajero
            psCor = con.prepareStatement("DELETE FROM CORREO_USUARIO WHERE id_usuario = ?");
            psCor.setInt(1, idUsuario);
            psCor.executeUpdate();

            // Borra telefonos asociados
            psTel = con.prepareStatement("DELETE FROM TELEFONO_USUARIO WHERE id_usuario = ?");
            psTel.setInt(1, idUsuario);
            psTel.executeUpdate();

            // Desafilia forzosamente de su puesto 
            psAsig = con.prepareStatement("DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?");
            psAsig.setInt(1, idUsuario);
            psAsig.executeUpdate();

            // Ahora sí, ya no habiendo hijos en ninguna otra tabla con él como llave foránea (Dependencia externa)... procede a extirpar la raíz USUARIO
            psUsu = con.prepareStatement("DELETE FROM USUARIO WHERE id_usuario = ?");
            psUsu.setInt(1, idUsuario);
            int rows = psUsu.executeUpdate();

            if (rows > 0) { // Si la estocada final triunfó (matar al propio Usuario)
                con.commit(); // Afirmar genocidio a disco
                exito = true;
            } else {
                con.rollback(); // Abortar
            }
        } catch (SQLException e) {
            System.err.println("Error grave en exterminio del trabajador: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (SQLException ex) {} // Deshacer todas las DELETES si al final estallaba.
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (psCor != null) psCor.close();
                if (psTel != null) psTel.close();
                if (psAsig != null) psAsig.close();
                if (psUsu != null) psUsu.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    /**
     * Trae solamente un listado en texto con los correos de una persona
     */
    public java.util.List<String> listarCorreos(int idUsuario) {
        java.util.List<String> correos = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT correo_electronico FROM CORREO_USUARIO WHERE id_usuario = ?"; // Fácil peticion a tabla externa de correos
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                correos.add(rs.getString("correo_electronico")); // Manda para arreglo
            }
        } catch (SQLException e) { System.err.println("Problema busqueda e-mails : " + e.getMessage()); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return correos;
    }

    /**
     * Trae solamente un listado en texto con los telefonos agregados por esa persona
     */
    public java.util.List<String> listarTelefonos(int idUsuario) {
        java.util.List<String> telefonos = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT numero_telefono FROM TELEFONO_USUARIO WHERE id_usuario = ?"; // Petición sencilla a celulres
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(rs.getString("numero_telefono")); // Almacena
            }
        } catch (SQLException e) { System.err.println("Falla lista celus: " + e.getMessage());} finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return telefonos; // Fin
    }

    /**
     * Se puede agregar un correo NUEVO (además del base) en el panel Configuración Perfil.
     */
    public boolean agregarCorreo(int idUsuario, String correo) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            // Averiguamos primero: Oiga jefe, ¿Acaso ese correo exacto ya lo tenía esta persona amarrado?
            String sqlCheck = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE id_usuario = ? AND correo_electronico = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) { // Si nos responde mayor a 0...
                // ... quiere decir que nos iban a enviar algo duplicado. Cortar aquí y volver falso el guardado. 
                rs.close();
                ps.close();
                return false; 
            }
            rs.close();
            ps.close();

            // Como dio limpio, sí insertamos el nuevo string correo a la tabla respectiva
            String sql = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            if (ps.executeUpdate() > 0) {
                agregado = true; // Win
            }
        } catch (SQLException e) { System.err.println("Imposible inyectar coreo nuevo: " + e.getMessage()); } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return agregado;
    }

    /**
     * Agrega un nuevo registro de un teléfono (por si el empleado quiere añadir otro whatsapp) desde Editar Perfil
     */
    public boolean agregarTelefono(int idUsuario, String telefono) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            // Igual al correo, revisa si este mismo numero ya no existia primero en esa persona
            String sqlCheck = "SELECT COUNT(*) FROM TELEFONO_USUARIO WHERE id_usuario = ? AND numero_telefono = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) { // Cortar si repiten campo
                rs.close();
                ps.close();
                return false; 
            }
            rs.close();
            ps.close();

            // Insertar de lo lindo un nuevo elemento 
            String sql = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            if (ps.executeUpdate() > 0) {
                agregado = true; // Win
            }
        } catch (SQLException e) { System.err.println("Falla agregar cel a base de dato: " + e.getMessage()); } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return agregado;
    }

    /**
     * Sirve para cuando desde ConfigurarPerfil el usuario o admin quiere cambiar su PIN o Contraseña
     */
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        try {
            con = Conexion.getConexion();
            // Actualiza únicamente la casilla contraseña de la tabla fundamental USUARIO localizando al hombre por ID
            String sql = "UPDATE USUARIO SET password = ? WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, Cifrado.sha256(nuevaPassword)); // Se empaqueta en Hash de una vez para que no viaje por ahi como "12345" sino "Axfv239df..."
            ps.setInt(2, idUsuario); // El elegido a cambiar
            if (ps.executeUpdate() > 0) {
                actualizado = true; // Todo ok, confirmacion
            }
        } catch (SQLException e) { System.err.println("Crash editando Clave secreta personal : " + e.getMessage()); } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }

    /**
     * Cuenta cuántas veces alguien en el sistema ha escogido este correo (Sirve para impedir crear 2 empleados con un mismo correo (Aplica a todas las cuentas))
     */
    public boolean existeCorreo(String correo) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // Cuenta a cuántos hay en absolutamente todas las cuentas del servidor (La columna de tabla CORREO) y ve si hay al menos uno idéntico al String enviado desde el JSP
            String sql = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE correo_electronico = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, correo); // Ingresa el string recibido para su cruce
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) { // Si es más de 0...
                existe = true; // Sí exiten clones con tu correo, busca otro. True.
            }
        } catch (SQLException e) { System.err.println("Error detector de redundacias arrobas base: " + e.getMessage()); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe; // boolean decision
    }
}
