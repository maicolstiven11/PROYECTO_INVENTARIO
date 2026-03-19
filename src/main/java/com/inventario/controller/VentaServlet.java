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
 * Controlador Transaccional Orquestador: VentaServlet.
 * 
 * Fachada encapsuladora que extiende abstracción HttpServlet. Controla y dirige el ciclo de vida mutativo (CRUD dinámico in-memory param HTTP Session) del carrito de ventas y persiste el POJO contenedor al sistema Gestor relacional de Base de Datos apoyándose en su delegativo Data Access Object.
 */
@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"}) // Atributo decorativo enrutador instanciando la clase subyacente al Servlet container JVM
public class VentaServlet extends HttpServlet { // Polimorfismo hereditario del Framework Java EE base asíncrono.

    /**
     * Rescritura Genérica evaluadora HTTP Get.
     * Funciona como método de passthrough encadenando peticiones directas de URL (Query string parameters) al procesador central de Orquestación o switch.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Capturador delegativo .
        processRequest(request, response);  // Invoca Sub-rutina asilada delegando scope del contexto buffer HTTP actual vivo.
    }

    /**
     * Rescritura Genérica evaluadora HTTP Post.
     * Funciona como método de passthrough encadenando peticiones de Payload asíncronos FormData.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Amparo catch frame object
        processRequest(request, response);  // Delega in-memory a método central unificado encapsulado .
    }

    /**
     * Algoritmo Principal de Orquestación y Enrutamiento Funcional (Router).
     * Subrutina central que actúa como Switch Statement sobre la variable flag asimétrica (Action). Analiza el param String y decide en base a flujos condicionales de tipo Árbol de Ejecución el método a invocar, manipulando las Interacciones asíncronas de in-memory object List Arrays en Sesión viva.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Frame buffer error catch .
        
        String action = request.getParameter("action"); // Capturador de metadato param evaluativo lógico restrictivo o Flag .
        if (action == null) action = "mostrar";  // Inyección protectora Null fall-back (Default switch loop behavior)
        
        HttpSession session = request.getSession(); // Getter constructor asilando Factory Pattern u originando Object Container HTTP Scope Persistence State Session Vivo de framework .
        
        // =====================================================================
        // ALGORITMO ACUMULADOR MEMORIA VIVA (Instanciación Carrito Dinámico Buffer Session Object Arrays)
        // =====================================================================
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito"); // Recuperador In Memory Pointer Cast Object .
        if (carrito == null) { // Validador limitante Boolean Array Size Initialization Checker .
            carrito = new ArrayList<>();                // Invoca constructor vacío Array POJO Object list memory heap allocation factoría de colecciones (Estructura dinámica .
            session.setAttribute("carrito", carrito);   // Binding atómico Inyectando referencial pointer array al Pool Singleton user JVM context session persistente .
        }
        
        // Extracción In Memory flag activo id de contexto relacional inventario 
        Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Cast referencial object wrapper primitivo PK base scope Vivo Session.
        
        switch (action) { // Ciclo comparador literal Iterativo switch boolean param flag strings .
            case "mostrar":
                // Algoritmo de inicialización base y acumulación matemática sumatoría Front UI View.
                cargarProductos(request);                                       // Disparador a helper subrutina para bind de POJO arr lists general
                double totalActual = calcularTotal(carrito);                    // Función matemática constructora getter total param.
                request.setAttribute("totalVenta", totalActual);               // Passthrough Setter In memory de motor View renderizador 
                request.getRequestDispatcher("view/agregar_venta.jsp").forward(request, response); // Despachador asíncrono o forward local .
                break;
                
            case "agregar":
                // Inyector lógico mutativo transitorio en in-memory arrays model details.
                agregarProducto(request, session, carrito); // Helper Passthrough encapsulando param context framework variables e Instancias Arrays locales.
                response.sendRedirect("VentaServlet?action=mostrar");           // Redirect Asíncrono Refresh Buffer HTTP ciclo Request.
                break;
                
            case "quitar":
                // Destructor lógico remove() en array Collection Collection utils memory target
                quitarProducto(request, carrito); // Helpers encapsulados POO destructors.
                response.sendRedirect("VentaServlet?action=mostrar"); // Clear UI UI render update redirect web param framework.
                break;
                
            case "finalizar":
                // Disparo Atómico delegador Final Persistencia In Memoriam arrays object to Database DAO transactions.
                if (idInventario == null) { // Guard checking param flag FK .
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); // Clean log UI redirect fail abort.
                    return; // Nullifies logic flow stack .
                }
                finalizarVenta(session, carrito, idInventario, response); // Helper Method Subrutina Construct wrapper model .
                break;
                
            case "listar":
                // Gestor visual iterador pasivo arrays Object Models DAO getters arrays list collection 
                Integer idNegocio = (Integer) session.getAttribute("idNegocioActual"); // Instancia cast pointer PK referencial
                if (idNegocio != null) { // Guardian Boolean restrictivo flag variable error protector null Exception.
                    VentaDAO vDao = new VentaDAO(); // Instanciador generatriz DAO
                    List<Venta> listaVentas = vDao.listarVentas(idNegocio);    // Extracción encapsulando a Array Objeto envolvente Entity
                    request.setAttribute("listaVentas", listaVentas);          // Context Injector Object variables List reference UI Web layer.
                    request.getRequestDispatcher("view/visualizar_ventas.jsp").forward(request, response); // Delegate Rendering bypass .
                } else { // Abort fall back exception Null check .
                    response.sendRedirect("index.jsp");                        // Log Off Refresh clean
                }
                break;
                
            case "ver_detalle":
                // Visualizador Analítico secundario Arrays Hijos details FK models collections
                try {
                    int idVenta = Integer.parseInt(request.getParameter("id_venta")); // Parse primitive getter url String query params .
                    VentaDAO vDaoDet = new VentaDAO(); // Constructor instance Object Factory Transaction relation
                    List<DetalleVenta> listaDetalles = vDaoDet.listarDetalleVenta(idVenta); // Constructor delegador query DAO getters array object details POJOs Entities models.
                    request.setAttribute("listaDetalles", listaDetalles); // Setter Framework object variables 
                    request.setAttribute("idVenta", idVenta); // Setter primitives variables
                    request.getRequestDispatcher("view/detalle_venta.jsp").forward(request, response); // Despacho síncrono Framework .
                } catch (Exception e) { // Trata param dirty exception
                    response.sendRedirect("VentaServlet?action=listar&error=ErrorAlVerDetalle"); // Web error Redirect Log  .
                }
                break;
                
            case "cancelar":
                // Destructor Abstracto JVM memory object referencial Session attribute.
                session.removeAttribute("carrito");                             // Mutador framework JVM memory free destructor collection reference.
                response.sendRedirect("view/menu_inventario.html"); // Refresh flow View UI
                break;
                
            default: // Guard genérico preventivo fallbacks 
                response.sendRedirect("view/menu_inventario.html");
        }
    }

    /**
     * Módulo Auxiliar Setter Pasivo de Objetos.
     * Carga array list dinámico factory de modelos de entidad base producto invocando delegación abstracta read query del Data Access object y lo setea in memory binding the servlet container contexts.
     */
    private void cargarProductos(HttpServletRequest request) { // Scope Method helper POO subrutina private asilada abstraction.
        ProductoDAO pDao = new ProductoDAO(); // Constructor instanciador
        List<Producto> lista = pDao.listarProductos();      // Getter array factoría iterativa Entity Models collection polimorfa List .
        request.setAttribute("listaProductos", lista); // Binding pointer .
    }

