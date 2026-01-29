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
        
        // 1. RECIBIR DATOS DEL FORMULARIO
        // Nota: Los nombres aquí deben coincidir con los name="" del HTML
        String nombre = request.getParameter("nombre");
        String marca = request.getParameter("marca");
        String tipo = request.getParameter("tipo");
        String imagen = request.getParameter("imagen"); // Por ahora solo guardamos el nombre del archivo
        String cantidadMedida = request.getParameter("cantidad_medida");
        
        // Conversión de tipos (String a Double/Date)
        double precio = 0.0;
        try {
            precio = Double.parseDouble(request.getParameter("precio"));
        } catch (NumberFormatException e) {
            precio = 0.0; // Valor por defecto si falla
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

        // 2. CREAR OBJETO PRODUCTO
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setMarca(marca);
        p.setPrecioUnitario(precio);
        p.setTipo(tipo);
        p.setImagen(imagen);
        p.setFechaVencimiento(fechaVencimiento);
        p.setCantidadMedida(cantidadMedida);

        // 3. LLAMAR AL DAO
        ProductoDAO dao = new ProductoDAO();
        
        // Modificación para capturar error:
        try {
            boolean exito = dao.registrarProducto(p);
            if (exito) {
                response.sendRedirect("view/Produc_registrado.html"); 
            } else {
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");
            }
        } catch (Exception e) {
             // Enviamos el error a la URL para que el usuario lo vea
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
        } else {
            // Lógica por defecto: LISTAR
            java.util.List<Producto> lista = dao.listarProductos();
            request.setAttribute("listaProductos", lista);
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response);
        }
    }
}
