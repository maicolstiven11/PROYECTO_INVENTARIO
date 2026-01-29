
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

@WebServlet("/GastoServlet")
public class GastoServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String descripcion = request.getParameter("descripcion");
        
        String fechaStr = request.getParameter("fecha");
        
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        
        double subtotal = Double.parseDouble(request.getParameter("subtotal"));
        
        Gasto g = new Gasto();
        g.setDescripcion(descripcion);
        
        g.setFecha(Date.valueOf(fechaStr));
        
        g.setCantidad(cantidad);
        
        g.setSubtotal(subtotal);
        
        HttpSession session = request.getSession();
        
        Integer idInventario = (Integer)
        session.getAttribute("idInventarioActual");
        
        g.setId_inventario(idInventario);
        
        GastoDao dao = new GastoDao();
        try{
            boolean ok = dao.registrarGasto(g);
            if (ok){
                response.sendRedirect("view/gasto_finalizado.html");
               
            }
            else{
                response.sendRedirect("view/agregar_gasto.html?error=1");
            }
        }catch (Exception e){
            e.printStackTrace();
            response.sendRedirect("view/agregar_gasto.html?error=1");
        }
               
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("listar".equals(action)) {
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual");

            if (idNegocio != null) {
                GastoDao dao = new GastoDao();
                List<Gasto> listaGastos = dao.listarGastos(idNegocio);
                request.setAttribute("listaGastos", listaGastos);
                request.getRequestDispatcher("view/visualizar_gastos.jsp").forward(request, response);
            } else {
                response.sendRedirect("index.jsp");
            }
        }
    }
    
}
