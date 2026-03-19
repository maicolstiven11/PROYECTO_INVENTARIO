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
 * Clase NegocioDAO.
 * 
 * Contiene todas las instrucciones SQL (crear, buscar, eliminar) 
 * que permiten manejar la información de las tiendas/bares registrados 
 * en este proyecto directamente en la base de datos principal.
 */
public class NegocioDAO {

    /**
     * Acción para registrar un nuevo negocio en el sistema y a su vez 
     * asignarle un dueño (un usuario).
     * Se trata de dos tablas: la tabla NEGOCIO y luego la tabla USUARIO_NEGOCIO.
     */
    public int registrarNegocio(Negocio negocio, int idUsuario) {
        Connection con = null;
        PreparedStatement psNegocio = null;  // Consulta para el negocio
        PreparedStatement psVinculo = null;  // Consulta para vincular al usuario
        ResultSet rsKeys = null;             // Para capturar cuál ID se generó
        int idGenerado = -1;                 // Bandera, si queda en -1 falló
        
        try {
            con = Conexion.getConexion(); // Hacemos enlace al motor SQL
            con.setAutoCommit(false); // Activamos protección (Transactions): Si la segunda consulta se daña, la primera no se guarda
            
            // Este comando guarda en la base de datos de NEGOCIO un negocio nuevo (siempre arranca inactivo)
            String sqlNegocio = "INSERT INTO NEGOCIO (nombre, direccion, estado) VALUES (?, ?, ?)";
            psNegocio = con.prepareStatement(sqlNegocio, PreparedStatement.RETURN_GENERATED_KEYS); // Retenemos ID autogenerado
            psNegocio.setString(1, negocio.getNombre());    // Rellena la pregunta 1 con el nombre del bar
            psNegocio.setString(2, negocio.getDireccion()); // Rellena la 2 con su dirección
            psNegocio.setString(3, "inactivo");             // Se impone estado a 'inactivo' al no existir inventario aún
            
            int filas = psNegocio.executeUpdate(); // Realizamos la inserción al sistema
            if (filas > 0) { // Validamos si SQL dice que copió 1 renglón 
                rsKeys = psNegocio.getGeneratedKeys(); // Solicitamos a SQL que nos diga cuál identificador se puso
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // Agarramos ese número 
                    negocio.setIdNegocio(idGenerado); // El objeto Java se actualiza
                }
                
                // Si sí pudimos recuperar un número válido de negocio, y tenemos el numero de trabajador...
                if (idUsuario > 0 && idGenerado > 0) {
                    // Armamos consulta a la tabla puente (la que conecta al usuario y al negocio)
                    String sqlVinculo = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
                    psVinculo = con.prepareStatement(sqlVinculo); // Preparamos otra solicitud SQL
                    psVinculo.setInt(1, idUsuario);   // Ponemos ID de empleado
                    psVinculo.setInt(2, idGenerado);  // Ponemos el recién fabricado de local
                    psVinculo.executeUpdate(); // Se guarda el vinculo
                    System.out.println("DAO: Negocio " + idGenerado + " vinculado con Usuario " + idUsuario);
                }
                
                con.commit(); // CONFIRMACION FINAL: Ambas consultas salieron bien, la base de datos escribe permanentemente esta sesión
                System.out.println("DAO: Negocio registrado con ID: " + idGenerado);
            } else {
                con.rollback(); // En caso de que sqlNegocio fallara, abortamos procedimiento
            }
            
        } catch (SQLException e) {
            System.out.println("Error al registrar negocio: " + e.getMessage());
            e.printStackTrace();
            if (con != null) { // Si hay pánico y se revienta la memoria
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Aborta todos los cambios desde la base de datos
            }
            throw new RuntimeException("ErrorSQL: " + e.getMessage()); 
        } finally {
            try {
                // Pasos de higiene: Restaurar configuración normal y matar todo resto en memoria
                if (con != null) con.setAutoCommit(true); 
                if (rsKeys != null) rsKeys.close();
                if (psVinculo != null) psVinculo.close();
                if (psNegocio != null) psNegocio.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; // Finaliza arrojando si su creación entregó ID válido (+1) o si erró (-1)
    }

    /**
     * Muestra todos los negocios pertenecientes a un determinado usuario administrativo.
     * También permite saber (mediante una sub-consulta) si ese negocio está con inventario andando.
     */
    public List<Negocio> listarNegocios(int idUsuario) {
        List<Negocio> lista = new ArrayList<>(); // Organizador para nuestra tabla en pantalla
        Connection con = null; // Link
        PreparedStatement ps = null; // Compilador
        ResultSet rs = null; // Cajón del resultado
        
        try {
            con = Conexion.getConexion();
            
            // Esta consulta gigante hace dos cosas: 
            // 1. SELECT n.* saca los datos desde la tabla NEGOCIO
            // 2. Hace un (SELECT COUNT...) dentro de otra consulta para averiguar si en INVENTARIO hay algún registro en estado 'activo' atado a este bar. Y a ese true/false lo llama 'tiene_inv' 
            // 3. Usa el INNER JOIN para saber relacionar y filtrar solo los que pertenezcan a id_usuario ?
            String sql = "SELECT n.*, " +
                         "(SELECT COUNT(*) FROM INVENTARIO i WHERE i.id_negocio = n.id_negocio AND i.estado = 'activo') as tiene_inv " +
                         "FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " + 
                         "WHERE un.id_usuario = ?"; 
            ps = con.prepareStatement(sql); // Monta el string
            ps.setInt(1, idUsuario); // Ingresa el parámetro
            rs = ps.executeQuery(); // Exige resultado lectivo
            
            while(rs.next()){ // Empieza lectura de renglones respondidos desde la base
                Negocio n = new Negocio(); // Se forma plantilla para ir armando cada caja
                n.setIdNegocio(rs.getInt("id_negocio"));       // Obtenemos su identificador único
                n.setNombre(rs.getString("nombre"));           // Obtenemos su nombre comercial
                n.setDireccion(rs.getString("direccion"));     // Dónde queda
                n.setEstado(rs.getString("estado"));           // Abierto o cancelado
                
                boolean activo = rs.getInt("tiene_inv") > 0;   // Ese bloque '(SELECT COUNT...) as tiene_inv' lo interpretamos así. Si su número contable fue 1 o más, da Verdadero. Si dio 0, es falso.
                n.setTieneInventarioActivo(activo);            // Mandamos esa deducción a nuestro Objeto en Java
                
                lista.add(n); // Anexamos la caja armada a nuestro inventario logístico (al final es esto lo que ve el JSP)
            }
        } catch (SQLException e) {
            System.err.println("Error listar negocios: " + e.getMessage()); // Excepción control
        } finally {
            try {
                if (rs != null) rs.close(); // Limpiar y matar
                if (ps != null) ps.close(); // Limpiar y matar
                if (con != null) con.close(); // Fin
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Se reenvía a Servlet la colección de locales
    }


    /**
     * Tarea extremadamente crítica: ELIMINACIÓN TOTAL de un negocio.
     * Ya que en base de datos toda la historia está enlazada al ID de un local, es imposible borrar el local sin antes borrar TODO.
     * Esta consulta es larga porque elimina por nivel, desde la rama más pequeña que pide un pedido hasta destruir un inventario entero y luego sí dejar borrar a un local. 
     */
    public boolean eliminarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null; // Reutilizamos esta consulta por que la memoria de esto se vuelve pesada
        boolean eliminado = false; // El veredicto de éxito
        
        try {
            con = Conexion.getConexion(); // Nos alineamos servidor DB
            con.setAutoCommit(false); // Activamos la red de contención: si algo de las lineas inferiores falla, TODO LA FUNCIÓN SE DESHACE, así se previene dejar DB corruptas.
            
            // FASE 1: Destruir detalles del producto que se haya VENDIDO (hace join con inventario para lograr ubicar este local)
            String sql1 = "DELETE dv FROM DETALLE_VENTA dv " +
                         "INNER JOIN VENTA v ON dv.id_venta = v.id_venta " +         
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + 
                         "WHERE i.id_negocio = ?";                                    
            ps = con.prepareStatement(sql1);
            ps.setInt(1, idNegocio); // Le enchufa la variable que pasamos
            ps.executeUpdate(); // Orden ejecutada pero en suspenso hasta el .commit
            ps.close(); // Se apaga la memoria para no asfixiar a Java
            
            // FASE 2: Destruir la base de las VENTAS facturadas como tal
            String sql2 = "DELETE v FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 3 PARTE 1: Eliminar artículos comprados al PROVEEDOR (join es para localizar a través de la red)
            String sql3_1 = "DELETE dp FROM DETALLE_PEDIDOS dp " +
                            "INNER JOIN PEDIDOS_PROVEEDOR pp ON dp.id_pedido_base = pp.id_pedido_base " + 
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +            
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 3 PARTE 2: Destruir el cascarón de la FACTURA entera hacia en proveedor
            String sql3_2 = "DELETE pp FROM PEDIDOS_PROVEEDOR pp " +
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 4: Sacudir a lo que haya de GASTO DIARIOS que contuviera la factura manual (arriendos recibos papas)
            String sql3 = "DELETE g FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 5: Vaciar físicamente cuántos elementos estaban reportados y avaluados en la bodega local como estock estricto
            String sql4 = "DELETE di FROM INVENTARIO_DETALLE di " +
                         "INNER JOIN INVENTARIO i ON di.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql4);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 6: Anular definitivamente los propios inventarios (la caja mayor virtual ahora inexistente)
            String sql5 = "DELETE FROM INVENTARIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql5);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 7: Acaba el puente relacional de dueños a locales o dueños a administradores. 
            String sql6 = "DELETE FROM USUARIO_NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql6);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // FASE 8 CLÍMAX: La sentencia base, al matar todo el árbol en sus raíces, el sistema dejará eliminar de golpe la existencia superior del identificador original
            String sql7 = "DELETE FROM NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql7);
            ps.setInt(1, idNegocio);
            
            int filas = ps.executeUpdate(); // Confirmamos resultado que nos dará este ultimo SQL
            if (filas > 0) { // Si efectivamente eliminó 1 
                eliminado = true; // Seteamos nuestra meta
                con.commit();  // AHORA SÍ, ESTA SOLA PALABRA MATERIALIZA Y CONFIRMA TODAS LA ORDENES EN TABLAS 1 AL 8
                System.out.println("Negocio " + idNegocio + " eliminado con todos sus datos."); // Logging de server ok
            } else {
                con.rollback(); // Caso donde ya en el FASE 8 fallara, manda rollback
            }
            
        } catch (SQLException e) { // Si algo explotara a mitad de camino en SQL, se activará este catch
            System.err.println("Error al eliminar negocio: " + e.getMessage()); // Escribe lo que falló en DB
            e.printStackTrace(); 
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Orden imperativa para que en caso de excepción, la memoria se vacie y NADA A MEDIAS DE LOS PASOS se altere
        } finally {
            try {
                if (ps != null) ps.close(); // Apagados de consumo
                if (con != null) {
                    con.setAutoCommit(true); // Vuelta a predeterminados
                    con.close(); // FIN proceso
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado; // Mandará un True informático confirmando o falso en que el proyecto local no existía al tocarlo o reventó
    }

    /**
     * Sumador contador que únicamente ve si el Administrador en sesión todavía tiene dominios sobre algún establecimiento.
     * Retorna sólo un número entero para no cargar memoria de arreglos pesados de string u objetos modelados, sólo cifra.
     */
    public int contarNegocios(int idUsuario) {
        int cantidad = 0; // Preasignamos para evitar lecturas nulas matematicas nulos
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null; // Almacenará tan sólo una casilla de un 1 column row
        try {
            con = Conexion.getConexion(); // Solicitud al link estatico
            // Usamos COUNT(*), la forma más rapida posible en bases de datos donde unimos NEGOCIO y USUARIO NEGOCIO buscando solo un cruce de IDs correspondientes al ID asignado en Java.
            String sql = "SELECT COUNT(*) FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                         "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql); // Instamos
            ps.setInt(1, idUsuario);  // Llenamos el ?
            rs = ps.executeQuery(); // Pedimos el número 
            if (rs.next()) { // Solo habrá un resultado de primer renglón porque es función agregada, asi que movemos el limitador.
                cantidad = rs.getInt(1); // Se recupera ese dígito solitario encontrado en el primer (y único) campo evaluado.
            }
        } catch (SQLException e) { 
            e.printStackTrace(); // Log
        } finally {
            try {
                if (rs != null) rs.close(); // Kill var
                if (ps != null) ps.close(); // Kill var
                if (con != null) con.close(); // Close server
            } catch (SQLException e) { e.printStackTrace(); } 
        }
        return cantidad; // Exponemos solo un digito numerico final a vistas Front End Control Servlet 
    }

}
