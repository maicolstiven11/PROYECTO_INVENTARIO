package com.inventario.controller; // Entorno Base Modulo App Namespaces 

// =====================================================================
// IMPORTACIONES NECESARIAS DE CLASES (Acoplamiento a Modelos de Abstracción y Persistencia)
// =====================================================================
import com.inventario.dao.UsuarioDAO;      // Clase Gestora Transaccional y Constructora Generatriz Invocadora DAO Database persistence wrapper Manager.
import com.inventario.model.Usuario;        // Entidad Primordial Base Constructor Objeto Polimórfico Wrapper Logic Model POJO type Definition .
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador Transaccional Orquestador Secundario: PerfilServlet.
 * 
 * Fachada encapsuladora encargada de Mutar Object Entity Variables List Attributes Relacionales Multi-Valuados (Properties Object Collection Type string arrays parameters lists of Arrays strings email telephone parameters context model type wrapper definition of primitive parameters variables properties variables model data setter modifiers bounds check properties context logic constraint logic mutator bounds validation property logic property validator logic constraint limit properties boundaries of limits bounds boolean properties flags bounds values object array lists references ).
 */
@WebServlet(name = "PerfilServlet", urlPatterns = {"/PerfilServlet"}) // Decorador instanciador asimétrico Web Servlet routing framework annotation model class instantiator object base factory instantiator JVM parameter web XML mapping decorator metadata class behavior annotation flag boolean true limit.
public class PerfilServlet extends HttpServlet { // Polimorfismo hereditario Base Wrapper class Http Servlet Framework API.