    /**
     * Algoritmo Inyector / Modificador de Array In-memory Session.
     * Generador factoría iterativo que lee variables input param y, comparador condicional loop for-each buscando repetidos in array (acumulación int sumar cantidad), y/o instanciación Zero constructor object Array list Append inyección Elemento Nuevo Array of Details object.
     */
    private void agregarProducto(HttpServletRequest request, HttpSession session, List<DetalleVenta> carrito) { // Dependency Inyection method POO Helper.
        try { // Vigila String Parses format int errors 
            int idProd = Integer.parseInt(request.getParameter("id_producto")); // Cast primitivo getter 
            int cantidad = Integer.parseInt(request.getParameter("cantidad")); // Cast param .
            
            if (cantidad <= 0) return;  // Nullifies loop restriction flag control.
            
            // Consultor instanciador singular Getter Delegative DAO abstract connection inyección 1 obj model .
            ProductoDAO pDao = new ProductoDAO(); // Constructor delegativo 
            Producto p = pDao.obtenerProducto(idProd); // Invoca Setter POJO Base reference 
            
            if (p != null) { // Guardian Exception obj .
                // SUB-ALGORITMO ESTRICTO GUARDIA LOGICA MATEMÁTICO DISPONIBILIDAD DAO CHECK 
                com.inventario.dao.DetalleInventarioDAO detDao = new com.inventario.dao.DetalleInventarioDAO(); // Factory constructor .
                Integer idInventario = (Integer) session.getAttribute("idInventarioActual"); // Pointer PK 
                double stockDisponible = detDao.obtenerStockActual(idInventario, idProd); // Delegative Getter Math .
                
                // Módulo analítico loop sumatoria iterador para validar en memoria vs base de datos stock amount
                int cantidadEnCarrito = 0; // Setter default math sum array vars base.
                for (DetalleVenta d : carrito) { // Iterator POO Objects collection List model items in abstract collection .
                    if (d.getIdProducto() == idProd) { // Condicional POJO Getter id Math int eq comparator target .
                        cantidadEnCarrito = d.getCantidad(); // Math getter variable extraction scope limited pointer int.
                        break; // Loop optimizado destructor return
                    }
                }
                
                if (cantidad + cantidadEnCarrito > stockDisponible) { // Logic Math Boolean checker constraint
                    session.setAttribute("error_stock", "Stock insuficiente: " + p.getNombre() + " (Disponible: " + (int)stockDisponible + ")"); // UI flag setter String message error validation log error bind attribute context var param in JVM frame .
                    return; // Aborts Append cycle process validation Error exception
                }

                // SUB ALGORITMO ACUMULADOR INCREMENTAL UPDATER OR INSERT ARRAY FACTORY APPEN 
                boolean existe = false; // Flag Bool Iterador comparator boolean.
                for (DetalleVenta d : carrito) { // Iterador Iterator collection List de model pojos Array object 
                    if (d.getIdProducto() == idProd) { // Comparator
                        d.setCantidad(d.getCantidad() + cantidad);                 // Mutable Setter In Memory Update model logic sum math property
                        d.setSubtotal(d.getCantidad() * p.getPrecioUnitario());    // Math Float property Set update logic.
                        existe = true; // Boolean Mutator State updater condition check
                        break; // Break optimizar speed memory
                    }
                }
                
                if (!existe) { // Mutador Check constructor instanciación Zero Fall-back condition
                    // Factoría de Nuevo Entidad POJO Detalle Construct Model 
                    DetalleVenta det = new DetalleVenta(); // Inyection Constructor vacío de Objeto Instanciador Memory Object Allocation heap ram space.
                    det.setIdProducto(idProd); // Setter Mutador Primitive attribute POJO Abstract object reference
                    det.setNombreProducto(p.getNombre());                          // Setter mutator POO object.
                    det.setCantidad(cantidad); // ...
                    det.setSubtotal(cantidad * p.getPrecioUnitario());             // ...
                    carrito.add(det);                                              // Collection In Memory Append Method Setter pointer array buffer mutador.
                }
            }
        } catch (NumberFormatException e) { // framework IO primitive Cast exception error check protection scope
            e.printStackTrace(); // dump buffer trace error console log dirty string .
        }
    }
    
