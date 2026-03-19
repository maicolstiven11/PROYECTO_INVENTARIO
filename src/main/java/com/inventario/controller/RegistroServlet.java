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
 * Controlador RegistroServlet.
 * 
 * Se dedica exclusivamente al registro o creación inicial de Administradores
 * y Cajeros a nuestra gran base comercial.
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"}) 
public class RegistroServlet extends HttpServlet { 

    /**
     * El método doPost se dispara después de llenar todos tus campos. 
     * Arma un nuevo Usuario, comprueba que el correo no persista ya y salva a BD.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        // =====================================================================
        // ATRAPAR ESCRITURAS DE USUARIO
        // =====================================================================
        String nombre = request.getParameter("nombre");       // Juan Perez
        String rol = request.getParameter("rol");             // ADMIN, TRABAJADOR
        String telefono1 = request.getParameter("telefono1"); // Teléfono personal
        String email1 = request.getParameter("email1");       // Base arroba
        String password = request.getParameter("password");   // Contrasenia a resguardar
        
        // =====================================================================
        // CREACIÓN DE ESQUELETO O ENTIDAD VIRTUAL DE LA PERSONA (Obj POJO)
        // =====================================================================
        Usuario nuevoUsuario = new Usuario();        
        nuevoUsuario.setNombre(nombre);              // Rellenando su nombre
        nuevoUsuario.setPassword(password);          // Rellenando su contraseña
        
        // =====================================================================
        // CONECTAR CON DATABASE Y VALIDAR INTRUSOS O CORREOS COPIADOS
        // =====================================================================
        UsuarioDAO dao = new UsuarioDAO(); 

        // Si ya hay alguien registrado con "jperez@gmail.com" lo rechaza desde el inicio (Unique Constraints)
        if (dao.existeCorreo(email1)) { 
            response.sendRedirect("view/registroUser2.html?error=correo_duplicado&nombre=" + nombre + "&rol=" + rol); 
            return; // Detiene o cancela todo su guardado de BD, retornando al inicio
        }

        // Si pasó y estaba fresco el correo, dispara toda la cascada creadora en tres tablas (Registro + Correo + Telefono)
        int idGenerado = dao.registrarUsuario(nuevoUsuario, email1, telefono1, rol); 
        
        // =====================================================================
        // ANÁLISIS DEL PUESTO AL QUE FUE DESIGNADO Y ENVÍOS
        // =====================================================================
        if (idGenerado > 0) { // Si sí le dio un número de cédula válido BD...
            
            // Creamos su sesión general temporal inmediata.
            HttpSession session = request.getSession(); 
            // Esto lo deja "logueado" de forma permanente sin que deba digitar otra vez la contra
            session.setAttribute("usuarioLogueado", nuevoUsuario);  
            System.out.println("Servlet: Usuario registrado y guardado en sesión con ID: " + idGenerado); 
            
            // Ramificación UI (Bifurcación para Cajero vs Administrador)
            if ("TRABAJADOR".equalsIgnoreCase(rol)) { 
                response.sendRedirect("view/Menu_sistema.jsp"); // Si es un cajero esclavo, entra a su menu aburrido.
            } else {
                response.sendRedirect("view/Bienvenida.jsp"); // Si es Admin creador, ve panel de Bienvenida crear bares.
            }
        } else {
            // SI LA BD EXTRUSTRUYÓ MAL ALGO EN UNA INSERCIÓN... (Error Genérico) 
            response.sendRedirect("view/registroUser.html?error=fallo_registro"); 
        }
    }
}
