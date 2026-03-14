package com.inventario.dao;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.util.Conexion;   // Clase utilitaria que crea la conexión a MySQL (ver: util/Conexion.java)
import com.inventario.model.Producto;  // Modelo POJO que representa la tabla PRODUCTO (ver: model/Producto.java)
import java.sql.Connection;            // Representa la conexión abierta con la base de datos MySQL
import java.sql.PreparedStatement;     // Permite ejecutar consultas SQL seguras (con ? para evitar inyección SQL)
import java.sql.ResultSet;            // Contiene los resultados de un SELECT (filas devueltas por la BD)
import java.sql.SQLException;         // Captura errores de SQL (tabla no existe, columna mal escrita, etc.)
import java.util.ArrayList;           // Lista dinámica para almacenar los productos encontrados
import java.util.List;                // Interfaz genérica de lista (se usa como tipo de retorno)

/**
 * DAO: Clase ProductoDAO (Data Access Object)
 * 
 * Esta clase es la ÚNICA que habla directamente con la tabla PRODUCTO de MySQL.
 * Ningún Servlet ni JSP hace consultas SQL directas; todo pasa por aquí.
 * 
 * QUIÉN LA USA:
 * - ProductoServlet.java: Para listar, registrar, editar y eliminar productos
 * - VentaServlet.java: Para obtener datos del producto al agregar al carrito
 * 
 * TABLA QUE MANEJA: PRODUCTO
 * Columnas: id_producto, nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida
 */
public class ProductoDAO {

    /**
     * 1. LISTAR TODOS LOS PRODUCTOS
     * 
     * QUIÉN LO LLAMA: ProductoServlet.doGet() → Carga la lista para editar_productos.jsp
     *                  VentaServlet.doGet(action=nuevo) → Carga la lista para el select de agregar_venta.jsp
     * QUÉ RETORNA: Lista de objetos Producto con TODOS los campos llenos
     * QUÉ HACE EN LA BD: SELECT * FROM PRODUCTO
     * 
     * DESTINO FINAL: El Servlet pone la lista en el request con:
     *   request.setAttribute("listaProductos", lista) → Llega al JSP como ${listaProductos}
     */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>(); // Lista vacía donde guardaremos cada producto encontrado
        Connection con = null;        // Conexión a MySQL (se abre y se cierra aquí)
        PreparedStatement ps = null;  // Objeto que ejecuta la consulta SQL
        ResultSet rs = null;          // Resultado de la consulta (las filas devueltas)
        
