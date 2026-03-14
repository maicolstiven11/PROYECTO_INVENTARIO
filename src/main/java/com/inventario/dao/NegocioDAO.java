package com.inventario.dao;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.util.Conexion;   // Clase utilitaria que crea la conexión a MySQL (ver: util/Conexion.java)
import com.inventario.model.Negocio;   // Modelo POJO que representa la tabla NEGOCIO (ver: model/Negocio.java)
import java.sql.Connection;            // Conexión abierta con MySQL
import java.sql.PreparedStatement;     // Consulta SQL segura (con ? para evitar inyección)
import java.sql.SQLException;         // Captura errores de SQL
import java.sql.ResultSet;            // Resultados de un SELECT
import java.util.ArrayList;           // Lista dinámica
import java.util.List;                // Interfaz genérica de lista

/**
 * DAO: Clase NegocioDAO (Data Access Object)
 * 
 * Esta clase maneja TODAS las operaciones de la tabla NEGOCIO en MySQL.
 * Un Negocio = Un Bar/Tienda registrado por un administrador.
 * 
 * QUIÉN LA USA:
 * - NegocioServlet.java: Para listar, registrar y eliminar bares
 * - LoginServlet.java: Para contar bares del usuario (estadísticas)
 * 
 * TABLAS QUE MANEJA: NEGOCIO, USUARIO_NEGOCIO (vínculo usuario↔negocio)
 */
public class NegocioDAO {

