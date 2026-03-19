package com.inventario.controller; // Enrutamiento lógico y declarativo estructural 

import com.inventario.dao.InformeDAO;
import com.inventario.dao.InventarioDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador de Flujo Operativo: InformeServlet.
 * 
 * Clase estructurada bajo principios MVC enfocada en pre-procesar cálculos aritméticos complejos,
 * orquestar lecturas desde distintas fuentes (DAO) y construir subentidades (Attributes) que se pasarán a vistas en Dashboard.
 * Hereda de HttpServlet permitiendo control escalar sobre los verbos (HTTP methods) nativos.
 */
@WebServlet(name = "InformeServlet", urlPatterns = {"/InformeServlet"}) // Anotación de mapeo directo al despachador general (Tomcat)
public class InformeServlet extends HttpServlet { // Subclase orientada a comportamiento Servlet 

    /**
     * Sobrecarga del método iterador e interfaz consultivo GET.
     * Evalúa estados y parámetros mutables provistos por URL, disparando selectores de lógica de negocio (Business Logic) 
     * en capas persistentes subyacentes e interconectando resultados (Modelos y Primitivos) de regreso al front (Vista).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Delegación forzada de errores de contexto al contenedor 

        HttpSession session = request.getSession(); // Generación o extracción de la bolsa de envoltura temporal del usuario navegante 
        String idNegocioStr = request.getParameter("idNegocio"); // Selector literal o String temporal asimétrico 
        String idInventarioStr = request.getParameter("idInventario"); // Selector literal de sub-recurso 

        // Operador condicional (Ternario) que resuelve el polimorfismo o ausencia de variable.  Fallo en gracia acudiendo a memoria local de sesión
        Integer idNegocio = (idNegocioStr != null) ? Integer.parseInt(idNegocioStr) : (Integer) session.getAttribute("idNegocioActual");

        if (idNegocio == null) { // Punto restrictivo de anclaje base: si objeto Integer está huérfano (nulo) escapa el flujo.
            response.sendRedirect("NegocioServlet"); // Redirecionamiento en cascada para sanear estado in-memory 
            return; // Cierre de scope lógico inmediato abortando hilo transaccional.
        }

        InventarioDAO invDao = new InventarioDAO(); // Constructor base inyectivo de clase lectora relacional persistencia.

        // =====================================================================
        // ALGORITMO SELECTOR 1: Orquestación y Listado Histórico Maestro Base
        // =====================================================================
        if (idInventarioStr == null || idInventarioStr.isEmpty()) { // Comprobación string nativo y nulidad
            List<com.inventario.model.Inventario> listaInvs = invDao.listarInventariosPorNegocio(idNegocio); // Extracción con carga masiva Arraylist genérico tipado a modelo fuerte
            request.setAttribute("listaInventarios", listaInvs); // Modificador contextual: Acopla data al objeto contenedor Request transiente 
            request.setAttribute("nombreNegocio", session.getAttribute("nombreNegocioActual")); // Inyección simple de alias referencial String 
            request.getRequestDispatcher("view/lista_informes.jsp").forward(request, response); // Despacho estático del Buffer procesado hacia el JSP (Template Engine)
            return; // Termina el proceso encapsulado
        }

        // =====================================================================
        // ALGORITMO SELECTOR 2: Reporte Analítico e Inter-cruzamiento de Registros y Descuadres (Afectaciones Numéricas)
        // =====================================================================
        try { // Bloque vigilante de integridad paramétrica general (Ate un crash numérico)
            int idInventario = Integer.parseInt(idInventarioStr); // Parsificación de cadena a escalar puro Int
            
            // Sub-condicionamiento lógico extraído a partir del discriminador action 
            String action = request.getParameter("action"); // Localizador semántico asimétrico
            if ("ver_descuadre".equals(action)) { // Instancia eval in-memory
                com.inventario.dao.DetalleInventarioDAO detalleDao = new com.inventario.dao.DetalleInventarioDAO(); // Generación de interactor directo a capa inferior
                java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario); // Extracción relacional profunda estructurada
                request.setAttribute("listaDescuadre", detallesFinales); // Binding dinámico list a JSP
                request.setAttribute("modoHistorial", true); // Binding booleano de bandera visual condicional
                request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response); // Direccionamiento final hacia la plantilla reactiva 
                return; // Corta flujo actual
            }

            InformeDAO dao = new InformeDAO(); // Constructor lógico de orquestación analítica profunda inter-tabla.

            // =====================================================================
            // COMPUTACIÓN DE CLASE CERRADA (Extractor Múltiple de Cargas Acumuladas)
            // =====================================================================
            double totalVentas = dao.obtenerTotalVentas(idInventario); // Llamada resolutiva de carga doble 
            double totalGastos = dao.obtenerTotalGastos(idInventario); // Invocación a subrutinas independientes numéricas
            double totalPedidos = dao.obtenerTotalPedidos(idInventario); // Operador extractivo foráneo.
            int cantidadVentas = dao.obtenerCantidadVentas(idInventario); // Extractor sumatorio de cardinalidad Int

            // =====================================================================
            // POLIMORFISMO ARITMÉTICO LÓGICO Y CÁLCULOS 
            // =====================================================================
            double gananciaNeta = totalVentas - totalGastos - totalPedidos; // Reestructuracion mutativa en RAM (Ecuacion Financiera)

            // Extracción proporcional iterada matemáticamente
            double maxReferencia = Math.max(totalVentas, Math.max(totalGastos, totalPedidos)); // Operación estática abstracta utilitaria base de Math
            int porcentajeVentas = 0, porcentajeGastos = 0, porcentajePedidos = 0; // Inicializador escalar inicializado preventivamente para impedir crash Null.

            if (maxReferencia > 0) { // Limitante preventivo de divisiones por cero abstractas 
                porcentajeVentas = (int) ((totalVentas / maxReferencia) * 100); // Polimorfismo cast forzado (Double to Int) reductor
                porcentajeGastos = (int) ((totalGastos / maxReferencia) * 100); // Idem encapsulado anterior
                porcentajePedidos = (int) ((totalPedidos / maxReferencia) * 100); // Disminución de coma flotante asimétrica a base
            }

            // =====================================================================
            // MUTACIÓN Y ENROLLADO EN CONTEXTO DE DESPACHO
            // =====================================================================
            request.setAttribute("idInventario", idInventario); // Añade atributos aislados al motor JSP
            request.setAttribute("totalVentas", totalVentas); // Trasvasa float dobles puros a variables plantilla
            request.setAttribute("totalGastos", totalGastos); // Idem Inserción 
            request.setAttribute("totalPedidos", totalPedidos); // Idem acoplamiento estático 
            request.setAttribute("gananciaNeta", gananciaNeta); // Despacha resultante atada 
            request.setAttribute("cantidadVentas", cantidadVentas); // Amarra cardinal
            request.setAttribute("porcentajeVentas", porcentajeVentas); // Adjunta enteros porcentuales calculados
            request.setAttribute("porcentajeGastos", porcentajeGastos); // Agrega derivado temporal 
            request.setAttribute("porcentajePedidos", porcentajePedidos); // Adjunta la variable computada final en memoria 

            request.getRequestDispatcher("view/visualizar_informes.jsp").forward(request, response); // Despacha el envoltorio cargado (Pipeline MVC).

        } catch (NumberFormatException e) { // Resolutor condicional a error de incompatibilidad cast 
            response.sendRedirect("NegocioServlet?error=IdInventarioInvalido"); // Vaciado estático asíncrono preventivo redirigiendo error 
        }
    }
}
