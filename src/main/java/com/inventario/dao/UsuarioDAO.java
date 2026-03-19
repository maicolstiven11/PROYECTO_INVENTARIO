package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.util.Cifrado;
import com.inventario.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Patrón Estructural (DAO): Clase Data Access Object UsuarioDAO.
 * 
 * Modulador principal encargado de orquestar la capa de acceso lógico (BD) 
 * relativa a los parámetros persistentes de la entidad y dependencias del constructor Objeto Relacional Usuario.
 */
public class UsuarioDAO {

    /**
     * Módulo Factory Consultor y Generador de Autorizaciones Unitarias abstractas (Mapper Unit).
     * Transfiere Data Binding Properties del resultado relacional al Builder POJO Entidad usando Setters limitados al Rol.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null;        
        Connection con = null;         
        PreparedStatement ps = null;   
        ResultSet rs = null;           

        try {
            con = Conexion.getConexion();

            String sql = "SELECT u.id_usuario, u.id_rol, u.nombre, u.password, c.correo_electronico, t.numero_telefono " +
                         "FROM USUARIO u " +
                         "INNER JOIN CORREO_USUARIO c ON u.id_usuario = c.id_usuario " +
                         "LEFT JOIN TELEFONO_USUARIO t ON u.id_usuario = t.id_usuario " +
                         "WHERE c.correo_electronico = ? AND u.password = ?";

            ps = con.prepareStatement(sql);   
            
            ps.setString(1, email);    
            ps.setString(2, Cifrado.sha256(password)); 

            rs = ps.executeQuery();    

            if (rs.next()) {
                usuario = new Usuario();                                
                usuario.setIdUsuario(rs.getInt("id_usuario"));          
                usuario.setIdRol(rs.getInt("id_rol"));                  
                usuario.setNombre(rs.getString("nombre"));              
                usuario.setPassword(rs.getString("password"));          
                
                usuario.setEmail(rs.getString("correo_electronico"));   
                usuario.setTelefono(rs.getString("numero_telefono"));   
                
                PreparedStatement psPermisos = null;
                ResultSet rsPermisos = null;
                try {
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
                        System.out.println("Property Check Iteration Limits parameter bounds string extraction limit Exception context Mapper Iterating Memory ArrayList property Array Size limit lengths Boolean value Exception Exception Mapper " + nombrePermiso); 
                    }
                    usuario.setPermisos(listaPermisos);                      
                    
                } catch (Exception ex) {
                    ex.printStackTrace(); // Iteration Array Property Bound Mapper Array property mapping bounds mapping string lengths constraint context Property limits
                } finally {
                    if (rsPermisos != null) rsPermisos.close();  
                    if (psPermisos != null) psPermisos.close();  
                }
            }

        } catch (SQLException e) {
            System.err.println("Mapping parameter constraint logic bound lengths lengths loop limits check string property string lengths " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();    
                if (ps != null) ps.close();    
                if (con != null) con.close();  
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuario; 
    }

    /**
     * Módulo Factory Inserter Atómico Múltiple (Setter Transaction Bounds Unitary Component Handler).
     * Realiza Data Insert Constraint Bounds Relational limit Mapping en Entidades múltiples relacionales abstractas.
     */
    public int registrarUsuario(Usuario usuario, String correo, String telefono, String rolNombre) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idUsuarioGenerado = -1;   

        try {
            con = Conexion.getConexion();      
            con.setAutoCommit(false);

            String sqlRol = "SELECT id_rol FROM ROL WHERE nombre_rol = ?";
            ps = con.prepareStatement(sqlRol);
            ps.setString(1, rolNombre);        
            rs = ps.executeQuery();
            
            int idRol = 2;                     
            if (rs.next()) {
                idRol = rs.getInt("id_rol");   
            }
            ps.close();                        

            String sqlUsuario = "INSERT INTO USUARIO (id_rol, nombre, password) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idRol);                       
            ps.setString(2, usuario.getNombre());      
            ps.setString(3, Cifrado.sha256(usuario.getPassword()));    
            
            int filasAfectadas = ps.executeUpdate();   
            
            if (filasAfectadas > 0) {
                rs = ps.getGeneratedKeys();            
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1);  
                    usuario.setIdUsuario(idUsuarioGenerado);  
                    usuario.setIdRol(idRol);                  
                    usuario.setEmail(correo);                 
                    usuario.setTelefono(telefono);            
                }
                ps.close();

