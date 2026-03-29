package com.inventario.controller; 

import com.inventario.dao.ProductoDAO;
import com.inventario.model.Producto;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
/**
 * Controlador ProductoServlet.
 * 
 * Es el gestor del Catálogo de la tienda. Puedes añadir productos (con imagen),
 * quitar del catálogo o editar sus descripciones y precios.
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"}) // Registra el servlet con nombre y URL
@MultipartConfig( // Configuración para manejar subida de archivos
    fileSizeThreshold = 1024 * 1024 * 1, // Tamaño en memoria antes de escribir en disco (1 MB)
    maxFileSize = 1024 * 1024 * 10,      // Tamaño máximo de archivo permitido (10 MB)
    maxRequestSize = 1024 * 1024 * 100   // Tamaño máximo de la petición completa (100 MB)
)
public class ProductoServlet extends HttpServlet { // Clase servlet que hereda de HttpServlet

    private String subirImagen(Part part, HttpServletRequest request) throws IOException { // Método para subir imagen
        String fileName = null; 
        if (part != null && part.getSize() > 0) { // Verifica que exista archivo y tenga tamaño
            String contentDisp = part.getHeader("content-disposition"); // Obtiene cabecera con info del archivo
            String[] items = contentDisp.split(";"); // Divide la cabecera en partes
            for (String s : items) { 
                if (s.trim().startsWith("filename")) { // Busca el nombre del archivo
                    fileName = s.substring(s.indexOf("=") + 2, s.length() - 1); // Extrae nombre
                }
            }
            if (fileName != null && !fileName.isEmpty()) { // Si hay nombre válido
                fileName = System.currentTimeMillis() + "_" + new File(fileName).getName(); // Renombra con timestamp
                
                String uploadPath = request.getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img"; // Carpeta destino en servidor
                String sourcePath = "c:\\adso\\2994281\\PROYECTO_INVENTARIO\\src\\main\\webapp\\assets\\img"; // Carpeta fuente en proyecto
                
                File uploadDir = new File(uploadPath); 
                if (!uploadDir.exists()) uploadDir.mkdirs(); // Crea carpeta destino si no existe
                
                File sourceDir = new File(sourcePath); 
                if (!sourceDir.exists()) sourceDir.mkdirs(); // Crea carpeta fuente si no existe
                
                part.write(uploadPath + File.separator + fileName); // Guarda archivo en carpeta destino
                
                try {
                    Files.copy(Paths.get(uploadPath + File.separator + fileName), // Copia archivo
                               Paths.get(sourcePath + File.separator + fileName), 
                               StandardCopyOption.REPLACE_EXISTING); // Reemplaza si ya existe
                } catch(Exception e) {
                    System.out.println("Error copiando imagen a carpeta fuente: " + e.getMessage()); // Mensaje de error
                }
            }
        }
        return (fileName != null && !fileName.isEmpty()) ? fileName : null; // Devuelve nombre o null
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método POST
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Obtiene acción
        if ("actualizar".equals(action)) { // Si acción es actualizar
            procesarActualizacion(request, response); // Llama método de actualización
            return; 
        }
        
        String nombre = request.getParameter("nombre");    // Nombre del producto
        String marca = request.getParameter("marca");      // Marca del producto
        String tipo = request.getParameter("tipo");        // Tipo de producto
        
        String imagen = null; 
        try {
            Part filePart = request.getPart("imagen"); // Obtiene archivo imagen
            if (filePart != null && filePart.getSize() > 0) { 
                imagen = subirImagen(filePart, request); // Sube imagen
            }
        } catch(Exception e) { 
            System.out.println("Error al procesar la imagen: " + e.getMessage()); // Error imagen
        }
        
        String cantidadMedida = request.getParameter("cantidad_medida"); // Cantidad medida
        
        double precio = 0.0; 
        try { 
            precio = Double.parseDouble(request.getParameter("precio")); // Convierte precio
        } catch (NumberFormatException e) {
            precio = 0.0;  // Si falla, precio = 0
        }
        
        Date fechaVencimiento = null; 
        try {
            String fechaStr = request.getParameter("fecha_vencimiento"); // Fecha vencimiento
            if(fechaStr != null && !fechaStr.isEmpty()){ 
                fechaVencimiento = Date.valueOf(fechaStr);  // Convierte a Date
            }
        } catch (IllegalArgumentException e) { 
            fechaVencimiento = null;  // Si falla, null
        }

        Producto p = new Producto(); // Crea objeto producto
        p.setNombre(nombre); 
        p.setMarca(marca); 
        p.setPrecioUnitario(precio); 
        p.setTipo(tipo); 
        p.setImagen(imagen);
        p.setFechaVencimiento(fechaVencimiento); 
        p.setCantidadMedida(cantidadMedida); 

        ProductoDAO dao = new ProductoDAO(); // DAO de producto
        
        if (dao.existeNombreProducto(nombre)) { // Valida duplicado
            response.sendRedirect("view/Registro_produc.html?error=producto_duplicado"); 
            return; 
        }

        try {
            boolean exito = dao.registrarProducto(p); // Registra producto
            if (exito) { 
                response.sendRedirect("view/Produc_registrado.html"); // Éxito
            } else {
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");  // Error BD
            }
        } catch (Exception e) {
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_"));  // Error general
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método GET
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Acción
        ProductoDAO dao = new ProductoDAO(); // DAO producto
        
        if (action != null && action.equals("eliminar")) { // Acción eliminar
            // =====================================================================
            // ELIMINAR: CON VALIDACIÓN DE HISTORIAL
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id")); // ID producto
                
                // VALIDACIÓN: ¿Tiene el producto ventas o pedidos?
                if (dao.productoTieneDatos(id)) { // Si tiene datos asociados
                    response.sendRedirect("ProductoServlet?error=producto_con_datos"); // No se borra
                } else {
                    boolean eliminado = dao.eliminarProducto(id); // Elimina producto
                    if (eliminado) {
                        response.sendRedirect("ProductoServlet?msg=eliminado_exito"); // Éxito
                    } else {
                        response.sendRedirect("ProductoServlet?error=ErrorEliminar"); // Error
                    }
                }
                return;
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorProcesarEliminar"); // Error general
            }
        } else if (action != null && action.equals("editar")) { // Acción editar
            try {
                int id = Integer.parseInt(request.getParameter("id")); // ID producto
                Producto p = dao.obtenerProducto(id); // Obtiene producto
                
                if (p != null) { 
                    request.setAttribute("productoEditar", p); // Envía producto a JSP
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response);
                } else {
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado"); // No encontrado
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion"); // Error edición
            }
        } else { 
            // =====================================================================
            // LISTAR: COMPORTAMIENTO POR DEFECTO
            // =====================================================================
            HttpSession session = request.getSession(); // Sesión
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // ID negocio actual
            
            if (idNegocio == null) idNegocio = 0; // Si no existe, 0
            
            java.util.List<Producto> lista = dao.listarProductos(idNegocio); // Lista productos
            request.setAttribute("listaProductos", lista); // Envía lista
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response); // Redirige JSP
        }
    }
    
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response) // Método privado para actualizar producto
            throws ServletException, IOException { 
        
        try { 
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); // Obtiene ID del producto desde el formulario
            String nombre = request.getParameter("nombre"); // Obtiene nombre del producto
            String marca = request.getParameter("marca");   // Obtiene marca del producto
            String tipo = request.getParameter("tipo");     // Obtiene tipo del producto
            
            String imagen = null; 
            try {
                Part filePart = request.getPart("imagen"); // Obtiene archivo de imagen enviado
                if (filePart != null && filePart.getSize() > 0) { 
                    imagen = subirImagen(filePart, request); // Sube imagen y devuelve nombre
                }
            } catch(Exception e) { 
                System.out.println("Error al actualizar imagen: " + e.getMessage()); // Mensaje de error si falla la subida
            }
            
            if (imagen == null) { // Si no se subió nueva imagen
                ProductoDAO tempDao = new ProductoDAO(); // Instancia DAO temporal
                Producto original = tempDao.obtenerProducto(idProducto); // Obtiene producto original
                if (original != null) { 
                    imagen = original.getImagen(); // Mantiene la imagen anterior
                }
            }
            
            String cantidadMedida = request.getParameter("cantidad_medida"); // Obtiene cantidad medida
            
            double precio = 0.0; 
            try {
                precio = Double.parseDouble(request.getParameter("precio")); // Convierte precio a double
            } catch (NumberFormatException e) {
                precio = 0.0; // Si falla conversión, asigna 0
            }
            
            java.sql.Date fechaVencimiento = null; 
            try {
                String fechaStr = request.getParameter("fecha_vencimiento"); // Obtiene fecha vencimiento
                if (fechaStr != null && !fechaStr.isEmpty()) { 
                    fechaVencimiento = java.sql.Date.valueOf(fechaStr); // Convierte a tipo Date
                }
            } catch (IllegalArgumentException e) {
                fechaVencimiento = null; // Si falla, asigna null
            }
            
            Producto p = new Producto(); // Crea objeto producto
            p.setIdProducto(idProducto); // Asigna ID
            p.setNombre(nombre);         // Asigna nombre
            p.setMarca(marca);           // Asigna marca
            p.setPrecioUnitario(precio); // Asigna precio
            p.setTipo(tipo);             // Asigna tipo
            p.setImagen(imagen);         // Asigna imagen
            p.setFechaVencimiento(fechaVencimiento); // Asigna fecha vencimiento
            p.setCantidadMedida(cantidadMedida);     // Asigna cantidad medida
            
            ProductoDAO dao = new ProductoDAO(); // Instancia DAO de producto
            boolean exito = dao.actualizarProducto(p); // Llama método para actualizar producto en BD
            
            if (exito) { 
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado"); // Redirige con mensaje de éxito
            } else { 
                response.sendRedirect("ProductoServlet?error=ErrorActualizar"); // Redirige con mensaje de error
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); // Muestra error en consola
            response.sendRedirect("ProductoServlet?error=" + e.getMessage()); // Redirige mostrando error
        }
    }
}
