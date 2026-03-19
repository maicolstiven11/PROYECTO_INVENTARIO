package com.inventario.controller; 

import com.inventario.dao.UsuarioDAO;      
import com.inventario.model.Usuario;        
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador PerfilServlet.
 * 
 * Es la página personal del usuario donde puede ver su nombre, 
 * sus números telefónicos asociados o agregar un segundo correo.
 */
@WebServlet(name = "PerfilServlet", urlPatterns = {"/PerfilServlet"}) 
public class PerfilServlet extends HttpServlet { 

    /**
     * El doGet simplemente consulta a la Base de datos todos tus teléfonos
     * y correos viejos, los mete en listas y los dibuja en la pantalla de tu perfil.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        HttpSession session = request.getSession(); // Llama variables activas
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado"); // Identifica quién eres (Ej: Cajero Juan)
        
        if (usuario == null) { // Por seguridad, si tu sesión expiró te bota
            response.sendRedirect("view/Inicio_sesion.html"); 
            return; 
        }
        
        // BD a trabajar
        UsuarioDAO dao = new UsuarioDAO(); 
        List<String> correos = dao.listarCorreos(usuario.getIdUsuario()); // Trae todos los @ que has usado
        List<String> telefonos = dao.listarTelefonos(usuario.getIdUsuario()); // Trae todos tus celulares
        
        // Pega esa lista de palabras a la memoria para que JSP las despliegue iterando
        request.setAttribute("listaCorreos", correos); 
        request.setAttribute("listaTelefonos", telefonos); 
        
        // Dirige a la pantalla UI del perfil de empleado
        request.getRequestDispatcher("view/perfil_admin.jsp").forward(request, response); 
    }

    /**
     * El doPost guarda las inserciones de nuevos correos o nuevos teléfonos. 
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
        
        String action = request.getParameter("action"); // Lee qué formulario mandaste apretar
        UsuarioDAO dao = new UsuarioDAO(); 
        
        if ("agregarCorreo".equals(action)) { 
            // =====================================================================
            // MANDASTE A AGREGAR OTRO E-MAIL DE RESPALDO A TU CUENTA
            // =====================================================================
            String nuevoCorreo = request.getParameter("nuevoCorreo"); // Atrapa qué escribiste
            if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) { // Revisa que no hayas mandado algo vacío
                boolean exito = dao.agregarCorreo(usuario.getIdUsuario(), nuevoCorreo.trim()); // Si logras guardarlo en BD...
                if (exito) {  
                    response.sendRedirect("PerfilServlet?msg=CorreoAgregado"); // Éxito verde
                } else { 
                    response.sendRedirect("PerfilServlet?error=CorreoYaExiste"); // Error de tabla, ya lo tenías asociado
                }
            } else { 
                response.sendRedirect("PerfilServlet?error=CorreoVacio"); // Te regaña por caja vacía
            }
            
        } else if ("agregarTelefono".equals(action)) { 
            // =====================================================================
            // MANDASTE A AGREGAR OTRO CELULAR DE EMERGENCIA
            // =====================================================================
            String nuevoTelefono = request.getParameter("nuevoTelefono"); 
            if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) { 
                boolean exito = dao.agregarTelefono(usuario.getIdUsuario(), nuevoTelefono.trim()); // Guarda tu celular
                if (exito) { 
                    response.sendRedirect("PerfilServlet?msg=TelefonoAgregado"); // Vuelve
                } else { 
                    response.sendRedirect("PerfilServlet?error=TelefonoYaExiste"); // Error si ya lo habias metido antes
                }
            } else { 
                response.sendRedirect("PerfilServlet?error=TelefonoVacio"); 
            }
            
        } else { 
            // Escape por defecto
            response.sendRedirect("PerfilServlet"); 
        }
    }
}
