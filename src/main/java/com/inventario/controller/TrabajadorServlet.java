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
 * Controlador Transaccional Orquestador: TrabajadorServlet.
 * 
 * Fachada encapsuladora encargada de la inyección de dependencias de negocio y seguridad de empleados.
 * Gestiona el ciclo vital y vinculación de Actores Sistema instanciando y asignando objetos Relacionales mediante el patrón DAO Model.
 */
@WebServlet(name = "TrabajadorServlet", urlPatterns = {"/TrabajadorServlet"}) // Binding decorativo asíncrono instanciando la ruta de la factoría Servlet Http nativa.
public class TrabajadorServlet extends HttpServlet { // Polimorfismo hereditario del Framework Java EE base HTTP Rest.

    /**
     * Rescritura Genérica evaluadora HTTP Get.
     * Funciona como método de inicialización o passthrough de lectura (Read Context). Generando listas pasivas iterando colecciones de Objetos Model para in-memory display de datos Frontales en capa de Interfaz (JSP).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Amparo catch frame object
        
        String action = request.getParameter("action"); // Lector string iterativo bandera de limitación
        if (action == null) action = "listar"; // Cast in memory base constructor null protection.

        if ("listar".equals(action)) { // String equals validation guard .
            // Instanciador Recolector de Colección Masiva de Objetos Entidad Tipo Trabajador 
            UsuarioDAO usuarioDAO = new UsuarioDAO(); // Constructor Factoría Gestora Object Relation Model de capa lógica SQL DAO.
            List<Usuario> listaTrabajadores = usuarioDAO.listarTrabajadores(); // Envoltorio iterativo pointer in-memory Heap collections list of entities models type Usuario.
            
            // Factory getter iterativo PK Inyección context base pool session framework HTTP pointer auth session live checker.
            HttpSession session = request.getSession(); // Getter constructor.
            Usuario admin = (Usuario) session.getAttribute("usuarioLogueado"); // Wrapper setter instanciador de Entidad Autenticada Polimórfico object cast as type Usuario context Alive JVM.

            if (admin != null) { // Validation Boolean limit alive security Session state .
                NegocioDAO negocioDAO = new NegocioDAO(); // Constructor instance Object Dao transaction
                List<Negocio> listaNegocios = negocioDAO.listarNegocios(admin.getIdUsuario()); // Method delegador List Wrapper instance Array Model Collection entity type Negocio.
                
                request.setAttribute("listaTrabajadores", listaTrabajadores); // Bind context alive parameter object property Array list to local context HTTP var param.
                request.setAttribute("listaNegocios", listaNegocios); // Binding POJO collection as param framework rendering attribute.
                request.getRequestDispatcher("view/gestion_trabajadores.jsp").forward(request, response); // Despacho delegación o passthrough síncrono bypassing routing framework motor to JVM local JSP Render Engine scope.
            } else { // Abort log off .
                response.sendRedirect("view/Inicio_sesion.html"); // Cleanup URL param clean reload page logic flag error redirect string.
            }
        }
    }

    /**
     * Rescritura Genérica evaluadora HTTP Post.
     * Orquesta el Mutador Mutacional en base a Sub-Estructuras switch iterativas: Action Flag string parameters para Disparo Síncrono de Inserción Atómica o Update Transacciones Mutadoras Delegadas a Persistencia SQL Factory connection Manager DAO Logic.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Capturador exception
        
        String action = request.getParameter("action"); // Capturador metadata url o form param Action.

        if ("asignar".equals(action)) { // Loop condicional Action setter math target .
            try { // Valida casteos param format String int exception catch IO.
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // Primitive instanciador PK setter
                int idNegocio = Integer.parseInt(request.getParameter("id_negocio")); // Primitive PK math instantiator.
                
                UsuarioDAO dao = new UsuarioDAO(); // Wrapper Object Relation method transaccional builder
                
                // Guard constraint Logic Sub-rutina Matemática Booleana Inyección Dependency validation param 
                if (dao.negocioTieneTrabajador(idNegocio)) { // Delegate method return Boolean if model exist relation validator 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=bar_ocupado"); // UI exception flag log abort action in session. 
                    return; // Nullifies logic flow stack interruptor destruction memory execution stack exit 
                }
                
                boolean exito = dao.asignarNegocio(idUsuario, idNegocio); // Atomico Multiple parameters setter execution method method bool return status boolean getter flag log.
                
                if (exito) { // Validation true return .
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=asignado"); // Redir Clean statu log OK Boolean 
                } else { // Check fail logic no object changes 
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_asignar"); // Loop Error Boolean Flag False 
                }
            } catch (Exception e) { // Suciedad framework Exception catch
                e.printStackTrace(); // Dump memory JVM trace object err console log output CLI silent failure .
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); // Flag logger.
            }
        } else if ("desasignar".equals(action)) { // Second sub-switch bool equals iter loop flag String.
            try { // Exception trap framework runtime .
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // Variable setter getter URL string target primitivo .
                UsuarioDAO dao = new UsuarioDAO(); // Instanciador generatriz Model Persistence Factory 
                boolean exito = dao.desasignarNegocio(idUsuario); // Destructor referencial SQL Atomic update Boolean execution .
                
                if (exito) { // Validation loop Boolean True flag .
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=desasignado"); // OK URL Loop return 
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_desasignar"); // Log Fall boolean setter param
                }
            } catch (Exception e) { // exception base frame error param log
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos");
            }
        } else if ("eliminar".equals(action)) { // Action Flag Third string equality validator
            try { // Guard condition .
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // Primitive number object integer extraction .
                UsuarioDAO dao = new UsuarioDAO(); // Factory Model Relation.
                boolean exito = dao.eliminarTrabajador(idUsuario); // Mutable destruction target SQL row relational object property destructor return state var flag Boolean check.
                
                if (exito) { // Check Success execution status Boolean state wrapper .
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=eliminado"); // Ok loop 
                } else { // fail execution Boolean destructor
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_eliminar"); // UI fail .
                }
            } catch (Exception e) { // exception handler
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); // dirty catch
            }
        } else if ("resetPassword".equals(action)) { // Action Flag Switch Boolean target String parameter.
            // Privileged Administrator Method Delegation Factory Pattern sub-rutine mutator parameter boolean object setter properties context validation logic string.
            try { // catch base format primitives object errors parameters query string exception handler execution .
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario")); // param primitive cast string math int reference.
                String nuevaPassword = request.getParameter("nueva_password"); // String property parameter logic payload injection object model 
                String confirmarPassword = request.getParameter("confirmar_password"); // Validator validation string flag .
                
                // Algoritmo Verificador String property Validation Limits Bounds logic.
                if (nuevaPassword == null || nuevaPassword.length() < 6) { // Math limiter boundary Boolean Condition flag Check logic fail bounds limits sizes property Array strings limit.
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_corta"); // Return fail execution Bounds validation constraint Exception
                    return; // Nullifies Stack loop exit return clean memory execution .
                }
                if (!nuevaPassword.equals(confirmarPassword)) { // Equality Constraint limiter object validator Boolean loop .
                    response.sendRedirect("TrabajadorServlet?action=listar&error=password_no_coincide"); // Redirect Return String error format property limits
                    return; // Destroy stack 
                }
                
                UsuarioDAO dao = new UsuarioDAO(); // constructor wrapper Factory Manager Relational class .
                boolean exito = dao.actualizarPassword(idUsuario, nuevaPassword); // Setter Object Update model param attribute mutative query relation db object framework response Flag log Boolean return validation check status true false check logic .
                
                if (exito) { // Math Flag Validator bool ok property limit .
                    response.sendRedirect("TrabajadorServlet?action=listar&msg=password_reseteada"); // Response Return Loop 
                } else {
                    response.sendRedirect("TrabajadorServlet?action=listar&error=fallo_reset"); // Response Error Flag Return UI parameter bounds checks error execution bool fail update sql 
                }
            } catch (Exception e) { // Exception Trap Trace exception execution base JVM model errors framework primitive formats
                e.printStackTrace(); // dump buffer .
                response.sendRedirect("TrabajadorServlet?action=listar&error=datos_invalidos"); // logger exception fallback format primitive constraint checks boolean false.
            }
        }
    }
}
