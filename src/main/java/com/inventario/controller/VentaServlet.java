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

@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"})
public class VentaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "mostrar"; // Por defecto
        
        HttpSession session = request.getSession();
        
        // RECUPERAR O INICIALIZAR CARRITO
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        // OBTENER CONTEXTO INVENTARIO
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
        
        switch (action) {
            case "mostrar":
                cargarProductos(request);
                // Calcular total actual
                double totalActual = calcularTotal(carrito);
                request.setAttribute("totalVenta", totalActual);
                request.getRequestDispatcher("view/agregar_venta.jsp").forward(request, response);
                break;
                
            case "agregar":
                agregarProducto(request, session, carrito);
                response.sendRedirect("VentaServlet?action=mostrar");
                break;
                
            case "quitar":
                quitarProducto(request, carrito);
                response.sendRedirect("VentaServlet?action=mostrar");
                break;
                
            case "finalizar":
                if (idInventario == null) {
                    // Si no hay inventario activo, no se puede vender
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo");
                    return;
                }
                finalizarVenta(session, carrito, idInventario, response);
                break;
                
            case "listar":
                Integer idNegocio = (Integer) session.getAttribute("idNegocioActual");
                if (idNegocio != null) {
                    VentaDAO vDao = new VentaDAO();
                    List<Venta> listaVentas = vDao.listarVentas(idNegocio);
                    request.setAttribute("listaVentas", listaVentas);
                    request.getRequestDispatcher("view/visualizar_ventas.jsp").forward(request, response);
                } else {
                    response.sendRedirect("index.jsp"); // Sesión perdida
                }
                break;
                
            case "ver_detalle":
                try {
                    int idVenta = Integer.parseInt(request.getParameter("id_venta"));
                    VentaDAO vDaoDet = new VentaDAO();
                    List<DetalleVenta> listaDetalles = vDaoDet.listarDetalleVenta(idVenta);
                    request.setAttribute("listaDetalles", listaDetalles);
                    request.setAttribute("idVenta", idVenta); // Para mostrar en el título
                    request.getRequestDispatcher("view/detalle_venta.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendRedirect("VentaServlet?action=listar&error=ErrorAlVerDetalle");
                }
                break;
                
            case "cancelar":
                session.removeAttribute("carrito");
                response.sendRedirect("view/menu_inventario.html");
                break;
                
            default:
                response.sendRedirect("view/menu_inventario.html");
        }
    }

    private void cargarProductos(HttpServletRequest request) {
        ProductoDAO pDao = new ProductoDAO();
        List<Producto> lista = pDao.listarProductos();
        request.setAttribute("listaProductos", lista);
    }

    private void agregarProducto(HttpServletRequest request, HttpSession session, List<DetalleVenta> carrito) {
        try {
            int idProd = Integer.parseInt(request.getParameter("id_producto"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            
            if (cantidad <= 0) return;
            
            // Buscar producto para precio
            ProductoDAO pDao = new ProductoDAO();
            Producto p = pDao.obtenerProducto(idProd);
            
            if (p != null) {
                // Verificar si ya existe en carrito para sumar cantidad (Opcional, pero recomendado)
                boolean existe = false;
                for (DetalleVenta d : carrito) {
                    if (d.getIdProducto() == idProd) {
                        d.setCantidad(d.getCantidad() + cantidad);
                        d.setSubtotal(d.getCantidad() * d.getPrecioUnitario());
                        existe = true;
                        break;
                    }
                }
                
                if (!existe) {
                    DetalleVenta det = new DetalleVenta();
                    det.setIdProducto(idProd);
                    // Guardamos temporalmente el nombre del producto en el objeto Detalle? NO tiene campo nombre.
                    // Para mostrar el nombre en la tabla necesitamos una forma.
                    // Solución rápida: El JSP iterará y buscará... O mejor, cargamos nombres en el DAO?
                    // Por ahora, solo ID. En el JSP usaremos JSTL para mostrar nombre si es posible, 
                    // o añadiremos una propiedad "Transient" al modelo, o un Map.
                    // SIMPLIFICACIÓN: Asumimos que el JSP recibe la lista completa de productos y puede cruzar datos? No eficiente.
                    // MEJOR: Modificaremos DetalleVenta para añadir campo auxiliar "nombreProducto" (no persistido en DB)
                    // O simplemente confiamos en el ID por ahora.
                    
                    det.setNombreProducto(p.getNombre()); // Para mostrar en tabla
                    det.setCantidad(cantidad);
                    det.setPrecioUnitario(p.getPrecioUnitario());
                    det.setSubtotal(cantidad * p.getPrecioUnitario());
                    carrito.add(det);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void quitarProducto(HttpServletRequest request, List<DetalleVenta> carrito) {
        try {
            int index = Integer.parseInt(request.getParameter("index"));
            if (index >= 0 && index < carrito.size()) {
                carrito.remove(index);
            }
        } catch (NumberFormatException e) {}
    }

    private double calcularTotal(List<DetalleVenta> carrito) {
        double total = 0;
        for (DetalleVenta d : carrito) {
            total += d.getSubtotal();
        }
        return total;
    }
    
    private void finalizarVenta(HttpSession session, List<DetalleVenta> carrito, int idInventario, HttpServletResponse response) throws IOException {
        if (carrito.isEmpty()) {
            response.sendRedirect("VentaServlet?action=mostrar&error=CarritoVacio");
            return;
        }
        
        VentaDAO vDao = new VentaDAO();
        Venta venta = new Venta();
        venta.setIdInventario(idInventario);
        venta.setFechaVenta(new Date(System.currentTimeMillis()));
        venta.setTotalVenta(calcularTotal(carrito));
        
        boolean resultado = vDao.registrarVenta(venta, carrito);
        
        if (resultado) {
            session.removeAttribute("carrito");
            response.sendRedirect("view/venta_finalizada.html"); // Podríamos pasar el ID de venta
        } else {
            response.sendRedirect("VentaServlet?action=mostrar&error=ErrorAlGuardar");
        }
    }
}
