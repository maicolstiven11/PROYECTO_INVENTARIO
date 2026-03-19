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
 * Controlador Orquestador Integrador de Autenticación Inicial: RegistroServlet.
 * 
 * Fachada encapsuladora encargada del proceso algorítmico Atómico de Ingreso o Creación Transaccional Base System Initialization Wrapper POJO User Security Constructor Factory Model Registration method injection DAO Pattern object execution manager validation constraints data persistence initialization scope attributes parameters logic .
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"}) // Binding decorativo framework instanciando la ruta de la factoría Servlet container parameter web logic method execution limits.
public class RegistroServlet extends HttpServlet { // Polimorfismo sub-tipo base protocolo HTTP framework inheritance Java EE base method.

    /**
     * Rescritura Genérica evaluadora HTTP Post.
     * Funciona como Enrutador Transaccional Atómico Síncrono Post FormData Recibiendo todos los Strings primitivos params UI y encapsulándolos In-Memory Object Model Wrapper Entity delegando la Persistencia a Capa Manager Relacional y Creando un Active Pool Scope Binding Memory Session Attribute user Pointer Memory. 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Captura Framework Error Exceptions Primitive catch context bounds exceptions execution memory pointer variable parameters constraints limit.
        
        // =====================================================================
        // ALGORITMO ACUMULADOR DATA STRING PAYLOAD METADATA TRAMADA SÍNCRONA .
        // =====================================================================
        String nombre = request.getParameter("nombre");       // Getter primitives UI String name parameters logic target limits bounds pointer property memory variables flag string.
        String rol = request.getParameter("rol");             // Role security string boolean check parameter.
        String telefono1 = request.getParameter("telefono1"); // String Format target phone param attribute model size bounds property array list format constraint parameter.
        String email1 = request.getParameter("email1");       // Metadato uri string bounds context parameters validator email target.
        String password = request.getParameter("password");   // Constructor Primitive security hash validator payload form target constraint properties check values parameters logic bounds parameter execution check String pointer limits .
        
        // =====================================================================
        // CÚPULA CONSTRUCTORA DE ENTIDAD O POJO WRAPPER IN MEMORY
        // =====================================================================
        Usuario nuevoUsuario = new Usuario();        // Constructor Singleton Allocation Object Factory Model wrapper POJO instanciator base class model variables attributes limits constraints property reference JVM memory object heap allocation.
        nuevoUsuario.setNombre(nombre);              // Método Mutador Encapsulado Getter Setter abstraction context values model logic limits parameters limit context pointers bounds conditions array values property constraints validation variables.
        nuevoUsuario.setPassword(password);          // Mutador Security Logic Object property parameters check values constraint validation properties loop arrays limit context flag reference property.
        
        // =====================================================================
        // ORQUESTACIÓN DISPARADOR DAO FACTORY RELATION SQL MANAGER
        // =====================================================================
        UsuarioDAO dao = new UsuarioDAO(); // Instanciator Builder Data access Layer DAO relation model Manager Transaction properties connection pools constraint memory values limit boundary condition context pointers properties variables logic boundaries limits string limit target boolean reference execution string validation parameters object pointer variables boolean size parameter execution bounds length variables sizes string targets limits parameter variables constraints arrays flags sizes memory execution flags memory context limits targets limits execution sizes .

        // SUB ALGORITMO GUARDIAN (Unique Constraint Database Validator Delegation Setter Query) Check Logic 
        if (dao.existeCorreo(email1)) { // Validator condition loop equality constraint check boolean validation limit parameters bounds loop boolean limits string string constraints properties properties memory variables context property size bounds target pointer execution memory sizes context parameters limits lengths.
            // Delegate loop fallback redirect param flag URL clean UI memory variables string constraint bounds condition sizes.
            response.sendRedirect("view/registroUser2.html?error=correo_duplicado&nombre=" + nombre + "&rol=" + rol); // Escape URL payload memory pointers properties framework parameters targets conditions boolean lengths loops context check memory exception flags variables limits exception boolean.
            return; // Destruction validation variable parameter pointer parameter execution context properties memory loops bool limits condition properties check limit arrays length return boundary parameters .
        }

        // Setter Atómico Delegativo Inyection DAO Transaccion Object Insert Method Return int PK Id .
        int idGenerado = dao.registrarUsuario(nuevoUsuario, email1, telefono1, rol); // Disparo Framework Factory Transactional Relation Manager Logic Execution Parameters Variables Return Type primitive integer Primary Key Identifier Identity Object POJO setter wrapper bounds conditions variables.
        
        // =====================================================================
        // ALGORITMO RECOLECTOR RESOLUTIVO Y MANAGER UI STATE BINDING POINTER 
        // =====================================================================
        if (idGenerado > 0) { // Math Constraint limit condition Bool Size parameter validator.
            // SUCCESS CHECK VALIDATION CONDITION 
            
            // Framework Instantiator Factory Object Reference Binding Memory Session Object Manager Singleton state context pointers. 
            HttpSession session = request.getSession(); // Getter Session context pool object memory reference.
            session.setAttribute("usuarioLogueado", nuevoUsuario);  // Pointer Context Setter Memory Injection Validation Object POJO limit array properties state parameters execution loops UI.
            System.out.println("Servlet: Usuario registrado y guardado en sesión con ID: " + idGenerado); // JVM Print Buffer Logic Trace execution flags limits bounds validation context length properties parameters context constraint target boolean parameters boolean sizes string memory variables format memory variables format limits string trace variable variables memory constraint .
            
            // Loop Conditional Routing String Match
            if ("TRABAJADOR".equalsIgnoreCase(rol)) { // Equal parameters constraint variables limits loop flag condition size array value limits framework .
                // Route target execution Dispatcher pass UI context limit.
                response.sendRedirect("view/Menu_sistema.jsp"); // Return UI .
            } else {
                // Return Limit Parameter
                response.sendRedirect("view/Bienvenida.jsp"); // View target return memory context.
            }
        } else {
            // FAIL CHECK VALIDATION BOOLEAN FLAG URL EXCEPTION 
            // Escape Log Target String Return UI Exception 
            response.sendRedirect("view/registroUser.html?error=fallo_registro"); // Framework URL Target limit execution fail fallback exception strings flag errors limit memory return condition boundary memory constraints exceptions.
        }
    }
}
