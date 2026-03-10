
package com.inventario.controller;

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
 * CONTROLADOR: Servlet encargado de gestionar los Gastos operativos.
 * 
 * Implementa: RF-26 (Registrar Gasto), RF-27 (Listar Gastos)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - obtiene idInventarioActual de la sesión)
 *         RNF-08 (Mensajes de Error - redirige con ?error=1 si falla el registro)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet("/GastoServlet")
public class GastoServlet extends HttpServlet{
    /**
     * RF-26: Método doPost - Registra un nuevo gasto operativo.
     * Recibe los datos del formulario agregar_gasto.html y los guarda en la BD
     * vinculándolos al inventario activo.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // RF-26 PASO 1: Recibir datos del formulario HTML
        // RF-30: Campos obligatorios validados con "required" en el HTML
        String descripcion = request.getParameter("descripcion");   // RF-26: Descripción del gasto (obligatorio)
        
        String fechaStr = request.getParameter("fecha");            // RF-26: Fecha del gasto (obligatorio, formato YYYY-MM-DD)
        
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));      // RF-26: Cantidad (obligatorio, entero > 0)
        
        double subtotal = Double.parseDouble(request.getParameter("subtotal")); // RF-26: Subtotal (obligatorio, numérico > 0)
        
        // RF-26 PASO 2: Crear objeto Modelo (Gasto) y llenarlo con los datos
        Gasto g = new Gasto();
        g.setDescripcion(descripcion);
        g.setFecha(Date.valueOf(fechaStr));
        g.setCantidad(cantidad);
        g.setSubtotal(subtotal);
        
        // RESTAURADO: Obtener el inventario actual de la sesión (antes era negocio)
        HttpSession session = request.getSession();
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual");
        g.setId_inventario(idInventario); // RESTAURADO: Vincular gasto con el inventario
        
        // RF-26 PASO 3: Llamar al DAO para guardar en la BD
        GastoDao dao = new GastoDao();
        try{
            boolean ok = dao.registrarGasto(g);      // RF-26: Inserta el gasto en la tabla GASTO
            if (ok){
                response.sendRedirect("view/gasto_finalizado.html");  // RF-26: Redirigir a confirmación
               
            }
            else{
                // RNF-08: Redirigir con error
                response.sendRedirect("view/agregar_gasto.html?error=1");
            }
        }catch (Exception e){
            e.printStackTrace();
            // RNF-08: Redirigir con error
            response.sendRedirect("view/agregar_gasto.html?error=1");
        }
               
    }

    /**
     * RF-27: Método doGet - Lista el historial de gastos del negocio actual.
     * RF-27 Restricción 1: Filtra los gastos por el idNegocioActual almacenado en sesión.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("listar".equals(action)) {
            // RF-27: Listar gastos del negocio actual
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // RF-27 Restricción 1: Filtrar por negocio

            if (idNegocio != null) {
                GastoDao dao = new GastoDao();
                List<Gasto> listaGastos = dao.listarGastos(idNegocio);      // RF-27: Consulta gastos de este negocio
                request.setAttribute("listaGastos", listaGastos);           // Pasa la lista al JSP
                request.getRequestDispatcher("view/visualizar_gastos.jsp").forward(request, response); // RF-27: Muestra la vista
            } else {
                response.sendRedirect("index.jsp");  // Sesión perdida
            }
        }
    }
    
}
