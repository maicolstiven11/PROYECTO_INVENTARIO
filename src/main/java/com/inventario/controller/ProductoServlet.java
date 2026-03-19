package com.inventario.controller; // Declaración de espacio de nombres organizativo de artefactos de red 

import com.inventario.dao.ProductoDAO;
import com.inventario.model.Producto;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
/**
 * Controlador Orquestador Integrador: ProductoServlet.
 * 
 * Fachada interceptora delegativa enfocada en el Modelo base dependiente (Producto).
 * Extiende la funcionalidad nativa de Servlet manejando I/O asíncrono e integrado con soporte Multipart/Form-Data para subida de binarios (imágenes).
 * Resuelve interacciones orquestando un Data Access Object propio.
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"}) // Decorador instanciador asimétrico Web .
@MultipartConfig( // Metadata restrictiva relacional sobre payload binario
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB (Umbral en memoria RAM)
    maxFileSize = 1024 * 1024 * 10,      // 10 MB (Techo de entidad binaria simple)
    maxRequestSize = 1024 * 1024 * 100   // 100 MB (Techo total payload HTTP)
)
public class ProductoServlet extends HttpServlet { // Polimorfismo sub-tipo base protocolo Web Framework HTTP .

    /**
     * Subrutina utilitaria IO de abstracción binaria.
     * Recibe una Part o Entidad Binaria Multipart encapsulándola para su persistencia en el filesystem Host de la JVM,
     * y genera una cadena alfanumérica persistible en BD de la URI o name del fichero.
     */
    private String subirImagen(Part part, HttpServletRequest request) throws IOException { // Declaración que acusa fallo potencial base IO .
        String fileName = null; // Instancia temporal string pointer nulo .
        if (part != null && part.getSize() > 0) { // Limitador boolean comprobador objeto payload 
            String contentDisp = part.getHeader("content-disposition"); // Busca metadata de capa HTTP .
            String[] items = contentDisp.split(";"); // Splitter divisor en Arrays para extraer parametrización de headers .
            for (String s : items) { // Iterador recursivo index simple
                if (s.trim().startsWith("filename")) { // String utils validador
                    fileName = s.substring(s.indexOf("=") + 2, s.length() - 1); // Mutador extractor String puro.
                }
            }
            if (fileName != null && !fileName.isEmpty()) { // Comprueba flag .
                fileName = System.currentTimeMillis() + "_" + new File(fileName).getName(); // Factoría concatenadora asegurando llave única pseudo-random (Timestamp) y nombre original validado.
                
                String uploadPath = request.getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img"; // Orquestador armador String ruta Absoluta Host compilado
                String sourcePath = "c:\\adso\\2994281\\PROYECTO_INVENTARIO\\src\\main\\webapp\\assets\\img"; // Orquestador ruta absoluta hardcoded Host estático .
                
                File uploadDir = new File(uploadPath); // Envuelve y genera Modelación Objeto File Directory API .
                if (!uploadDir.exists()) uploadDir.mkdirs(); // Disparador constructor comando subyacente mkdirs .
                
                File sourceDir = new File(sourcePath); // Ídem source.
                if (!sourceDir.exists()) sourceDir.mkdirs(); // Ídem create .
                
                // Traspaso de buffers IO del stream a memoria estática disco .
                part.write(uploadPath + File.separator + fileName); // Method writer framework envuelto .
                
                // Traspaso asimétrico síncrono reflejando copia a carpeta Dev
                try {
                    Files.copy(Paths.get(uploadPath + File.separator + fileName), // Java NIO API Object param .
                               Paths.get(sourcePath + File.separator + fileName), 
                               StandardCopyOption.REPLACE_EXISTING); // Flag sobreescribir.
                } catch(Exception e) {
                    System.out.println("Error copiando imagen a carpeta fuente: " + e.getMessage()); // Fail silencioso y purgado buffer trace.
                }
            }
        }
        return (fileName != null && !fileName.isEmpty()) ? fileName : null; // Retorno de variable local (Ternario utilitario boolean).
    }

    /**
     * Sobreescritura del método transaccional de posteo mutativo síncrono.
     * Enruta flujos alternos: Instanciación/Creación Total de Entidad, o Actualización de Entidad Activa, discriminado semánticamente.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Captura framework API
        
        // Atrapa y filtra param semántico para subrutina update.
        String action = request.getParameter("action"); // Binding Local
        if ("actualizar".equals(action)) { // String Match restrictivo 
            procesarActualizacion(request, response);  // Invoca Helper passthrough en la subclase local proxy .
            return; // Nullifies current execution stack.
        }
        
        // =====================================================================
        // ALGORITMO ORQUESTADOR DE CREACIÓN ZERO E INYECCIÓN CON BINDING MULTIPART
        // =====================================================================
        
        // Recoge param string de forms frontend (Instanciación variable local asilada nulas si no manda el front)
        String nombre = request.getParameter("nombre");              // Extractor literal primitivo .
        String marca = request.getParameter("marca");                // Idem.
        String tipo = request.getParameter("tipo");                  // Idem categorizado.
        
        // Delegación Binary File (Multipart) a método auxiliar .
        String imagen = null; // Variable nula por defecto .
        try {
            Part filePart = request.getPart("imagen"); // Atrapa el binario framework 
            if (filePart != null && filePart.getSize() > 0) { // Limitador flag logic 
                imagen = subirImagen(filePart, request); // Delega buffer Stream .
            }
        } catch(Exception e) { // Suciedad null .
            System.out.println("Error al procesar la imagen: " + e.getMessage()); // Print Trace asilado .
        }
        
        String cantidadMedida = request.getParameter("cantidad_medida"); // String relacional escalar semántico .
        
        // Constructor Float Decimal.
        double precio = 0.0; // Base constructor primitivo.
        try { // Trata conversión 
            precio = Double.parseDouble(request.getParameter("precio")); // Matemáticas Float parsing cast .
        } catch (NumberFormatException e) {
            precio = 0.0;  // Asilamiento fall-back o protector de Null .
        }
        
        // Constructor y parser SQL Entity time-model Date.
        Date fechaVencimiento = null; // Nulo Base
        try {
            String fechaStr = request.getParameter("fecha_vencimiento"); // Cadena format param
            if(fechaStr != null && !fechaStr.isEmpty()){ // Booleano .
                fechaVencimiento = Date.valueOf(fechaStr);  // Polimorfismo o Factoría de instanciación tipo Date desde string param ISO.
            }
        } catch (IllegalArgumentException e) { // Excepciones por format Date .
            fechaVencimiento = null;  // Purifica setter protector base 
        }

        // FÁBRICA CONSTRUCTIVA DEL POJO ENTIDAD MADRE .
        Producto p = new Producto(); // Asigna HEAP RAM space vacío al wrapper Object Entity .
        p.setNombre(nombre); // Método mutador o setter en instanciado de abstracción private object.
        p.setMarca(marca); // Setter String .
        p.setPrecioUnitario(precio); // Setter Floating point.
        p.setTipo(tipo); // Setter Category string referencial .
        p.setImagen(imagen); // Set URI pointer a filesystem Host BD abstracción string 
        p.setFechaVencimiento(fechaVencimiento); // Setter capa modelo object instanciador o puntero .
        p.setCantidadMedida(cantidadMedida); // Setter text-form float/unit abstraction .

        // INYECCIÓN MÚLTIPLE DE TRANSACCIÓN A CAPA BD
        ProductoDAO dao = new ProductoDAO(); // Constructor del Orquestador Enrutador de Modelo DAO .
        
        // Subrutina restrictiva condicional chequeando consistencia Booleana. 
        if (dao.existeNombreProducto(nombre)) { // Llamada método comparador selectivo boolean retornado.
            response.sendRedirect("view/Registro_produc.html?error=producto_duplicado"); // Resolutivo Web redirect y flag error .
            return; // Corta flujo actual por integridad.
        }

        try {
            boolean exito = dao.registrarProducto(p);  // Instancia llamada principal insertando Object Entity completo en método asíncrono.
            if (exito) { // Rama afirmativa bool
                response.sendRedirect("view/Produc_registrado.html");  // Conclusión UI exitosa síncrona visual .
            } else {
                // Caída lógica false boolean
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD"); // Param Error fallback logic .
            }
        } catch (Exception e) {
            // Caída asíncrona error runtime SQL/Parse Exception 
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_")); // Muta e inyecta log .
        }
    }

    /**
     * Sobreescritura evaluativa transitoria GET.
     * Módulo switch iterativo analizando Strings para bifurcar el árbol asíncrono hacia subrutinas de Carga Inicial Frontal, Lista de Colección Modelo o Destructor.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Capturador API Web .
        
        String action = request.getParameter("action"); // Extrae limitante y flag director String from HTTP Get query
        ProductoDAO dao = new ProductoDAO(); // Generador Gestor Relacional
        
        if (action != null && action.equals("eliminar")) { // Limitador y Match boolean operator and String Equaliser.
            // =====================================================================
            // ALGORITMO ORTOGONAL DE DESTRUCCIÓN:
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id"));  // Getter unívoco PK param casteo Primitivo numérico .
                boolean eliminado = dao.eliminarProducto(id);           // Método Setter Disparador booleano destructor en Orquesta DAO .
                response.sendRedirect("ProductoServlet");               // Retorna recursivo frontal purificando base view param o refresh cycle .
            } catch (Exception e) { // Suciedad I/O .
                e.printStackTrace(); // Consola trace .
                // Exito no completado fallback
                response.sendRedirect("ProductoServlet?error=ErrorEliminar"); // Salida param flag asíncrono
            }
        } else if (action != null && action.equals("editar")) { // Switch iterativo secundario string eq comparator
            // =====================================================================
            // ALGORITMO INVERSO PREPARADOR EDICIÓN ENTIDAD:
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id")); // Lector PK asimétrica url query a Integer
                Producto p = dao.obtenerProducto(id);                   // Método Getter delegativo y cargador instanciador asilado singular PK obj .
                
                if (p != null) { // Instancia flag protectora null.
                    request.setAttribute("productoEditar", p);          // Carga el encapsulado POJO activo referencial a param transitorio HTTP Context buffer del framework frontend.
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response); // Render de passthrough server side directo para JSP.
                } else {
                    // Null checker error 
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado"); // Flag log .
                }
            } catch (Exception e) { // Catch base object errors framework
                e.printStackTrace(); // Suciedad logger JVM
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion"); // Clean param fallbacks .
            }
        } else { // Switch Iterativo default genérico final 
            // =====================================================================
            // GENERADOR MATRIZ VISUAL LISTA ENTIDADES
            // =====================================================================
            java.util.List<Producto> lista = dao.listarProductos();    // Inicia fábrica recolectora masiva y agrupa en Array Dinámico estricto polimorfismo Entidad .
            request.setAttribute("listaProductos", lista);             // Bind in-memory a variable literal referencial render .
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response); // Disparo final pasivo asilado para motor template local render (Server render bypass).
        }
    }
    
    /**
     * Módulo Privado Sub-Estructural Auxiliar (Helper encapsulado de Update).
     * Secuencia repetitiva extraída que parsea un Payload FormData y un MultiPart, generando
     * o conservando punteros de binarios previos, para recrear un Modelo modificado y sobreescribir atómicamente la capa DAO.
     */
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Amparo Web Frame 
        
        try { // Vigila parseo num y MultiPart Array errors.
            // Parseo primitivo a vars de capa base JVM y variables aisladas locale
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); // Cast clave inamovible .
            String nombre = request.getParameter("nombre"); // Bind String 
            String marca = request.getParameter("marca"); // Bind literal .
            String tipo = request.getParameter("tipo"); // Bind semántico .
            
            // Re-procesamiento iterativo para File Pointer stream
            String imagen = null; // Instancia Nulo a priori .
            try {
                Part filePart = request.getPart("imagen"); // Captura Blob File param in framework .
                if (filePart != null && filePart.getSize() > 0) { // Test check sizes .
                    imagen = subirImagen(filePart, request); // Traspaso Stream delegativo a function custom subrutina binaria helper IO y devuelve pointer .
                }
            } catch(Exception e) { // Catch Stream .
                System.out.println("Error al actualizar imagen: " + e.getMessage()); // System Buffer Print trace only.
            }
            
            // Subrutina auxiliar protectora u lógica asimétrica de conservación referencial: Si la part es nula, recarga el pointer string object Entity Old Base .
            if (imagen == null) { // Lógica Null.
                ProductoDAO tempDao = new ProductoDAO(); // Inicializa gestor transaccional iterativo corto .
                Producto original = tempDao.obtenerProducto(idProducto); // Llama Factory generatriz y acopla objeto puro modelo previo .
                if (original != null) { // Validador 
                    imagen = original.getImagen(); // Extrae con getter string pointer previo in memory string variable param asilada mutable target.
                }
            }
            
            String cantidadMedida = request.getParameter("cantidad_medida"); // Setter .
            
            double precio = 0.0; // Constructor Default .
            try {
                precio = Double.parseDouble(request.getParameter("precio")); // Matemáticas string Cast Float.
            } catch (NumberFormatException e) {
                precio = 0.0; // Fail 
            }
            
            java.sql.Date fechaVencimiento = null; // String setter param Date Base .
            try {
                String fechaStr = request.getParameter("fecha_vencimiento"); // Variable asilada .
                if (fechaStr != null && !fechaStr.isEmpty()) { // Booleano .
                    fechaVencimiento = java.sql.Date.valueOf(fechaStr); // Parsificador object Date base SQL construct object model .
                }
            } catch (IllegalArgumentException e) {
                fechaVencimiento = null; // Purgador format error Date param.
            }
            
            // FÁBRICA MUTATIVA COMPLETA POJO INSTANCIADO: Wrapper Builder completo de sobreescritura a memoria .
            Producto p = new Producto(); // Asigna Heap vacío para contenedor modelo .
            p.setIdProducto(idProducto);  // Setter Clave (Inamovible Update Condicional relacional where) .
            p.setNombre(nombre); // Método encapsulado .
            p.setMarca(marca); // ..
            p.setPrecioUnitario(precio); // ..
            p.setTipo(tipo); // ..
            p.setImagen(imagen); // Inyección polimórfica (nuevo ó viejo link URI pointer local).
            p.setFechaVencimiento(fechaVencimiento); // Metadato de time abstract model inyector.
            p.setCantidadMedida(cantidadMedida); // Inyector genérico 
            
            // DISPARO A BD FINALIZANDO
            ProductoDAO dao = new ProductoDAO(); // Lector DB relacional base .
            boolean exito = dao.actualizarProducto(p);  // Disparo método generatriz boolean afirmativo transaccional atómico delegativo .
            
            if (exito) { // Check true
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado");  // Redirect clean UI param msg success flag iterativo log .
            } else { // Fail check 
                response.sendRedirect("ProductoServlet?error=ErrorActualizar");    // Redirect log error boolean fail  .
            }
            
        } catch (Exception e) { // Suciedad Exception catch 
            e.printStackTrace(); // Dump trace  CLI buffer
            response.sendRedirect("ProductoServlet?error=" + e.getMessage()); // Redirect flag print exception .
        }
    }
}
