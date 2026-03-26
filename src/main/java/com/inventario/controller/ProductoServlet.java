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
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"}) 
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 100
)
public class ProductoServlet extends HttpServlet { 

    private String subirImagen(Part part, HttpServletRequest request) throws IOException { 
        String fileName = null; 
        if (part != null && part.getSize() > 0) {
            String contentDisp = part.getHeader("content-disposition"); 
            String[] items = contentDisp.split(";");
            for (String s : items) { 
                if (s.trim().startsWith("filename")) { 
                    fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                }
            }
            if (fileName != null && !fileName.isEmpty()) { 
                fileName = System.currentTimeMillis() + "_" + new File(fileName).getName(); 
                
                String uploadPath = request.getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img"; 
                String sourcePath = "c:\\adso\\2994281\\PROYECTO_INVENTARIO\\src\\main\\webapp\\assets\\img"; 
                
                File uploadDir = new File(uploadPath); 
                if (!uploadDir.exists()) uploadDir.mkdirs(); 
                
                File sourceDir = new File(sourcePath); 
                if (!sourceDir.exists()) sourceDir.mkdirs(); 
                
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
        return (fileName != null && !fileName.isEmpty()) ? fileName : null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); 
        if ("actualizar".equals(action)) {
            procesarActualizacion(request, response);
            return; 
        }
        
        String nombre = request.getParameter("nombre");    
        String marca = request.getParameter("marca");
        String tipo = request.getParameter("tipo");
        
        String imagen = null; 
        try {
            Part filePart = request.getPart("imagen"); 
            if (filePart != null && filePart.getSize() > 0) { 
                imagen = subirImagen(filePart, request);
            }
        } catch(Exception e) { 
            System.out.println("Error al procesar la imagen: " + e.getMessage()); 
        }
        
        String cantidadMedida = request.getParameter("cantidad_medida");
        
        double precio = 0.0; 
        try { 
            precio = Double.parseDouble(request.getParameter("precio")); 
        } catch (NumberFormatException e) {
            precio = 0.0;  
        }
        
        Date fechaVencimiento = null; 
        try {
            String fechaStr = request.getParameter("fecha_vencimiento"); 
            if(fechaStr != null && !fechaStr.isEmpty()){ 
                fechaVencimiento = Date.valueOf(fechaStr);  
            }
        } catch (IllegalArgumentException e) { 
            fechaVencimiento = null;  
        }

        Producto p = new Producto(); 
        p.setNombre(nombre); 
        p.setMarca(marca); 
        p.setPrecioUnitario(precio); 
        p.setTipo(tipo); 
        p.setImagen(imagen);
        p.setFechaVencimiento(fechaVencimiento); 
        p.setCantidadMedida(cantidadMedida); 

        ProductoDAO dao = new ProductoDAO(); 
        
        if (dao.existeNombreProducto(nombre)) { 
            response.sendRedirect("view/Registro_produc.html?error=producto_duplicado"); 
            return; 
        }

        try {
            boolean exito = dao.registrarProducto(p);
            if (exito) { 
                response.sendRedirect("view/Produc_registrado.html");
            } else {
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");  
            }
        } catch (Exception e) {
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_"));  
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); 
        ProductoDAO dao = new ProductoDAO(); 
        
        if (action != null && action.equals("eliminar")) { 
            // =====================================================================
            // ELIMINAR: CON VALIDACIÓN DE HISTORIAL
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                
                // VALIDACIÓN: ¿Tiene el producto ventas o pedidos?
                if (dao.productoTieneDatos(id)) {
                    // TIENE DATOS: No se deja borrar
                    response.sendRedirect("ProductoServlet?error=producto_con_datos");
                } else {
                    // ESTÁ LIMPIO: Se puede borrar
                    boolean eliminado = dao.eliminarProducto(id);
                    if (eliminado) {
                        response.sendRedirect("ProductoServlet?msg=eliminado_exito");
                    } else {
                        response.sendRedirect("ProductoServlet?error=ErrorEliminar");
                    }
                }
                return;
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorProcesarEliminar"); 
            }
        } else if (action != null && action.equals("editar")) { 
            try {
                int id = Integer.parseInt(request.getParameter("id")); 
                Producto p = dao.obtenerProducto(id);
                
                if (p != null) { 
                    request.setAttribute("productoEditar", p);
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response);
                } else {
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado"); 
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion"); 
            }
        } else { 
            // =====================================================================
            // LISTAR: COMPORTAMIENTO POR DEFECTO
            // =====================================================================
            HttpSession session = request.getSession();
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual");
            
            if (idNegocio == null) idNegocio = 0;
            
            java.util.List<Producto> lista = dao.listarProductos(idNegocio);
            request.setAttribute("listaProductos", lista);
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response);
        }
    }
    
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        try { 
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); 
            String nombre = request.getParameter("nombre"); 
            String marca = request.getParameter("marca"); 
            String tipo = request.getParameter("tipo"); 
            
            String imagen = null; 
            try {
                Part filePart = request.getPart("imagen"); 
                if (filePart != null && filePart.getSize() > 0) { 
                    imagen = subirImagen(filePart, request); 
                }
            } catch(Exception e) { 
                System.out.println("Error al actualizar imagen: " + e.getMessage()); 
            }
            
            if (imagen == null) { 
                ProductoDAO tempDao = new ProductoDAO(); 
                Producto original = tempDao.obtenerProducto(idProducto);
                if (original != null) { 
                    imagen = original.getImagen();
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
            
            Producto p = new Producto(); 
            p.setIdProducto(idProducto);
            p.setNombre(nombre); 
            p.setMarca(marca); 
            p.setPrecioUnitario(precio); 
            p.setTipo(tipo); 
            p.setImagen(imagen); 
            p.setFechaVencimiento(fechaVencimiento); 
            p.setCantidadMedida(cantidadMedida); 
            
            ProductoDAO dao = new ProductoDAO(); 
            boolean exito = dao.actualizarProducto(p);
            
            if (exito) { 
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado");
            } else { 
                response.sendRedirect("ProductoServlet?error=ErrorActualizar");
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            response.sendRedirect("ProductoServlet?error=" + e.getMessage()); 
        }
    }
}
