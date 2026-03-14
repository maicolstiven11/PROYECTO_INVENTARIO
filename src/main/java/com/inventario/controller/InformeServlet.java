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
 * CONTROLADOR: Servlet encargado de generar los datos para la página de Informes.
 * Calcula totales de ventas, gastos, pedidos y la ganancia neta.
 */
@WebServlet(name = "InformeServlet", urlPatterns = {"/InformeServlet"})
public class InformeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String idNegocioStr = request.getParameter("idNegocio");
        String idInventarioStr = request.getParameter("idInventario");

        // Si no hay idNegocio en parámetros, tratar de sacarlo de la sesión
        Integer idNegocio = (idNegocioStr != null) ? Integer.parseInt(idNegocioStr) : (Integer) session.getAttribute("idNegocioActual");

        if (idNegocio == null) {
            response.sendRedirect("NegocioServlet");
            return;
        }

        InventarioDAO invDao = new InventarioDAO();

        // CASO 1: LISTAR HISTORIAL DE INVENTARIOS DEL NEGOCIO
        if (idInventarioStr == null || idInventarioStr.isEmpty()) {
            List<com.inventario.model.Inventario> listaInvs = invDao.listarInventariosPorNegocio(idNegocio);
            request.setAttribute("listaInventarios", listaInvs);
            request.setAttribute("nombreNegocio", session.getAttribute("nombreNegocioActual"));
            request.getRequestDispatcher("view/lista_informes.jsp").forward(request, response);
            return;
        }

        // CASO 2: MOSTRAR INFORME DETALLADO O DESCUADRE DE UN INVENTARIO ESPECÍFICO
        try {
            int idInventario = Integer.parseInt(idInventarioStr);
            
            // Si la acción es "ver_descuadre", mostramos el reporte de descuadre.
            String action = request.getParameter("action");
            if ("ver_descuadre".equals(action)) {
                com.inventario.dao.DetalleInventarioDAO detalleDao = new com.inventario.dao.DetalleInventarioDAO();
                java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario);
                request.setAttribute("listaDescuadre", detallesFinales);
                request.setAttribute("modoHistorial", true); // Para mostrar un botón de volver distinto
                request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response);
                return;
            }

            InformeDAO dao = new InformeDAO();

            // Obtener totales específicos de este periodo de inventario
            double totalVentas = dao.obtenerTotalVentas(idInventario);
            double totalGastos = dao.obtenerTotalGastos(idInventario);
            double totalPedidos = dao.obtenerTotalPedidos(idInventario);
            int cantidadVentas = dao.obtenerCantidadVentas(idInventario);

            // Calcular ganancia neta
            double gananciaNeta = totalVentas - totalGastos - totalPedidos;

            // Calcular porcentajes
            double maxReferencia = Math.max(totalVentas, Math.max(totalGastos, totalPedidos));
            int porcentajeVentas = 0, porcentajeGastos = 0, porcentajePedidos = 0;

            if (maxReferencia > 0) {
                porcentajeVentas = (int) ((totalVentas / maxReferencia) * 100);
                porcentajeGastos = (int) ((totalGastos / maxReferencia) * 100);
                porcentajePedidos = (int) ((totalPedidos / maxReferencia) * 100);
            }

            // Pasar datos al JSP
            request.setAttribute("idInventario", idInventario);
            request.setAttribute("totalVentas", totalVentas);
            request.setAttribute("totalGastos", totalGastos);
            request.setAttribute("totalPedidos", totalPedidos);
            request.setAttribute("gananciaNeta", gananciaNeta);
            request.setAttribute("cantidadVentas", cantidadVentas);
            request.setAttribute("porcentajeVentas", porcentajeVentas);
            request.setAttribute("porcentajeGastos", porcentajeGastos);
            request.setAttribute("porcentajePedidos", porcentajePedidos);

            request.getRequestDispatcher("view/visualizar_informes.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("NegocioServlet?error=IdInventarioInvalido");
        }
    }
}
