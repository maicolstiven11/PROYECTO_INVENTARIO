package com.inventario.controller;

import com.inventario.dao.ProductoDAO;
import com.inventario.model.Producto;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // VERIFICAR SI ES UNA ACTUALIZACIÓN
        String action = request.getParameter("action");
        if ("actualizar".equals(action)) {
            procesarActualizacion(request, response);
            return;
        }
        
        // --- LÓGICA PARA REGISTRAR NUEVO PRODUCTO ---
        // 1. RECIBIR DATOS DEL FORMULARIO
        String nombre = request.getParameter("nombre");
        String marca = request.getParameter("marca");
        String tipo = request.getParameter("tipo");
        String imagen = request.getParameter("imagen");
        String cantidadMedida = request.getParameter("cantidad_medida");
        
        double precio = 0.0;
        try {
            precio = Double.parseDouble(request.getParameter("precio"));
        } catch (NumberFormatException e) {
            precio = 0.0;
        }
        
        Date fechaVencimiento = null;
        try {
            String fechaStr = request.getParameter("fecha_vencimiento");
            if(fechaStr != null && !fechaStr.isEmpty()){
                fechaVencimiento = Date.valueOf(fechaStr);
            }
        } catch (IllegalArgumentException e) {
            fechaVencimiento = null;
        }

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setMarca(marca);
        p.setPrecioUnitario(precio);
        p.setTipo(tipo);
        p.setImagen(imagen);
        p.setFechaVencimiento(fechaVencimiento);
        p.setCantidadMedida(cantidadMedida);

        ProductoDAO dao = new ProductoDAO();
        
        try {
            boolean exito = dao.registrarProducto(p);
            if (exito) {
                response.sendRedirect("view/Produc_registrado.html"); 
            } else {
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");
            }
        } catch (Exception e) {
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        ProductoDAO dao = new ProductoDAO();
        
        if (action != null && action.equals("eliminar")) {
            // Lógica para eliminar
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean eliminado = dao.eliminarProducto(id);
                // Redirigir al mismo servlet para recargar la lista
                response.sendRedirect("ProductoServlet"); 
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("ProductoServlet?error=ErrorEliminar");
            }
        } else if (action != null && action.equals("editar")) {
            // CARGAR DATOS DEL PRODUCTO PARA EDITAR
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Producto p = dao.obtenerProducto(id);
                
                if (p != null) {
                    request.setAttribute("productoEditar", p);
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response);
                } else {
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion");
            }
        } else {
            // Lógica por defecto: LISTAR
            java.util.List<Producto> lista = dao.listarProductos();
            request.setAttribute("listaProductos", lista);
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response);
        }
    }
    
    // MÉTODO PARA PROCESAR LA ACTUALIZACIÓN (POST)
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int idProducto = Integer.parseInt(request.getParameter("id_producto"));
            String nombre = request.getParameter("nombre");
            String marca = request.getParameter("marca");
            String tipo = request.getParameter("tipo");
            String imagen = request.getParameter("imagen");
            String cantidadMedida = request.getParameter("cantidad_medida");
            
            double precio = 0.0;
            try {
                precio = Double.parseDouble(request.getParameter("precio"));
            } catch (NumberFormatException e) {
                precio = 0.0;
            }
            
            java.sql.Date fechaVencimiento = null;
            try {
                String fechaStr = request.getParameter("fecha_vencimiento");
                if (fechaStr != null && !fechaStr.isEmpty()) {
                    fechaVencimiento = java.sql.Date.valueOf(fechaStr);
                }
            } catch (IllegalArgumentException e) {
                fechaVencimiento = null;
            }
            
            Producto p = new Producto();
            p.setIdProducto(idProducto);
            p.setNombre(nombre);
            p.setMarca(marca);
            p.setPrecioUnitario(precio);
            p.setTipo(tipo);
            p.setImagen(imagen);
            p.setFechaVencimiento(fechaVencimiento);
            p.setCantidadMedida(cantidadMedida);
            
            ProductoDAO dao = new ProductoDAO();
            boolean exito = dao.actualizarProducto(p);
            
            if (exito) {
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado");
            } else {
                response.sendRedirect("ProductoServlet?error=ErrorActualizar");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ProductoServlet?error=" + e.getMessage());
        }
    }
}
