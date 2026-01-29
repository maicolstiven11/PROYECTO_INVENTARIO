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

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. RECIBIR TODOS LOS DATOS
        String nombre = request.getParameter("nombre");
        String rol = request.getParameter("rol");
        String telefono1 = request.getParameter("telefono1");
        String email1 = request.getParameter("email1");
        String password = request.getParameter("password");
        
        // 2. PREPARAR EL OBJETO USUARIO
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setPassword(password);
        
        // 3. LLAMAR AL DAO PARA GUARDAR (Ahora devuelve el ID)
        UsuarioDAO dao = new UsuarioDAO();
        int idGenerado = dao.registrarUsuario(nuevoUsuario, email1, telefono1, rol);
        
        // 4. VERIFICAR SI SE REGISTRÓ EXITOSAMENTE
        if (idGenerado > 0) {
            // El DAO ya actualizó el objeto nuevoUsuario con el ID y rol
            
            // GUARDAR EN SESIÓN (igual que el Login)
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", nuevoUsuario);
            System.out.println("Servlet: Usuario registrado y guardado en sesión con ID: " + idGenerado);
            
            // Redirigir a Bienvenida (que lleva a registrar bar)
            response.sendRedirect("view/Bienvenida.jsp");
        } else {
            // Falló -> De vuelta al registro con error
            response.sendRedirect("view/registroUser.html?error=fallo_registro");
        }
    }
}
