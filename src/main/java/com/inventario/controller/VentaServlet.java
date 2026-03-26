package com.inventario.controller;

import com.inventario.dao.ProductoDAO;
import com.inventario.dao.VentaDAO;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador VentaServlet.
 * 
 * Es la caja registradora del sistema. Maneja un "carrito" virtual temporal 
 * mientras el usuario decide qué facturar, permite agregar o quitar productos,
 * y luego consolida y guarda la venta final a la BD.
 */
@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"}) 
public class VentaServlet extends HttpServlet { 

    /**
     * El doGet y el doPost apuntan a la misma función "processRequest" 
     * porque como es un carrito de compras, recibe clics rápidos (Get) y botones de form (Post).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        processRequest(request, response);  
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        processRequest(request, response);  
    }

    /**
     * La función central. Es un menú o "Switch" gigante 
     * que según el botón que oprimas, hace una acción del carrito diferente.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Lee la acción (Agregar, Quitar...)
        if (action == null) action = "mostrar";  // Si recargas la página, por defecto solo muestra el carrito
        
        HttpSession session = request.getSession(); 
        
        // =====================================================================
        // BOLSILLO MÁGICO DEL CARRITO: Lo guardamos en su Sesión para no perderlo
        // =====================================================================
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito"); 
        if (carrito == null) { // Si su carrito estaba vacío o no existía al loguearse...
            carrito = new ArrayList<>();                // Le regalamos una canasta de compras nueva y vacía
            session.setAttribute("carrito", carrito);   // Se la pegamos a su sesión de forma permanente
        }
        
        // Atrapamos su turno de facturación (ID de Inventario Activo para sumar las facturas al mes correcto)
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); 
        
        switch (action) { 
            case "mostrar":
                // 1. DIBUJAR PANTALLA CARRO: 
                cargarProductos(request);                                       // Trae toda la lista de empanadas/cervezas disponibles a la vista
                double totalActual = calcularTotal(carrito);                    // Suma el precio de su carro
                request.setAttribute("totalVenta", totalActual);                // Asigna total a la pantalla
                request.getRequestDispatcher("view/agregar_venta.jsp").forward(request, response); // Mostrar HTML Formulario Caja
                break;
                
            case "agregar":
                // 2. METER UN PRODUCTO CON SU CANTIDAD AL CARRITO
                agregarProducto(request, session, carrito); 
                response.sendRedirect("VentaServlet?action=mostrar");           // Recarga la página
                break;
                
            case "quitar":
                // 3. QUITAR UN REGLÓN (Me equivoqué marcando 10 papas)
                quitarProducto(request, carrito); 
                response.sendRedirect("VentaServlet?action=mostrar"); 
                break;
                
            case "finalizar":
                // 4. ¡IMPRIMIR FACTURA Y COBRAR!: Guardar todo el carrito en BD
                if (idInventario == null) { // Error, la caja está cerrada, no hay Inventario mensual
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); 
                    return; 
                }
                finalizarVenta(session, carrito, idInventario, response); 
                break;
                
            case "listar":
                // 5. HISTORIAL DE RECIBOS DE ESTE BAR
                Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); 
                if (idNegocio != null) { 
                    VentaDAO vDao = new VentaDAO(); 
                    List<Venta> listaVentas = vDao.listarVentas(idNegocio);    // Extrae todas las ventas exitosas
                    request.setAttribute("listaVentas", listaVentas);          
                    request.getRequestDispatcher("view/visualizar_ventas.jsp").forward(request, response); 
                } else { 
                    response.sendRedirect("index.jsp");                        
                }
                break;
                
            case "ver_detalle":
                // 6. MIRAR DETALLE ÍNTIMO DE UNA FACTURA EN ESPECÍFICO (Qué me cobraron adentro del total)
                try {
                    int idVenta = Integer.parseInt(request.getParameter("id_venta")); 
                    VentaDAO vDaoDet = new VentaDAO(); 
                    List<DetalleVenta> listaDetalles = vDaoDet.listarDetalleVenta(idVenta); // Desglosa hijos
                    request.setAttribute("listaDetalles", listaDetalles); 
                    request.setAttribute("idVenta", idVenta); 
                    request.getRequestDispatcher("view/detalle_venta.jsp").forward(request, response); 
                } catch (Exception e) { 
                    response.sendRedirect("VentaServlet?action=listar&error=ErrorAlVerDetalle"); 
                }
                break;
                
            case "cancelar":
                // 7. BOTAR LA CANASTA (Vaciar Carrito en rojo)
                session.removeAttribute("carrito");                             
                response.sendRedirect("view/menu_inventario.html"); // Volver a escritorio principal sin factura
                break;
                
            default: 
                response.sendRedirect("view/menu_inventario.html");
        }
    }

    /**
     * Subrutina auxiliar. Trae el catálogo del kiosko 
     * para que el cajero pueda seleccionarlo en una etiqueta HTML select.
     */
    private void cargarProductos(HttpServletRequest request) { 
        ProductoDAO pDao = new ProductoDAO(); 
        Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual");
        if (idNegocio == null) idNegocio = 0;
        List<Producto> lista = pDao.listarProductos(idNegocio);   
        request.setAttribute("listaProductos", lista); 
    }

