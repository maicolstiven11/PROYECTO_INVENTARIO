package com.inventario.controller;

import com.inventario.dao.ProductoDAO;
import com.inventario.dao.VentaDAO;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CONTROLADOR: Servlet encargado de gestionar las Ventas.
 * 
 * Implementa: RF-16 (Agregar Producto al Carrito), RF-17 (Quitar Producto del Carrito),
 *             RF-18 (Finalizar Venta), RF-19 (Listar Ventas), RF-20 (Ver Detalle Venta),
 *             RF-21 (Cancelar Venta en Proceso)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO)
 *         RNF-03 (Gestión de Sesiones - carrito en sesión, idInventarioActual)
 *         RNF-07 (Proceso de Venta Rápida - flujo optimizado con carrito en sesión)
 *         RNF-08 (Mensajes de Error - redirige con parámetros descriptivos)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"})
public class VentaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);  // Delega al método principal
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);  // Delega al método principal
    }

    /**
     * Método principal que procesa TODAS las acciones de ventas según el parámetro "action".
     * Usa un switch para decidir qué hacer: mostrar, agregar, quitar, finalizar, listar, ver detalle o cancelar.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "mostrar";  // Acción por defecto
        
        HttpSession session = request.getSession();
        
        // =====================================================================
        // RF-16 Restricción 2: RECUPERAR O INICIALIZAR CARRITO DE COMPRAS
        // El carrito es una Lista de DetalleVenta almacenada en la sesión HTTP.
        // RNF-03: Se mantiene vivo mientras dure la sesión del usuario.
        // =====================================================================
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();                // Crear carrito vacío si no existe
            session.setAttribute("carrito", carrito);   // Guardarlo en sesión
        }
        
        // RF-18 Restricción 1: Obtener inventario activo de la sesión
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
        
        switch (action) {
            case "mostrar":
                // RF-16: Mostrar formulario de ventas con lista de productos y carrito actual
                cargarProductos(request);                                       // Carga lista de productos desde BD
                double totalActual = calcularTotal(carrito);                    // Calcula total del carrito
                request.setAttribute("totalVenta", totalActual);               // Pasa el total a la vista
                request.getRequestDispatcher("view/agregar_venta.jsp").forward(request, response);
                break;
                
            case "agregar":
                // RF-16: Agregar producto al carrito
                agregarProducto(request, session, carrito);
                response.sendRedirect("VentaServlet?action=mostrar");           // Recargar la vista
                break;
                
            case "quitar":
                // RF-17: Quitar producto del carrito por índice
                quitarProducto(request, carrito);
                response.sendRedirect("VentaServlet?action=mostrar");
                break;
                
            case "finalizar":
                // RF-18: Finalizar la venta (guardar en BD)
                // RF-18 Restricción 1: Debe existir un inventario activo
                if (idInventario == null) {
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo");
                    return;
                }
                finalizarVenta(session, carrito, idInventario, response);
                break;
                
            case "listar":
                // RF-19: Listar historial de ventas del negocio actual
                // RF-19 Restricción 1: Filtra por idNegocioActual de la sesión
                Integer idNegocio = (Integer) session.getAttribute("idNegocioActual");
                if (idNegocio != null) {
                    VentaDAO vDao = new VentaDAO();
                    List<Venta> listaVentas = vDao.listarVentas(idNegocio);    // RF-19: Consulta ventas de este negocio
                    request.setAttribute("listaVentas", listaVentas);          // Pasa la lista al JSP
                    request.getRequestDispatcher("view/visualizar_ventas.jsp").forward(request, response);
                } else {
                    response.sendRedirect("index.jsp");                        // Sesión perdida
                }
                break;
                
            case "ver_detalle":
                // RF-20: Ver detalle de una venta específica
                try {
                    int idVenta = Integer.parseInt(request.getParameter("id_venta"));
                    VentaDAO vDaoDet = new VentaDAO();
                    List<DetalleVenta> listaDetalles = vDaoDet.listarDetalleVenta(idVenta); // RF-20: Obtiene productos de la venta
                    request.setAttribute("listaDetalles", listaDetalles);
                    request.setAttribute("idVenta", idVenta);
                    request.getRequestDispatcher("view/detalle_venta.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendRedirect("VentaServlet?action=listar&error=ErrorAlVerDetalle");
                }
                break;
                
            case "cancelar":
                // RF-21: Cancelar venta en proceso
                // RF-21 Restricción 1: Solo limpia el carrito en sesión, no afecta ventas ya guardadas
                session.removeAttribute("carrito");                             // Elimina el carrito de la sesión
                response.sendRedirect("view/menu_inventario.html");
                break;
                
            default:
                response.sendRedirect("view/menu_inventario.html");
        }
    }

    /**
     * RF-10, RF-16: Carga la lista de productos desde la BD para mostrar en el formulario de ventas.
     */
    private void cargarProductos(HttpServletRequest request) {
        ProductoDAO pDao = new ProductoDAO();
        List<Producto> lista = pDao.listarProductos();      // RF-10: Usa ProductoDAO.listarProductos()
        request.setAttribute("listaProductos", lista);
    }

    /**
     * RF-16: Agrega un producto al carrito de ventas.
     * RF-16 Restricción 1: La cantidad debe ser mayor a 0.
     * RF-16 Restricción 3: Si el producto ya existe en el carrito, suma cantidades y recalcula subtotal.
     */
    private void agregarProducto(HttpServletRequest request, HttpSession session, List<DetalleVenta> carrito) {
        try {
            int idProd = Integer.parseInt(request.getParameter("id_producto")); // RF-16: ID del producto seleccionado
            int cantidad = Integer.parseInt(request.getParameter("cantidad")); // RF-16: Cantidad a agregar
            
            if (cantidad <= 0) return;  // RF-16 Restricción 1: Validar cantidad > 0
            
            // RF-16: Buscar el producto en la BD para obtener su precio
            ProductoDAO pDao = new ProductoDAO();
            Producto p = pDao.obtenerProducto(idProd);
            
            if (p != null) {
                // VALIDACIÓN DE STOCK REAL
                com.inventario.dao.DetalleInventarioDAO detDao = new com.inventario.dao.DetalleInventarioDAO();
                Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
                double stockDisponible = detDao.obtenerStockActual(idInventario, idProd);
                
                // Calcular cuánto hay YA en el carrito para este producto
                int cantidadEnCarrito = 0;
                for (DetalleVenta d : carrito) {
                    if (d.getIdProducto() == idProd) {
                        cantidadEnCarrito = d.getCantidad();
                        break;
                    }
                }
                
                if (cantidad + cantidadEnCarrito > stockDisponible) {
                    session.setAttribute("error_stock", "Stock insuficiente: " + p.getNombre() + " (Disponible: " + (int)stockDisponible + ")");
                    return;
                }

                // RF-16 Restricción 3: Verificar si ya existe en el carrito para sumar cantidades
                boolean existe = false;
                for (DetalleVenta d : carrito) {
                    if (d.getIdProducto() == idProd) {
                        d.setCantidad(d.getCantidad() + cantidad);                 // Sumar cantidades
                        d.setSubtotal(d.getCantidad() * d.getPrecioUnitario());    // Recalcular subtotal
                        existe = true;
                        break;
                    }
                }
                
                if (!existe) {
                    // RF-16: Crear nuevo detalle de venta y agregarlo al carrito
                    DetalleVenta det = new DetalleVenta();
                    det.setIdProducto(idProd);
                    det.setNombreProducto(p.getNombre());                          // RF-16 Restricción 3: Nombre para mostrar en tabla
                    det.setCantidad(cantidad);
                    det.setPrecioUnitario(p.getPrecioUnitario());                  // RF-16: Precio unitario del producto
                    det.setSubtotal(cantidad * p.getPrecioUnitario());             // RF-16: subtotal = cantidad × precio unitario
                    carrito.add(det);                                              // Agregar al carrito
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * RF-17: Quita un producto del carrito por su índice en la lista.
     * RF-17 Restricción 1: El índice debe ser válido (>= 0 y < tamaño del carrito).
     */
    private void quitarProducto(HttpServletRequest request, List<DetalleVenta> carrito) {
        try {
            int index = Integer.parseInt(request.getParameter("index")); // RF-17: Índice del producto a quitar
            if (index >= 0 && index < carrito.size()) {                  // RF-17 Restricción 1: Validar índice
                carrito.remove(index);                                   // RF-17: Eliminar de la lista
            }
        } catch (NumberFormatException e) {}
    }

    /**
     * Método auxiliar: Calcula el total de la venta sumando todos los subtotales del carrito.
     */
    private double calcularTotal(List<DetalleVenta> carrito) {
        double total = 0;
        for (DetalleVenta d : carrito) {
            total += d.getSubtotal();
        }
        return total;
    }
    
    /**
     * RF-18: Finaliza la venta guardándola en la base de datos.
     * RF-18 Restricción 2: El carrito no puede estar vacío.
     * RF-18 Restricción 3: La operación es transaccional (todo o nada) - implementado en VentaDAO.
     */
    private void finalizarVenta(HttpSession session, List<DetalleVenta> carrito, int idInventario, HttpServletResponse response) throws IOException {
        // RF-18 Restricción 2: Validar que el carrito no esté vacío
        if (carrito.isEmpty()) {
            response.sendRedirect("VentaServlet?action=mostrar&error=CarritoVacio");
            return;
        }
        
        // RF-18: Crear objeto Venta (Modelo) con los datos necesarios
        VentaDAO vDao = new VentaDAO();
        Venta venta = new Venta();
        venta.setIdInventario(idInventario);                           // RF-18: Vincular al inventario activo
        venta.setFechaVenta(new Date(System.currentTimeMillis()));     // RF-18: Fecha actual del sistema
        venta.setTotalVenta(calcularTotal(carrito));                    // RF-18: Total = suma de subtotales
        
        // RF-18 Restricción 3: registrarVenta usa transacción en el DAO (commit/rollback)
        boolean resultado = vDao.registrarVenta(venta, carrito);
        
        if (resultado) {
            // RF-18: Éxito - Limpiar el carrito de la sesión
            session.removeAttribute("carrito");
            response.sendRedirect("view/venta_finalizada.html");
        } else {
            // RNF-08: Error al guardar
            response.sendRedirect("VentaServlet?action=mostrar&error=ErrorAlGuardar");
        }
    }
}
