package com.inventario.controller;

import com.inventario.dao.NegocioDAO;
import com.inventario.dao.UsuarioDAO;
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
 * Controlador TrabajadorServlet.
 * 
 * Es el módulo de Recursos Humanos del dueño. Le permite al Administrador 
 * ver sus esclavos/cajeros, botarlos, reasignarles un bar distinto o cambiarles claves si las olvidan.
 */
@WebServlet(name = "TrabajadorServlet", urlPatterns = {"/TrabajadorServlet"}) 
public class TrabajadorServlet extends HttpServlet { 

    /**
     * El doGet en este controlador solo sirve para "Listar" a tus trabajadores 
     * en pantalla, cruzando también qué locales tienes para las etiquetas del HTML
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Lee "action" por si es algo en especial
        if (action == null) action = "listar"; // Si entró desnudo a la URL, asume que quería listar

        if ("listar".equals(action)) { 
            // =====================================================================
            // TRAER LAS 2 LISTAS PARA EMPAREJAR (Bares y Hombres)
            // =====================================================================
            UsuarioDAO usuarioDAO = new UsuarioDAO(); 
            List<Usuario> listaTrabajadores = usuarioDAO.listarTrabajadores(); // Tráeme a todos mis esclavos Cajeros disponibles a un arreglo array
            
            // Averiguo quién soy yo (El admin dueño) para traer MIS BARES PROPIOS, no los del mundo.
            HttpSession session = request.getSession(); 
            Usuario admin = (Usuario) session.getAttribute("usuarioLogueado"); 

            if (admin != null) { // Por si no ha caducado la sesión
                NegocioDAO negocioDAO = new NegocioDAO(); 
                List<Negocio> listaNegocios = negocioDAO.listarNegocios(admin.getIdUsuario()); // Tráeme Mis Bares "Bar de Juan" etc...
                
                // Pegar ambos Arreglos o Colecciones directamente a la pantalla gráfica (JSP)
                request.setAttribute("listaTrabajadores", listaTrabajadores); 
                request.setAttribute("listaNegocios", listaNegocios); 
                request.getRequestDispatcher("view/gestion_trabajadores.jsp").forward(request, response); // Desata la pantalla .jsp
            } else { 
                response.sendRedirect("view/Inicio_sesion.html"); // ¡Expiró login!
            }
        }
    }

    /**
     * El doPost maneja los diferentes Botones con acciones de choque y castigo como:
     * Asignar local, desasignarlo, despedirlo del sistema o castigarle su clave temporal
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Leemos la clave del botón rojo
        
        // =====================================================================
        // ACCIÓN 1: PONERLO A TRABAJAR EN UN BAR ESPECÍFICO
        // =====================================================================
        if ("asignar".equals(action)) { 
            try { 
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // ID Empleado Jose
                int idNegocio = Integer.parseInt(request.getParameter("id_negocio")); // Bar de Moe ID
                
                UsuarioDAO dao = new UsuarioDAO(); 
                
                // Primero miramos si el Bar de Moe ya tiene un cajero trabajando, no permitimos dos juntos.
                if (dao.negocioTieneTrabajador(idNegocio)) { 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=bar_ocupado"); // Chocó, abortar con error
                    return; 
                }
                
                boolean exito = dao.asignarNegocio(idUsuario, idNegocio); // Sentencia SQL Inyectando Update
                
                if (exito) { 
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=asignado"); // Re-lista limpiamente
                } else {  
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_asignar"); // Error genérico SQL false
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); // Catch Exception Error cast number Formats primitives.
            }
            
        // =====================================================================
        // ACCIÓN 2: DEJARLO SIN BAR O SIN EMPLEO TEMPORALMENTE
        // =====================================================================
        } else if ("desasignar".equals(action)) { 
            try { 
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // Target ID Jose .
                UsuarioDAO dao = new UsuarioDAO(); 
                boolean exito = dao.desasignarNegocio(idUsuario); // Le rompe su relación a Nulo "NULL" en bd a sus FK's .
                
                if (exito) {  
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=desasignado"); // Listo, devuelto pantalla normal OK
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_desasignar"); // Error null limit 
                }
            } catch (Exception e) { 
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
            
        // =====================================================================
        // ACCIÓN 3: ELIMINARLO DEL PROGRAMA PARA SIEMPRE (DESTERRARLO)
        // =====================================================================
        } else if ("eliminar".equals(action)) { 
            try { 
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // Target Obj User parameter .
                UsuarioDAO dao = new UsuarioDAO(); 
                boolean exito = dao.eliminarTrabajador(idUsuario); // Destructor booleano transaccional Delete CASCADE.
                
                if (exito) { // Confirmación OK 
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=eliminado"); 
                } else { 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_eliminar"); 
                }
            } catch (Exception e) { 
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); 
            }
            
        // =====================================================================
        // ACCIÓN 4: RESETEAR CONTRASEÑA EN CASO DE EMERGENCIA QUE OLVIDÓ
        // =====================================================================
        } else if ("resetPassword".equals(action)) { 
            try { 
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // ID obj José
                String nuevaPassword = request.getParameter("nueva_password"); // La 1234
                String confirmarPassword = request.getParameter("confirmar_password"); // De nuevo a 1234 para asegurar 
                
                // Chequeo pre-bd
                if (nuevaPassword == null || nuevaPassword.length() < 6) { 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_corta"); // Exige más texto largo.
                    return; 
                }
                if (!nuevaPassword.equals(confirmarPassword)) { 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_no_coincide"); // No coinciden cajas texto format validation constraint  
                    return; 
                }
                
                // Manda hash final para reemplazar antigua
                UsuarioDAO dao = new UsuarioDAO(); 
                boolean exito = dao.actualizarPassword(idUsuario, nuevaPassword); // Envía flag transaccional bool .
                
                if (exito) {  
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=password_reseteada"); // Se devolvió correcto con verde log 
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_reset"); 
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); 
            }
        }
    }
}
