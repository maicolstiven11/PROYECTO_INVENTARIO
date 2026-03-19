package com.inventario.dao;

import com.inventario.model.DetalleVenta; 
import com.inventario.model.Venta;        
import com.inventario.util.Conexion;      
import java.sql.Connection;               
import java.sql.PreparedStatement;        
import java.sql.ResultSet;               
import java.sql.SQLException;            
import java.sql.Statement;              
import java.util.List;                   

/**
 * Clase VentaDAO.
 * 
 * Controlador base que gestiona la facturación hacia los clientes (La famosa Venta al mostrador).
 * Crea el esqueleto de la venta (La factura) junto a sus items y debita el Stock que el cliente se consumió.
 */
public class VentaDAO {

    /**
     * Guarda la operación comercial de venta general, metiendo sus detalles (qué consumió, papas, cervezas),
     * y bajando esos mismos del bodegaje central que tengamos en el sistema. Todo esto mediante proceso Transaccional de alto rigor. 
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection con = null; // Enchufe
        PreparedStatement psVenta = null;    // Preparación a base de datos Factura
        PreparedStatement psDetalle = null;  // Preparación a los items consumidos
        PreparedStatement psStock = null;    // Preparación para el descuento masivo
        PreparedStatement psBuscar = null;   // Localizar quién de verdad es el ID bodeguero
        ResultSet rsKeys = null;             // Llaves autogeneradas devueltas
        ResultSet rsBuscar = null;           // Para hallar los identificadores de detalle  
        boolean estatus = false; // Como siempre veredicto booleano final.

        try {
            con = Conexion.getConexion(); // Conexion iniciada
            con.setAutoCommit(false); // Seguro activado. Prohibido guardar cosas a las carreras, deben confirmarlo manualmente. 

            // PASO 1. Crear el cajón (Factura Base VENTA) donde irán el total y la fecha 
            String sqlVenta = "INSERT INTO VENTA (id_inventario, total_venta, fecha_venta) VALUES (?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);  // Solicitamos que nos diga a qué código quedó asociado ese cajón (Ticket nro XXX)
            psVenta.setInt(1, venta.getIdInventario());    // Es propio de nuestro inventario de mes actual
            psVenta.setDouble(2, venta.getTotalVenta());   // En total le cobré al usuario $$ tantos pesos
            psVenta.setDate(3, venta.getFechaVenta());     // Facturado el Viernes de quincena

            int filas = psVenta.executeUpdate(); // Ejecuta 1
            if (filas == 0) { // Si estalló...
                throw new SQLException("Error base. Falla creación factura esqueleto general."); 
            }

            // Atrapamos su generador
            rsKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado = -1;
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1);  // Es lo que ocupamos (El Nro Ticket Factura de la BD)
            } else {
                throw new SQLException("Error fatal, no hubo identificador numérico de base.");
            }

            // PASO 2. Preparemos el terreno por lotes antes de disparar nada (Performance). 
            // Inserciones en DETALLE_VENTA
            String sqlDetalle = "INSERT INTO DETALLE_VENTA (id_venta, id_inv_detalle, cantidad, subtotal) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // Búsqueda del identificador interno del inventario específico de producto. (INVENTARIO_DETALLE)
            String sqlBuscar = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            psBuscar = con.prepareStatement(sqlBuscar);

            // Bajar del renglón (Descontar de bodegaje/Estantes INVENTARIO_DETALLE nuestra nueva venta)
            String sqlRestar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial - ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlRestar);

            // PASO 3. Iteramos todos los productos que estaban en la lista del "carrito de compras"
            for (DetalleVenta det : detalles) {
                // Hay un problema, DetalleVenta tiene ID de "Producto" (ej, ID 5 = Galletas), pero DETALLE_VENTA nos exige ID de "Inventario_Detalle", 
                // Por lo tanto usamos el psBuscar armado antes para preguntarle a BD "Oiga, ¿cuál es el ID en la bodega este mes de las Galletas?".
                psBuscar.setInt(1, venta.getIdInventario()); // Mes 1
                psBuscar.setInt(2, det.getIdProducto());     // Galletas 5 
                rsBuscar = psBuscar.executeQuery();

                int idInvDetalle = -1; // Lo atraparemos aquí...
                if (rsBuscar.next()) {
                    idInvDetalle = rsBuscar.getInt("id_detalle"); // ¡Bingo! Se extrajo el ID correcto del Inventario
                } else { // ¿Qué pasa si la galleta era 5 en el catálogo central, pero en ESTE MES nunca lo agregamos al inventario de nuestro local? ¡Crash Transaction! No puedo vender lo que no me asigné al local 
                    throw new SQLException("Imposible encontrar este producto en estado de tu inventario: Producto con identificador Maestro " + det.getIdProducto());
                }

                // Ya que estamos tranquilos con nuestro ID_InvDetalle en mano, se lo pasamos al detalle de la factura (Lista 2 de compras a BD)
                psDetalle.setInt(1, idVentaGenerado);          // Ticket Venta Nro
                psDetalle.setInt(2, idInvDetalle);             // ID inventario deducido
                psDetalle.setInt(3, det.getCantidad());        // El cliente llevó 4 
                psDetalle.setDouble(4, det.getSubtotal());     // Le contaron 20 mil
                psDetalle.addBatch(); // Empacar y guardar a que demos aviso múltiple

                // Preparamos el descontamiento
                psStock.setInt(1, det.getCantidad());          // Restar 4 galletas
                psStock.setInt(2, idInvDetalle);               // De ESTE id_inventario específico que averiguamos
                psStock.addBatch();  // Empacar y guardar...
            }

            // PASO 4. Soltar la ráfaga de consultas. Ambas iteraciones envían su Batch cargado.
            psDetalle.executeBatch(); // Ingresa el carro facturero
            psStock.executeBatch();   // Resta en masivo los items en la matriz

            // PASO 5. Sellado oficial
            con.commit(); // Éxito profundo de la programación.
            estatus = true; // Win
            System.out.println("Venta en mesa generada. Substraccion general masiva BD concretada: Factura " + idVentaGenerado);

        } catch (SQLException e) { // Pánico
            System.err.println("Rebote general bloque en facturación ventas al publico: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Anular cualquier cajón Creado en la Fase 1. Se aborta la operación integra.
                    System.out.println("Rollback transaccional invocado con salvación al registro matriz.");
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally { // Suicidio objetos DB
            try {
                if (rsBuscar != null) rsBuscar.close();
                if (rsKeys != null) rsKeys.close();
                if (psBuscar != null) psBuscar.close();
                if (psStock != null) psStock.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) {
                    con.setAutoCommit(true); // Vuelta a automatismo HTTP
                    con.close(); // Apagar enchufe
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return estatus; // Responde a la Vista web si hubo dinero entrante
    }

    /**
     * Trae todos los esqueletos de las ventas de determinado administrador de locales 
     */
    public java.util.List<Venta> listarVentas(int idNegocio) {
        java.util.List<Venta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Trae datos basicos pero une y cruza INVENTARIO para limitar y lograr filtrar los ides de los locales atados 
            String sql = "SELECT v.* FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + 
                         "WHERE i.id_negocio = ? " +                                       
                         "ORDER BY v.fecha_venta DESC"; // Descendente. Las más de ayer se ven, las del '80 estan al fondo.
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery();
            
            while (rs.next()) { // Recorrido renglones venta en crudo
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));          
                v.setIdInventario(rs.getInt("id_inventario"));
                v.setTotalVenta(rs.getDouble("total_venta")); 
                v.setFechaVenta(rs.getDate("fecha_venta"));   
                lista.add(v);
            }
        } catch (SQLException e) { System.err.println("Error Venta crudo general: " + e.getMessage()); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; 
    }

    /**
     * Busca para un Ticket específico Nro # ID, todos los pormenores y snacks consumidos durante dicha venta por el público
     */
    public java.util.List<DetalleVenta> listarDetalleVenta(int idVenta) {
        java.util.List<DetalleVenta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Para poder pintar "Papas Fritas" en lugar de ver id_producto "5", requerimos cruzar 3 tablas
            String sql = "SELECT d.*, p.nombre FROM DETALLE_VENTA d " +
                         "INNER JOIN INVENTARIO_DETALLE id ON d.id_inv_detalle = id.id_detalle " + // Entra el inv general id
                         "INNER JOIN PRODUCTO p ON id.id_producto = p.id_producto " +              // Entra el catálogo para darnos sus nombres string reales
                         "WHERE d.id_venta = ?";                                                   // Filtramos buscando el Recibo o Facturero exactos de este parametro Java
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta);  // Inyecta Factura nro 
            rs = ps.executeQuery(); // Actua!
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalleVenta(rs.getInt("id_detalle_venta")); 
                d.setIdVenta(rs.getInt("id_venta"));                
                d.setIdInvDetalle(rs.getInt("id_inv_detalle"));     
                d.setCantidad(rs.getInt("cantidad")); // Nos compró 3 unidades              
                d.setSubtotal(rs.getDouble("subtotal")); // Costaron tantos billetes          
                d.setNombreProducto(rs.getString("nombre")); // ¡Cervezas!       
                
                lista.add(d); // Incorpora este modelo al final de nuestra lista recopilatoria.
            }
        } catch (SQLException e) { System.err.println("Falla crítica en Desglose General Detalle Vistas : " + e.getMessage()); } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Finaliza listado 
    }
}
