package com.inventario.controller; // Declaración de espacio de nombres organizativo de artefactos de red 

import com.inventario.dao.ProveedorDAO;
import com.inventario.model.Proveedor;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador Orquestador Independiente: ProveedorServlet.
 * 
 * Clase de abstracción web MVC que opera como delegado de las Entidades Foráneas Proveedor (Actores externos en la BD).
 * Recibe, procesa y sirve instanciación en memoria y peticiones transaccionales lógicas para CRUD sobre dichas colecciones.
 */
@WebServlet(name = "ProveedorServlet", urlPatterns = {"/ProveedorServlet"}) // Metadato anotacional inyectando la ruta local a nivel Servlet Container JVM
public class ProveedorServlet extends HttpServlet { // Capa controladora extendiendo la serialización nativa de peticiones web asíncronas

    /**
     * Rescritura de método pasivo HTTP Get.
     * Funciona como evaluador asimétrico principal del orquestador: Si hay String flag de acción en la query URL delega comportamientos mutativos como la eliminación en cascada controlada; sino, revuelve el Arreglo List puro a Vista JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Amparo de marco Java .
        
        ProveedorDAO dao = new ProveedorDAO(); // Setter o generador instanciador del orquestador de persistencia .
        String action = request.getParameter("action"); // Capturador semántico de petición
        
        // =====================================================================
        // ALGORITMO ORTOGONAL DE DESTRUCCIÓN PREVENTIVA (Protección FK Constraint)
        // =====================================================================
        if (action != null && action.equalsIgnoreCase("eliminar")) { // Estructura sub lógica string checker 
            int id = Integer.parseInt(request.getParameter("id")); // Lector cast de ID param as Integer primitive.
            boolean eliminado = dao.eliminarProveedor(id);         // Setter disparador DAO de subrutina de destrucción .
            if (!eliminado) { // Si check flag falla o falso .
                // Excepción funcional in - memory (No abort control sino flag string) para prever Integrity constraint Foreign keys FK asociadas al objeto . 
                request.setAttribute("errorEliminar", "No se puede eliminar porque tiene asociaciones dependientes o FKs restrictivas vinculadas (Pedidos Activos)."); // Envío string .
            }
        }
        
        // =====================================================================
        // DISPARADOR ACUMULADOR DE VISTA TABLA GLOBAL (Manejo general default Object Lists)
        // =====================================================================
        List<Proveedor> lista = dao.listarProveedores();           // Envoltura factory Array Dinámico POJO asimétrica iterando query.
        
        request.setAttribute("listaProveedores", lista);           // Atadura de puntero de List Relacional al request Buffer Scope vivo .
        request.getRequestDispatcher("view/lista_proveedores.jsp").forward(request, response); // Passthrough o despachador delegando in memory al Motor gráfico Template (Render Bypass).
    }

    /**
     * Rescritura de método Transaccional mutacional POST.
     * Toma String fields binarios HTML forms y los transforma iterativamente a atributos set en un POJO base asilado de Modelo para dispararlo mediante persistencia SQL Atómica de Inserción delegada via Gestor DAO.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Captura catch frame .
        
        // ALGORITMO OBTENEDOR DATA TRAMADO HTML SÍNCRONO .
        String nombre = request.getParameter("nombre_proveedor"); // Asimila descriptor text plano nombre.
        String contacto = request.getParameter("contacto");       // Asimila text name persona form.
        String telefono = request.getParameter("telefono");       // Extracción cardinal param text format number form.
        String correo = request.getParameter("correo");           // Extracción metadato uri mail form string.
        
        // CÚPULA CONSTRUCTORA DE ENTIDAD O POJO WRAPPER
        Proveedor p = new Proveedor(); // Instancia nuevo en Stack Heap Object Entity
        p.setNombreProveedor(nombre); // Método mutador o setter en variable abstracta privada capa encapsulada Oop.
        p.setContacto(contacto); // ..
        p.setTelefono(telefono); // ..
        p.setCorreo(correo);     // Setter Oop.
        
        // ORQUESTACIÓN DISPARADOR DAO (Transaction manager inyector SQL).
        ProveedorDAO dao = new ProveedorDAO(); // Generador factory .
        
        // PRE - CONDICIONAL ALGORÍTMICO VERIFICADOR DUPLICIDAD Booleana and String params.
        if (dao.existeProveedor(nombre, correo)) { // Invoca chequeador pasivo de constraint lógicos boolean flag string params .
            response.sendRedirect("view/Registro_datos_prv.html?error=proveedor_duplicado"); // Clean escape log URL param append error flag .
            return; // Corta flujo lógico asíncrono para invalidar el POJO vivo e insertion process.
        }

        try { // Trata insert exception .
            boolean exito = dao.registrarProveedor(p);  // Método síncrono setter delegativo enviando Wrapper Entity in memory completa al objeto Factory DAO de conexión .
            if (exito) { // Control check 
                response.sendRedirect("view/Proveedor_registrado.html");  // Concluye flujo UI en pantalla afirmativo statics frontend.
            } else { // Fallo atómico no runtime sino bool flag sql error false 
                response.sendRedirect("view/Registro_datos_prv.html?error=FalloRegistro"); // Devuelve print error url param redir .
            }
        } catch (Exception e) { // Suciedad framework error runtime Exception Trace  
            response.sendRedirect("view/Registro_datos_prv.html?error=" + e.getMessage().replace(" ", "_")); // Envuelve JVM Error msg in append string UI Error Redirect log.
        }
    }
}
