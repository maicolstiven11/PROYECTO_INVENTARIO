package com.inventario.controller;

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
 * Controlador InformeServlet.
 * 
 * Se encarga de mostrar los informes contables. Pide al DAO la lista de inventarios 
 * y usa operaciones matemáticas para calcular ganancias brutas, netas y promedios 
 * dependiendo de lo que el cliente gastó y vendió.
 */
@WebServlet(name = "InformeServlet", urlPatterns = {"/InformeServlet"}) // Mapeo de URL para que reconozca este archivo
public class InformeServlet extends HttpServlet { 

    /**
     * Al recibir una petición tipo GET (desde un botón o enlace en la web), 
     * arma la información financiera del inventario seleccionado.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 

        HttpSession session = request.getSession(); // Ingresa a la memoria viva de la sesión del usuario
        String idNegocioStr = request.getParameter("idNegocio"); // Extrae un ID de negocio si vino por la URL
        String idInventarioStr = request.getParameter("idInventario"); // Extrae un ID de inventario si vino por la URL

        // Si llegó el idNegocio por URL lo convierte a número. Si no, lo extrae directamente de la memoria de la sesión (Atributo guardado)
        Integer idNegocio = (idNegocioStr != null) ? Integer.parseInt(idNegocioStr) : (Integer) session.getAttribute("idNegocioActual");

        if (idNegocio == null) { // Si resulta que no se encontró en qué negocio estamos parados
            response.sendRedirect("NegocioServlet"); // Redirige al inicio de locales
            return; // Detiene la ejecución aquí
        }

        InventarioDAO invDao = new InventarioDAO(); // Instanciamos la clase que manipula Inventarios en la BD

        // =====================================================================
        // OPCIÓN 1: MOSTRAR LISTA RESUMIDA DE TODOS LOS PERIODOS (INVENTARIOS)
        // =====================================================================
        if (idInventarioStr == null || idInventarioStr.isEmpty()) { // Si en la URL NO especificaron un inventario concreto...
            // Trae toda la colección o lista de periodos (inventarios) de este negocio
            List<com.inventario.model.Inventario> listaInvs = invDao.listarInventariosPorNegocio(idNegocio); 
            request.setAttribute("listaInventarios", listaInvs); // Pega la lista a la página
            request.setAttribute("nombreNegocio", session.getAttribute("nombreNegocioActual")); // Pega el nombre del bar
            request.getRequestDispatcher("view/lista_informes.jsp").forward(request, response); // Manda a pintar el JSP de lista de informes
            return; // Corta la ejecución
        }

        // =====================================================================
        // OPCIÓN 2: MOSTRAR EL REPORTE MATEMÁTICO DETALLADO DE UN INVENTARIO ESPECÍFICO
        // =====================================================================
        try { 
            int idInventario = Integer.parseInt(idInventarioStr); // Convertimos a número entero el ID del inventario que le dimos click
            
            // Si el cliente pide ver qué productos NO cuadraron
            String action = request.getParameter("action"); 
            if ("ver_descuadre".equals(action)) { 
                com.inventario.dao.DetalleInventarioDAO detalleDao = new com.inventario.dao.DetalleInventarioDAO(); 
                // Sacamos todos los productos (papás, cervezas) con su precio del inventario
                java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario); 
                request.setAttribute("listaDescuadre", detallesFinales); // Se lo adjuntamos a la vista
                request.setAttribute("modoHistorial", true); // Activamos una bandera para que el JSP sepa que es lectura vieja
                request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response); // Pintamos pantalla
                return; // Fin
            }

            InformeDAO dao = new InformeDAO(); // Si no es descuadre, vamos a matematicas analiticas, instanciamos DAO de informes

            // Extraemos los consolidados pidiendo ayuda a las subrutinas de la Base de datos
            double totalVentas = dao.obtenerTotalVentas(idInventario); // Cuanta plata entró por compras clientes
            double totalGastos = dao.obtenerTotalGastos(idInventario); // Cuanta plata se gastó en trapeadores
            double totalPedidos = dao.obtenerTotalPedidos(idInventario); // Cuanta plata se invirtió en comprar a Cervecería
            int cantidadVentas = dao.obtenerCantidadVentas(idInventario); // Cuántos tickets o recibos diferentes se originaron

            // 1. Cálculo de ganancia libre o Neta (Plata Bruta - Gastos Locales - Proveedores)
            double gananciaNeta = totalVentas - totalGastos - totalPedidos; 

            // 2. Cálculo para barras de porcentajes UI
            double maxReferencia = Math.max(totalVentas, Math.max(totalGastos, totalPedidos)); // Encuentra cuál es el valor más grande de los 3 
            int porcentajeVentas = 0, porcentajeGastos = 0, porcentajePedidos = 0; // Prepara variables en cero

            if (maxReferencia > 0) { // Matemática de regla de tres para obtener el porcentaje
                porcentajeVentas = (int) ((totalVentas / maxReferencia) * 100); 
                porcentajeGastos = (int) ((totalGastos / maxReferencia) * 100); 
                porcentajePedidos = (int) ((totalPedidos / maxReferencia) * 100); 
            }

            // =====================================================================
            // MANDAR TODAS ESTAS VARIABLES (ATRIBUTOS) A LA VISTA JSP PARA PINTAR
            // =====================================================================
            request.setAttribute("idInventario", idInventario); 
            request.setAttribute("totalVentas", totalVentas); 
            request.setAttribute("totalGastos", totalGastos); 
            request.setAttribute("totalPedidos", totalPedidos); 
            request.setAttribute("gananciaNeta", gananciaNeta); 
            request.setAttribute("cantidadVentas", cantidadVentas); 
            request.setAttribute("porcentajeVentas", porcentajeVentas); 
            request.setAttribute("porcentajeGastos", porcentajeGastos); 
            request.setAttribute("porcentajePedidos", porcentajePedidos); 

            // Se envía de forma silenciosa e interna para que pinte todo
            request.getRequestDispatcher("view/visualizar_informes.jsp").forward(request, response); 

        } catch (NumberFormatException e) { 
            // Si estalló intentando convertir un ID de letras raras en vez de números, devuelve error.
            response.sendRedirect("NegocioServlet?error=IdInventarioInvalido"); 
        }
    }
}