    /**
     * Rescritura Genérica evaluadora HTTP Get.
     * Módulo Factory Constructor Getter Method Subrutina Lectura Object Data Relational Collections array string Properties setter property arrays parameter lists attributes context model in-memory attributes context logic variables attributes list String format collection bounds parameters context scope context setter model lists property parameters setter logic limits mutator constraint bounds array size loop validator limits check null. 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Amparo web framework .
        
        // Extracción Setter In-Memory Constructor Instance Pointer Object Model Singleton HTTP Pool Session Context User Variables 
        HttpSession session = request.getSession(); // Getter Session context memory pool
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado"); // Setter Getter param cast POJO reference wrapper instantiator logic parameter cast primitive object model type reference.
        
        if (usuario == null) { // Validator Flag Null Exception Protector Logic bounds.
            // Destructor loop redirect exit execution memory logic redirect param URL exception UI fall logic.
            response.sendRedirect("view/Inicio_sesion.html"); // Cleanup URL param clean reload page logic flag error redirect string UI HTTP Request UI Logic validation logic log exception Bounds logic limit.
            return; // Destroy memory variable method subrutina logic flow recursion method stack variable param bounds abort condition bool return execution memory. 
        }
        
        // Factoria Constructor Colecciones Relacionales Properties String parameters object reference arrays attributes lists array setter collection.
        UsuarioDAO dao = new UsuarioDAO(); // Wrapper Object Relation method transaccional builder Constructor Factory Data access model manager.
        List<String> correos = dao.listarCorreos(usuario.getIdUsuario()); // Method delegador List Wrapper instance Array Model Collection property param setter logic return List type collection string parameters.
        List<String> telefonos = dao.listarTelefonos(usuario.getIdUsuario()); // Getter mutador setter array string .
        
        // Context Injector Object variables List reference UI Web layer render parameters variables properties model objects. 
        request.setAttribute("listaCorreos", correos); // Inject Array variables parameter bounds context parameter wrapper limit object list limits boundary attributes pointer variables.
        request.setAttribute("listaTelefonos", telefonos); // Object property list variables.
        
        // Passthrough Render Output Despacho delegación o passthrough síncrono bypassing routing framework motor to JVM local JSP Render Engine scope context rendering variables properties framework param limit variables view.
        request.getRequestDispatcher("view/perfil_admin.jsp").forward(request, response); // Render dispatcher exception framework forward execution wrapper method loop logic validation framework dispatch UI render Engine response string buffer context variables properties objects UI context attribute parameter array lists execution request output data logic view .
    }

    /**
     * Rescritura de método Mutativo HTTP Post.
     * Controlador Modificador Mutador Setter Methods param action form input strings object bounds validations object properties attributes constraint checks properties limits constraint strings sizes check limit boolean mutative transactions method check condition bounds condition parameter. 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Framework base Exception handler error throw HTTP exceptions catch param errors limit execution constraints variables.
        
        HttpSession session = request.getSession(); // Session context memory factory instancer wrapper param scope limits pointer.
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado"); // POJO Property Cast method return parameter value setter parameter constraint bounds context memory object limit validation type cast method setter value pointer logic parameter.
        
        if (usuario == null) { // Guard checking param flag limit array check Null Exception pointer memory variable size limit execution limit state bounds parameters context variable limits .
            response.sendRedirect("view/Inicio_sesion.html"); // Return exception loop execution validation boolean constraint bounds false variable pointer property .
            return; // Exit execution method context.
        }
        
        String action = request.getParameter("action"); // Capturador Action Limit validator check param flag URL form method variable action flag pointer String variables condition bounds.
        UsuarioDAO dao = new UsuarioDAO(); // Object factory relation constructor Manager pattern variable variables logic bounds query constraint object pattern pointer bounds checks sizes model type manager.
        
        if ("agregarCorreo".equals(action)) { // String equality flag boolean return variable logic param string evaluator mutator condition loop boundary boolean parameters context equality validator properties limits boolean param match constraints target condition loop loop execution.
            // Insert Property Variable String target limit string bounds string properties model .
            String nuevoCorreo = request.getParameter("nuevoCorreo"); // UI input text attribute pointer property model variable param URL variable memory format primitive properties.
            if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) { // Boolean guard flag boundary property String lengths array Boolean checker exception pointer size bounds size parameter bounds variables limit flag variable memory Boolean pointer .
                boolean exito = dao.agregarCorreo(usuario.getIdUsuario(), nuevoCorreo.trim()); // Method invocation Property Boolean Validation return Boolean object property wrapper limits status return setter bounds array properties model attribute sizes parameters boolean setter parameter execution model variables string setter logic limit values parameters properties size values bounds flag validator limit constraint boolean property logic object context.
                if (exito) { // Boolean Loop condition flag . 
                    response.sendRedirect("PerfilServlet?msg=CorreoAgregado"); // Return Param condition execution loop flag validation.
                } else { // Exception condition fail flag boundary variables.
                    response.sendRedirect("PerfilServlet?error=CorreoYaExiste"); // Exceptions URL fallback limits boolean constraints memory values properties parameter constraint memory false flag variables.
                }
            } else { // Format check property constraint parameters logic object limits .
                response.sendRedirect("PerfilServlet?error=CorreoVacio"); // Null bounds pointer exception log limit variables fallback boolean param context limits boolean limits string checks variable bounds parameters logic memory pointer variables.
            }
            
        } else if ("agregarTelefono".equals(action)) { // Constraint boolean target properties logic equal limit string variables property limits check variables.
            // Setter property input parameter condition String size context type validation bounds form target boolean format boundary properties.
            String nuevoTelefono = request.getParameter("nuevoTelefono"); // UI string variable param .
            if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) { // Guard sizes Boolean constraint lengths format String bounds limits sizes target checks limits Boolean flags string parameter condition loops properties variables condition properties parameters flags limits values parameter boolean logic condition array property values constraint bounds target parameters lengths boolean exception sizes string checks limits memory properties logic boolean context parameters formats parameter limits lengths flags memory sizes .
                boolean exito = dao.agregarTelefono(usuario.getIdUsuario(), nuevoTelefono.trim()); // Pointer relation Dao object method wrapper memory constraints Boolean context property boolean parameters limit constraints size method Setter param limits properties.
                if (exito) { // Condition return validator logic checks properties bounds variables limits flags. 
                    response.sendRedirect("PerfilServlet?msg=TelefonoAgregado"); // Ok loop limit return condition UI.
                } else { // Exception constraint parameters properties arrays boundaries parameters bounds False .
                    response.sendRedirect("PerfilServlet?error=TelefonoYaExiste"); // Exception fall properties context loops parameters constraint memory string arrays limits memory.
                }
            } else { // Null format properties parameters arrays. 
                response.sendRedirect("PerfilServlet?error=TelefonoVacio"); // Null pointers flag exceptions limits constraints string checks parameters properties logic variables parameters execution limit parameters context flags bounds loop execution contexts memory logic array string variables parameters flag string property constraint memory limit check.
            }
            
        } else { // Genérico escape loop constraints variables condition exceptions memory limits .
            response.sendRedirect("PerfilServlet"); // Loop clear UI Context pointer validation checks flag limits reset property constraints variables bounds array limits context property limits .
        }
    }
}
