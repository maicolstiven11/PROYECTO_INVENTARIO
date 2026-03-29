package com.inventario.controller;

import com.inventario.dao.NegocioDAO;
import com.inventario.model.Negocio;
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
 * Controlador NegocioServlet.
 * 
 * Maneja las operaciones sobre los bares/negocios:
 * - Listar los negocios del admin logueado
 * - Registrar uno nuevo
 * - Eliminar (si no tiene datos) o Inactivar (si tiene datos vinculados)
 */
@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"}) // Anotación que registra el servlet con nombre y URL
public class NegocioServlet extends HttpServlet { // Clase servlet que hereda de HttpServlet

    /**
     * doGet: Maneja las peticiones GET.
     * Según el parámetro "action", puede eliminar/inactivar un bar o listar todos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que atiende solicitudes GET
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Obtiene el parámetro "action" de la petición
        
        // =====================================================================
        // ACCIÓN: ELIMINAR O INACTIVAR
        // =====================================================================
        if ("eliminar".equals(action)) { // Si la acción es "eliminar"
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id")); // Convierte el parámetro "id" a entero
                NegocioDAO dao = new NegocioDAO(); // Instancia el DAO de negocio
                
                // Validar si el negocio tiene un inventario activo (abierto)
                com.inventario.dao.InventarioDAO invDao = new com.inventario.dao.InventarioDAO(); // DAO de inventario
                if (invDao.obtenerInventarioActivo(idNegocio) != null) { // Si existe inventario activo
                    // Si tiene inventario abierto, no se permite eliminar ni inactivar
                    response.sendRedirect("NegocioServlet?error=" + java.net.URLEncoder.encode("No se puede eliminar ni inactivar el negocio hasta que cierre el inventario activo.", "UTF-8"));
                    return; // Termina ejecución
                }
                
                // Verificar si el negocio tiene datos vinculados (inventarios, trabajadores, etc.)
                if (dao.negocioTieneDatos(idNegocio)) { // Si el negocio tiene datos asociados
                    // TIENE DATOS: solo se puede inactivar
                    boolean inactivado = dao.inactivarNegocio(idNegocio); // Intenta inactivar
                    if (inactivado) {
                        response.sendRedirect("NegocioServlet?status=inactivado"); // Redirige con estado inactivado
                    } else {
                        response.sendRedirect("NegocioServlet?error=No se pudo inactivar el negocio"); // Error al inactivar
                    }
                } else {
                    // NO TIENE DATOS: se puede eliminar completamente
                    boolean eliminado = dao.eliminarNegocio(idNegocio); // Intenta eliminar
                    if (eliminado) {
                        // Actualizar contador de bares en sesión
                        HttpSession session = request.getSession(); // Obtiene sesión
                        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Usuario en sesión
                        if (usuarioLogueado != null) {
                            int cantBares = dao.contarNegocios(usuarioLogueado.getIdUsuario()); // Cuenta bares del usuario
                            session.setAttribute("numBares", cantBares); // Actualiza número de bares en sesión
                        }
                        response.sendRedirect("NegocioServlet?status=eliminado"); // Redirige con estado eliminado
                    } else {
                        response.sendRedirect("NegocioServlet?error=No se pudo eliminar el negocio"); // Error al eliminar
                    }
                }
                return; // Termina ejecución
            } catch (Exception e) {
                e.printStackTrace(); // Muestra error en consola
                response.sendRedirect("NegocioServlet?error=" + e.getMessage()); // Redirige mostrando error
                return; // Termina ejecución
            }
        }
        
        // =====================================================================
        // ACCIÓN POR DEFECTO: LISTAR NEGOCIOS
        // =====================================================================
        HttpSession session = request.getSession(); // Obtiene sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Usuario en sesión
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario(); // Obtiene ID del usuario logueado
        }
        
        NegocioDAO dao = new NegocioDAO(); // Instancia DAO de negocio
        List<Negocio> lista = dao.listarNegocios(idUsuario); // Lista negocios del usuario
        
        request.setAttribute("listaBares", lista); // Envía lista como atributo a la vista
        request.getRequestDispatcher("view/Lista_bares.jsp").forward(request, response); // Redirige a JSP de lista
    }

    /**
     * doPost: Maneja el registro de un nuevo bar (formulario de creación).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que atiende solicitudes POST
            throws ServletException, IOException { 
        
        String nombre = request.getParameter("nombre"); // Obtiene nombre del bar
        String direccion = request.getParameter("direccion"); // Obtiene dirección del bar
        
        HttpSession session = request.getSession(); // Obtiene sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Usuario en sesión
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario(); // Obtiene ID del usuario logueado
        }
        
        Negocio n = new Negocio(); // Crea objeto negocio
        n.setNombre(nombre); // Asigna nombre
        n.setDireccion(direccion); // Asigna dirección
        
        NegocioDAO dao = new NegocioDAO(); // Instancia DAO de negocio
        try {
            int idGenerado = dao.registrarNegocio(n, idUsuario); // Registra negocio y devuelve ID generado
            
            if (idGenerado > 0) {
                try {
                    int cantBares = dao.contarNegocios(idUsuario); // Cuenta bares del usuario
                    session.setAttribute("numBares", cantBares); // Actualiza número de bares en sesión
                } catch(Exception e) { e.printStackTrace(); } 

                session.setAttribute("idNegocioActual", idGenerado);     // Guarda ID del nuevo negocio en sesión
                session.setAttribute("nombreNegocioActual", nombre);     // Guarda nombre del nuevo negocio en sesión
                response.sendRedirect("view/registroBar_fin.html");      // Redirige a página final de registro
            } else {
                response.sendRedirect("view/registroBar.html?error=FalloRegistroDAO"); // Error al registrar
            }
        } catch (Exception e) { 
            e.printStackTrace(); // Muestra error en consola
            response.sendRedirect("view/registroBar.html?error=" + e.getMessage().replace(" ", "_")); // Redirige mostrando error
        }
    }
}