    /**
     * Función que maneja la lógica de inyectar papas al carrito vivo y evalua Stock Restante.
     */
    private void agregarProducto(HttpServletRequest request, HttpSession session, List<DetalleVenta> carrito) { 
        try {  
            int idProd = Integer.parseInt(request.getParameter("id_producto")); // Id Papa Limón 
            int cantidad = Integer.parseInt(request.getParameter("cantidad")); // Quiero 5 paquetes
            
            if (cantidad <= 0) return;  // Si tipeó -5, se devuelve.
            
            ProductoDAO pDao = new ProductoDAO(); 
            Producto p = pDao.obtenerProducto(idProd); // Buscamos toda la info (Precio e Imagen)
            
            if (p != null) { 
                
                // VALIDACIÓN CRÍTICA MATEMÁTICA: ¿SÍ TENEMOS 5 PAPAS PARA VENDER?
                com.inventario.dao.DetalleInventarioDAO detDao = new com.inventario.dao.DetalleInventarioDAO(); 
                Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); 
                double stockDisponible = detDao.obtenerStockActual(idInventario, idProd); 
                
                // Validar cuántas papas YA ECHÓ a esta misma canasta
                int cantidadEnCarrito = 0; 
                for (DetalleVenta d : carrito) { 
                    if (d.getIdProducto() == idProd) { 
                        cantidadEnCarrito = d.getCantidad(); 
                        break; 
                    }
                }
                
                // Si la suma de las 5 de ahora + las que ya tenía rebasa el sobrante, lo escupe con un mensajito rojo.
                if (cantidad + cantidadEnCarrito > stockDisponible) { 
                    session.setAttribute("error_stock", "Stock insuficiente: " + p.getNombre() + " (Disponible: " + (int)stockDisponible + ")"); 
                    return; 
                }

                // SI PASÓ EL FILTRO DE ESTANTERÍAS FÍSICAS:
                boolean existe = false; 
                // A) Si el producto ya existía en la canasta (Le suma encima en esa misma fila)
                for (DetalleVenta d : carrito) { 
                    if (d.getIdProducto() == idProd) { 
                        d.setCantidad(d.getCantidad() + cantidad);                 // Sumar
                        d.setSubtotal(d.getCantidad() * p.getPrecioUnitario());    // Multiplico Precio total
                        existe = true; 
                        break; 
                    }
                }
                
                // B) Si el producto es nuevo en el carro, crea un renglón virgen 
                if (!existe) { 
                    DetalleVenta det = new DetalleVenta(); 
                    det.setIdProducto(idProd); 
                    det.setNombreProducto(p.getNombre());                          
                    det.setCantidad(cantidad); 
                    det.setSubtotal(cantidad * p.getPrecioUnitario());             
                    carrito.add(det); // Al cajón
                }
            }
        } catch (NumberFormatException e) { 
            e.printStackTrace(); 
        }
    }
    
    /**
     * Romper la canasta un objeto según su índice (Remover renglón específico)
     */
    private void quitarProducto(HttpServletRequest request, List<DetalleVenta> carrito) { 
        try { 
            int index = Integer.parseInt(request.getParameter("index")); // Sacar el ID posicional (Ej: Elimina renglón Num 2) 
            if (index >= 0 && index < carrito.size()) {                  
                carrito.remove(index);                                   // Cortar!
            }
        } catch (NumberFormatException e) {} 
    }

    /**
     * Matemáticas de calculadora simple, sumando todos los parciales de la Tira Factura.
     */
    private double calcularTotal(List<DetalleVenta> carrito) { 
        double total = 0; 
        for (DetalleVenta d : carrito) { 
            total += d.getSubtotal(); // Va sumándole al cajón cada renglón
        }
        return total; 
    }
    
    /**
     * Módulo Final: Venderlo oficialmente a Bases relacionales SQL
     */
    private void finalizarVenta(HttpSession session, List<DetalleVenta> carrito, int idInventario, HttpServletResponse response) throws IOException { 
        
        if (carrito.isEmpty()) { // Nadie factura aire 
            response.sendRedirect("VentaServlet?action=mostrar&error=CarritoVacio"); 
            return; 
        }
        
        // Cúspide: Crea el papel Venta de Factura grande
        VentaDAO vDao = new VentaDAO(); 
        Venta venta = new Venta(); 
        venta.setIdInventario(idInventario);                           
        venta.setFechaVenta(new Date(System.currentTimeMillis()));     
        venta.setTotalVenta(calcularTotal(carrito));                    
        
        // ¡Se guarda en el DAO de las transacciones atómicas dobles!
        boolean resultado = vDao.registrarVenta(venta, carrito); 
        
        if (resultado) { 
            // Felicidades, el cajero cobró y el cliente se llevó la cerveza.
            session.removeAttribute("carrito"); // Borramos su carrito para la próxima persona
            response.sendRedirect("view/venta_finalizada.html"); // Ventana de ¡Éxito de caja!
        } else { 
            response.sendRedirect("VentaServlet?action=mostrar&error=ErrorAlGuardar"); // Base error
        }
    }
}
