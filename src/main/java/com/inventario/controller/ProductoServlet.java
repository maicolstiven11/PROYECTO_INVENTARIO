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
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"}) 
@MultipartConfig( // Etiqueta estricta obligatoria para que Java te permita arrastrar Archivos (Fotos jpg, png) en los formularios
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB límite temporal antes de usar el disco
    maxFileSize = 1024 * 1024 * 10,      // 10 MB límite de peso de tu simple foto
    maxRequestSize = 1024 * 1024 * 100   // 100 MB máximo en toda la red de peticiones a la vez
)
public class ProductoServlet extends HttpServlet { 

    /**
     * Función interna oculta auxiliar. 
     * Se usa para recortarle el nombre original a la foto, hacerle un código único (Para que 2 fotos de 'papa.jpg' no se pisen entre ellas)
     * y las guarda/copia físicamente en la carpeta 'assets/img' de tu proyecto Java.
     */
    private String subirImagen(Part part, HttpServletRequest request) throws IOException { 
        String fileName = null; 
        if (part != null && part.getSize() > 0) { // Si realmente sí arrastró una imagen 
            String contentDisp = part.getHeader("content-disposition"); 
            String[] items = contentDisp.split(";"); // Leemos la cabecera oculta para averiguar su extensión (.png, .jpg)
            for (String s : items) { 
                if (s.trim().startsWith("filename")) { 
                    fileName = s.substring(s.indexOf("=") + 2, s.length() - 1); // Cortamos la URI original ("foto1") 
                }
            }
            if (fileName != null && !fileName.isEmpty()) { 
                // Le pegamos el Tiempo Actual a la foto + nombre (Ej: 13904810934_foto1.jpg)
                fileName = System.currentTimeMillis() + "_" + new File(fileName).getName(); 
                
                // Ubicaciones de carpetas de tu servidor local
                String uploadPath = request.getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img"; 
                String sourcePath = "c:\\adso\\2994281\\PROYECTO_INVENTARIO\\src\\main\\webapp\\assets\\img"; 
                
                // Crea carpetas si no existían 
                File uploadDir = new File(uploadPath); 
                if (!uploadDir.exists()) uploadDir.mkdirs(); 
                
                File sourceDir = new File(sourcePath); 
                if (!sourceDir.exists()) sourceDir.mkdirs(); 
                
                // Escribe en ambas carpetas copiándola
                part.write(uploadPath + File.separator + fileName); 
                
                try {
                    Files.copy(Paths.get(uploadPath + File.separator + fileName), 
                               Paths.get(sourcePath + File.separator + fileName), 
                               StandardCopyOption.REPLACE_EXISTING); 
                } catch(Exception e) {
                    System.out.println("Error copiando imagen a carpeta fuente: " + e.getMessage()); 
                }
            }
        }
        return (fileName != null && !fileName.isEmpty()) ? fileName : null; // Retorna cómo se llamaba finalmente el archivo para que BD lo sepa (ej: 031201_papa.png)
    }

