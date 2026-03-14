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

                // Validar que el bar no tenga ya un trabajador asignado
                if (dao.negocioTieneTrabajador(idNegocio)) {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=bar_ocupado");
                    return; // Detener la ejecución
                }

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
        } else if ("desasignar".equals(action)) {
            try {
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                UsuarioDAO dao = new UsuarioDAO();
                boolean exito = dao.desasignarNegocio(idUsuario);

                if (exito) {
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=desasignado");
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_desasignar");
                }
            } catch (Exception e) {
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
        } else if ("eliminar".equals(action)) {
            try {
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                UsuarioDAO dao = new UsuarioDAO();
                boolean exito = dao.eliminarTrabajador(idUsuario);

                if (exito) {
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=eliminado");
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_eliminar");
                }
            } catch (Exception e) {
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
        } else if ("resetPassword".equals(action)) {
            // RF-05: Solo admin puede resetear contraseña de trabajadores
            try {
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                String nuevaPassword = request.getParameter("nueva_password");
                String confirmarPassword = request.getParameter("confirmar_password");

                // Validar que coincidan
                if (nuevaPassword == null || nuevaPassword.length() < 6) {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_corta");
                    return;
                }
                if (!nuevaPassword.equals(confirmarPassword)) {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_no_coincide");
                    return;
                }

                UsuarioDAO dao = new UsuarioDAO();
                boolean exito = dao.actualizarPassword(idUsuario, nuevaPassword);

                if (exito) {
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=password_reseteada");
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_reset");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
        }
    }
}
