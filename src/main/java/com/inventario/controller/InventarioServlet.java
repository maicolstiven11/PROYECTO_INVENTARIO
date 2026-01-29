package com.inventario.controller;

import com.inventario.dao.InventarioDAO;
import com.inventario.dao.ProductoDAO;
import com.inventario.dao.DetalleInventarioDAO;
import com.inventario.model.Producto;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InventarioServlet", urlPatterns = {"/InventarioServlet"})
public class InventarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("InventarioServlet: action=" + action);
        
        if ("iniciar".equals(action)) {
            String idNegocioStr = request.getParameter("idNegocio");
            String tipoControl = request.getParameter("tipo");
            String fechaStr = request.getParameter("fecha");
            
            System.out.println("InventarioServlet: idNegocio=" + idNegocioStr + ", tipo=" + tipoControl + ", fecha=" + fechaStr);
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) {
                try {
                     int idNegocio = Integer.parseInt(idNegocioStr);
                    java.sql.Date fechaInicio = null;
                    
                    if (fechaStr != null && !fechaStr.isEmpty()) {
                        fechaInicio = java.sql.Date.valueOf(fechaStr);
                    } else {
                        fechaInicio = new java.sql.Date(System.currentTimeMillis());
                    }
                    
                    InventarioDAO dao = new InventarioDAO();
                    // Pasamos ahora la fecha
                    int idInventario = dao.iniciarInventario(idNegocio, tipoControl, fechaInicio);
                    
                    System.out.println("InventarioServlet: idInventario generado=" + idInventario);
                    
                    if (idInventario > 0) {
                        request.getSession().setAttribute("idInventarioActual", idInventario);
                        request.getSession().setAttribute("idNegocioActual", idNegocio);
                        // Redirigir a cargar detalle (Stock Inicial)
                        response.sendRedirect("InventarioServlet?action=cargar_detalle");
                    } else {
                        System.out.println("InventarioServlet: DAO devolvió -1, algo falló");
                        response.sendRedirect("NegocioServlet?error=FalloInicioInventario");
                    }
                } catch (Exception e) {
                    System.out.println("InventarioServlet ERROR: " + e.getMessage());
                    e.printStackTrace();
                    response.sendRedirect("NegocioServlet?error=" + e.getMessage());
                }
            } else {
                response.sendRedirect("NegocioServlet?error=SinIdNegocio");
            }
        } else if ("entrar".equals(action)) {
            // ENTRAR A INVENTARIO EXISTENTE
            String idNegocioStr = request.getParameter("idNegocio");
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) {
                int idNegocio = Integer.parseInt(idNegocioStr);
                InventarioDAO dao = new InventarioDAO();
                com.inventario.model.Inventario inv = dao.obtenerInventarioActivo(idNegocio);
                
                if (inv != null) {
                    request.getSession().setAttribute("idInventarioActual", inv.getIdInventario());
                    request.getSession().setAttribute("idNegocioActual", idNegocio);
                    response.sendRedirect("view/menu_inventario.jsp");
                } else {
                    response.sendRedirect("NegocioServlet?error=NoInventarioActivo");
                }
            }
        } else if ("cargar_detalle".equals(action)) {
            // CARGAR LISTA DE PRODUCTOS PARA STOCK INICIAL
            ProductoDAO prodDao = new ProductoDAO();
            List<Producto> listaProductos = prodDao.listarProductos();
            
            request.setAttribute("listaProductos", listaProductos);
            request.getRequestDispatcher("view/inventario_detalle.jsp").forward(request, response);
            
        } else if ("guardar_stock".equals(action)) {
            // AQUÍ PROCESAREMOS LA LISTA DE CANTIDADES
            try {
                // ... (código existente) ...

                // Redirigir al menú principal
                response.sendRedirect("view/menu_inventario.jsp");
                
            } catch (Exception e) {
                 e.printStackTrace();
                 response.sendRedirect("NegocioServlet?error=ErrorGuardarStock");
            }
            
        } else {
            response.sendRedirect("NegocioServlet");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
