package com.inventario.controller;

import com.inventario.dao.ProveedorDAO;
import com.inventario.model.Proveedor;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProveedorServlet", urlPatterns = {"/ProveedorServlet"})
public class ProveedorServlet extends HttpServlet {

    // GET: Para LISTAR proveedores y enviarlos a la página JSP
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ProveedorDAO dao = new ProveedorDAO();
        String action = request.getParameter("action");
        
        if (action != null && action.equalsIgnoreCase("eliminar")) {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.eliminarProveedor(id);
        }
        
        List<Proveedor> lista = dao.listarProveedores();
        
        request.setAttribute("listaProveedores", lista);
        request.getRequestDispatcher("view/lista_proveedores.jsp").forward(request, response);
    }

    // POST: Para REGISTRAR un nuevo proveedor desde el formulario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nombre = request.getParameter("nombre_proveedor");
        String contacto = request.getParameter("contacto");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        
        Proveedor p = new Proveedor();
        p.setNombreProveedor(nombre);
        p.setContacto(contacto);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        
        ProveedorDAO dao = new ProveedorDAO();
        
        try {
            boolean exito = dao.registrarProveedor(p);
            if (exito) {
                response.sendRedirect("view/Proveedor_registrado.html");
            } else {
                response.sendRedirect("view/Registro_datos_prv.html?error=FalloRegistro");
            }
        } catch (Exception e) {
            response.sendRedirect("view/Registro_datos_prv.html?error=" + e.getMessage().replace(" ", "_"));
        }
    }
}