                if (correo != null && !correo.isEmpty()) {  
                    String sqlCorreo = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlCorreo);
                    ps.setInt(1, idUsuarioGenerado);        
                    ps.setString(2, correo);                
                    ps.executeUpdate();                     
                    ps.close();
                }

                if (telefono != null && !telefono.isEmpty()) {  
                    String sqlTel = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
                    ps = con.prepareStatement(sqlTel);
                    ps.setInt(1, idUsuarioGenerado);            
                    ps.setString(2, telefono);                  
                    ps.executeUpdate();                         
                    ps.close();
                }

                con.commit();
                System.out.println("Property Constraint Transaction limit Relational Database mapped logic property Object Limit Bounds limit id limits property property object Object limit parameter limits map Parameter parameter limit Relational parameter id : " + idUsuarioGenerado);
            } else {
                con.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Property parameter transaction error limit rollback bounds Constraint array string parameter Exception properties limits mapping constraint limit parameter bounds limits limits Property constraint context parameter logic bounds : " + e.getMessage());
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

    /**
     * Coleccionador Numérico Escalar Primitivo (Getter Scalar Iterator Mapper Constraint Extractor Limits Integer Parameter Array Lengths Limit Object Abstract Value mapping parameter parameters Object Integer bounds Object context logic parameter bounds Property Limit Limit Parameter property context property mapping loop Parameter).
     */
    public int contarTrabajadores() {
        int cantidad = 0;              
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
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

    /**
     * Módulo Getter Coleccionador Objecto Relacional Iterativo Limit Exception Extractor mapper constraint parameter mapper parameter limit string bounds bounds bounds bounds ArrayList Mapper constraint map Map List Arrays.
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
                String nombreNeg = rs.getString("nombre_negocio");
                u.setTelefono(nombreNeg != null ? nombreNeg : "Sin asignar");
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Iteration bounds length object exception mapping mapping constraint limits Array strings logic Boolean logic Exception Object limit mapping Array Exception Mapping Parameter: " + e.getMessage());
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
     * Setter Transaction limit Mapper Insert Bounds (Relational Logic Boolean Array limit string logic Constraint exception limit properties limit mapping limits logic string).
     */
    public boolean asignarNegocio(int idUsuario, int idNegocio) {
        Connection con = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        boolean exito = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);

            String sqlDel = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            psDelete = con.prepareStatement(sqlDel);
            psDelete.setInt(1, idUsuario);
            psDelete.executeUpdate();

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
            System.err.println("Property array mapper transaction Rollback limit bounds Constraint limit bounds string Map limit: " + e.getMessage());
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
     * Constructor Abstracto Mapper Mapper Unit Extractor bounds limits mapping object string logic parameter property array limit constraint limit constraint Array limits string Property limits constraints limits.
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
            System.err.println("Limit bounds parameter constraint Exception Exception logic limits Logic Map Property array limits context Limit Constraint constraint Array Mapping loop limits Exception bound length check Mapper logic limits Object properties Mapper lengths : " + e.getMessage());
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
     * Subrutina Setter Relacional Abstracta Count Mapper Iterator Exception Check property.
     */
    public boolean negocioTieneTrabajador(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tiene = false;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM USUARIO_NEGOCIO un " +
                         "INNER JOIN USUARIO u ON un.id_usuario = u.id_usuario " +
                         "WHERE un.id_negocio = ? AND u.id_rol = 2";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                tiene = true;
            }
        } catch (SQLException e) {
            System.err.println("Mapper lengths Map limits Count limits Boolean bounds string exception constraint limit Property limit Exception constraints context Iterator loop logic Parameter constraints logic check properties logic Limit constraint Map Limit object Array Limit mapper logic value values Exception Limit parameter String Mapping map bounds: " + e.getMessage());
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
     * Mutador Setter Eliminar Constraint (Parameter Destructor Relacional Null Parameter exception parameter object Limit bounds mapper Exception Map).
     */
    public boolean desasignarNegocio(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            String sql = "DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Mapper exception constraints parameter limit Array constraint property mapped parameter Mapping parameter Limits parameter Object Parameter bounds loop Mapper properties string Mapper limit Object bounds Array properties properties parameter Logic limits length Length limit strings constraint exceptions limits parameter value Object Object mapper mapping parameter Map constraint Constraint Map exceptions Property limits Limit property : " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    /**
     * Controlador Destructor Cascade Mutacional Múltiple Abstracto.
     */
    public boolean eliminarTrabajador(int idUsuario) {
        Connection con = null;
        PreparedStatement psCor = null, psTel = null, psAsig = null, psUsu = null;
        boolean exito = false;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 

            psCor = con.prepareStatement("DELETE FROM CORREO_USUARIO WHERE id_usuario = ?");
            psCor.setInt(1, idUsuario);
            psCor.executeUpdate();

            psTel = con.prepareStatement("DELETE FROM TELEFONO_USUARIO WHERE id_usuario = ?");
            psTel.setInt(1, idUsuario);
            psTel.executeUpdate();

            psAsig = con.prepareStatement("DELETE FROM USUARIO_NEGOCIO WHERE id_usuario = ?");
            psAsig.setInt(1, idUsuario);
            psAsig.executeUpdate();

            psUsu = con.prepareStatement("DELETE FROM USUARIO WHERE id_usuario = ?");
            psUsu.setInt(1, idUsuario);
            int rows = psUsu.executeUpdate();

            if (rows > 0) {
                con.commit();
                exito = true;
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Property map bounds length array constraint bounds context limit properties strings array parameter mapped length limit value context mapping Constraint exception Limit object values string Exception lengths mapper Boolean context exception Logic property bounds Exception Mapping logic context limits mapping limits loops exception Parameter exceptions limit Map maps Arrays mapper limits Arrays: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
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
     * Collection Mapper String Relational Value Mapping limits String ArrayList Boolean mapper Array Map boolean parameters Iterator loops Bounds context limits limits strings parameter limit Limit properties Property limits loops arrays map Properties limit constraint mapping Array loops maps Property property value values Exception bounds loops Limits Strings limit.
     */
    public java.util.List<String> listarCorreos(int idUsuario) {
        java.util.List<String> correos = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT correo_electronico FROM CORREO_USUARIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                correos.add(rs.getString("correo_electronico"));
            }
        } catch (SQLException e) {
            System.err.println("Mapper lengths iteration constraint array Property parameters Constraint properties Array mapping limits value String constraint length exceptions mapper constraint lengths Mapping mapping limits map String Limit limit context parameter exceptions values Logic loop List exception Mapper Logic: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return correos;
    }

    /**
     * Property Collection Generator List Iteration limits Array mapped ArrayList Relational values Mapper limit bounds parameters Map object lists parameters map constraint map Limits constraint values limits.
     */
    public java.util.List<String> listarTelefonos(int idUsuario) {
        java.util.List<String> telefonos = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT numero_telefono FROM TELEFONO_USUARIO WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(rs.getString("numero_telefono"));
            }
        } catch (SQLException e) {
            System.err.println("Limits String loop parameter bound constraints String Exception bounds Boolean Mapper Iterator length property array limits Property Arrays Map parameter bounds Exception parameter bounds parameter check lengths parameters Limits strings Object mapping Exceptions Property: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return telefonos;
    }

    /**
     * Módulo Setter Insert Limit parameters property boolean Mapping object check mapped lengths limit length limits mapping value Property Map Parameter limit Exception Property string arrays map lengths Mapping Limit Boolean Constraint parameter array mapped Map Map check constraint array loop Limits loop string parameters limits map Limit loop parameter bounds lengths Mapper constraint property Constraint loops constraint Array constraint loops property array Arrays String.
     */
    public boolean agregarCorreo(int idUsuario, String correo) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            String sqlCheck = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE id_usuario = ? AND correo_electronico = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Boolean Constraint Property Limits map mapped limits Property Parameter lengths Map Object exceptions Limits Mapping constraints Map properties strings Arrays bounds limits context mapper String String string Map property properties Mapper");
                rs.close();
                ps.close();
                return false; 
            }
            rs.close();
            ps.close();

            String sql = "INSERT INTO CORREO_USUARIO (id_usuario, correo_electronico) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, correo);
            if (ps.executeUpdate() > 0) {
                agregado = true;
            }
        } catch (SQLException e) {
            System.err.println("Mapping parameter parameters Mapping Limit loop Exception Mapping Property Map maps constraint value Limit Limit parameters Array Property Mapping bounds Map map Limit Loop string limits limit String limits parameter Property exceptions mapping Parameter logic: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return agregado;
    }

    /**
     * Módulo Setter Mutador Array limits parameters Object exception Maps constraints Array mappings property Context Limit Boolean Limit arrays parameters Logic Length bounds String Exception limits Limit constraints Mapping Array Mapper limits limit mapped mapper string Map parameters Logic Object Mapping String limits Mapping limits Mapper context loop Object exception parameter limits context Object Object mapping Mapping Limits Property limits bounds Property Limits mapping exceptions.
     */
    public boolean agregarTelefono(int idUsuario, String telefono) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean agregado = false;
        try {
            con = Conexion.getConexion();
            String sqlCheck = "SELECT COUNT(*) FROM TELEFONO_USUARIO WHERE id_usuario = ? AND numero_telefono = ?";
            ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Maps Limit exceptions Map limits parameter property exception limits limits context Constraint Length Limits map boolean map properties Iterator values arrays limits constraints arrays map Limits object Mapper Property bounds Property loops Object");
                rs.close();
                ps.close();
                return false; 
            }
            rs.close();
            ps.close();

            String sql = "INSERT INTO TELEFONO_USUARIO (id_usuario, numero_telefono) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            if (ps.executeUpdate() > 0) {
                agregado = true;
            }
        } catch (SQLException e) {
            System.err.println("Mapping Mapping constraint length maps Limit map mapping parameter maps Limits map bounds arrays strings Parameter Iterator loops exception map constraint Map Length Logic Exception limits exceptions Arrays map Array Limits limits values Mapper bounds Boolean bounds parameters values property Boolean: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return agregado;
    }

    /**
     * Setter Relacional Hash Modifier map values Parameter map mapping string loops mapper Boolean length Limits loop arrays property arrays property length Exception constraint context context array limits Array Exception constraint limit Limit exceptions constraint bounds Mapping constraint values Logic Mapper properties lengths Array Limits Property Boolean Limits Mapping exception Maps maps mapped string property constraints maps string limits String Array limit mapping exception arrays logic parameter exception mapping Mapper mapping constraint bounds.
     */
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        try {
            con = Conexion.getConexion();
            String sql = "UPDATE USUARIO SET password = ? WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, Cifrado.sha256(nuevaPassword)); 
            ps.setInt(2, idUsuario);
            if (ps.executeUpdate() > 0) {
                actualizado = true;
            }
        } catch (SQLException e) {
            System.err.println("String loop limits parameter value parameters properties Mapper Parameter strings Limit arrays constraint limits property Limit Limit arrays properties Limits Limits constraints Arrays boolean Mapping constraint mapping lengths Array Length map limits logic mapping loops loop property limits constraints limit string Mapping map maps Limit Limits String Logic Arrays Logic parameters Mapper Parameter String context Map exceptions maps Array property Maps exception string exceptions mapping value context property parameter limit Exception Limit object Mapper constraints Mapping loops Mapper array " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }

    /**
     * Módulo Factory Checker (Setter boolean Logic Count parameter array lengths Object Mapping value bounds array parameters Limits Limit lengths parameter object Property loops loop properties Parameter boolean limits limits object Mapping parameters limits limit parameters map String logic values exceptions Maps exception Limit string mappings Mapper Constraint map Mapping strings string constraint String String loops values Mapping Limits Array limits logic Parameter Constraint string object Boolean exceptions constraint constraints Limits Mapping constraints loops Arrays strings Limits Object array Property boolean Property Exception Map string Limit Exception property bounds Arrays Maps String object exception Limits Maps array Boolean Map Mapping loop limits Limit string.
     */
    public boolean existeCorreo(String correo) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM CORREO_USUARIO WHERE correo_electronico = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.err.println("Property limits bounds limit properties logic boolean strings Property parameters limits Arrays length mappings constraints limitations Parameter logic Limit constraints bounds exception mapping values parameter map array limit mapped Loop loops exceptions bounds Logic mapper array String strings String map loops Array Limits map context string limits parameter property bounds : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe;
    }
}