    /**
     * El doPost guarda tanto que un producto se CREA nuevo en el catálogo general, 
     * como también en caso de que un administrador EDITE algún valor del producto.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); 
        if ("actualizar".equals(action)) { // Si nos avisaron que solo íbamos a editar...
            procesarActualizacion(request, response);  // Llama debajo la función de editar 
            return; 
        }
        
        // =====================================================================
        // ACCIÓN NORMAL: CREAR UN NUEVO PRODUCTO AL CATÁLOGO
        // =====================================================================
        String nombre = request.getParameter("nombre");              // Sabritas    
        String marca = request.getParameter("marca");                // Margarita
        String tipo = request.getParameter("tipo");                  // Paquete
        
        // Atrapa la imagen o binario
        String imagen = null; 
        try {
            Part filePart = request.getPart("imagen"); 
            if (filePart != null && filePart.getSize() > 0) { 
                imagen = subirImagen(filePart, request); // Utiliza subrutina mágica de arriba para transformar en string 'xx_foto.png'
            }
        } catch(Exception e) { 
            System.out.println("Error al procesar la imagen: " + e.getMessage()); 
        }
        
        String cantidadMedida = request.getParameter("cantidad_medida"); // (200 gr, 1.5 L) 
        
        // Precio transformado
        double precio = 0.0; 
        try { 
            precio = Double.parseDouble(request.getParameter("precio")); 
        } catch (NumberFormatException e) {
            precio = 0.0;  
        }
        
        // Vencimiento configurado
        Date fechaVencimiento = null; 
        try {
            String fechaStr = request.getParameter("fecha_vencimiento"); 
            if(fechaStr != null && !fechaStr.isEmpty()){ 
                fechaVencimiento = Date.valueOf(fechaStr);  
            }
        } catch (IllegalArgumentException e) { 
            fechaVencimiento = null;  
        }

        // Armamos un gran modelo POJO para empaquetarlo (El Producto)
        Producto p = new Producto(); 
        p.setNombre(nombre); 
        p.setMarca(marca); 
        p.setPrecioUnitario(precio); 
        p.setTipo(tipo); 
        p.setImagen(imagen); // Set de foto URI
        p.setFechaVencimiento(fechaVencimiento); 
        p.setCantidadMedida(cantidadMedida); 

        ProductoDAO dao = new ProductoDAO(); 
        
        // Primero, se asgura que no metas 2 veces a la fuerza el mismo string nombre "Coca-Colas"
        if (dao.existeNombreProducto(nombre)) { 
            response.sendRedirect("view/Registro_produc.html?error=producto_duplicado"); 
            return; 
        }

        try {
            boolean exito = dao.registrarProducto(p);  // Mágicamente lo manda completo 
            if (exito) { 
                response.sendRedirect("view/Produc_registrado.html");  // Todo chidori  
            } else {
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");  
            }
        } catch (Exception e) {
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_"));  
        }
    }

    /**
     * El doGet en este caso sirve para "Eliminar Catálogo", "Cargar Editar" o
     * simplemente listar todo el catálogo de compras para mostrarlos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); 
        ProductoDAO dao = new ProductoDAO(); 
        
        if (action != null && action.equals("eliminar")) { 
            // =====================================================================
            // ELIMINAR O QUITAR DEFINITIVAMENTE PRODUCTO DE TABLA
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id"));  // Coge el click ID PK
                boolean eliminado = dao.eliminarProducto(id);           // Manda la guillotina  
                response.sendRedirect("ProductoServlet");               // Vuelve a repintarse el servlet sin datos para ver vacío...
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorEliminar"); 
            }
        } else if (action != null && action.equals("editar")) { 
            // =====================================================================
            // ABRIR FORMULARIO DE EDICIÓN YA CON LOS TEXTOS LLENOS DEL VIEJO
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id")); 
                Producto p = dao.obtenerProducto(id); // Obtiene todos los campos del que vas a editar (Ej: cerveza Costeñita)                 
                
                if (p != null) { 
                    request.setAttribute("productoEditar", p);          // Le inyecta toda esa data al form HTML  
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response); // Redirige a Form
                } else {
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado"); 
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion"); 
            }
        } else { 
            // =====================================================================
            // LISTAR: COMPORTAMIENTO POR DEFECTO DEL NAVEGADOR (VER PRODUCTOS)
            // =====================================================================
            java.util.List<Producto> lista = dao.listarProductos();    // Pide su catálogo entero 
            request.setAttribute("listaProductos", lista);             // Los pega   
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response); // Dibuja la pestaña del menú
        }
    }
    
    /**
     * Subrutina complementaria: ACTUALIZAR PRODUCTO (MODIFICAR)
     * Cuando le dan al botón verde "Actualizar" del formulario
     */
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        try { 
            // Recibe todos los atributos nuevos modificados (Ej: Antes costaba 2mil y ahora cuesta 3mil)
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); 
            String nombre = request.getParameter("nombre"); 
            String marca = request.getParameter("marca"); 
            String tipo = request.getParameter("tipo"); 
            
            // Re-procesar Foto Nueva
            String imagen = null; 
            try {
                Part filePart = request.getPart("imagen"); 
                if (filePart != null && filePart.getSize() > 0) { 
                    imagen = subirImagen(filePart, request); 
                }
            } catch(Exception e) { 
                System.out.println("Error al actualizar imagen: " + e.getMessage()); 
            }
            
            // MAGIA: Si el usuario NO subió ninguna foto nueva, no queremos borrar la anterior.
            if (imagen == null) { 
                ProductoDAO tempDao = new ProductoDAO(); 
                Producto original = tempDao.obtenerProducto(idProducto); // Traemos una copia vieja de este producto
                if (original != null) { 
                    imagen = original.getImagen(); // Extraemos cómo se llamaba su antigua foto vieja para no perderla.
                }
            }
            
            String cantidadMedida = request.getParameter("cantidad_medida"); 
            
            double precio = 0.0; 
            try {
                precio = Double.parseDouble(request.getParameter("precio")); 
            } catch (NumberFormatException e) {
                precio = 0.0; 
            }
            
            java.sql.Date fechaVencimiento = null; 
            try {
                String fechaStr = request.getParameter("fecha_vencimiento"); 
                if (fechaStr != null && !fechaStr.isEmpty()) { 
                    fechaVencimiento = java.sql.Date.valueOf(fechaStr); 
                }
            } catch (IllegalArgumentException e) {
                fechaVencimiento = null; 
            }
            
            // Embolsamos la segunda versión de tu POJO nuevo editado
            Producto p = new Producto(); 
            p.setIdProducto(idProducto);  // ¡Muy importante decirle qué ID actualizar! 
            p.setNombre(nombre); 
            p.setMarca(marca); 
            p.setPrecioUnitario(precio); 
            p.setTipo(tipo); 
            p.setImagen(imagen); 
            p.setFechaVencimiento(fechaVencimiento); 
            p.setCantidadMedida(cantidadMedida); 
            
            // Mandar a Mutar base SQL
            ProductoDAO dao = new ProductoDAO(); 
            boolean exito = dao.actualizarProducto(p);  // Sentencia Update
            
            if (exito) { 
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado");  // Felicidades  
            } else { 
                response.sendRedirect("ProductoServlet?error=ErrorActualizar");    // Mal :c  
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            response.sendRedirect("ProductoServlet?error=" + e.getMessage()); 
        }
    }
}
