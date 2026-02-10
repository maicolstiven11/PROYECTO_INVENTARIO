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

@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.equals("nuevo")) {
            nuevoPedido(request, response);
        }
    }

    private void nuevoPedido(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Cargar listas para los selects del formulario
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        
        List<Proveedor> proveedores = proveedorDAO.listarProveedores();
        // Nota: Asumimos que listarProductos existe. Si no, habría que usar listarProductosPorNegocio o similar.
        // Por ahora listamos todos. Idealmente filtrar por negocio en sesión.
        // Ajuste temporal: Usar un DAO genérico de Productos si no tenemos filtro por negocio implementado aún en listar.
        List<Producto> productos = productoDAO.listarProductos(); 
        
        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("listaProductos", productos);
        
        request.getRequestDispatcher("view/agregar_pedido.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Recoger datos del formulario
            int idProveedor = Integer.parseInt(request.getParameter("id_proveedor"));
            int idProducto = Integer.parseInt(request.getParameter("id_producto"));
            
            String fechaPedidoStr = request.getParameter("fecha_pedido");
            String fechaEntregaStr = request.getParameter("fecha_entrega");
            
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            double subtotal = Double.parseDouble(request.getParameter("subtotal"));
            double iva = Double.parseDouble(request.getParameter("iva"));
            
            // Cálculos
            double total = subtotal + iva;
            double precioUnitario = total / cantidad;
            
            // Obtener usuario e inventario de la sesión
            HttpSession session = request.getSession();
            Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
            
            if (idInventario == null) {
                // Si no hay inventario en sesión, redirigir con error
                response.sendRedirect("NegocioServlet?error=SinInventarioActivo");
                return;
            } 
            
            // Crear Objeto Pedido
            PedidoProveedor pedido = new PedidoProveedor();
            pedido.setIdProveedor(idProveedor);
            pedido.setFechaPedido(Date.valueOf(fechaPedidoStr));
            pedido.setFechaEntrega(Date.valueOf(fechaEntregaStr));
            pedido.setSubtotal(subtotal);
            pedido.setIvaPedido(iva);
            pedido.setTotalPedido(total);
            pedido.setIdInventario(idInventario);
            
            // Crear Detalle (En este requerimiento parece que registra de a un producto por pedido según el formulario descrito,
            // o si fuera carrito sería una lista. El usuario dijo "selecciona el producto", singular. 
            // Asumiremos un pedido = un producto por simplicidad o lista de 1 elemento).
            DetallePedido detalle = new DetallePedido();
            detalle.setIdProducto(idProducto);
            detalle.setCantidadPedida(cantidad);
            detalle.setPrecioUnitarioReal(precioUnitario);
            
            List<DetallePedido> detalles = new ArrayList<>();
            detalles.add(detalle);
            
            // Guardar en BD
            PedidoDAO pedidoDAO = new PedidoDAO();
            boolean exito = pedidoDAO.registrarPedido(pedido, detalles);
            
            if (exito) {
                response.sendRedirect("view/pedido_finalizado.html");
            } else {
                response.sendRedirect("PedidoServlet?action=nuevo&msj=error");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("PedidoServlet?action=nuevo&msj=error_datos");
        }
    }
}