        try {
            con = Conexion.getConexion(); // Abrimos conexión usando la clase utilitaria (ver: util/Conexion.java)
            String sql = "SELECT * FROM PRODUCTO"; // Consulta que trae TODAS las columnas de TODOS los productos
            ps = con.prepareStatement(sql);        // Preparamos la consulta (segura contra inyección SQL)
            rs = ps.executeQuery();                // Ejecutamos y guardamos los resultados
            
            // Recorremos cada fila devuelta por la BD
            while (rs.next()) {
                Producto p = new Producto(); // Creamos un objeto Producto vacío para esta fila
                // Llenamos el objeto con los datos de la fila actual usando los setters del Modelo
                p.setIdProducto(rs.getInt("id_producto"));             // Columna id_producto → atributo idProducto
                p.setNombre(rs.getString("nombre"));                   // Columna nombre → atributo nombre (se muestra en JSP como ${p.nombre})
                p.setMarca(rs.getString("marca"));                     // Columna marca → atributo marca
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  // Columna precio_unitario → atributo precioUnitario
                p.setTipo(rs.getString("tipo"));                       // Columna tipo → atributo tipo
                p.setImagen(rs.getString("imagen"));                   // Columna imagen → atributo imagen
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));// Columna fecha_vencimiento → atributo fechaVencimiento
                p.setCantidadMedida(rs.getString("cantidad_medida"));  // Columna cantidad_medida → atributo cantidadMedida
                lista.add(p); // Agregamos el producto completo a la lista
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage()); // Si falla, mostramos en consola
        } finally {
            // SIEMPRE cerramos los recursos para liberar la conexión (evita fugas de memoria)
            try {
                if (rs != null) rs.close();   // Cerramos el ResultSet
                if (ps != null) ps.close();   // Cerramos el PreparedStatement
                if (con != null) con.close(); // Cerramos la conexión a MySQL
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; // Retornamos la lista al Servlet que la llamó
    }

    /**
     * 2. REGISTRAR UN NUEVO PRODUCTO
     * 
     * QUIÉN LO LLAMA: ProductoServlet.doPost() → Cuando el usuario envía el formulario de Registro_produc.html
     * QUÉ RECIBE: Objeto Producto con los datos del formulario (nombre, marca, precio, tipo, etc.)
     *   - Los datos vienen de: Registro_produc.html → input name="nombre", input name="precio", etc.
     *   - El Servlet los captura con: request.getParameter("nombre") y los pone en el objeto Producto
     * QUÉ RETORNA: true si se insertó correctamente, false si falló
     * QUÉ HACE EN LA BD: INSERT INTO PRODUCTO (...) VALUES (?, ?, ?, ?, ?, ?, ?)
     */
    public boolean registrarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false; // Flag para saber si se insertó o no
        
        try {
            con = Conexion.getConexion();
            // SQL de inserción con 7 signos de interrogación (?) para los 7 campos
            // Los ? son "placeholders" que se llenan de forma segura con ps.setString(), ps.setDouble(), etc.
            String sql = "INSERT INTO PRODUCTO (nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());         // ? #1 ← Modelo: p.getNombre() ← Servlet: request.getParameter("nombre") ← HTML: input name="nombre"
            ps.setString(2, p.getMarca());           // ? #2 ← Modelo: p.getMarca() ← Servlet: request.getParameter("marca") ← HTML: input name="marca"
            ps.setDouble(3, p.getPrecioUnitario());  // ? #3 ← Modelo: p.getPrecioUnitario() ← Servlet: Double.parseDouble(request.getParameter("precio"))
            ps.setString(4, p.getTipo());            // ? #4 ← Modelo: p.getTipo() ← Servlet: request.getParameter("tipo") ← HTML: select name="tipo"
            ps.setString(5, p.getImagen());          // ? #5 ← Modelo: p.getImagen() ← Servlet: request.getParameter("imagen")
            
            // La fecha puede ser null si el usuario no la ingresó → enviamos null a la BD
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); // ? #6 ← Modelo: p.getFechaVencimiento() ← Servlet: Date.valueOf(...)
            } else {
                ps.setNull(6, java.sql.Types.DATE);     // ? #6 ← null si no se proporcionó fecha
            }
            
            ps.setString(7, p.getCantidadMedida());  // ? #7 ← Modelo: p.getCantidadMedida() ← Servlet: request.getParameter("cantidad_medida")
            
            int filas = ps.executeUpdate(); // Ejecuta el INSERT. Retorna el número de filas afectadas.
            if (filas > 0) {
                registrado = true; // Si se insertó al menos 1 fila, fue exitoso
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR SQL AL REGISTRAR PRODUCTO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ErrorSQL: " + e.getMessage()); // Lanza error al Servlet para mostrar mensaje al usuario
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado; // true = registrado, false = falló
    }

    /**
     * 3. ELIMINAR UN PRODUCTO (CON ELIMINACIÓN EN CASCADA)
     * 
     * QUIÉN LO LLAMA: ProductoServlet.doGet(action=eliminar) → Cuando el admin presiona "Eliminar" en editar_productos.jsp
     * QUÉ RECIBE: int id → El id_producto del producto a eliminar (viene de la URL: ?action=eliminar&id=5)
     * QUÉ RETORNA: true si eliminó correctamente, false si falló
     * 
     * IMPORTANTE: Antes de eliminar el producto, debemos eliminar todos los registros que dependen de él:
     * 1. DETALLE_VENTA (tiene FK a INVENTARIO_DETALLE que tiene FK a PRODUCTO)
     * 2. DETALLE_PEDIDOS (tiene FK a INVENTARIO_DETALLE que tiene FK a PRODUCTO)
     * 3. INVENTARIO_DETALLE (tiene FK directa a PRODUCTO)
     * 4. Finalmente: PRODUCTO
     */
    public boolean eliminarProducto(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN: Si algo falla, se deshacen TODOS los cambios
            
            // PASO 1: Eliminar de DETALLE_VENTA (las ventas que usaron este producto)
            // Subconsulta: Busca en INVENTARIO_DETALLE los registros con este id_producto
            String sqlDetalleVenta = "DELETE FROM DETALLE_VENTA WHERE id_inv_detalle IN " +
                                     "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetalleVenta);
            ps.setInt(1, id); // ? ← id del producto a eliminar
            ps.executeUpdate();
            ps.close();
            
            // PASO 2: Eliminar de DETALLE_PEDIDOS (los pedidos que incluyeron este producto)
            String sqlDetallePedidos = "DELETE FROM DETALLE_PEDIDOS WHERE id_inv_detalle IN " +
                                       "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetallePedidos);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            // PASO 3: Eliminar de INVENTARIO_DETALLE (el registro de stock de este producto)
            String sqlDetalleInv = "DELETE FROM INVENTARIO_DETALLE WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleInv);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            // PASO 4: Finalmente, eliminar el PRODUCTO en sí
            String sqlProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";
            ps = con.prepareStatement(sqlProducto);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
            }
            
            con.commit(); // Si todo salió bien, CONFIRMAR todos los cambios en la BD
            System.out.println("Producto ID " + id + " y sus dependencias (ventas, pedidos, inventario) eliminados correctamente.");
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Si algo falló, DESHACER todos los cambios
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true); // Restaurar modo auto-commit
                    con.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }

    /**
     * 4. OBTENER UN PRODUCTO POR SU ID
     * 
     * QUIÉN LO LLAMA: 
     *   - VentaServlet.doPost(action=agregar) → Para obtener nombre y precio al agregar al carrito
     *   - ProductoServlet.doGet(action=editar) → Para cargar los datos en formulario_editar_producto.jsp
     * QUÉ RECIBE: int id → El id_producto a buscar (viene de: request.getParameter("id_producto"))
     * QUÉ RETORNA: Objeto Producto con todos sus datos, o null si no existe
     * QUÉ HACE EN LA BD: SELECT * FROM PRODUCTO WHERE id_producto = ?
     */
    public Producto obtenerProducto(int id) {
        Producto p = null; // null si no se encuentra
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?"; // Busca un producto específico por ID
            ps = con.prepareStatement(sql);
            ps.setInt(1, id); // ? ← id del producto a buscar
            rs = ps.executeQuery();
            
            if (rs.next()) { // Si encontró el producto
                p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));             // Columna BD → Modelo
                p.setNombre(rs.getString("nombre"));                   // Se usa en VentaServlet para: item.setNombreProducto(p.getNombre())
                p.setMarca(rs.getString("marca"));                     // Se muestra en agregar_venta.jsp: ${p.nombre} - $${p.precioUnitario} (${p.marca})
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  // Se usa en VentaServlet para: item.setPrecioUnitario(p.getPrecioUnitario())
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return p; // Retorna el producto encontrado (o null)
    }
    
    /**
     * 5. ACTUALIZAR UN PRODUCTO EXISTENTE
     * 
     * QUIÉN LO LLAMA: ProductoServlet.doPost(action=actualizar) → Cuando se envía formulario_editar_producto.jsp
     * QUÉ RECIBE: Objeto Producto con los datos EDITADOS del formulario
     *   - Los datos vienen de: formulario_editar_producto.jsp → inputs con los valores actuales pre-llenados
     *   - El Servlet los captura con: request.getParameter("nombre"), etc. y los pone en el objeto Producto
     * QUÉ RETORNA: true si se actualizó, false si falló
     * QUÉ HACE EN LA BD: UPDATE PRODUCTO SET nombre=?, marca=?, ... WHERE id_producto=?
     */
    public boolean actualizarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        
        try {
            con = Conexion.getConexion();
            // SQL UPDATE: Actualiza TODOS los campos editables del producto
            String sql = "UPDATE PRODUCTO SET nombre = ?, marca = ?, precio_unitario = ?, " +
                         "tipo = ?, imagen = ?, fecha_vencimiento = ?, cantidad_medida = ? " +
                         "WHERE id_producto = ?"; // El WHERE filtra por el ID del producto a editar
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());         // ? #1 ← JSP: input name="nombre" value="${productoEditar.nombre}"
            ps.setString(2, p.getMarca());           // ? #2 ← JSP: input name="marca" value="${productoEditar.marca}"
            ps.setDouble(3, p.getPrecioUnitario());  // ? #3 ← JSP: input name="precio" value="${productoEditar.precioUnitario}"
            ps.setString(4, p.getTipo());            // ? #4 ← JSP: select name="tipo" (opción selected según ${productoEditar.tipo})
            ps.setString(5, p.getImagen());          // ? #5 ← JSP: input name="imagen"
            
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); // ? #6 ← JSP: input name="fecha_vencimiento"
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, p.getCantidadMedida());  // ? #7 ← JSP: input name="cantidad_medida"
            ps.setInt(8, p.getIdProducto());          // ? #8 ← JSP: input hidden name="id_producto" value="${productoEditar.idProducto}"
            
            int filas = ps.executeUpdate(); // Ejecuta el UPDATE
            if (filas > 0) {
                actualizado = true;
                System.out.println("Producto ID " + p.getIdProducto() + " actualizado correctamente.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }

    /**
     * Verifica si un nombre de producto ya existe en la base de datos.
     * @param nombre El nombre del producto a verificar.
     * @return true si existe, false si no.
     */
    public boolean existeNombreProducto(String nombre) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM PRODUCTO WHERE nombre = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar nombre de producto: " + e.getMessage());
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
