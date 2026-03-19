package com.inventario.controller; // Regla de paquetería jerárquica y localizadora 

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
 * Controlador de Identidad o Autenticador: LoginServlet.
 * 
 * Clase de fachada y direccionamiento frontal (Front Controller Pattern approach).
 * Ejerce de núcleo iterativo captador en la capa Servlet, interconectando componentes DAO (Persistencia)
 * con la inyección de atributos de Sesión transientes para orquestar los estados duraderos pre-autorizados 
 * del conjunto de la estructura MVC a toda la plataforma.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
// Decorador de compilación para apuntar automáticamente sin XML este endpoint a las reglas estipuladas. 
// Aísla el patrón request-response acoplándolo como un observador estático visible ante envíos de formulario en View.
public class LoginServlet extends HttpServlet { // Subclase que instancia un ciclo de vida web 

    /**
     * Reescritura condicional posteo (HTTP POST Payload Catcher).
     * Aísla credenciales brutas provistas en la web (Interfaz cliente),
     * invocando las reglas internas lógicas encapsuladas en la clase DAO e introduciendo una jerarquía 
     * en memoria para gobernar sesiones completas durante el tránsito local o en cascada.
     * 
     * @param request  Contenedor envoltorio de peticiones con hash interno key-value (formulario de origen)
     * @param response Conector abstracto hacia flujos de salida o encabezados reactivos (redireccionamiento asíncrono o sincrónico)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Delegación forzada genérica de fallos runtime hacia Tomcat
        
        // =====================================================================
        // PARSEO DE PAYLOAD O TEXTOS BRUTOS A MEMORIA LÓGICA
        // Función interviniente: Mapea identificadores HTML contra String temporales en RAM local de este hilo
        // =====================================================================
        String email = request.getParameter("email");       // Adquisición de cadena literal a nivel red o de vista .
        String password = request.getParameter("password"); // Extracción del String ocultado del formulario .
        
        // =====================================================================
        // DISPARO DE PROCESO DE INTEGRIDAD BASE DE DATOS (Delegado)
        // Invoca subentidad o clase Data Access Object que esconde el framework relacional y
        // evita fugas de lógica foránea en el controlador. 
        // =====================================================================
        UsuarioDAO dao = new UsuarioDAO();                        // Instanciación directa en rama viva referenciable conectora MVC.
        Usuario usuario = dao.validarLogin(email, password);     // Ejecución resolutiva y de carga (Devuelve Entity Model fuerte encapsulado local o NULO total).
        
        // =====================================================================
        // ENRUTADOR CONDICIONAL DE RESOLUCIÓN BIFURCADA (Control Flujo Central)
        // =====================================================================
        if (usuario != null) { // Validador de existencia y confirmación lógica sobre el POJO resultante
            // INSTANCIA POSITIVA - Objeto no nulo asimila credencial real y validada .
            
            // CONSTRUCCIÓN E INYECCIÓN AMBIENTAL DE SESSION
            // Inicializa un canal transiente protegido que supervive en paralelo al transcurrir en la UI.
            HttpSession session = request.getSession();                    // Instancia recolectora de metadato abstracto vinculante al hash cliente-servidor 
            session.setAttribute("usuarioLogueado", usuario);              // Graba el perfil completo POJO de la DB dentro de un apuntador en vivo en el SessionScope
            // (La arquitectura modelo ya incluye de por sí listas de variables privadas y funciones lógicas que actúan en cascada o pasivamente)
            
            // =====================================================================
            // ORQUESTACIÓN ADICIONAL PARAMÉTRICA Y CÁLCULOS (Dashboards y vistas accesorias)
            // Pre-cargador (Eager loader) estático para complementar polimorfismo dinámico de interface antes de despachar visual .
            // =====================================================================
            try { // Envoltura asiladora frente a fallos analíticos que no deben colgar la matriz de seguridad .
                com.inventario.dao.NegocioDAO negocioDao = new com.inventario.dao.NegocioDAO(); // Llama entidad generadora asociada
                int cantBares = negocioDao.contarNegocios(usuario.getIdUsuario()); // Interroga y extrae iteración matemática base
                
                UsuarioDAO usuarioDao = new UsuarioDAO(); // Generador asilado 
                int cantTrabajadores = usuarioDao.contarTrabajadores();            // Resolución total contable asimétrica sobre registros locales
                
                session.setAttribute("numBares", cantBares);                       // Pasa el flag contable al contexto scope de presentación
                session.setAttribute("numTrabajadores", cantTrabajadores);         // Trasvasa resultante para manipulación y visual inter-app
                
            } catch(Exception e) { // Atrapatodo encapsulador o protector pasivo sobre analíticas relacionales en runtime
                System.out.println("Error cargando estadísticas en login: " + e.getMessage()); // Registro consola nativa 
                // Omisión lógica para sostener polimorfismo resiliente (Fallback safety)
            }

            // APLICACIÓN LÓGICA INDUCIDA Y CARGA REFERENCIAL A ENTIDADES DÉBILES RELACIONADAS (Trabajador)
            if (usuario.getIdRol() == 2) { // Verificador del campo relacional embebido numérico en POJO .
                try { // Bloque vigilante y resolutor extra
                    com.inventario.model.Negocio negAsignado = dao.obtenerNegocioAsignado(usuario.getIdUsuario()); // Interpelación con resultante polimórfico a modelo foráneo (POJO negocio dependiente).
                    if (negAsignado != null) { // Test pasivo asilador 
                        session.setAttribute("idNegocioActual", negAsignado.getIdNegocio()); // Disparo inyectivo contextual
                        session.setAttribute("nombreNegocioActual", negAsignado.getNombre()); // Amarre de semántica literal asilada.
                        
                        // CARGA EN CASCADA INTERNA TRANSIENTE (Sub-entidad relacionada Inventario)
                        com.inventario.dao.InventarioDAO invDao = new com.inventario.dao.InventarioDAO(); // Fabricación dinámica foránea DAO
                        com.inventario.model.Inventario invActivo = invDao.obtenerInventarioActivo(negAsignado.getIdNegocio()); // Cae a otro modelo o POJO independiente validando 
                        if (invActivo != null) { // Estricta nulidad evaluada transiente
                            session.setAttribute("idInventarioActual", invActivo.getIdInventario()); // Disparo contextual al puntero inter-vivo relacional 
                        }
                    }
                } catch (Exception e) { // Consola fallback 
                    System.out.println("Error cargando negocio del trabajador: " + e.getMessage()); // Impresión encapsulada 
                }
            }

            // REDIRECCIONAMIENTO DIRECTO EXITOSO EN BIFURCACIÓN PRINCIPAL 
            response.sendRedirect("view/Menu_sistema.jsp"); // Corta la subrutina devolviendo cabecera de enlace nuevo 
            
        } else {
            // INSTANCIA NEGATIVA O FALSA - POJO sin acoplar o fallo de inyección iterativa (Error Credencial)
            // Resolución asíncrona reactiva para enviar al buffer la rearmadura local
            response.sendRedirect("view/Inicio_sesion.html?error=1"); // Redirecciona inyectando flag temporal parametrizado restrictivo.
        }
    }

    /**
     * Sobrecarga del extractor polimórfico descriptivo simple GET.
     * En este scope, asume o intercepta una acción lógica aislada (Logout), interrumpiendo, 
     * purificando la memoria temporal viva y borrando todo su arbolaje session antes del renderizado de salida final.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Capturador forzado local error I/O framework Web 

        String action = request.getParameter("action"); // Extrae un discriminador transaccional puro.

        if ("logout".equals(action)) { // Disparador condicionado o puente evaluador lógico.
            HttpSession session = request.getSession(false); // Refiere el objeto contexto sin inicializar o abrir espacios redundantes vacíos ("False").
            if (session != null) { // Observador no nulo temporal
                session.invalidate(); // Llamado de subrutina destructora integral sobre la UI y envoltorio inter-comunicador
            }
            response.sendRedirect("view/Inicio_sesion.html"); // Vuelta hacia página base renderizada sin persistencia local .
        } else { // Fallback alterno 
            response.sendRedirect("view/Inicio_sesion.html"); // Evita loopings redirigiendo forzado a ruta sana originaria del Login general .
        }
    }
}
