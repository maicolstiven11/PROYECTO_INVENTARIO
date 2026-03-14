package com.inventario.controller;

import com.inventario.dao.InventarioDAO;
import com.inventario.dao.ProductoDAO;
import com.inventario.dao.DetalleInventarioDAO;
import com.inventario.model.Producto;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CONTROLADOR: Servlet encargado de gestionar el Inventario.
 * 
 * Implementa: RF-13 (Iniciar Inventario), RF-14 (Entrar a Inventario Existente), RF-15 (Cargar Stock Inicial)
 * Cumple: RNF-02 (Protección SQL Injection - delega en DAO con PreparedStatement)
 *         RNF-03 (Gestión de Sesiones - guarda idInventarioActual e idNegocioActual en sesión)
 *         RNF-08 (Mensajes de Error - redirige con parámetros de error descriptivos)
 *         RNF-13 (Arquitectura MVC - Capa Controlador)
 */
@WebServlet(name = "InventarioServlet", urlPatterns = {"/InventarioServlet"})
public class InventarioServlet extends HttpServlet {

    /**
     * Método doGet - Maneja todas las acciones de inventario según el parámetro "action":
     * - "iniciar"         → RF-13: Crear nuevo inventario
     * - "entrar"          → RF-14: Acceder a inventario existente activo
     * - "cargar_detalle"  → RF-15: Mostrar formulario de stock inicial
     * - "guardar_stock"   → RF-15: Guardar cantidades de stock inicial
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("InventarioServlet: action=" + action); // Log de depuración
        
        if ("iniciar".equals(action)) {
            // Validación de Rol: Los trabajadores NO pueden iniciar inventarios
            com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado");
            if (usuario == null || usuario.getIdRol() == 2) {
                response.sendRedirect("view/Menu_sistema.jsp?error=AccesoDenegado");
                return;
            }

            // =====================================================================
            // RF-13: INICIAR NUEVO INVENTARIO
            // Crea un registro en la tabla INVENTARIO con estado activo.
            // RF-13 Restricción 1: Solo se permite un inventario activo por negocio.
            // =====================================================================
            String idNegocioStr = request.getParameter("idNegocio"); // RF-13: ID del bar seleccionado
            String tipoControl = request.getParameter("tipo");       // RF-13: Tipo de control del inventario
            String fechaStr = request.getParameter("fecha");         // RF-13: Fecha de inicio
            
            System.out.println("InventarioServlet: idNegocio=" + idNegocioStr + ", tipo=" + tipoControl + ", fecha=" + fechaStr);
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) {
                try {
                     int idNegocio = Integer.parseInt(idNegocioStr);
                    java.sql.Date fechaInicio = null;
                    
                    // RF-13: Si no se proporciona fecha, usar la fecha actual del sistema
                    if (fechaStr != null && !fechaStr.isEmpty()) {
                        fechaInicio = java.sql.Date.valueOf(fechaStr);
                    } else {
                        fechaInicio = new java.sql.Date(System.currentTimeMillis()); // RF-13: Fecha actual por defecto
                    }
                    
                    // RF-13: Llamar al DAO para crear el inventario en la BD
                    InventarioDAO dao = new InventarioDAO();
                    int idInventario = dao.iniciarInventario(idNegocio, tipoControl, fechaInicio); // RF-13: INSERT en tabla INVENTARIO
                    
                    System.out.println("InventarioServlet: idInventario generado=" + idInventario);
                    
                    if (idInventario > 0) {
                        // RF-13: Guardar el ID del nuevo inventario en la sesión
                        // RNF-03: Estos atributos de sesión son usados por VentaServlet, GastoServlet y PedidoServlet
                        request.getSession().setAttribute("idInventarioActual", idInventario);  // RF-13: Para que otros módulos lo usen
                        request.getSession().setAttribute("idNegocioActual", idNegocio);        // RF-13: Para filtrar datos por negocio
                        // RF-15: Redirigir a cargar stock inicial
                        response.sendRedirect("InventarioServlet?action=cargar_detalle");
                    } else {
                        // RNF-08: Error al crear inventario
                        System.out.println("InventarioServlet: DAO devolvió -1, algo falló");
                        response.sendRedirect("NegocioServlet?error=FalloInicioInventario");
                    }
                } catch (Exception e) {
                    System.out.println("InventarioServlet ERROR: " + e.getMessage());
                    e.printStackTrace();
                    // RNF-08: Redirigir con error descriptivo
                    response.sendRedirect("NegocioServlet?error=" + e.getMessage());
                }
            } else {
                // RNF-08: No se proporcionó ID de negocio
                response.sendRedirect("NegocioServlet?error=SinIdNegocio");
            }
        } else if ("entrar".equals(action)) {
            // =====================================================================
            // RF-14: ENTRAR A INVENTARIO EXISTENTE
            // Busca si existe un inventario activo para el negocio seleccionado.
            // RF-14 Restricción 1: Solo se puede entrar si existe un inventario activo.
            // =====================================================================
            String idNegocioStr = request.getParameter("idNegocio");
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) {
                int idNegocio = Integer.parseInt(idNegocioStr);
                InventarioDAO dao = new InventarioDAO();
                com.inventario.model.Inventario inv = dao.obtenerInventarioActivo(idNegocio); // RF-14: Buscar inventario activo
                
                if (inv != null) {
                    // RF-14: Cargar el ID del inventario en la sesión
                    // RNF-03: Estos atributos permiten operar (ventas, pedidos, gastos) dentro del inventario
                    request.getSession().setAttribute("idInventarioActual", inv.getIdInventario()); // RF-14: ID del inventario en sesión
                    request.getSession().setAttribute("idNegocioActual", idNegocio);                // RF-14: ID del negocio en sesión
                    response.sendRedirect("view/menu_inventario.jsp");                              // RF-14: Ir al menú del inventario
                } else {
                    // RF-14 Restricción 1: No hay inventario activo para este negocio
                    com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado");
                    if (usuario != null && usuario.getIdRol() == 2) {
                        response.sendRedirect("view/Menu_sistema.jsp?error=NoInventarioActivoTrabajador");
                    } else {
                        response.sendRedirect("NegocioServlet?error=NoInventarioActivo");
                    }
                }
            }
        } else if ("cargar_detalle".equals(action)) {
            // =====================================================================
            // RF-15: CARGAR LISTA DE PRODUCTOS PARA STOCK INICIAL
            // Muestra la lista de productos disponibles para que el usuario
            // ingrese las cantidades iniciales de stock.
            // RF-15 Restricción 1: Se registra en DETALLE_INVENTARIO.
            // =====================================================================
            ProductoDAO prodDao = new ProductoDAO();
            List<Producto> listaProductos = prodDao.listarProductos();    // RF-10, RF-15: Listar productos disponibles
            
            request.setAttribute("listaProductos", listaProductos);      // Pasar al JSP
            request.getRequestDispatcher("view/inventario_detalle.jsp").forward(request, response); // RF-15: Mostrar formulario de stock
            
        } else if ("guardar_stock".equals(action)) {
            // =====================================================================
            // RF-15: GUARDAR CANTIDADES DE STOCK INICIAL
            // Procesa las cantidades ingresadas por el usuario y las guarda
            // en la tabla DETALLE_INVENTARIO vinculando producto con inventario.
            // =====================================================================
            try {
                // RNF-03: Obtener el inventario actual de la sesión
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual");
                
                if (idInventario != null) {
                    // RF-15: Obtener arreglos de IDs de producto y sus cantidades
                    String[] idProductosStr = request.getParameterValues("id_producto");
                    String[] cantidadesStr = request.getParameterValues("cantidad");
                    
                    if (idProductosStr != null && cantidadesStr != null) {
                        DetalleInventarioDAO detalleDao = new DetalleInventarioDAO();
                        
                        for (int i = 0; i < idProductosStr.length; i++) {
                            int idProd = Integer.parseInt(idProductosStr[i]);
                            // RF-15: Si el campo está vacío, asumir 0
                            double cant = 0;
                            if (cantidadesStr[i] != null && !cantidadesStr[i].isEmpty()) {
                                cant = Double.parseDouble(cantidadesStr[i]);
                            }
                            
                            // RF-15: Guardar cada registro en la tabla INVENTARIO_DETALLE
                            detalleDao.insertarDetalle(idInventario, idProd, cant);
                        }
                    }
                    
                    // RF-15: Redirigir al menú principal del inventario tras éxito
                    response.sendRedirect("view/menu_inventario.jsp");
                } else {
                    // RNF-08: No hay inventario en sesión
                    response.sendRedirect("NegocioServlet?error=SesionInventarioInvalida");
                }
                
            } catch (Exception e) {
                 e.printStackTrace();
                 // RNF-08: Error al guardar stock
                 response.sendRedirect("NegocioServlet?error=ErrorGuardarStock");
            }
            
        } else if ("cargar_cierre".equals(action)) {
            // =====================================================================
            // CARGAR VISTA DE CIERRE DE INVENTARIO
            // Lista los productos registrados en el inventario actual para que el
            // usuario ingrese la cantidad física final.
            // =====================================================================
            try {
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual");
                if (idInventario != null) {
                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO();
                    List<com.inventario.model.DetalleInventario> detalles = detalleDao.listarDetalles(idInventario);
                    
                    request.setAttribute("listaDetalles", detalles);
                    request.getRequestDispatcher("view/inventario_cierre.jsp").forward(request, response);
                } else {
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("NegocioServlet?error=ErrorCargandoCierre");
            }
            
        } else if ("finalizar_inventario".equals(action)) {
            // =====================================================================
            // FINALIZAR INVENTARIO (CIERRE PERIODO)
            // Guarda las cantidades finales y cambia el estado del inventario.
            // =====================================================================
            try {
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual");
                if (idInventario != null) {
                    // VALIDACIÓN DE TIEMPO (Semanal/Mensual)
                    InventarioDAO invDao = new InventarioDAO();
                    Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual");
                    com.inventario.model.Inventario invActual = invDao.obtenerInventarioActivo(idNegocio);
                    
                    if (invActual != null) {
                        long msActual = System.currentTimeMillis();
                        long msInicio = invActual.getFechaInicio().getTime();
                        long diffMs = msActual - msInicio;
                        long diffDias = diffMs / (1000 * 60 * 60 * 24);
                        
                        String tipo = invActual.getTipoControl();
                        boolean puedeCerrar = true;
                        
                        if ("semanal".equalsIgnoreCase(tipo) && diffDias >= 7) puedeCerrar = true;
                        else if ("mensual".equalsIgnoreCase(tipo) && diffDias >= 30) puedeCerrar = true;
                        else if (!"semanal".equalsIgnoreCase(tipo) && !"mensual".equalsIgnoreCase(tipo)) puedeCerrar = true; // Por si hay otro tipo

                        if (!puedeCerrar) {
                            String msg = "No puede cerrar el inventario " + tipo + " aun. Faltan dias (Lleva: " + diffDias + ")";
                            response.sendRedirect("InventarioServlet?action=cargar_cierre&error_tiempo=" + java.net.URLEncoder.encode(msg, "UTF-8"));
                            return;
                        }
                    }

                    String[] idProductosStr = request.getParameterValues("id_producto");
                    String[] cantidadesFinalesStr = request.getParameterValues("cantidad_final");
                    
                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO();
                    if (idProductosStr != null && cantidadesFinalesStr != null) {
                        for (int i = 0; i < idProductosStr.length; i++) {
                            int idProd = Integer.parseInt(idProductosStr[i]);
                            double cantFinal = 0;
                            if (cantidadesFinalesStr[i] != null && !cantidadesFinalesStr[i].isEmpty()) {
                                cantFinal = Double.parseDouble(cantidadesFinalesStr[i]);
                            }
                            detalleDao.actualizarCantidadFinal(idInventario, idProd, cantFinal);
                        }
                    }
                    
                    // Cambiar estado del inventario a 'finalizado' (negocio sigue activo)
                    // NOTA: Se valida si efectivamente se actualizó en la BD
                    boolean cerrado = invDao.finalizarInventario(idInventario);
                    
                    if (cerrado) {
                        // Limpiar sesión para forzar inicio de nuevo periodo
                        request.getSession().removeAttribute("idInventarioActual");
                        
                        // Cargar los detalles actualizados con precios para el reporte de descuadre
                        java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario);
                        request.setAttribute("listaDescuadre", detallesFinales);
                        request.setAttribute("mensajeExito", "¡Inventario cerrado y guardado correctamente!");
                        request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("NegocioServlet?error=ErrorGuardandoBD");
                    }
                } else {
                    response.sendRedirect("NegocioServlet?error=NoSePudoCerrar");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("NegocioServlet?error=ErrorAlFinalizar");
            }
            
        } else {
            // Acción no reconocida, redirigir a lista de negocios
            response.sendRedirect("NegocioServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Redirige POST al mismo procesamiento que GET
    }
}
