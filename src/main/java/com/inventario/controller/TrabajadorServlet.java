package com.inventario.controller;

import com.inventario.dao.NegocioDAO;
import com.inventario.dao.UsuarioDAO;
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
 * CONTROLADOR: Servlet para la gestión de trabajadores.
 * Permite al administrador ver trabajadores registrados
 * y asignarles un negocio (bar).
 */
@WebServlet(name = "TrabajadorServlet", urlPatterns = {"/TrabajadorServlet"})
public class TrabajadorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "listar";

        if ("listar".equals(action)) {
            // Cargar lista de trabajadores
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> listaTrabajadores = usuarioDAO.listarTrabajadores();

            // Cargar lista de negocios para el select de asignación
            HttpSession session = request.getSession();
            Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");

            if (admin != null) {
                NegocioDAO negocioDAO = new NegocioDAO();
                List<Negocio> listaNegocios = negocioDAO.listarNegocios(admin.getIdUsuario());

                request.setAttribute("listaTrabajadores", listaTrabajadores);
                request.setAttribute("listaNegocios", listaNegocios);
                request.getRequestDispatcher("view/gestion_trabajadores.jsp").forward(request, response);
            } else {
                response.sendRedirect("view/Inicio_sesion.html");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("asignar".equals(action)) {
            try {
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                int idNegocio = Integer.parseInt(request.getParameter("id_negocio"));

                UsuarioDAO dao = new UsuarioDAO();
                boolean exito = dao.asignarNegocio(idUsuario, idNegocio);

                if (exito) {
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=asignado");
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_asignar");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
        }
    }
}
