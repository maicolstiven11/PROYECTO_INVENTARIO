package com.inventario.controller; // Declaración de espacio de nombres organizativo de artefactos de red 

import com.inventario.dao.PedidoDAO;
import com.inventario.dao.ProductoDAO;
import com.inventario.dao.ProveedorDAO;
import com.inventario.model.DetallePedido;
import com.inventario.model.PedidoProveedor;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
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
 * Controlador Lógico Orquestador de Intermediación: PedidoServlet.
 * 
 * Clase de fachada interceptora (Front Controller MVC).
 * Ejerce la centralización estructural en la capa Servlet, orquestando objetos POJO relacionales complejos 
 * y multi-DAO delegativos (Proveedor, Producto, Pedido y Detalle) para resolver colecciones completas e inyecciones dependientes.
 */
@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"}) // Vinculación decorativa hacia la interfaz HTTP del Servlet Container.
public class PedidoServlet extends HttpServlet { // Subclase especializada de Servlet base para web interactivo.

    /**
     * Sobreescritura del método consultivo HTTP GET.
     * Procesa la interfaz selectiva que secciona dos modos de subrutina: 
     * - Listado Relacional (Visualizar base existente instanciada en memoria).
     * - Disparo Constructor / Preparador (Recoge las relaciones lógicas foráneas Previas y adjuntarlas como variables de contexto al View).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Ampara contra la cadena de excepciones relativas Web MVC .
        
        String action = request.getParameter("action"); // Localizador semántico simple del Request param de control .
        
        if ("listar".equals(action)) { // Inicia filtro controlador de acción en memoria booleana 
            // Acceso relacional transiente en polimorfismo referencial 
            HttpSession session = request.getSession(); // Accesor a la capa envoltura
            Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // Instancia a puntero estático o nulo 
            
            if (idNegocio != null) { // Punto restrictivo de anclaje base .
                PedidoDAO pedidoDAO = new PedidoDAO(); // Carga Orquestador modelo BD.
                List<PedidoProveedor> listaPedidos = pedidoDAO.listarPedidos(idNegocio); // Estructura Array List transiente asimétrica.
                request.setAttribute("listaPedidos", listaPedidos); // Empaque semántico polimórfico al scope buffer de sesión
                request.getRequestDispatcher("view/visualizar_pedidos.jsp").forward(request, response); // Redireccionamiento o Forward abstracto hacia JSP Template.
            } else {
                response.sendRedirect("index.jsp"); // Castigo purificador de enrutamiento 
            }
        } else if (action == null || action.equals("nuevo")) { // Fallback u originador condicionado
            nuevoPedido(request, response);  // Llama método utilitario o helper referenciado interno de la propia subclase
        }
    }

    /**
     * Metodo helper subestructural interno de preparación.
     * Carga y cruza las colecciones independientes Listas dependientes del DAO de Producto y Proveedor 
     * en simultáneo y las inyecta en el objeto Request como colecciones estáticas antes de re-dirigir a su Vista.
     */
    private void nuevoPedido(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { // Firma pasiva
        ProveedorDAO proveedorDAO = new ProveedorDAO(); // Generador lector primario foráneo
        ProductoDAO productoDAO = new ProductoDAO(); // Generador lector sub-foráneo
        
        HttpSession session = request.getSession(); // Lector session scope
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Lector transaccional escalar 
        
        List<Proveedor> proveedores = proveedorDAO.listarProveedores(); // Instanciador Colección List relacional
        // Lector general absoluto sin filtro para permitir inserciones atómicas desde el View de nuevos
        List<Producto> listaProductos = productoDAO.listarProductos(); // Colección general persistente de Modelo "Producto" 
        
        request.setAttribute("listaProveedores", proveedores); // Adjunta Arreglo Tipado
        request.setAttribute("listaProductos", listaProductos); // Adjunta Arreglo relacional Tipado
        request.setAttribute("idInventarioActual", idInventario); // Pasa la variable base numérica atómica 
        
        request.getRequestDispatcher("view/agregar_pedido.jsp").forward(request, response); // Despacha ortogonal o re-direcciona todo al JSP con buffer.
    }

    /**
     * Sobreescritura del método transaccional HTTP POST (Payload inyectivo cruzado multi-tabla).
     * Intercepta el empaquetado del frontend y recrea lógicamente Entidades completas (Modelo Pedido y Detalle) para enviarlas en 
     * una sola transacción atómica doble al gestor DAO, completando y mutando dependencias secundarias en el proceso iterativamente calculadas.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Lanza excepcion asilada o framework
        
        try { // Contenedor protector y vigilador subyacente de integridad en parseo númerico enlazado y null-pointers .
            // =====================================================================
            // INSTANCIACIÓN DE MAPEO DIRECTO ESCALAR Y ALFANUMÉRICO LÓGICO
            // =====================================================================
            int idProveedor = Integer.parseInt(request.getParameter("id_proveedor")); // Casteo iterativo de param String de llave primaria
            int idProducto = Integer.parseInt(request.getParameter("id_producto")); // Idem para la entidad final (Producto referencial asilado)
            
            String fechaPedidoStr = request.getParameter("fecha_pedido"); // Binding temporal Date literal 
            String fechaEntregaStr = request.getParameter("fecha_entrega"); // Binding para cronológico estimado literal 
            
            int cantidad = Integer.parseInt(request.getParameter("cantidad")); // Cast numérico Cardinal primitivo
            double subtotal = Double.parseDouble(request.getParameter("subtotal")); // Mutación numeral Coma Flotante fraccionaria (Double nativo) .
            double iva = Double.parseDouble(request.getParameter("iva")); // Conversión extractiva de impuesto foránea (Aritmética decimal)
            
            // Reestructuracion mutativa en RAM por Ecuacion Financiera
            double total = subtotal + iva; // Fusión escalar de coste neto base .
            double precioUnitario = total / cantidad; // Cálculo del divisor final fraccionario
            
            HttpSession session = request.getSession(); // Restaura memoria temporal HTTP.
            Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Consigue variable delimitadora int de memoria 
            
            if (idInventario == null) { // Blindaje si hay error de pérdida de contexto lógico o sesion vaciada
                response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); // Corta flujo redireccionando a un origen control
                return; // Corta scope.
            } 
            
            // Subrutina auxiliar auto-mágica delegativa a otra DAO. Resuelve ID interno de InventarioDetalle (Inyección Polimórfica In-Time).
            com.inventario.dao.DetalleInventarioDAO detDao = new com.inventario.dao.DetalleInventarioDAO(); // Fabricación dinámica inter-conexion
            int idInvDetalle = detDao.obtenerOCrearDetalle(idInventario, idProducto); // Generación de clave por subrutina delegada
            
            // CONSTRUCCIÓN ASIMÉTRICA DEL POJO ENTIDAD MADRE (PedidoProveedor) 
            PedidoProveedor pedido = new PedidoProveedor(); // Genera atómico instanciado Wrapper nuevo en HEAP
            pedido.setIdProveedor(idProveedor); // Setter relacional clave ID
            pedido.setFechaPedido(Date.valueOf(fechaPedidoStr)); // Transvasa factorizado LocalDate estático
            pedido.setFechaEntrega(Date.valueOf(fechaEntregaStr)); // Constructor de Date en capa modelo setter
            pedido.setSubtotal(subtotal);   // Método modificador a encapsulado interno
            pedido.setIvaPedido(iva);      // Idem
            pedido.setTotalPedido(total); // Setter compuesto deducido
            pedido.setIdInventario(idInventario); // Setter relacion FK de objeto Contextual
            
            // CONSTRUCCIÓN SIMÉTRICA DEL POJO ENTIDAD DEPENDIENTE (DetallePedido) 
            DetallePedido detalle = new DetallePedido(); // Envoltorio secundario
            detalle.setIdInvDetalle(idInvDetalle); // Modificador relacional foráneo a la tabla link subyacente
            detalle.setCantidadPedida(cantidad); // Setter cardinalidad numeral
            detalle.setPrecioUnitarioReal(precioUnitario); // Mutador de factor computado
            
            List<DetallePedido> detalles = new ArrayList<>(); // Inicializador general base Array Dinámico .
            detalles.add(detalle); // Inyector o Additivo hacia el listado Collection.
            
            // ORQUESTACIÓN TRANSACCIONAL ÚNICA DAO (Commit Double Batch)
            PedidoDAO pedidoDAO = new PedidoDAO(); // Gestor de capa BD
            boolean exito = pedidoDAO.registrarPedido(pedido, detalles); // Disparo doble resolutivo que amarra el Booleano final indicando exito logico transaccional completo
            
            if (exito) { // Bifurcacion condicional
                response.sendRedirect("view/pedido_finalizado.html");  // Conclusión asíncrona hacia una interfaz statica de cierre feliz
            } else {
                // Resolución asíncrona reactiva alterna si SQL transaction falló pero atrapó internamente (No Exception)
                response.sendRedirect("PedidoServlet?action=nuevo&msj=error"); // Salida con param
            }
            
        } catch (Exception e) { // Atrapatodo y debug 
            e.printStackTrace(); // Salida sucia
            // Re-armado del buffer con Query Parameter de choque de datos para el UI Error
            response.sendRedirect("PedidoServlet?action=nuevo&msj=error_datos"); // Limpieza controlada
        }
    }
}
