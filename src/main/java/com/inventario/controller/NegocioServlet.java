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

@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"})
public class NegocioServlet extends HttpServlet {

    // GET: Para listar bares del usuario actual
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // MANEJO DE ELIMINACIÓN
        if ("eliminar".equals(action)) {
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id"));
                NegocioDAO dao = new NegocioDAO();
                boolean eliminado = dao.eliminarNegocio(idNegocio);
                
                // Redirigir de nuevo a la lista (sin parámetros para recargar)
                response.sendRedirect("NegocioServlet");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // MANEJO DE LISTADO (Comportamiento por defecto)
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

    // POST: Para registrar un nuevo bar
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nombre = request.getParameter("nombre");
        String direccion = request.getParameter("direccion");
        
        // OBTENER EL USUARIO DE LA SESIÓN
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
            // Pasar el idUsuario para vincular en USUARIO_NEGOCIO
            int idGenerado = dao.registrarNegocio(n, idUsuario);
            
            if (idGenerado > 0) {
                // ACTUALIZAR ESTADÍSTICA DE BARES EN LA SESIÓN (Para el perfil)
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
