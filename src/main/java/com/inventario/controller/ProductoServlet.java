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

/**
 * CONTROLADOR: Servlet encargado de gestionar los Productos.
 * 
 * Implementa: RF-09 (Registrar Producto), RF-10 (Listar Productos), RF-11 (Editar Producto), RF-12 (Eliminar Producto)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-08 (Mensajes de Error - redirige con parámetros de error descriptivos)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    /**
     * RF-09, RF-11: Método doPost - Registra un nuevo producto o actualiza uno existente.
     * Si recibe action=actualizar, ejecuta la actualización (RF-11).
     * Si no recibe action, ejecuta el registro de nuevo producto (RF-09).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // RF-11: Verificar si es una actualización de producto existente
        String action = request.getParameter("action");
        if ("actualizar".equals(action)) {
            procesarActualizacion(request, response);  // RF-11: Delega al método de actualización
            return;
        }
        
        // =====================================================================
        // RF-09: REGISTRAR NUEVO PRODUCTO
        // =====================================================================
        
        // RF-09 PASO 1: Recibir datos del formulario
        // RF-30: Los campos obligatorios se validan con "required" en el HTML
        String nombre = request.getParameter("nombre");              // RF-09: Nombre (obligatorio)
        String marca = request.getParameter("marca");                // RF-09: Marca (obligatorio)
        String tipo = request.getParameter("tipo");                  // RF-09: Tipo/categoría (obligatorio)
        String imagen = request.getParameter("imagen");              // RF-09: Ruta de la imagen
        String cantidadMedida = request.getParameter("cantidad_medida"); // RF-09: Cantidad/medida (ej: "1 Litro")
        
        // RF-09 Restricción 1: El precio debe ser numérico positivo
        // RF-09 Restricción 2: Si no se proporciona precio válido, se establece en 0.0
        double precio = 0.0;
        try {
            precio = Double.parseDouble(request.getParameter("precio"));
        } catch (NumberFormatException e) {
            precio = 0.0;  // RF-09 Restricción 2: Valor por defecto si el precio no es válido
        }
        
        // RF-09 Restricción 3: La fecha de vencimiento es opcional y debe tener formato válido
        Date fechaVencimiento = null;
        try {
            String fechaStr = request.getParameter("fecha_vencimiento");
            if(fechaStr != null && !fechaStr.isEmpty()){
                fechaVencimiento = Date.valueOf(fechaStr);  // Convierte String "YYYY-MM-DD" a objeto Date
            }
        } catch (IllegalArgumentException e) {
            fechaVencimiento = null;  // RF-09 Restricción 3: Si el formato es inválido, se establece en null
        }

        // RF-09 PASO 2: Crear objeto Modelo (Producto) y llenarlo con los datos
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setMarca(marca);
        p.setPrecioUnitario(precio);
        p.setTipo(tipo);
        p.setImagen(imagen);
        p.setFechaVencimiento(fechaVencimiento);
        p.setCantidadMedida(cantidadMedida);

        // RF-09 PASO 3: Llamar al DAO para guardar en la BD
        ProductoDAO dao = new ProductoDAO();
        
        try {
            boolean exito = dao.registrarProducto(p);  // RF-09: Inserta el producto en la tabla PRODUCTO
            if (exito) {
                response.sendRedirect("view/Produc_registrado.html");  // RF-09: Redirigir a confirmación
            } else {
                // RNF-08: Redirigir con error descriptivo
                response.sendRedirect("view/Registro_produc.html?error=NoSeGuardoEnBD");
            }
        } catch (Exception e) {
            // RNF-08: Redirigir con mensaje de excepción
             response.sendRedirect("view/Registro_produc.html?error=" + e.getMessage().replace(" ", "_"));
        }
    }

    /**
     * RF-10, RF-11, RF-12: Método doGet - Lista, edita o elimina productos.
     * action=eliminar → RF-12: Eliminar producto por ID
     * action=editar   → RF-11: Cargar datos del producto para formulario de edición
     * sin action      → RF-10: Listar todos los productos
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        ProductoDAO dao = new ProductoDAO();
        
        if (action != null && action.equals("eliminar")) {
            // =====================================================================
            // RF-12: ELIMINAR PRODUCTO POR ID
            // Se ejecuta cuando la URL contiene ?action=eliminar&id=X
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id"));  // RF-12: Obtener ID del producto
                boolean eliminado = dao.eliminarProducto(id);           // RF-12: Eliminar de la BD
                response.sendRedirect("ProductoServlet");               // RF-12: Redirigir a la lista actualizada
            } catch (Exception e) {
                e.printStackTrace();
                // RNF-08: Redirigir con error
                response.sendRedirect("ProductoServlet?error=ErrorEliminar");
            }
        } else if (action != null && action.equals("editar")) {
            // =====================================================================
            // RF-11: CARGAR DATOS DEL PRODUCTO PARA EDICIÓN
            // Obtiene el producto por ID y lo envía al formulario de edición (JSP).
            // RF-11 Restricción 2: El producto debe existir en la BD.
            // =====================================================================
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Producto p = dao.obtenerProducto(id);                   // RF-11: Busca el producto en la BD por ID
                
                if (p != null) {
                    request.setAttribute("productoEditar", p);          // RF-11: Pasa el objeto Producto al JSP
                    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(request, response);
                } else {
                    // RNF-08: Producto no encontrado
                    response.sendRedirect("ProductoServlet?error=ProductoNoEncontrado");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("ProductoServlet?error=ErrorCargarEdicion");
            }
        } else {
            // =====================================================================
            // RF-10: LISTAR TODOS LOS PRODUCTOS (Comportamiento por defecto del GET)
            // RF-10 Restricción 1: La lista se carga mediante ProductoDAO.listarProductos()
            // RF-10 Restricción 2: Se muestra en la vista editar_productos.jsp
            // =====================================================================
            java.util.List<Producto> lista = dao.listarProductos();    // RF-10: Obtiene todos los productos de la BD
            request.setAttribute("listaProductos", lista);             // RF-10: Pasa la lista al JSP
            request.getRequestDispatcher("view/editar_productos.jsp").forward(request, response); // RF-10: Muestra la vista
        }
    }
    
    /**
     * RF-11: Método privado que procesa la actualización de un producto existente.
     * Lee los datos del formulario de edición, crea el objeto Producto actualizado
     * y llama al DAO para hacer el UPDATE en la BD.
     * RF-11 Restricción 1: El id_producto no es modificable (se recibe como campo oculto).
     */
    private void procesarActualizacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // RF-11: Recibir todos los datos del formulario de edición
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); // RF-11 Restricción 1: ID no modificable
            String nombre = request.getParameter("nombre");
            String marca = request.getParameter("marca");
            String tipo = request.getParameter("tipo");
            String imagen = request.getParameter("imagen");
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
            
            // RF-11: Crear objeto Producto con los datos actualizados
            Producto p = new Producto();
            p.setIdProducto(idProducto);  // RF-11 Restricción 1: Mantener el ID original
            p.setNombre(nombre);
            p.setMarca(marca);
            p.setPrecioUnitario(precio);
            p.setTipo(tipo);
            p.setImagen(imagen);
            p.setFechaVencimiento(fechaVencimiento);
            p.setCantidadMedida(cantidadMedida);
            
            // RF-11: Llamar al DAO para hacer UPDATE en la BD
            ProductoDAO dao = new ProductoDAO();
            boolean exito = dao.actualizarProducto(p);  // RF-11: Ejecuta UPDATE en la tabla PRODUCTO
            
            if (exito) {
                response.sendRedirect("ProductoServlet?msg=ProductoActualizado");  // RF-11: Éxito
            } else {
                response.sendRedirect("ProductoServlet?error=ErrorActualizar");    // RNF-08: Error
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ProductoServlet?error=" + e.getMessage());
        }
    }
}