    /**
     * Módulo Privado Sub-Estructural Destructor de Colecciones in-Memory.
     * Auxiliar utilitario encapsulado encapsulando API nativa de List Objects Collections framework method .remove(int) eliminando de heap variable de Instanciación un POJO Detail vivo .
     */
    private void quitarProducto(HttpServletRequest request, List<DetalleVenta> carrito) { // Dependency Inyection list pointer collection helper module private asilado abstract .
        try { // Trata cast error params .
            int index = Integer.parseInt(request.getParameter("index")); // Instancia pointer get as integer 
            if (index >= 0 && index < carrito.size()) {                  // Guardian Array Index Boolean bounds limitator protector restrictivo limits bounds size
                carrito.remove(index);                                   // Setter JVM object destruct collection In-Memory Mutator.
            }
        } catch (NumberFormatException e) {} // Framework Empty Catch exception handler exception silent destructor.
    }

    /**
     * Subrutina Matemática Iteradora Exclusiva Retorno Getter Sumatoria Dinámica Array Properties getter Collection Models in memoty.
     */
    private double calcularTotal(List<DetalleVenta> carrito) { // Argument pointer pass-through collection.
        double total = 0; // Instanciación Math Default variable local var Float wrapper.
        for (DetalleVenta d : carrito) { // OOP loop Iterator collection of entity model objects Detalle.
            total += d.getSubtotal(); // Expresión sumatoria param get math property return float number primitivo sum loop.
        }
        return total; // Devuelve num float primitive sum limit math property
    }
    
