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

/**
 * CONTROLADOR: Servlet encargado de gestionar los Proveedores.
 * 
 * Implementa: RF-22 (Registrar Proveedor), RF-23 (Listar Proveedores), RF-24 (Eliminar Proveedor)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-08 (Mensajes de Error - redirige con parámetros de error descriptivos)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "ProveedorServlet", urlPatterns = {"/ProveedorServlet"})
public class ProveedorServlet extends HttpServlet {

    /**
     * RF-23, RF-24: Método doGet - Lista proveedores y maneja eliminación.
     * Si recibe action=eliminar, elimina el proveedor (RF-24).
     * Siempre lista los proveedores al final (RF-23).
     * RF-23 Restricción 1: La lista se muestra en lista_proveedores.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ProveedorDAO dao = new ProveedorDAO();
        String action = request.getParameter("action");
        
        // =====================================================================
        // RF-24: ELIMINAR PROVEEDOR
        // Se ejecuta cuando la URL contiene ?action=eliminar&id=X
        // =====================================================================
        if (action != null && action.equalsIgnoreCase("eliminar")) {
            int id = Integer.parseInt(request.getParameter("id")); // RF-24: Obtener ID del proveedor a eliminar
            dao.eliminarProveedor(id);                             // RF-24: Eliminar de la BD
        }
        
        // =====================================================================
        // RF-23: LISTAR TODOS LOS PROVEEDORES (siempre se ejecuta)
        // =====================================================================
        List<Proveedor> lista = dao.listarProveedores();           // RF-23: Obtener todos los proveedores de la BD
        
        request.setAttribute("listaProveedores", lista);           // RF-23: Pasar la lista al JSP
        request.getRequestDispatcher("view/lista_proveedores.jsp").forward(request, response); // RF-23 Restricción 1: Vista JSP
    }

    /**
     * RF-22: Método doPost - Registra un nuevo proveedor.
     * Recibe los datos del formulario de registro de proveedores.
     * RF-22 Restricción 2: Todos los campos son obligatorios.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // RF-22 PASO 1: Recibir datos del formulario
        // RF-30: Campos obligatorios validados con "required" en el HTML
        String nombre = request.getParameter("nombre_proveedor"); // RF-22: Nombre del proveedor (obligatorio)
        String contacto = request.getParameter("contacto");       // RF-22: Persona de contacto (obligatorio)
        String telefono = request.getParameter("telefono");       // RF-22: Teléfono (obligatorio)
        String correo = request.getParameter("correo");           // RF-22: Correo electrónico (obligatorio)
        
        // RF-22 PASO 2: Crear objeto Modelo (Proveedor) y llenarlo
        Proveedor p = new Proveedor();
        p.setNombreProveedor(nombre);
        p.setContacto(contacto);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        
        // RF-22 PASO 3: Llamar al DAO para guardar en la BD
        ProveedorDAO dao = new ProveedorDAO();
        
        try {
            boolean exito = dao.registrarProveedor(p);  // RF-22: Inserta el proveedor en la tabla PROVEEDOR
            if (exito) {
                response.sendRedirect("view/Proveedor_registrado.html");  // RF-22: Redirigir a confirmación
            } else {
                // RNF-08: Redirigir con error
                response.sendRedirect("view/Registro_datos_prv.html?error=FalloRegistro");
            }
        } catch (Exception e) {
            // RNF-08: Redirigir con mensaje de error descriptivo
            response.sendRedirect("view/Registro_datos_prv.html?error=" + e.getMessage().replace(" ", "_"));
        }
    }
}
