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
 * CONTROLADOR: Servlet encargado de gestionar los Negocios (Bares).
 * 
 * Implementa: RF-06 (Registrar Negocio), RF-07 (Listar Negocios del Usuario), RF-08 (Eliminar Negocio)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - guarda idNegocioActual y nombreNegocioActual en sesión)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"})
public class NegocioServlet extends HttpServlet {

    /**
     * RF-07, RF-08: Método doGet - Maneja listado de bares y eliminación.
     * Si recibe action=eliminar, elimina el bar.
     * Si no recibe action, lista todos los bares del usuario.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action"); // Leer parámetro de acción de la URL
        
        // =====================================================================
        // RF-08: ELIMINAR NEGOCIO
        // Se ejecuta cuando la URL contiene ?action=eliminar&id=X
        // =====================================================================
        if ("eliminar".equals(action)) {
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id")); // RF-08: Obtener ID del bar a eliminar
                NegocioDAO dao = new NegocioDAO();
                boolean eliminado = dao.eliminarNegocio(idNegocio);           // RF-08: Llama al DAO para eliminar de la BD
                
                // RF-08: Redirigir a la lista para recargar (sin parámetros = listar)
                response.sendRedirect("NegocioServlet");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // =====================================================================
        // RF-07: LISTAR NEGOCIOS DEL USUARIO (Comportamiento por defecto del GET)
        // Obtiene el usuario de la sesión y lista solo SUS bares.
        // RF-07 Restricción 1: Solo los bares asociados al id_usuario del usuario logueado.
        // =====================================================================
        HttpSession session = request.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Obtener usuario de sesión
        
        int idUsuario = 0;
        if (usuarioLogueado != null) {
            idUsuario = usuarioLogueado.getIdUsuario(); // RF-07: Obtener ID del usuario logueado
        }
        
        NegocioDAO dao = new NegocioDAO();
        List<Negocio> lista = dao.listarNegocios(idUsuario);        // RF-07: Consulta la BD por los bares de este usuario
        
        request.setAttribute("listaBares", lista);                  // RF-07: Pasa la lista a la vista JSP
        request.getRequestDispatcher("view/Lista_bares.jsp").forward(request, response); // RF-07: Muestra la vista
    }

    /**
     * RF-06: Método doPost - Registra un nuevo bar/negocio.
     * Recibe nombre y dirección del formulario, crea el negocio en la BD
     * y lo vincula al usuario logueado mediante la tabla USUARIO_NEGOCIO.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // RF-06 PASO 1: Recibir datos del formulario
        String nombre = request.getParameter("nombre");       // RF-06: Nombre del bar (obligatorio)
        String direccion = request.getParameter("direccion"); // RF-06: Dirección del bar (obligatoria)
        
        // RF-06 PASO 2: Obtener el usuario de la sesión para vincularlo al bar
        HttpSession session = request.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        int idUsuario = 0;
        if (usuarioLogueado != null) {
            idUsuario = usuarioLogueado.getIdUsuario(); // RF-06 Restricción 1: El bar queda vinculado al usuario autenticado
        }
        
        // RF-06 PASO 3: Crear objeto Negocio (Modelo) y llenarlo
        Negocio n = new Negocio();
        n.setNombre(nombre);
        n.setDireccion(direccion);
        
        // RF-06 PASO 4: Llamar al DAO para guardar en la BD
        NegocioDAO dao = new NegocioDAO();
        try {
            // RF-06: registrarNegocio inserta en NEGOCIO y en USUARIO_NEGOCIO
            // RF-06 Restricción 2: Se genera un ID automático (AUTO_INCREMENT)
            int idGenerado = dao.registrarNegocio(n, idUsuario);
            
            if (idGenerado > 0) {
                // RF-06, RF-28: Actualizar estadística de bares en la sesión
                try {
                    int cantBares = dao.contarNegocios(idUsuario);        // RF-28: Recalcular cantidad de bares
                    session.setAttribute("numBares", cantBares);          // RF-28: Actualizar en sesión
                } catch(Exception e) { e.printStackTrace(); }

                // RF-06 Restricción 3: Guardar datos del nuevo bar en sesión para uso posterior
                session.setAttribute("idNegocioActual", idGenerado);     // Para que otros módulos sepan qué bar está activo
                session.setAttribute("nombreNegocioActual", nombre);     // Para mostrar el nombre en las vistas
                response.sendRedirect("view/registroBar_fin.html");      // RF-06: Redirigir a confirmación
            } else {
                // RNF-08: Redirigir con error
                response.sendRedirect("view/registroBar.html?error=FalloRegistroDAO");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // RNF-08: Redirigir con mensaje de error
            response.sendRedirect("view/registroBar.html?error=" + e.getMessage().replace(" ", "_"));
        }
    }
}
