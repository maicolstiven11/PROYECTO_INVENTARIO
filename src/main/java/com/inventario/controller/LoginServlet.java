package com.inventario.controller;

import com.inventario.dao.UsuarioDAO;
import com.inventario.model.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador LoginServlet.
 * 
 * Es el portero o guardián de toda la web. Valida las contraseñas,
 * crea la mochila general de sesión para cada persona y permite desconectar usuarios.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet { 

    /**
     * El método doPost es el que atrapa el intento del individuo 
     * tratando de meter su correo y password.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        // 1. Extraer los datos brutos del texto ingresado 
        String email = request.getParameter("email");       // Correo escrito
        String password = request.getParameter("password"); // Contraseña digitada (Llegará como texto normal como "1234")
        
        // 2. Pedirle al Gestor UsuarioDAO que cruce la información
        UsuarioDAO dao = new UsuarioDAO();                       
        Usuario usuario = dao.validarLogin(email, password); // Evaluar... Esto cifrará la password a SHA256 e irá a la tabla 
        
        // 3. Resultado
        if (usuario != null) { // Si devolvió al individuo empacado en un objeto Usuario (Es decir que sus datos eran verídicos)
            
            // CREACIÓN DE SESIÓN EN VIVO
            HttpSession session = request.getSession(); // Le armamos una pequeña memoria privada entre la web y nuestra arquitectura
            session.setAttribute("usuarioLogueado", usuario); // Pegamos SU PERFIL dentro de ella como una billetera 
            
            // =====================================================================
            // CARGA E INFORMACIÓN PREVENTIVA (Pequeños metadatos usados en pantallas de bienvenida Admins)
            // =====================================================================
            try { 
                com.inventario.dao.NegocioDAO negocioDao = new com.inventario.dao.NegocioDAO(); 
                int cantBares = negocioDao.contarNegocios(usuario.getIdUsuario()); // Calcula cuantas tiendas tiene el admin
                
                UsuarioDAO usuarioDao = new UsuarioDAO(); 
                int cantTrabajadores = usuarioDao.contarTrabajadores(); // Suma el recuento global
                
                session.setAttribute("numBares", cantBares); // Guarda estas cifras estáticas en los atributos         
                session.setAttribute("numTrabajadores", cantTrabajadores);         
                
            } catch(Exception e) { 
                System.out.println("Error cargando estadísticas en login: " + e.getMessage()); 
            }

            // =====================================================================
            // CONDICIONAL: SI EN VEZ DE ADMIN (1), ES UN CAJERO (2)
            // =====================================================================
            if (usuario.getIdRol() == 2) { 
                try { 
                    com.inventario.model.Negocio negAsignado = dao.obtenerNegocioAsignado(usuario.getIdUsuario()); // Vamos asimilando para qué locación labora
                    if (negAsignado != null) { 
                        session.setAttribute("idNegocioActual", negAsignado.getIdNegocio()); // Le forzamos su tienda local
                        session.setAttribute("nombreNegocioActual", negAsignado.getNombre()); 
                        
                        // De una y automáticamente, también localizamos cuál es el periodo o Inventario vivo que tiene el bar ese mes activado 
                        com.inventario.dao.InventarioDAO invDao = new com.inventario.dao.InventarioDAO(); 
                        com.inventario.model.Inventario invActivo = invDao.obtenerInventarioActivo(negAsignado.getIdNegocio()); 
                        if (invActivo != null) { 
                            session.setAttribute("idInventarioActual", invActivo.getIdInventario()); // Le atamos a ese inventario general 
                        }
                    }
                } catch (Exception e) { 
                    System.out.println("Error cargando negocio del trabajador: " + e.getMessage());  
                }
            }

            // Manda todo contento al Panel Integrador
            response.sendRedirect("view/Menu_sistema.jsp"); 
            
        } else {
            // INSTANCIA FALLIDA O HACKER. Si "usuario" es Nulo, osea que el cruce fue erróneo
            response.sendRedirect("view/Inicio_sesion.html?error=1"); // Echamos a la cara un mensaje que vuelva a revisar letras 
        }
    }

    /**
     * El método doGet atiende las URL simples directas. En este caso se usa para
     * destruir el sistema y generar el "DESCONECTARSE".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 

        String action = request.getParameter("action"); // Lee qué quisimos 

        if ("logout".equals(action)) { // Si le dimos al botón Cerrar Sesión
            HttpSession session = request.getSession(false); // Pedimos al navegador su envoltorio
            if (session != null) { // Si hay envoltorio de memoria en este hilo local
                session.invalidate(); // DESTRUCTURADOR: Cierra, rompe y vacía todo en la sesión, forzándolo a la nada abstracta
            }
            response.sendRedirect("view/Inicio_sesion.html"); // Volvemos sin llaves al Login UI
        } else { 
            response.sendRedirect("view/Inicio_sesion.html"); 
        }
    }
}
