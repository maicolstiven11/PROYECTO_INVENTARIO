package com.inventario.controller;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.dao.UsuarioDAO;      // DAO para acceder a correos y teléfonos del usuario
import com.inventario.model.Usuario;        // Modelo POJO del usuario logueado
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CONTROLADOR: Servlet encargado de gestionar el Perfil del Usuario.
 * 
 * Permite:
 * - Ver el perfil con todos los correos y teléfonos registrados
 * - Agregar correos electrónicos adicionales
 * - Agregar teléfonos adicionales
 * 
 * URL: /PerfilServlet
 */
@WebServlet(name = "PerfilServlet", urlPatterns = {"/PerfilServlet"})
public class PerfilServlet extends HttpServlet {

    /**
     * doGet: Carga los datos del perfil y los envía a perfil_admin.jsp
     * Carga: lista de correos y lista de teléfonos del usuario logueado
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener el usuario de la sesión
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            // Si no hay usuario logueado, redirigir al login
            response.sendRedirect("view/Inicio_sesion.html");
            return;
        }
        
        // Cargar correos y teléfonos del usuario desde la BD
        UsuarioDAO dao = new UsuarioDAO();
        List<String> correos = dao.listarCorreos(usuario.getIdUsuario());
        List<String> telefonos = dao.listarTelefonos(usuario.getIdUsuario());
        
        // Pasar las listas al JSP
        request.setAttribute("listaCorreos", correos);
        request.setAttribute("listaTelefonos", telefonos);
        
        // Mostrar la vista del perfil
        request.getRequestDispatcher("view/perfil_admin.jsp").forward(request, response);
    }

    /**
     * doPost: Agrega un correo o teléfono adicional según el parámetro "action"
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            response.sendRedirect("view/Inicio_sesion.html");
            return;
        }
        
        String action = request.getParameter("action");
        UsuarioDAO dao = new UsuarioDAO();
        
        if ("agregarCorreo".equals(action)) {
            // Agregar un nuevo correo electrónico
            String nuevoCorreo = request.getParameter("nuevoCorreo");
            if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) {
                boolean exito = dao.agregarCorreo(usuario.getIdUsuario(), nuevoCorreo.trim());
                if (exito) {
                    response.sendRedirect("PerfilServlet?msg=CorreoAgregado");
                } else {
                    response.sendRedirect("PerfilServlet?error=CorreoYaExiste");
                }
            } else {
                response.sendRedirect("PerfilServlet?error=CorreoVacio");
            }
            
        } else if ("agregarTelefono".equals(action)) {
            // Agregar un nuevo teléfono
            String nuevoTelefono = request.getParameter("nuevoTelefono");
            if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) {
                boolean exito = dao.agregarTelefono(usuario.getIdUsuario(), nuevoTelefono.trim());
                if (exito) {
                    response.sendRedirect("PerfilServlet?msg=TelefonoAgregado");
                } else {
                    response.sendRedirect("PerfilServlet?error=TelefonoYaExiste");
                }
            } else {
                response.sendRedirect("PerfilServlet?error=TelefonoVacio");
            }
            
        } else {
            response.sendRedirect("PerfilServlet");
        }
    }
}
