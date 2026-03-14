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
 * CONTROLADOR: Servlet encargado del proceso de Registro de Usuarios.
 * 
 * Implementa: RF-01 (Registrar Usuario)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - crea sesión automáticamente tras registro exitoso)
 *         RNF-08 (Mensajes de Error - redirige con ?error=fallo_registro si falla)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 * 
 * Recibe los datos del formulario registroUser2.html (nombre, rol, teléfono, email, password)
 * y delega al DAO para insertar en las tablas USUARIO, CORREO_USUARIO y TELEFONO_USUARIO.
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
// RF-01: La URL "/RegistroServlet" es a donde apunta el form de registroUser2.html (action="../RegistroServlet")
public class RegistroServlet extends HttpServlet {

    /**
     * RF-01: Método doPost - Se ejecuta cuando el usuario envía el formulario de registro (method="POST").
     * Recibe TODOS los datos juntos: los de la página 1 (nombre, rol) vienen como campos ocultos (hidden),
     * y los de la página 2 (teléfono, email, password) vienen de los inputs visibles.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // =====================================================================
        // RF-01 PASO 1: RECIBIR TODOS LOS DATOS DEL FORMULARIO HTML
        // Cada getParameter busca un input con ese name="" en el HTML.
        // RF-30: Los campos obligatorios tienen "required" en el HTML (validación cliente).
        // =====================================================================
        String nombre = request.getParameter("nombre");       // RF-01: Nombre del usuario (viene como campo hidden desde pág 1)
        String rol = request.getParameter("rol");             // RF-01: Rol seleccionado: "ADMIN" o "TRABAJADOR" (viene como campo hidden desde pág 1)
        String telefono1 = request.getParameter("telefono1"); // RF-01: Teléfono (campo opcional según RF-01)
        String email1 = request.getParameter("email1");       // RF-01, RF-31, RF-32: Correo electrónico (obligatorio, único)
        String password = request.getParameter("password");   // RF-01: Contraseña (obligatoria, mín 6 caracteres según RF-01)
        
        // =====================================================================
        // RF-01 PASO 2: CREAR EL OBJETO MODELO (Usuario)
        // Creamos una instancia vacía de la clase Usuario y la llenamos con los datos recibidos.
        // RNF-13: Usamos el Modelo como transportador de datos entre capas.
        // =====================================================================
        Usuario nuevoUsuario = new Usuario();        // RF-01: Crea un objeto Usuario vacío (constructor sin parámetros)
        nuevoUsuario.setNombre(nombre);              // RF-01: Asigna el nombre al objeto
        nuevoUsuario.setPassword(password);          // RF-01: Asigna la contraseña al objeto. RNF-01: PENDIENTE - Debería cifrarse antes de guardar.
        
        // =====================================================================
        // RF-01 PASO 3: LLAMAR AL DAO PARA GUARDAR EN LA BASE DE DATOS
        // =====================================================================
        UsuarioDAO dao = new UsuarioDAO();

        // VALIDACIÓN DE UNICIDAD: Verificar si el correo ya existe
        if (dao.existeCorreo(email1)) {
            // Redirigir de vuelta al paso 2 con los datos originales para no perder el primer paso
            response.sendRedirect("view/registroUser2.html?error=correo_duplicado&nombre=" + nombre + "&rol=" + rol);
            return; // Detener el flujo
        }

        int idGenerado = dao.registrarUsuario(nuevoUsuario, email1, telefono1, rol); // RF-01: Inserta usuario en BD. Retorna ID generado o -1 si falló.
        
        // =====================================================================
        // RF-01 PASO 4: VERIFICAR RESULTADO Y RESPONDER
        // =====================================================================
        if (idGenerado > 0) {
            // RF-01: REGISTRO EXITOSO - La BD generó un ID para el nuevo usuario
            // El DAO ya actualizó el objeto nuevoUsuario con su ID y rol internamente
            
            // RNF-03: Crear sesión HTTP automáticamente (el usuario queda logueado después de registrarse)
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", nuevoUsuario);  // RF-01: Guarda al nuevo usuario en sesión
            System.out.println("Servlet: Usuario registrado y guardado en sesión con ID: " + idGenerado);
            
            // RF-01: Redirigir según el rol
            if ("TRABAJADOR".equalsIgnoreCase(rol)) {
                // Al trabajador se le asignan bares, no los crea. Redirigimos a su menú principal.
                response.sendRedirect("view/Menu_sistema.jsp");
            } else {
                // El admin sí va a Bienvenida para crear su primer bar
                response.sendRedirect("view/Bienvenida.jsp");
            }
        } else {
            // RF-01: REGISTRO FALLIDO - Algo salió mal en la BD (correo duplicado, error de conexión, etc.)
            // RNF-08: Redirigir de vuelta al formulario de registro con parámetro de error
            response.sendRedirect("view/registroUser.html?error=fallo_registro");
        }
    }
}
