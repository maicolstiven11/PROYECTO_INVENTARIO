package com.inventario.controller;

import com.inventario.dao.UsuarioDAO;
import com.inventario.model.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador (Servlet) encargado de gestionar el proceso de inicio de sesión.
 * Recibe las peticiones desde el formulario de login, valida los datos con el DAO
 * y gestiona la sesión del usuario.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Procesa las solicitudes HTTP POST.
     * Este método se ejecuta cuando el usuario envía el formulario de inicio de sesión.
     * 
     * @param request Objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param response Objeto HttpServletResponse para enviar la respuesta al cliente.
     * @throws ServletException Si ocurre un error específico del Servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. OBTENER DATOS DEL FORMULARIO
        // Los nombres "email" y "password" deben coincidir con el 'name' en tu HTML
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // 2. LLAMAR AL MODELO (DAO)
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.validarLogin(email, password);
        
        // 3. RESPONDER SEGÚN EL RESULTADO
        if (usuario != null) {
            // LOGIN EXISTOSO
            
            // Creamos una "Sesión". Es como una credencial temporal que el servidor recuerda.
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario); // Guardamos al usuario en la sesión
            
            // ---- NUEVO: CARGAR ESTADÍSTICAS PARA EL PERFIL ----
            try {
                // Solo si es ADMIN (rol 1) nos interesa ver todos los trabajadores, etc.
                // O si quieres mostrarlo siempre, lo dejas fuera del if.
                
                com.inventario.dao.NegocioDAO negocioDao = new com.inventario.dao.NegocioDAO();
                int cantBares = negocioDao.contarNegocios(usuario.getIdUsuario());
                
                UsuarioDAO usuarioDao = new UsuarioDAO();
                int cantTrabajadores = usuarioDao.contarTrabajadores(); // Total en el sistema
                
                session.setAttribute("numBares", cantBares);
                session.setAttribute("numTrabajadores", cantTrabajadores);
                
            } catch(Exception e) {
                System.out.println("Error cargando estadísticas en login: " + e.getMessage());
                // No detenemos el login por esto, solo no saldrán los números
            }
            // ----------------------------------------------------

            // REDIRECCIONAR al menú principal
            // Ajusta "view/Menu_sistema.jsp" (antes .html)
            response.sendRedirect("view/Menu_sistema.jsp");
            
        } else {
            // LOGIN FALLIDO
            
            // Redirigimos de vuelta al login, quizás con un mensaje de error (podemos mejorarlo luego)
            // Agregamos ?error=1 para que el front sepa que falló
            response.sendRedirect("view/Inicio_sesion.html?error=1");
        }
    }
}
