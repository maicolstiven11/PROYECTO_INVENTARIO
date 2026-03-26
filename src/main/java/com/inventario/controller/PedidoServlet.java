package com.inventario.controller;

import com.inventario.dao.PedidoDAO;
import com.inventario.dao.ProductoDAO;
import com.inventario.dao.ProveedorDAO;
import com.inventario.model.DetallePedido;
import com.inventario.model.PedidoProveedor;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
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
 * Controlador PedidoServlet.
 * 
 * Se encarga de manejar las compras a proveedores (ej: Pedir más cervezas).
 * Sirve para ver la lista de facturas pendientes o crear un nuevo pedido.
 */
@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"}) 
public class PedidoServlet extends HttpServlet { 

    /**
     * El método doGet puede mostrar los pedidos hechos o abrir
     * la pantalla en blanco para armar uno nuevo.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Lee qué botón apretaste
        
        if ("listar".equals(action)) { 
            // =====================================================================
            // ACCIÓN: VER TODOS LOS PEDIDOS QUE HEMOS HECHO
            // =====================================================================
            HttpSession session = request.getSession(); // Accedemos a la memoria local 
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // Identificamos qué bar es
            
            if (idNegocio != null) { 
                PedidoDAO pedidoDAO = new PedidoDAO(); // Preparamos la Base de Datos
                List<PedidoProveedor> listaPedidos = pedidoDAO.listarPedidos(idNegocio); // Pedimos nuestra colección de recibos
                request.setAttribute("listaPedidos", listaPedidos); // Pegamos el resultado para que el HTML lo lea
                request.getRequestDispatcher("view/visualizar_pedidos.jsp").forward(request, response); // Pintamos pantalla
            } else {
                response.sendRedirect("index.jsp"); // Si no tiene negocio, lo sacamos del programa
            }
        } else if (action == null || action.equals("nuevo")) { 
            // =====================================================================
            // ACCIÓN: INICIAR EL FORMULARIO PARA CREAR UNO NUEVO
            // =====================================================================
            nuevoPedido(request, response);  // Llama a una función más pequeña para no amontonar código aquí
        }
    }

    /**
     * Función que prepara el carrito de compras del Nuevo Pedido.
     * Como para pedir necesitas saber a quién (Proveedores) y qué (Productos), 
     * pedimos esas listas a la BD antes de pintar la web.
     */
    private void nuevoPedido(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { 
        ProveedorDAO proveedorDAO = new ProveedorDAO(); 
        ProductoDAO productoDAO = new ProductoDAO(); 
        
        HttpSession session = request.getSession(); 
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Revisar en qué mes nos lo gastaremos
        
        List<Proveedor> proveedores = proveedorDAO.listarProveedores(); // Traer todos los vendedores de cerveza/papas
        Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual");
        if (idNegocio == null) idNegocio = 0;
        List<Producto> listaProductos = productoDAO.listarProductos(idNegocio);
        
        // Pega estas dos listas en el request para que tu HTML dibuje las etiquetas <select>
        request.setAttribute("listaProveedores", proveedores); 
        request.setAttribute("listaProductos", listaProductos); 
        request.setAttribute("idInventarioActual", idInventario); 
        
        request.getRequestDispatcher("view/agregar_pedido.jsp").forward(request, response); // Mostrar HTML
    }

    /**
     * El método doPost se gatilla cuando le das a "Confirmar Compra/Pedido".
     * Atrapa precio, cantidad y fecha, creando el modelo para mandarlo guardar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        try { 
            // Transformamos lo que digitó el usuario en textos, a números matemáticos
            int idProveedor = Integer.parseInt(request.getParameter("id_proveedor")); // El vendedor Bavaria
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); // El producto Cerveza 
            
            String fechaPedidoStr = request.getParameter("fecha_pedido"); // Fecha de hoy
            String fechaEntregaStr = request.getParameter("fecha_entrega"); // Llegará el viernes
            
            int cantidad = Integer.parseInt(request.getParameter("cantidad")); // Quiero 50 unidades
            double subtotal = Double.parseDouble(request.getParameter("subtotal")); // Costaron $10.000
            double iva = Double.parseDouble(request.getParameter("iva")); // Impuestos
            
            // Matemática calculada
            double total = subtotal + iva; // Cobro final
            double precioUnitario = total / cantidad; // Cuánto cuesta dividiéndolo
            
            HttpSession session = request.getSession(); 
            Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Conocer a qué periodo contable nos van a cargar este gasto
            
            if (idInventario == null) { // Por si acaso se quedó la sesión vacía
                response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); 
                return; 
            } 
            
            // Averiguar si ese producto ya está en las estanterías de este mes (para darle su ID) o crearlo en 0
            com.inventario.dao.DetalleInventarioDAO detDao = new com.inventario.dao.DetalleInventarioDAO(); 
            int idInvDetalle = detDao.obtenerOCrearDetalle(idInventario, idProducto); 
            
            // 1. Armamos la caja gigante (La Factura base del proveedor)
            PedidoProveedor pedido = new PedidoProveedor(); 
            pedido.setIdProveedor(idProveedor); 
            pedido.setFechaPedido(Date.valueOf(fechaPedidoStr)); 
            pedido.setFechaEntrega(Date.valueOf(fechaEntregaStr)); 
            pedido.setSubtotal(subtotal);   
            pedido.setIvaPedido(iva);      
            pedido.setTotalPedido(total); 
            pedido.setIdInventario(idInventario); 
            
            // 2. Armamos la bolsita pequeña (El detalle específico "Cervezas x50")
            DetallePedido detalle = new DetallePedido(); 
            detalle.setIdInvDetalle(idInvDetalle); 
            detalle.setCantidadPedida(cantidad); 
            detalle.setPrecioUnitarioReal(precioUnitario); 
            
            List<DetallePedido> detalles = new ArrayList<>(); // Ponemos la bolsita pequeña en un arreglo general de compras
            detalles.add(detalle); 
            
            // 3. Mandamos TODO empaquetado a la BD para guardar
            PedidoDAO pedidoDAO = new PedidoDAO(); 
            boolean exito = pedidoDAO.registrarPedido(pedido, detalles); // Realiza toda la operación
            
            if (exito) { 
                response.sendRedirect("view/pedido_finalizado.html");  // ¡Pedido logrado!
            } else {
                response.sendRedirect("PedidoServlet?action=nuevo&msj=error"); // Mensaje error
            }
            
        } catch (Exception e) {  
            e.printStackTrace(); 
            response.sendRedirect("PedidoServlet?action=nuevo&msj=error_datos"); // Devuelve porque alguien digitó mal un número o formato
        }
    }
}
