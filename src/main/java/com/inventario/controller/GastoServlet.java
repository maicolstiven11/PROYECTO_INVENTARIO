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
 * Controlador GastoServlet.
 * 
 * Es el intermediario entre las pantallas Web donde el usuario anota sus gastos
 * y la base de datos (a través del GastoDao). Recibe la información, crea el objeto Gasto y lo manda a guardar.
 */
@WebServlet("/GastoServlet") // Esta etiqueta le dice al servidor que este archivo responde a la URL /GastoServlet
public class GastoServlet extends HttpServlet{ // Hereda de HttpServlet para poder recibir peticiones web
    
    /**
     * El método doPost se ejecuta cuando un formulario web nos envía datos ocultos (método POST).
     * Aquí atrapamos los datos del nuevo gasto, llenamos el "molde" Gasto y pedimos guardarlo.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Atrapar los datos que el usuario escribió en las cajas de texto de la página web
        String descripcion = request.getParameter("descripcion"); // La descripción del gasto (ej: Limpieza)
        String fechaStr = request.getParameter("fecha"); // La fecha escrita como texto
        int cantidad = Integer.parseInt(request.getParameter("cantidad")); // Convertimos la cantidad a número entero
        double subtotal = Double.parseDouble(request.getParameter("subtotal")); // Convertimos el costo a número decimal
        
        // 2. Crear nuestro objeto u "entidad" Gasto para empaquetarle los valores
        Gasto g = new Gasto(); // Generamos un gasto vacío en memoria
        g.setDescripcion(descripcion); // Le guardamos la descripción
        g.setFecha(Date.valueOf(fechaStr)); // Convertimos el texto a una Fecha SQL y se la guardamos
        g.setCantidad(cantidad); // Guardamos la cantidad
        g.setSubtotal(subtotal); // Guardamos el costo total del gasto
        
        // 3. Averiguar en qué inventario estamos trabajando usando la "Sesión" del usuario
        HttpSession session = request.getSession(); // Accedemos a la memoria de la sesión actual
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Rescatamos el ID del inventario activo
        g.setId_inventario(idInventario); // Se lo asignamos al gasto para saber a qué mes/periodo pertenece
        
        // 4. Mandar a guardar este objeto Gasto a la base de datos usando el GastoDao
        GastoDao dao = new GastoDao(); // Instanciamos la clase que hace el trabajo sucio en la BD
        try{
            boolean ok = dao.registrarGasto(g); // Le entregamos el objeto lleno; nos dirá True si guardó bien
            if (ok){ 
                // Si guardó exitosamente, redirigimos al usuario a una página de éxito
                response.sendRedirect("view/gasto_finalizado.html"); 
            }
            else{
                // Si algo falló en BD, lo devolvemos al formulario con un mensaje de error
                response.sendRedirect("view/agregar_gasto.html?error=1"); 
            }
        }catch (Exception e){ // Si ocurre algún error catastrófico (ej: se cae la base de datos)
            e.printStackTrace(); // Imprimir el error en consola para los programadores
            response.sendRedirect("view/agregar_gasto.html?error=1"); // Devolver al usuario al form
        }
               
    }

    /**
     * El método doGet atiende peticiones directas de URL o Enlaces (método GET).
     * Lo usamos para listar o mostrar los gastos que se han registrado en el negocio.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Consultamos qué acción quiere hacer el usuario (ej: ?action=listar)
        String action = request.getParameter("action"); 
        HttpSession session = request.getSession(); // Buscamos la memoria de la sesión

        if ("listar".equals(action)) { // Si nos pidió la lista de gastos...
            // Rescatamos de su inicio de sesión en qué negocio está parado
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); 

            if (idNegocio != null) { // Si realmente hay un negocio activo
                GastoDao dao = new GastoDao(); // Creamos nuestro intermediario de Base de datos
                List<Gasto> listaGastos = dao.listarGastos(idNegocio); // Le pedimos la colección o lista de gastos
                
                // Anclamos (Atribute) esa lista a la petición para que la página JSP la pueda dibujar
                request.setAttribute("listaGastos", listaGastos); 
                
                // Redirigimos internamente el tráfico a la vista (JSP) encargada de mostrar la tabla
                request.getRequestDispatcher("view/visualizar_gastos.jsp").forward(request, response); 
            } else {
                // Si por alguna razón perdió su sesión o no tiene negocio, lo botamos al login
                response.sendRedirect("index.jsp");  
            }
        }
    }
}
