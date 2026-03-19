package com.inventario.controller; 

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
 * Controlador ProveedorServlet.
 * 
 * Es una libreta de contactos virtual de nuestros vendedores de insumos.
 * Permite listarlos, agregar sus números o borrarlos de nuestro sistema comercial.
 */
@WebServlet(name = "ProveedorServlet", urlPatterns = {"/ProveedorServlet"}) 
public class ProveedorServlet extends HttpServlet { 

    /**
     * El doGet permite mostrar todo el repertorio de Vendedores
     * e intercepta también cuándo apretamos destruir o borrarlos
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        ProveedorDAO dao = new ProveedorDAO(); // Orquesta Base datos Proveedores
        String action = request.getParameter("action"); // Lee botón 
        
        // =====================================================================
        // ELIMINAR DE BASE PROVEEDORES (Cuidado Relacional)
        // =====================================================================
        if (action != null && action.equalsIgnoreCase("eliminar")) { 
            int id = Integer.parseInt(request.getParameter("id")); // Agarra ID en cuestión
            boolean eliminado = dao.eliminarProveedor(id);         // Ordena aniquilación
            
            if (!eliminado) {  // Si BD le negó por seguridad...
                // Si alguien tiene "Pedidos" activos asocidados (LLave foránea Restrictiva FK), SQL aborta automáticamente
                // Así que evitamos caída y le decimos por qué no lo borra: 
                request.setAttribute("errorEliminar", "No se puede eliminar porque tiene pedidos activos asociados."); 
            }
        }
        
        // =====================================================================
        // LISTAR DE PAQUETE (Siempre que estemos visualizandolos)
        // =====================================================================
        List<Proveedor> lista = dao.listarProveedores();           // Array Dinámico Modelado iterativo a todos los registros     
        
        request.setAttribute("listaProveedores", lista);           // Lo pega al fondo request 
        request.getRequestDispatcher("view/lista_proveedores.jsp").forward(request, response); // Despacha en vista gráfica  
    }

    /**
     * El doPost guarda al señor en su base de datos como Proveedor para pedirle cerveza o alimentos
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        // Atrapa texto plano formularios de contacto 
        String nombre = request.getParameter("nombre_proveedor"); // Comercializadora Bavaria.
        String contacto = request.getParameter("contacto");       // Don Ramón.
        String telefono = request.getParameter("telefono");       // 31238910 ...
        String correo = request.getParameter("correo");           // donramon@correo.com
        
        // EMPAQUETA NUEVO VENDEDOR
        Proveedor p = new Proveedor(); // Objeto POJO de molde individual 
        p.setNombreProveedor(nombre); // Método mutador  
        p.setContacto(contacto); // ..
        p.setTelefono(telefono); // ..
        p.setCorreo(correo);     // Setter .. 
        
        // SOLICITA BD
        ProveedorDAO dao = new ProveedorDAO(); 
        
        // Impide dos correos iguales o mismo nombre comercial exacto!
        if (dao.existeProveedor(nombre, correo)) { 
            response.sendRedirect("view/Registro_datos_prv.html?error=proveedor_duplicado"); // Rebotado UI 
            return; // Corta flujo 
        }

        try { 
            boolean exito = dao.registrarProveedor(p);  // Inyecta en la Matriz el paquete proveedor 
            if (exito) { // Control afirmativo
                response.sendRedirect("view/Proveedor_registrado.html");  // Finaliza con UI Bonita
            } else { 
                response.sendRedirect("view/Registro_datos_prv.html?error=FalloRegistro"); // Rebotado sin error JVM pero sin efecto SQL 
            }
        } catch (Exception e) { // Suciedad framework error runtime Exception   
            response.sendRedirect("view/Registro_datos_prv.html?error=" + e.getMessage().replace(" ", "_"));  
        }
    }
}
