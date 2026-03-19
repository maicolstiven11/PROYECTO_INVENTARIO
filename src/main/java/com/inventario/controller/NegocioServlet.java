package com.inventario.controller;

import com.inventario.dao.NegocioDAO;
import com.inventario.model.Negocio;
import com.inventario.model.Usuario;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador NegocioServlet.
 * 
 * Clase que opera con las "sucursales" o bares. Lista los locales de un dueño, 
 * inserta los nuevos construidos y es capaz de demolerlos en DB si se lo piden.
 */
@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"}) 
public class NegocioServlet extends HttpServlet { 

    /**
     * El método doGet puede mostrar todos los negocios en tabla o borrar uno dependiendo 
     * lo que le pongamos al final (action).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Escucha instrucciones
        
        // =====================================================================
        // ACCIÓN: ELIMINAR (O DESTRUIR EL ESTABLECIMIENTO)
        // =====================================================================
        if ("eliminar".equals(action)) { // Si pidió romper
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id")); // Extrae qué Local especifico destruir
                NegocioDAO dao = new NegocioDAO(); 
                boolean eliminado = dao.eliminarNegocio(idNegocio); // Le pide al martillo DAO borrar en base SQL (Tabla NEGOCIO)
                
                // Si eliminó bien, redirige o repinta a sí mismo para reflejar visualmente que la fila no está.
                response.sendRedirect("NegocioServlet"); 
                return;  
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
        
        // =====================================================================
        // ACCIÓN POR DEFECTO: LISTAR Y NAVEGAR BAR POR BAR (MIS NEGOCIOS VIEW)
        // =====================================================================
        HttpSession session = request.getSession(); // Acceso al guardián local de variables del usuario
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Se obtiene a la persona conectada para preguntar el listado por dueño
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario(); // Saca ID abstracto 
        }
        
        NegocioDAO dao = new NegocioDAO(); 
        List<Negocio> lista = dao.listarNegocios(idUsuario); // Trae el conjunto de locales de "Su Propiedad" exclusivamente 
        
        request.setAttribute("listaBares", lista); // Pinta la tabla de modelos al frente JSP
        request.getRequestDispatcher("view/Lista_bares.jsp").forward(request, response); 
    }

    /**
     * El método doPost atrapa la apertura o inicio de la creación de Local (Cuando llena el Form "El Bar de Homero").
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        // Atrapa texto formulario
        String nombre = request.getParameter("nombre");       // Bar Moe
        String direccion = request.getParameter("direccion"); // Evergreen Terrace 2 
        
        HttpSession session = request.getSession(); // Llama variables activas
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); 
        
        int idUsuario = 0; 
        if (usuarioLogueado != null) { 
            idUsuario = usuarioLogueado.getIdUsuario(); // Extraer el "Dueño" (ID usuario)
        }
        
        // Armamos un pequeño molde temporal "Negocio" POJO en memoria.
        Negocio n = new Negocio(); 
        n.setNombre(nombre); 
        n.setDireccion(direccion); 
        
        NegocioDAO dao = new NegocioDAO(); 
        try {
            // Guardamos físicamente en Base de Datos (Nos retorna el ID Nro que DB haya auto-creado)
            int idGenerado = dao.registrarNegocio(n, idUsuario); 
            
            if (idGenerado > 0) { // Si sí hubo confirmación atómica
                try { // Calcula actualizar la estática del dashboard 
                    int cantBares = dao.contarNegocios(idUsuario);        
                    session.setAttribute("numBares", cantBares);          
                } catch(Exception e) { e.printStackTrace(); } 

                // Por último, como acaba de crear un lugar, automáticamente "viajamos" o empezamos el hilo operando localmente en él.
                session.setAttribute("idNegocioActual", idGenerado);     
                session.setAttribute("nombreNegocioActual", nombre);     
                response.sendRedirect("view/registroBar_fin.html");      // Manda a panel de inicio inventario verde .
            } else {
                response.sendRedirect("view/registroBar.html?error=FalloRegistroDAO"); 
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            // Fallo devolviendo string exception al front visual 
            response.sendRedirect("view/registroBar.html?error=" + e.getMessage().replace(" ", "_")); 
        }
    }
}