    /**
     * 1. REGISTRAR NEGOCIO Y VINCULAR CON USUARIO
     * 
     * QUIÉN LO LLAMA: NegocioServlet.doPost() → Cuando el admin envía registroBar.html
     * QUÉ RECIBE: 
     *   - Objeto Negocio con nombre y dirección (viene de: registroBar.html → input name="nombre" y name="direccion")
     *   - int idUsuario → ID del usuario logueado (viene de: session.getAttribute("usuarioLogueado").getIdUsuario())
     * QUÉ RETORNA: int → ID generado del negocio, o -1 si falló
     * QUÉ HACE EN LA BD:
     *   1. INSERT INTO NEGOCIO (nombre, direccion, estado) → Crea el bar con estado 'inactivo'
     *   2. INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) → Vincula el bar con el admin que lo creó
     */
    public int registrarNegocio(Negocio negocio, int idUsuario) {
        Connection con = null;
        PreparedStatement psNegocio = null;  // PreparedStatement para insertar en NEGOCIO
        PreparedStatement psVinculo = null;  // PreparedStatement para insertar en USUARIO_NEGOCIO
        ResultSet rsKeys = null;             // Para obtener el ID auto-generado
        int idGenerado = -1;                 // -1 significa que falló
        
        try {
            con = Conexion.getConexion();     // Abrimos conexión a MySQL
            con.setAutoCommit(false);          // TRANSACCIÓN: Las 2 inserciones son atómicas (todo o nada)
            
            // PASO 1: Insertar el nuevo bar en la tabla NEGOCIO
            String sqlNegocio = "INSERT INTO NEGOCIO (nombre, direccion, estado) VALUES (?, ?, ?)";
            psNegocio = con.prepareStatement(sqlNegocio, PreparedStatement.RETURN_GENERATED_KEYS); // Pedimos que nos devuelva el ID generado
            psNegocio.setString(1, negocio.getNombre());    // ? #1 ← Modelo: negocio.getNombre() ← Servlet: request.getParameter("nombre") ← HTML: input name="nombre"
            psNegocio.setString(2, negocio.getDireccion()); // ? #2 ← Modelo: negocio.getDireccion() ← Servlet: request.getParameter("direccion") ← HTML: input name="direccion"
            psNegocio.setString(3, "inactivo");              // ? #3 ← Siempre empieza como 'inactivo' hasta que se inicie un inventario
            
            int filas = psNegocio.executeUpdate(); // Ejecuta el INSERT
            if (filas > 0) {
                rsKeys = psNegocio.getGeneratedKeys(); // Obtenemos el ID que MySQL generó automáticamente
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // El primer valor es el id_negocio generado
                    negocio.setIdNegocio(idGenerado); // Lo guardamos en el objeto para uso posterior
                }
                
                // PASO 2: Vincular el usuario con el negocio en la tabla puente USUARIO_NEGOCIO
                if (idUsuario > 0 && idGenerado > 0) {
                    String sqlVinculo = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
                    psVinculo = con.prepareStatement(sqlVinculo);
                    psVinculo.setInt(1, idUsuario);   // ? #1 ← ID del admin logueado (de la sesión)
                    psVinculo.setInt(2, idGenerado);  // ? #2 ← ID del negocio recién creado
                    psVinculo.executeUpdate();
                    System.out.println("DAO: Negocio " + idGenerado + " vinculado con Usuario " + idUsuario);
                }
                
                con.commit(); // CONFIRMAR ambas inserciones
                System.out.println("DAO: Negocio registrado con ID: " + idGenerado);
            } else {
                con.rollback(); // Si no se insertó, deshacer
            }
            
        } catch (SQLException e) {
            System.out.println("Error al registrar negocio: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Deshacer si hay error
            }
            throw new RuntimeException("ErrorSQL: " + e.getMessage());
        } finally {
            // Cerrar TODOS los recursos (en orden inverso al que se abrieron)
            try {
                if (con != null) con.setAutoCommit(true); // Restaurar modo auto-commit
                if (rsKeys != null) rsKeys.close();
                if (psVinculo != null) psVinculo.close();
                if (psNegocio != null) psNegocio.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; // Retorna el ID del negocio creado (o -1 si falló)
    }

    /**
     * 2. LISTAR NEGOCIOS (BARES) DEL USUARIO ACTUAL
     * 
     * QUIÉN LO LLAMA: NegocioServlet.doGet() → Carga la lista para Lista_bares.jsp
     * QUÉ RECIBE: int idUsuario → ID del usuario logueado (de la sesión)
     * QUÉ RETORNA: Lista de objetos Negocio, cada uno con el flag tieneInventarioActivo calculado
     * QUÉ HACE EN LA BD:
     *   - SELECT de la tabla NEGOCIO
     *   - JOIN con USUARIO_NEGOCIO para filtrar solo los bares de ESTE usuario
     *   - SUBCONSULTA a INVENTARIO para saber si cada bar tiene inventario activo
     * 
     * DESTINO FINAL: El Servlet los pone en request como:
     *   request.setAttribute("listaNegocios", lista) → En el JSP se accede como ${listaNegocios} y cada bar como ${bar.nombre}
     */
    public List<Negocio> listarNegocios(int idUsuario) {
        List<Negocio> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // SQL con subconsulta: Cuenta cuántos inventarios activos tiene cada negocio
            // Si tiene_inv > 0, el bar tiene inventario activo
            String sql = "SELECT n.*, " +
                         "(SELECT COUNT(*) FROM INVENTARIO i WHERE i.id_negocio = n.id_negocio AND i.estado = 'activo') as tiene_inv " +
                         "FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " + // Solo los bares vinculados al usuario
                         "WHERE un.id_usuario = ?"; // Filtrar por el ID del usuario logueado
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // ? ← ID del usuario logueado
            rs = ps.executeQuery();
            
            while(rs.next()){
                Negocio n = new Negocio();
                n.setIdNegocio(rs.getInt("id_negocio"));       // Columna BD → Modelo. Usado en JSP: ${bar.idNegocio} para armar URLs
                n.setNombre(rs.getString("nombre"));           // Columna BD → Modelo. Usado en JSP: ${bar.nombre} para mostrar nombre del bar
                n.setDireccion(rs.getString("direccion"));     // Columna BD → Modelo. Usado en JSP: ${bar.direccion}
                n.setEstado(rs.getString("estado"));           // Columna BD → Modelo. Usado en JSP: ${bar.estado}
                
                // Calculamos si tiene inventario activo (NO es columna de la BD, es resultado de la subconsulta)
                boolean activo = rs.getInt("tiene_inv") > 0;   // Si COUNT > 0, tiene inventario activo
                n.setTieneInventarioActivo(activo);            // Usado en JSP: ${bar.tieneInventarioActivo} para mostrar "Ver" vs "Iniciar"
                
                lista.add(n);
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
        return lista; // Retorna la lista al NegocioServlet
    }


    /**
     * 3. ELIMINAR NEGOCIO (CASCADA COMPLETA)
     * 
     * QUIÉN LO LLAMA: NegocioServlet.doGet(action=eliminar) → Cuando el admin confirma eliminar un bar
     * QUÉ RECIBE: int idNegocio → ID del negocio a eliminar (viene de: URL ?action=eliminar&id=X)
     * QUÉ RETORNA: true si se eliminó todo correctamente, false si falló
     * 
     * ORDEN DE ELIMINACIÓN (respetando FKs):
     * 1. DETALLE_VENTA (depende de VENTA)
     * 2. VENTA (depende de INVENTARIO)
     * 3. DETALLE_PEDIDOS (depende de PEDIDOS_PROVEEDOR)
     * 4. PEDIDOS_PROVEEDOR (depende de INVENTARIO)
     * 5. GASTO_DIARIO (depende de INVENTARIO)
     * 6. INVENTARIO_DETALLE (depende de INVENTARIO)
     * 7. INVENTARIO (depende de NEGOCIO)
     * 8. USUARIO_NEGOCIO (tabla puente)
     * 9. NEGOCIO (finalmente)
     */
    public boolean eliminarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN: Todo o nada
            
            // PASO 1: Eliminar DETALLE_VENTA (las líneas de venta de productos vendidos en este negocio)
            String sql1 = "DELETE dv FROM DETALLE_VENTA dv " +
                         "INNER JOIN VENTA v ON dv.id_venta = v.id_venta " +         // JOIN: Detalle → Venta
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + // JOIN: Venta → Inventario
                         "WHERE i.id_negocio = ?";                                    // Filtro: inventarios de ESTE negocio
            ps = con.prepareStatement(sql1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 2: Eliminar VENTA (las ventas registradas en inventarios de este negocio)
            String sql2 = "DELETE v FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 3: Eliminar DETALLE_PEDIDOS (las líneas de detalle de pedidos)
            String sql3_1 = "DELETE dp FROM DETALLE_PEDIDOS dp " +
                            "INNER JOIN PEDIDOS_PROVEEDOR pp ON dp.id_pedido_base = pp.id_pedido_base " + // JOIN: Detalle → Pedido
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +            // JOIN: Pedido → Inventario
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 4: Eliminar PEDIDOS_PROVEEDOR (los pedidos a proveedores)
            String sql3_2 = "DELETE pp FROM PEDIDOS_PROVEEDOR pp " +
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 5: Eliminar GASTO_DIARIO (los gastos registrados)
            String sql3 = "DELETE g FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 6: Eliminar INVENTARIO_DETALLE (los registros de stock)
            String sql4 = "DELETE di FROM INVENTARIO_DETALLE di " +
                         "INNER JOIN INVENTARIO i ON di.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql4);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 7: Eliminar INVENTARIO (los inventarios del negocio)
            String sql5 = "DELETE FROM INVENTARIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql5);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 8: Eliminar vínculo USUARIO_NEGOCIO (la relación usuario↔negocio)
            String sql6 = "DELETE FROM USUARIO_NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql6);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // PASO 9: Finalmente, eliminar el NEGOCIO en sí
            String sql7 = "DELETE FROM NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql7);
            ps.setInt(1, idNegocio);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
                con.commit(); // CONFIRMAR toda la transacción
                System.out.println("Negocio " + idNegocio + " eliminado con todos sus datos.");
            } else {
                con.rollback(); // Si no encontró el negocio, deshacer
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
     * 4. CONTAR NEGOCIOS DEL USUARIO (Para estadísticas en el perfil)
     * 
     * QUIÉN LO LLAMA: LoginServlet.doPost() → Al iniciar sesión, para cargar estadísticas
     * QUÉ RECIBE: int idUsuario → ID del usuario logueado
     * QUÉ RETORNA: int → Cantidad de bares que tiene el usuario
     * DESTINO: Se guarda en sesión como: session.setAttribute("numBares", cantBares)
     *          Se muestra en: perfil_admin.jsp como ${numBares}
     */
    public int contarNegocios(int idUsuario) {
        int cantidad = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            // Cuenta cuántos negocios tiene el usuario (vía tabla puente USUARIO_NEGOCIO)
            String sql = "SELECT COUNT(*) FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                         "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // ? ← ID del usuario logueado
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1); // La primera columna del COUNT(*) es el número
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
        return cantidad; // Retorna al LoginServlet → se guarda en sesión
    }

}
