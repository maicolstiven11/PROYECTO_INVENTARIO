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
@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"}) 
public class NegocioServlet extends HttpServlet { 

    /**
     * doGet: Maneja las peticiones GET.
     * Según el parámetro "action", puede eliminar/inactivar un bar o listar todos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action");
        
        // =====================================================================
        // ACCIÓN: ELIMINAR O INACTIVAR
        // =====================================================================
        if ("eliminar".equals(action)) {
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id"));
                NegocioDAO dao = new NegocioDAO();
                
                // Validar si el negocio tiene un inventario activo (abierto)
                com.inventario.dao.InventarioDAO invDao = new com.inventario.dao.InventarioDAO();
                if (invDao.obtenerInventarioActivo(idNegocio) != null) {
                    // Si tiene un inventario abierto, bloqueamos la acción por completo
                    response.sendRedirect("NegocioServlet?error=" + java.net.URLEncoder.encode("No se puede eliminar ni inactivar el negocio hasta que cierre el inventario activo.", "UTF-8"));
                    return;
                }
                
                // Verificar si el negocio tiene datos vinculados (inventarios, trabajadores, etc.)
                if (dao.negocioTieneDatos(idNegocio)) {
                    // TIENE DATOS: No se puede borrar, solo inactivar
                    boolean inactivado = dao.inactivarNegocio(idNegocio);
                    if (inactivado) {
                        response.sendRedirect("NegocioServlet?status=inactivado");
                    } else {
                        response.sendRedirect("NegocioServlet?error=No se pudo inactivar el negocio");
                    }
                } else {
                    // NO TIENE DATOS: Se puede borrar completamente
                    boolean eliminado = dao.eliminarNegocio(idNegocio);
                    if (eliminado) {
                        // Actualizar contador de bares en sesión
                        HttpSession session = request.getSession();
                        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
                        if (usuarioLogueado != null) {
                            int cantBares = dao.contarNegocios(usuarioLogueado.getIdUsuario());
                            session.setAttribute("numBares", cantBares);
                        }
                        response.sendRedirect("NegocioServlet?status=eliminado");
                    } else {
                        response.sendRedirect("NegocioServlet?error=No se pudo eliminar el negocio");
                    }
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("NegocioServlet?error=" + e.getMessage());
                return;
            }
        }
        
        // =====================================================================
        // ACCIÓN POR DEFECTO: LISTAR NEGOCIOS
        // =====================================================================
        HttpSession session = request.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario();
        }
        
        NegocioDAO dao = new NegocioDAO(); 
        List<Negocio> lista = dao.listarNegocios(idUsuario);
        
        request.setAttribute("listaBares", lista);
        request.getRequestDispatcher("view/Lista_bares.jsp").forward(request, response); 
    }

    /**
     * doPost: Maneja el registro de un nuevo bar (formulario de creación).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String nombre = request.getParameter("nombre");
        String direccion = request.getParameter("direccion");
        
        HttpSession session = request.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); 
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario();
        }
        
        Negocio n = new Negocio(); 
        n.setNombre(nombre); 
        n.setDireccion(direccion); 
        
        NegocioDAO dao = new NegocioDAO(); 
        try {
            int idGenerado = dao.registrarNegocio(n, idUsuario); 
            
            if (idGenerado > 0) {
                try {
                    int cantBares = dao.contarNegocios(idUsuario);        
                    session.setAttribute("numBares", cantBares);          
                } catch(Exception e) { e.printStackTrace(); } 

                session.setAttribute("idNegocioActual", idGenerado);     
                session.setAttribute("nombreNegocioActual", nombre);     
                response.sendRedirect("view/registroBar_fin.html");
            } else {
                response.sendRedirect("view/registroBar.html?error=FalloRegistroDAO"); 
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            response.sendRedirect("view/registroBar.html?error=" + e.getMessage().replace(" ", "_")); 
        }
    }
}
