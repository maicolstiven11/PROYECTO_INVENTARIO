
package com.inventario.controller; // Empaquetamiento estructural arquitectónico para los controladores de la aplicación

import com.inventario.dao.GastoDao;
import com.inventario.model.Gasto;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador de Flujo: GastoServlet.
 * 
 * Clase orquestadora (Controlador en patrón MVC) que intersecta las peticiones HTTP (Vistas) 
 * delegando la persistencia y consulta de estado lógico de la entidad 'Gasto' a la capa DAO (Modelo).
 * Hereda el comportamiento de procesamiento de servidor de la clase abstracta HttpServlet.
 */
@WebServlet("/GastoServlet") // Decorador o anotación de enrutamiento que suscribe esta clase al manejador de peticiones URL.
public class GastoServlet extends HttpServlet{ // Definición pública de clase con herencia fuerte de framework HTTP.
    
    /**
     * Sobreescritura del método transaccional HTTP POST.
     * Intercepta las inyecciones de formularios (payloads) para inicializar entidades POJO 
     * en memoria y dirigir su encapsulamiento hacia almacenaje persistente.
     * 
     * @param request Objeto encapsulador del estado y parámetros de la petición entrante.
     * @param response Objeto inyector para formular una salida resolutiva asíncrona o sincrónica.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Firma de método amparada contra manejo de excepciones de I/O y procesamiento Web.
        
        // =====================================================================
        // EXTRACCIÓN Y MAPEO DEL ESTADO (Binding de parámetros HTTP a variables RAM)
        // =====================================================================
        String descripcion = request.getParameter("descripcion");   // Extrae la cadena descriptiva de origen cliente.
        
        String fechaStr = request.getParameter("fecha");            // Extrae el literal cronológico para ser casteado posteriormente.
        
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));      // Extrae y transforma escalarmente a tipo primitivo Int.
        
        double subtotal = Double.parseDouble(request.getParameter("subtotal")); // Extrae y transforma escalarmente a tipo mutante Double.
        
        // =====================================================================
        // INSTANCIACIÓN DE ENTIDAD MODELO (Transferencia de estado inter-capas)
        // =====================================================================
        Gasto g = new Gasto(); // Genera un envoltorio atómico o POJO vacío en memoria.
        g.setDescripcion(descripcion); // Inyecta el estado alfanumérico al encapsulamiento interno.
        g.setFecha(Date.valueOf(fechaStr)); // Aplica Factory pattern sobre Date para parseo literal y setea la propiedad temporal.
        g.setCantidad(cantidad); // Aplica mutador inyectando el cardinal.
        g.setSubtotal(subtotal); // Aplica mutador inyectando la fracción computable.
        
        // RECUPERACIÓN DE CONTEXTO O AMBIENTE (Lectura de Sesión)
        HttpSession session = request.getSession(); // Llama al proveedor de sesión transiente.
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Invoca en polimorfismo o cast al envoltorio local temporal.
        g.setId_inventario(idInventario); // Amarre de relación transitiva o cardinal in memory.
        
        // =====================================================================
        // ORQUESTACIÓN DE CAPA DE DATOS (Data Access Object - DAO pattern)
        // =====================================================================
        GastoDao dao = new GastoDao(); // Crea una nueva instancia utilitaria constructiva inter BD.
        try{
            boolean ok = dao.registrarGasto(g);      // Delega el estado del objeto en cascada al motor transaccional. Retorna comprobación booleana de éxito.
            if (ok){ // Bifurcación en flujo regular.
                response.sendRedirect("view/gasto_finalizado.html");  // Cierre de hilo de ejecución ordenando al navegador una redirección a capa vista.
               
            }
            else{
                // Flujo alternativo si falla el motor transaccional pero no arroja excepción controlada
                response.sendRedirect("view/agregar_gasto.html?error=1"); // Redirige inyectando query param restrictivo
            }
        }catch (Exception e){ // Bloque atrapador de excepciones base operacionales inter-servicio.
            e.printStackTrace(); // Salida sucia o print logueable genérica del error apilable de la JVM.
            // Redireccionamiento forzado reactivo debido a volcado o null-pointer relacional.
            response.sendRedirect("view/agregar_gasto.html?error=1"); // Devuelve el hilo operando con param semántico
        }
               
    }

    /**
     * Sobreescritura del método consultivo HTTP GET.
     * Actúa como filtro lector, instanciando consultas masivas desde DAO para luego inyectar colecciones 
     * como atributos transientes en la petición actual que será re-despachada (forwarded) a una Vista JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Captura de subrutinas fallidas

        String action = request.getParameter("action"); // Extrae un discriminador lógico de flujo
        HttpSession session = request.getSession(); // Accesor a la capa temporal pre-autenticada o abstracta del usuario en memoria compartida

        if ("listar".equals(action)) { // Inicia filtro controlador de acción
            // Acceso relacional transiente (Atributo de sesión tipo envoltura)
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // Instancia el filtrador base a través de un cast implícito.

            if (idNegocio != null) { // Validador de existencia en memoria, para eludir Null-Pointer
                GastoDao dao = new GastoDao(); // Creación e inyección de generador lector relacional de capa de datos
                List<Gasto> listaGastos = dao.listarGastos(idNegocio);      // Disparo sincrónico del listado, retorna una Collection tipo List de objetos modelo.
                request.setAttribute("listaGastos", listaGastos);           // Amarra dinámicamente o acopla el Array extraído como metadato foráneo en la respuesta viva.
                request.getRequestDispatcher("view/visualizar_gastos.jsp").forward(request, response); // Despacha ortogonal o re-direcciona la request al template engine (JSP) internamente.
            } else {
                response.sendRedirect("index.jsp");  // Salida rápida en caso de pérdida de envoltura en memoria 
            }
        }
    }
    
}
