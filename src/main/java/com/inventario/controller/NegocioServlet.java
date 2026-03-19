package com.inventario.controller; // Declaración de espacio de nombres organizativo de artefactos de red 

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
 * Controlador Lógico Orquestador: NegocioServlet.
 * 
 * Clase de fachada interceptora (Front Controller Pattern approach limitado a su entidad).
 * Ejerce de núcleo iterativo captador en la capa Servlet, interconectando el modelo abstracto (Negocio)
 * y la persistencia (NegocioDAO) con el cliente HTTP (Front-end), gestionando el estado local a través de atributos de Sesión transientes.
 */
@WebServlet(name = "NegocioServlet", urlPatterns = {"/NegocioServlet"}) // Inyección de dependencia estática para el framework contenedor servlet HTTP.
public class NegocioServlet extends HttpServlet { // Extensión de herencia abstracta base para comunicación I/O asíncrona Web.

    /**
     * Sobreescritura del método transaccional HTTP GET.
     * Analizador condicionado del Request URI asumiendo dos subrutinas de consulta o destrucción:
     * - Destrucción condicional: elimina un nodo u objeto negocio si recibe parametrización específica.
     * - Listado Relacional (Default): Recoge el envoltorio de sesión vivo e intercede para llenar un buffer con Arrays relacionales del contexto.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Excepción de framework de I/O
        
        String action = request.getParameter("action"); // Binding inicial temporal de string discriminador
        
        // =====================================================================
        // ALGORITMO ORTOGONAL DE DESTRUCCIÓN: (Bifurcación Eliminación en Modelo DAO)
        // =====================================================================
        if ("eliminar".equals(action)) { // Chequeo booleano lógico comparador en stack local de cadena
            try {
                int idNegocio = Integer.parseInt(request.getParameter("id")); // Parseador unívoco de ID foráneo literal a tipo primitivo Java
                NegocioDAO dao = new NegocioDAO(); // Instanciador base utilitario extractor de persistencia relacional
                boolean eliminado = dao.eliminarNegocio(idNegocio);           // Llamada al orquestador delegador destructor y espera de afirmación booleana lógica.
                
                // Finalización del hilo ordenando un reciclaje reactivo al front apuntando al controlador limpio (sin action)
                response.sendRedirect("NegocioServlet"); // Reseteo transaccional Web de capa asíncrono
                return; // Corta el árbol condicionado 
            } catch (Exception e) {
                e.printStackTrace(); // Salida sucia no logueada de crash I/O 
            }
        }
        
        // =====================================================================
        // ALGORITMO SELECTIVO EN BIFURCACIÓN PRINCIPAL (Consulta transaccional)
        // Acoplamiento relacional vía atributo inyectado transiente.
        // =====================================================================
        HttpSession session = request.getSession(); // Llama al constructor provisto por el Wrapper general (Estado pre-autorizado).
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Cast explícito referenciado al POJO superior User en vivo .
        
        int idUsuario = 0; // Inicializador escalar defensivo
        if (usuarioLogueado != null) { // Validador atómico booleano contra punteros vacíos huérfanos .
            idUsuario = usuarioLogueado.getIdUsuario(); // Método asimila getter interno devolviendo puntero .
        }
        
        NegocioDAO dao = new NegocioDAO(); // Inicializa transaccional pasivo DAO relacional.
        List<Negocio> lista = dao.listarNegocios(idUsuario);        // Función interviniente retornando Arreglo List genérico estricto del modelo (Entity collection).
        
        request.setAttribute("listaBares", lista);                  // Atribución de objeto lista y metadato relacional interconectado para visualización .
        request.getRequestDispatcher("view/Lista_bares.jsp").forward(request, response); // Despachaje (Forwarding passivo) del request completo con la request pipeline .
    }

    /**
     * Sobreescritura del método transaccional HTTP POST (Payload inyectivo).
     * Orígenes form client -> Instanciación en memoria Local (Negocio POJO) -> DAO Save.
     * Completa el proceso con la inserción de banderas u atributos session relacionales de estado en runtime .
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Atrapa error 
        
        // Mapea y guarda en constructores Strings puros en la Local stack .
        String nombre = request.getParameter("nombre");       // Función extractora de atributo literario alfanumérico principal en POJO
        String direccion = request.getParameter("direccion"); // Extracto de metadato de ubicación string .
        
        // Contexto envoltura 
        HttpSession session = request.getSession(); // Accesor a la metadata relacional viva HTTP.
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado"); // Castea al perfil completo instanciado .
        
        int idUsuario = 0; // Pre-declaración a base 
        if (usuarioLogueado != null) { // Bloque null check en pipeline .
            idUsuario = usuarioLogueado.getIdUsuario(); // Extrae getter escalar clave 
        }
        
        // CONSTRUCTOR Y ALMACÉN DE MODELO VIVO 
        Negocio n = new Negocio(); // Factory patter simple, POJO vacío preparado en HEAP.
        n.setNombre(nombre); // Método encapsulado inyector modificador variable atributo privado interno .
        n.setDireccion(direccion); // Transvasa lógica escalar referenciada string .
        
        // DELEGATIVO DAO MANAGER 
        NegocioDAO dao = new NegocioDAO(); // Nuevo envoltorio de DB 
        try {
            // Persistencia del modelo acoplado y la Foreign Key abstracta , recibiendo escalar numérico autoincremental de llave Primaria 
            int idGenerado = dao.registrarNegocio(n, idUsuario); 
            
            if (idGenerado > 0) { // Evalúa condición booleana pasiva confirmando integridad .
                // Mantiene el polimorfismo o recalcula métrica foránea interconectada 
                try { // Previene caídas en métrica para no colgar hilo .
                    int cantBares = dao.contarNegocios(idUsuario);        // Subrutina contable a motor BD .
                    session.setAttribute("numBares", cantBares);          // Refresca la variable temporal relacional del flag cardinal 
                } catch(Exception e) { e.printStackTrace(); } // Nullifica error y pasa 

                // Almacena transaccionalmente en sesión los delimitadores lógicos .
                session.setAttribute("idNegocioActual", idGenerado);     // Ajuste dinámico referencial principal inyectado  
                session.setAttribute("nombreNegocioActual", nombre);     // Guardado literal transiente
                response.sendRedirect("view/registroBar_fin.html");      // Direccionamiento resolutivo asíncrono
            } else {
                // Fallo controlado por el recolector DAO (Validación False / <= 0)
                response.sendRedirect("view/registroBar.html?error=FalloRegistroDAO"); // Agrega parametro log
            }
        } catch (Exception e) { // Atrapatodo y print
            e.printStackTrace(); 
            // Fallo condicional por excepción JVM transaccional , serializa String Error
            response.sendRedirect("view/registroBar.html?error=" + e.getMessage().replace(" ", "_")); // Muta salida HTTP
        }
    }
}