    /**
     * Módulo Factory Subrutina Mutativa delegadora de Disparo Persistencia Relacional a Model Factory.
     * Cierra el ciclo encapsulando el array de Instancias Objeto List DetalleVenta en memoria RAM Context Server alive JVM session array objects a una capa Dao Generatriz y Limpia Object references Destroying the Object Session Attribute Collections List Array alive memory.
     */
    private void finalizarVenta(HttpSession session, List<DetalleVenta> carrito, int idInventario, HttpServletResponse response) throws IOException { // Helper Scope Asilado Delegatiion parameters IO error catching exceptions object model inyección dependency.
        // Limitador de seguridad para protección Check Null Sizes List Empty validation.
        if (carrito.isEmpty()) { // Getter size limit boolean condition list abstract evaluator api.
            response.sendRedirect("VentaServlet?action=mostrar&error=CarritoVacio"); // Delegate error loop response HTTP log param URL .
            return; // Destroy Method stack recursion context abort exit flow logic limit.
        }
        
        // Constructor ORQUESTADOR POJO Principal Contenedor Entity Transaccional Builder Pattern Instanciation wrapper object constructor Base logic.
        VentaDAO vDao = new VentaDAO(); // Heap Allocation Method instance class memory base manager query relacional object framework.
        Venta venta = new Venta(); // Heap Allocation Class Model wrapper instance constructor null .
        venta.setIdInventario(idInventario);                           // Setter POO attribute FK Object pointer dependency injection PK 
        venta.setFechaVenta(new Date(System.currentTimeMillis()));     // Constructor Time Date setter POO metadato Object model Date util .
        venta.setTotalVenta(calcularTotal(carrito));                    // Math method float Getter setter delegando total acumulador number float.
        
        // Disparo Síncrono Atómico Booleano de Transaction DAO Factory Commit SQL Logic Multiple inserts.
        boolean resultado = vDao.registrarVenta(venta, carrito); // Metodo delegativo DAO method send params arrays POJO and Wrapper Entytis for relation sql model generation asinc connection pool transaction check state getter logic return.
        
        if (resultado) { // Comparator boolean true ok validation constraint transaction if true successful flag check ok.
            // Destructor In Memory Framework object target Session context cleaning pointer Heap object
            session.removeAttribute("carrito"); // Eliminador recolector pointer array object model reference HTTP pool.
            response.sendRedirect("view/venta_finalizada.html"); // Frame URL Param UI Success View Dispatcher Asynchronous Refresh Cycle Response log ok flag success UI statics update loop user notification flow success conclusion action process ok flag
        } else { // Validation abort Check false validation check no changes or insert.
            // Log UI HTTP exception
            response.sendRedirect("VentaServlet?action=mostrar&error=ErrorAlGuardar"); // Asíncrona Log Update framework Web Response .
        }
    }
}
