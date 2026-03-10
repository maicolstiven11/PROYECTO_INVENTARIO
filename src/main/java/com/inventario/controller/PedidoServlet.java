package com.inventario.controller;

import com.inventario.dao.PedidoDAO;
import com.inventario.dao.ProductoDAO;
import com.inventario.dao.ProveedorDAO;
import com.inventario.model.DetallePedido;
import com.inventario.model.PedidoProveedor;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.model.Usuario;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CONTROLADOR: Servlet encargado de gestionar los Pedidos a Proveedores.
 * 
 * Implementa: RF-25 (Registrar Pedido a Proveedor)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - obtiene idInventarioActual de la sesión)
 *         RNF-08 (Mensajes de Error - redirige con parámetros de error descriptivos)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    /**
     * RF-25: Método doGet - Carga los datos necesarios para el formulario de nuevo pedido.
     * Carga la lista de proveedores (RF-23) y productos (RF-10) para los selects del formulario.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("listar".equals(action)) {
            // Listar pedidos del negocio actual
            HttpSession session = request.getSession();
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual");
            
            if (idNegocio != null) {
                PedidoDAO pedidoDAO = new PedidoDAO();
                List<PedidoProveedor> listaPedidos = pedidoDAO.listarPedidos(idNegocio);
                request.setAttribute("listaPedidos", listaPedidos);
                request.getRequestDispatcher("view/visualizar_pedidos.jsp").forward(request, response);
            } else {
                response.sendRedirect("index.jsp");
            }
        } else if (action == null || action.equals("nuevo")) {
            nuevoPedido(request, response);  // RF-25: Preparar formulario de nuevo pedido
        }
    }

    /**
     * RF-25: Carga listas de proveedores y productos del inventario para los dropdowns.
     * CAMBIADO: Ahora carga los items de inventario_detalle (productos con stock) en vez de todos los productos.
     */
    private void nuevoPedido(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        com.inventario.dao.DetalleInventarioDAO detalleDAO = new com.inventario.dao.DetalleInventarioDAO();
        
        HttpSession session = request.getSession();
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
        
        List<Proveedor> proveedores = proveedorDAO.listarProveedores();
        List<com.inventario.model.DetalleInventario> detalles = new ArrayList<>();
        
        if (idInventario != null) {
            detalles = detalleDAO.listarDetalles(idInventario);
        }
        
        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("listaDetalles", detalles);
        request.setAttribute("idInventarioActual", idInventario);
        
        request.getRequestDispatcher("view/agregar_pedido.jsp").forward(request, response);
    }

    /**
     * RF-25: Método doPost - Procesa y registra un nuevo pedido a proveedor.
     * Recibe los datos del formulario agregar_pedido.jsp, calcula total y precio unitario,
     * y guarda el pedido en la BD mediante transacción atómica.
     * RF-25 Restricción 1: Debe existir un inventario activo en sesión.
     * RF-25 Restricción 4: Operación transaccional (PEDIDOS_PROVEEDOR + DETALLE_PEDIDOS).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // =====================================================================
            // RF-25 PASO 1: Recoger datos del formulario HTML
            // =====================================================================
            int idProveedor = Integer.parseInt(request.getParameter("id_proveedor"));
            int idInvDetalle = Integer.parseInt(request.getParameter("id_inv_detalle")); // CAMBIADO: ahora recibe id_inv_detalle
            
            String fechaPedidoStr = request.getParameter("fecha_pedido");
            String fechaEntregaStr = request.getParameter("fecha_entrega");
            
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            double subtotal = Double.parseDouble(request.getParameter("subtotal"));
            double iva = Double.parseDouble(request.getParameter("iva")); // RESTAURADO
            
            // RESTAURADO: total = subtotal + IVA
            double total = subtotal + iva;
            double precioUnitario = total / cantidad;
            
            HttpSession session = request.getSession();
            Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
            
            if (idInventario == null) {
                response.sendRedirect("NegocioServlet?error=SinInventarioActivo");
                return;
            } 
            
            // Crear Pedido
            PedidoProveedor pedido = new PedidoProveedor();
            pedido.setIdProveedor(idProveedor);
            pedido.setFechaPedido(Date.valueOf(fechaPedidoStr));
            pedido.setFechaEntrega(Date.valueOf(fechaEntregaStr));
            pedido.setSubtotal(subtotal);   // RESTAURADO
            pedido.setIvaPedido(iva);      // RESTAURADO
            pedido.setTotalPedido(total);
            pedido.setIdInventario(idInventario);
            
            // Crear Detalle con id_inv_detalle (MANTIENE lógica de stock)
            DetallePedido detalle = new DetallePedido();
            detalle.setIdInvDetalle(idInvDetalle); 
            detalle.setCantidadPedida(cantidad);
            detalle.setPrecioUnitarioReal(precioUnitario);
            
            List<DetallePedido> detalles = new ArrayList<>();
            detalles.add(detalle);
            
            // Guardar en BD (transacción + stock automático)
            PedidoDAO pedidoDAO = new PedidoDAO();
            boolean exito = pedidoDAO.registrarPedido(pedido, detalles);
            
            if (exito) {
                response.sendRedirect("view/pedido_finalizado.html");  // RF-25: Redirigir a confirmación
            } else {
                // RNF-08: Redirigir con error
                response.sendRedirect("PedidoServlet?action=nuevo&msj=error");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // RNF-08: Redirigir con error descriptivo
            response.sendRedirect("PedidoServlet?action=nuevo&msj=error_datos");
        }
    }
}
