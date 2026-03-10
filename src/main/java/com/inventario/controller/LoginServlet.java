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
 * CONTROLADOR: Servlet encargado del proceso de Inicio de Sesión.
 * 
 * Implementa: RF-02 (Iniciar Sesión), RF-03 (Gestionar Roles y Permisos), RF-28 (Dashboard Estadísticas)
 * Cumple: RNF-02 (Protección contra Inyección SQL - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - crea sesión HTTP al loguearse)
 *         RNF-04 (Control de Acceso - carga permisos del rol al iniciar sesión)
 *         RNF-08 (Mensajes de Error - redirige con ?error=1 si falla el login)
 *         RNF-13 (Arquitectura MVC - Capa Controlador separada de Modelo y Vista)
 * 
 * Recibe peticiones POST desde el formulario Inicio_sesion.html,
 * valida credenciales llamando al DAO y gestiona la sesión del usuario.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
// RF-02: La anotación @WebServlet registra este Servlet en la URL "/LoginServlet".
// Cuando el HTML envía el formulario a action="../LoginServlet", Tomcat lo dirige aquí.
public class LoginServlet extends HttpServlet {

    /**
     * RF-02: Método doPost - Se ejecuta cuando el usuario envía el formulario de login (method="POST").
     * 
     * @param request  Objeto que contiene los datos enviados por el usuario (email, password)
     * @param response Objeto que permite enviar la respuesta (redirección) al navegador
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // =====================================================================
        // RF-02 PASO 1: OBTENER DATOS DEL FORMULARIO HTML
        // request.getParameter("nombre") busca el input con name="nombre" en el HTML.
        // Los nombres "email" y "password" DEBEN coincidir EXACTAMENTE con los name="" del HTML.
        // RF-30: Validación de campos - el HTML usa "required" para validar en el cliente.
        // RF-31: Validación de formato email - el HTML usa type="email" para validar formato.
        // =====================================================================
        String email = request.getParameter("email");       // RF-02: Recibe el correo del input name="email"
        String password = request.getParameter("password"); // RF-02: Recibe la contraseña del input name="password"
        
        // =====================================================================
        // RF-02 PASO 2: LLAMAR AL DAO PARA VALIDAR CREDENCIALES EN LA BD
        // Creamos un objeto DAO (Data Access Object) que sabe hablar con MySQL.
        // Le pasamos email y password para que busque si existe ese usuario.
        // RNF-02: El DAO usa PreparedStatement internamente (protección contra SQL Injection).
        // RNF-13: El Servlet NO hace consultas SQL directamente, delega esa responsabilidad al DAO.
        // =====================================================================
        UsuarioDAO dao = new UsuarioDAO();                        // Creamos un objeto de la clase UsuarioDAO
        Usuario usuario = dao.validarLogin(email, password);     // RF-02: Ejecuta la consulta en la BD. Retorna Usuario si lo encuentra, null si no.
        
        // =====================================================================
        // RF-02 PASO 3: RESPONDER SEGÚN EL RESULTADO DE LA VALIDACIÓN
        // =====================================================================
        if (usuario != null) {
            // RF-02: LOGIN EXITOSO - El DAO encontró un usuario con esas credenciales
            
            // RF-02, RNF-03: Crear SESIÓN HTTP
            // La sesión es como una credencial temporal que el servidor recuerda.
            // Permite que el usuario navegue por otras páginas sin volver a loguearse.
            // RNF-03: La sesión expira automáticamente después de 30 min de inactividad (configurable en web.xml).
            HttpSession session = request.getSession();                    // Crea o recupera la sesión actual
            session.setAttribute("usuarioLogueado", usuario);              // RF-02: Guarda el objeto Usuario completo en la sesión
            // RF-03: Los permisos del usuario ya están cargados dentro del objeto "usuario" (el DAO los cargó)
            // RNF-04: Cualquier página JSP puede verificar permisos con ${usuarioLogueado.tienePermiso('NOMBRE')}
            
            // =====================================================================
            // RF-28: CARGAR ESTADÍSTICAS PARA EL DASHBOARD/PERFIL
            // Al iniciar sesión, cargamos automáticamente las estadísticas:
            // - Cantidad de bares del usuario
            // - Cantidad total de trabajadores en el sistema
            // Estas se muestran en el perfil del administrador.
            // =====================================================================
            try {
                com.inventario.dao.NegocioDAO negocioDao = new com.inventario.dao.NegocioDAO();
                int cantBares = negocioDao.contarNegocios(usuario.getIdUsuario()); // RF-28: Cuenta los bares del usuario
                
                UsuarioDAO usuarioDao = new UsuarioDAO();
                int cantTrabajadores = usuarioDao.contarTrabajadores();            // RF-28: Cuenta trabajadores totales
                
                session.setAttribute("numBares", cantBares);                       // RF-28: Guarda en sesión para mostrar en perfil
                session.setAttribute("numTrabajadores", cantTrabajadores);         // RF-28: Guarda en sesión para mostrar en perfil
                
            } catch(Exception e) {
                System.out.println("Error cargando estadísticas en login: " + e.getMessage());
                // RNF-08: No detenemos el login por un fallo en estadísticas, solo no saldrán los números
            }

            // Si es TRABAJADOR (rol 2), cargar automáticamente su negocio asignado
            if (usuario.getIdRol() == 2) {
                try {
                    com.inventario.model.Negocio negAsignado = dao.obtenerNegocioAsignado(usuario.getIdUsuario());
                    if (negAsignado != null) {
                        session.setAttribute("idNegocioActual", negAsignado.getIdNegocio());
                        session.setAttribute("nombreNegocioActual", negAsignado.getNombre());
                        
                        // Buscar si tiene inventario activo en ese negocio
                        com.inventario.dao.InventarioDAO invDao = new com.inventario.dao.InventarioDAO();
                        com.inventario.model.Inventario invActivo = invDao.obtenerInventarioActivo(negAsignado.getIdNegocio());
                        if (invActivo != null) {
                            session.setAttribute("idInventarioActual", invActivo.getIdInventario());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error cargando negocio del trabajador: " + e.getMessage());
                }
            }

            // RF-02: Redirigir al menú principal del sistema
            response.sendRedirect("view/Menu_sistema.jsp");
            
        } else {
            // RF-02: LOGIN FALLIDO - Las credenciales no coinciden con ningún usuario en la BD
            // RNF-08: Redirigimos al login con parámetro ?error=1 para mostrar mensaje de error al usuario
            response.sendRedirect("view/Inicio_sesion.html?error=1");
        }
    }

    /**
     * doGet: Maneja el cierre de sesión.
     * Invalida la sesión HTTP (borra todos los atributos: carrito, usuario, negocio, inventario).
     * Redirige al formulario de login.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false); // No crear sesión nueva si no existe
            if (session != null) {
                session.invalidate(); // Destruye TODA la sesión (usuario, carrito, negocio, inventario, etc.)
            }
            response.sendRedirect("view/Inicio_sesion.html");
        } else {
            response.sendRedirect("view/Inicio_sesion.html");
        }
    }
}
