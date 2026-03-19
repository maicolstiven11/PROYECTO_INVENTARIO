package com.inventario.controller; // Declaración de espacio de nombres organizativo

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
 * Controlador Frontal Orquestador de Ciclo Lógico: InventarioServlet.
 * 
 * Clase MVC Interceptora que agrupa bajo una envoltura transaccional el estado y las colecciones
 * de los DAOs relacionados al entorno o Contexto Físico (Inventarios, Detalles y Productos Base).
 * Centraliza e inyecta parámetros de enrutamiento y sesión foráneos.
 */
@WebServlet(name = "InventarioServlet", urlPatterns = {"/InventarioServlet"}) // Vincula el Endpoint lógico a clase constructora nativa servlet.
public class InventarioServlet extends HttpServlet { // Polimorfismo sub-tipo base protocolo Web

    /**
     * Sobreescritura evaluativa GET.
     * Generador múltiple condicionado: Actúa como Switch Case asíncrono instanciando Factory DAOs 
     * y mutando contextos in-memory dependiendo de la clave string extractada en la solicitud HTTP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Propagación controlada asíncrona Web 
        
        String action = request.getParameter("action"); // Localizador semántico 
        System.out.println("InventarioServlet: action=" + action); // Instanciador print logger buffer consola JVM
        
        if ("iniciar".equals(action)) { // Inyector Booleano
            // Validador de Autorización: Condiciona instanciación basada en rol en memoria.
            com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado"); // Cast forzado object transiente a entity real.
            if (usuario == null || usuario.getIdRol() == 2) { // Fallback de autorización jerárquica
                response.sendRedirect("view/Menu_sistema.jsp?error=AccesoDenegado"); // Destruye hilo 
                return; // Break a scope 
            }

            // =====================================================================
            // ALGORITMO CONSTRUCTOR DE ESTADO ZERO (New Entity Context)
            // =====================================================================
            String idNegocioStr = request.getParameter("idNegocio"); // Extractor llave relacional FK
            String tipoControl = request.getParameter("tipo");       // Descriptor literal asimétrico 
            String fechaStr = request.getParameter("fecha");         // Descriptor crono asimétrico 
            
            System.out.println("InventarioServlet: idNegocio=" + idNegocioStr + ", tipo=" + tipoControl + ", fecha=" + fechaStr); // Print Trace local buffer.
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) { // Filtro de nulidad 
                try {
                     int idNegocio = Integer.parseInt(idNegocioStr); // Conversor aritmético forzoso
                    java.sql.Date fechaInicio = null; // Setter base a nulidad local Date modelo 
                    
                    // Inicializador condicional polimorfo usando utilitario o parsing Date String.
                    if (fechaStr != null && !fechaStr.isEmpty()) { 
                        fechaInicio = java.sql.Date.valueOf(fechaStr); // Factory valueOf SQL Date POJO
                    } else {
                        fechaInicio = new java.sql.Date(System.currentTimeMillis()); // Subrutina estática JVM 
                    }
                    
                    // Orquestación delegada a Gestor de Persistencia 
                    InventarioDAO dao = new InventarioDAO(); // Generador
                    int idInventario = dao.iniciarInventario(idNegocio, tipoControl, fechaInicio); // Getter constructor 
                    
                    System.out.println("InventarioServlet: idInventario generado=" + idInventario); // Debug Log 
                    
                    if (idInventario > 0) { // Integridad lógica verificador sobre id retornado 
                        // Inyecta delimitadores asimétricos al entorno envolvente HTTP (session container)
                        request.getSession().setAttribute("idInventarioActual", idInventario);  // Atadura temporal 
                        request.getSession().setAttribute("idNegocioActual", idNegocio);        // Amarra llave matriz referencial
                        
                        // ALGORITMO AUTO-CARGA: Referencia interconectada pasiva a inventario base anterior histórico cerrado 
                        com.inventario.model.Inventario invAnterior = dao.obtenerUltimoInventarioCerrado(idNegocio); // Getter relacional a modelo atómico
                        boolean stockCargado = false; // Flag interruptor
                        
                        if (invAnterior != null) { // Instanciador booleano anclaje preventivo
                            DetalleInventarioDAO detDao = new DetalleInventarioDAO(); // Llama manejador secundario enlace .
                            java.util.List<com.inventario.model.DetalleInventario> detallesAnteriores = detDao.listarDetalles(invAnterior.getIdInventario()); // Array sub-colección de modelo fuerte.
                            
                            if (detallesAnteriores != null && !detallesAnteriores.isEmpty()) { // Previene Null array y length iterador.
                                // Foreach constructivo iterado mapeando cierre viejo a inicio nuevo
                                for (com.inventario.model.DetalleInventario d : detallesAnteriores) { // Bucle extrae cada object List iterable
                                    detDao.insertarDetalle(idInventario, d.getIdProducto(), d.getCantidadFinal()); // Trasvasa iteración a DAO setter persistente.
                                }
                                stockCargado = true; // Activa flag de resolución condicional
                                System.out.println("InventarioServlet: Stock cargado automáticamente desde inventario anterior ID=" + invAnterior.getIdInventario());
                            }
                        }
                        
                        if (stockCargado) { // Valuador de switch asimétrico
                            // Direccionamiento resolutivo encodeando a asimétrico string param URL safe
                            response.sendRedirect("view/menu_inventario.jsp?msg_exito=" + 
                                java.net.URLEncoder.encode("¡Stock cargado automáticamente del inventario anterior!", "UTF-8")); // Factoría codificadora String Wrapper
                        } else {
                            // Subrutina cruzada recursiva asincrónica llamando otro case
                            response.sendRedirect("InventarioServlet?action=cargar_detalle"); // Corta redirigiendo ciclo nuevo 
                        }
                    } else {
                        // Bifurcación fallida atómica SQL negativa 
                        System.out.println("InventarioServlet: DAO devolvió -1, algo falló");
                        response.sendRedirect("NegocioServlet?error=FalloInicioInventario"); // Salida error log restrictiva 
                    }
                } catch (Exception e) { // Atrapatodo y debug.
                    System.out.println("InventarioServlet ERROR: " + e.getMessage());
                    e.printStackTrace(); // Salida genérica sucia CLI
                    // Fallback con trace catch 
                    response.sendRedirect("NegocioServlet?error=" + e.getMessage());
                }
            } else { // Validador ID Negocio null pointer preventer 
                response.sendRedirect("NegocioServlet?error=SinIdNegocio"); // Redireccion log clean
            }
        } else if ("entrar".equals(action)) { // Discriminador string sub-lógico Action #2
            // =====================================================================
            // ALGORITMO ENRUTADOR REACTIVO E INYECTOR CONTEXTUAL
            // =====================================================================
            String idNegocioStr = request.getParameter("idNegocio"); // Cast envoltorio HTML param 
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) { // Comprueba flag
                int idNegocio = Integer.parseInt(idNegocioStr); // Numeriza a escalar primario ID
                InventarioDAO dao = new InventarioDAO(); // Prepara Factory 
                com.inventario.model.Inventario inv = dao.obtenerInventarioActivo(idNegocio); // Getter asocia Entidad atómica POJO .
                
                if (inv != null) { // Preventivo crash y bandera resolutiva booleana condicional 
                    // Inyección Setter asigmático a capa Session .
                    request.getSession().setAttribute("idInventarioActual", inv.getIdInventario()); // Bind FK 
                    request.getSession().setAttribute("idNegocioActual", idNegocio);                // Bind PK FK ref 
                    response.sendRedirect("view/menu_inventario.jsp");                              // Cortador de redirect asíncrono frontend .
                } else {
                    // Bifurcación restrictiva de error y purgas .
                    com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado"); // Interpelación de permiso logueo 
                    if (usuario != null && usuario.getIdRol() == 2) { // Verificador if booleano restrictivo .
                        response.sendRedirect("view/Menu_sistema.jsp?error=NoInventarioActivoTrabajador"); // Redirec
                    } else { // Redirect Admin Fallback .
                        response.sendRedirect("NegocioServlet?error=NoInventarioActivo"); // Clean Param 
                    }
                }
            }
        } else if ("cargar_detalle".equals(action)) { // String evaluador iterativo #3
            // =====================================================================
            // GENERADOR DE FLUJO VISUAL LIST (Binding Dinámico Masivo a JSP Array)
            // =====================================================================
            ProductoDAO prodDao = new ProductoDAO(); // Constructor orquestador base 
            List<Producto> listaProductos = prodDao.listarProductos();    // Subrutina Array recolectora general .
            
            request.setAttribute("listaProductos", listaProductos);      // Despacha a memoria local viva .
            request.getRequestDispatcher("view/inventario_detalle.jsp").forward(request, response); // Finalizador por forwarding o puente directo interno sin refrescar .
            
        } else if ("guardar_stock".equals(action)) { // String Evaluador Setter #4
            // =====================================================================
            // ALGORITMO ITERADOR ACUMULADOR (Persistencia Multitarea Enlazada Detalle)
            // =====================================================================
            try { // Protector encapsulado 
                // Contexto base escalar Int cast 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); // Ref session 
                
                if (idInventario != null) { // Base nulidad check .
                    // Mapeos múltiples asimétricos HTTP Form HTML Input Name array binding .
                    String[] idProductosStr = request.getParameterValues("id_producto"); // Factory recolector de multiples string values en String Array .
                    String[] cantidadesStr = request.getParameterValues("cantidad"); // Array extractor cantidades html form param .
                    
                    if (idProductosStr != null && cantidadesStr != null) { // Validador Longitud Múltiple Booleano And .
                        DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); // Preparador Relacional .
                        
                        for (int i = 0; i < idProductosStr.length; i++) { // For indexado puro Java Base.
                            int idProd = Integer.parseInt(idProductosStr[i]); // Conversor asimétrico
                            // Evaluador a valor flotante cero u orígen null
                            double cant = 0; // Inicializador flotante pre-asignado double
                            if (cantidadesStr[i] != null && !cantidadesStr[i].isEmpty()) { // Comprueba cada iteración
                                cant = Double.parseDouble(cantidadesStr[i]); // Conversor coma flotante.
                            }
                            
                            // Traspaso DAO persistente setter
                            detalleDao.insertarDetalle(idInventario, idProd, cant); // Query param .
                        }
                    }
                    
                    // Resolución final asíncrona exitosa 
                    response.sendRedirect("view/menu_inventario.jsp"); // Choque o salida frontend UI
                } else {
                    // Rechazo local context perdido .
                    response.sendRedirect("NegocioServlet?error=SesionInventarioInvalida"); // Error URL param flag.
                }
                
            } catch (Exception e) { // Protector asilado sobre parseo numérico array.
                 e.printStackTrace(); // Salida standard sucia JVM console error apilada
                 // Redireccion control 
                 response.sendRedirect("NegocioServlet?error=ErrorGuardarStock"); // Pinta fallback general del módulo Negocio 
            }
            
        } else if ("cargar_cierre".equals(action)) { // Condicional Action sub-módulo #5
            // =====================================================================
            // PREPARADOR DE FLUJO ESTÁTICO (Extracción Array Cierre Relacional)
            // =====================================================================
            try { // Vigilancia de variables en RAM 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); // Generador de acceso transiente
                Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual"); // Getter asimétrico
                if (idInventario != null && idNegocio != null) { // Condicional limitante Nulo.
                    
                    // CALCULADOR LÓGICA DE TIEMPO FECHAS EN MEMORIA
                    InventarioDAO invDao = new InventarioDAO(); // Llamada al manager DAO
                    com.inventario.model.Inventario invActual = invDao.obtenerInventarioActivo(idNegocio); // Consigue POJO abstracto relacional vivo.
                    
                    if (invActual != null) { // Chequeo null 
                        long msActual = System.currentTimeMillis(); // Time util JVM as long num
                        long msInicio = invActual.getFechaInicio().getTime(); // Method extractor Time over SQL Date Object
                        long diffMs = msActual - msInicio; // Matematica de resta simple
                        long diffDias = diffMs / (1000 * 60 * 60 * 24); // Matemática computacional base ms to dias.
                        
                        String tipo = invActual.getTipoControl(); // Extraccion de metadato semantico descriptivo 
                        boolean puedeCerrar = true; // Setter base restrictiva 
                        
                        if ("semanal".equalsIgnoreCase(tipo) && diffDias >= 7) puedeCerrar = true; // String Utils igualador limitando .
                        else if ("mensual".equalsIgnoreCase(tipo) && diffDias >= 30) puedeCerrar = true; // Idem para mensual
                        else if (!"semanal".equalsIgnoreCase(tipo) && !"mensual".equalsIgnoreCase(tipo)) puedeCerrar = true; // Default handler libre asimetrico .

                        if (!puedeCerrar) { // Negativo bypass check boolean 
                            String msg = "Aún no puede cerrar este periodo (" + tipo + "). Solo han pasado " + diffDias + " días."; // String builder asimétrico
                            response.sendRedirect("view/menu_inventario.jsp?error_tiempo=" + java.net.URLEncoder.encode(msg, "UTF-8")); // Factoría segura HTTP redirect String buffer
                            return; // Break hilo
                        }
                    }

                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); // Lector secundario DAO
                    List<com.inventario.model.DetalleInventario> detalles = detalleDao.listarDetalles(idInventario); // Constructor List 
                    
                    request.setAttribute("listaDetalles", detalles); // Disparo o inyección relacional .
                    request.getRequestDispatcher("view/inventario_cierre.jsp").forward(request, response); // Despacho estático in memory render
                } else { // Default NULL catch
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); // Bypass error redirect flag.
                }
            } catch (Exception e) { // Excepciones base y null pointers asiladores
                e.printStackTrace(); // Salida catch consola 
                response.sendRedirect("NegocioServlet?error=ErrorCargandoCierre"); // Purificador.
            }
            
        } else if ("finalizar_inventario".equals(action)) { // Discriminador booleano String #6
            // =====================================================================
            // RUTINA CIERRE TRANSACCIONAL COMPLETO (Destructor Periodo/Setter Masivo)
            // =====================================================================
            try { // Limitador general 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); // Extract contexto RAM escalar 
                if (idInventario != null) { // Validador pre Nullpointer flag.
                    // RE-EVALUADOR DE TIEMPOS 
                    InventarioDAO invDao = new InventarioDAO(); // Generador de Orquesta 
                    Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual"); // Instancia llave 
                    com.inventario.model.Inventario invActual = invDao.obtenerInventarioActivo(idNegocio); // Interconexión delegativa
                    
                    if (invActual != null) { // Check objeto exist.
                        long msActual = System.currentTimeMillis(); // Extraccion Time Millis as Long num primitive  .
                        long msInicio = invActual.getFechaInicio().getTime(); // Idem object date method invoke.
                        long diffMs = msActual - msInicio; // Operador matematico restador 
                        long diffDias = diffMs / (1000 * 60 * 60 * 24); // Idem conversor numérico escalar largo.
                        
                        String tipo = invActual.getTipoControl(); // Extractor literal string  
                        boolean puedeCerrar = true; // Base condicional boolean  
                        
                        if ("semanal".equalsIgnoreCase(tipo) && diffDias >= 7) puedeCerrar = true; // Limitador restrictivo condicional combinacional 
                        else if ("mensual".equalsIgnoreCase(tipo) && diffDias >= 30) puedeCerrar = true; // Idem .
                        else if (!"semanal".equalsIgnoreCase(tipo) && !"mensual".equalsIgnoreCase(tipo)) puedeCerrar = true; // Fallback generico booleano and restrictivo invertido

                        if (!puedeCerrar) { // Check condicional Bypass Inverso booleano.
                            String msg = "No puede cerrar el inventario " + tipo + " aun. Faltan dias (Lleva: " + diffDias + ")"; // Cadena iteradora concat .
                            response.sendRedirect("InventarioServlet?action=cargar_cierre&error_tiempo=" + java.net.URLEncoder.encode(msg, "UTF-8")); // Coded redirect asimétrico.
                            return; // Break JVM logic hilo  .
                        }
                    }

                    String[] idProductosStr = request.getParameterValues("id_producto"); // Consigue Arrays string input Form HTML.
                    String[] cantidadesFinalesStr = request.getParameterValues("cantidad_final"); // Idem.
                    
                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); // Constructor relacional a capa Link_DAO_DB .
                    if (idProductosStr != null && cantidadesFinalesStr != null) { // Chequeador multi-nulo booleano conjuncion.
                        for (int i = 0; i < idProductosStr.length; i++) { // Bucle indexador contador clásico.
                            int idProd = Integer.parseInt(idProductosStr[i]); // Aritmética parsificadora en stack 
                            double cantFinal = 0; // Preinicializador flotante preventivo .
                            if (cantidadesFinalesStr[i] != null && !cantidadesFinalesStr[i].isEmpty()) { // Comprobador por casillas .
                                cantFinal = Double.parseDouble(cantidadesFinalesStr[i]); // Setter Aritmético fraccionario de Cadena a Real Primitivo .
                            }
                            detalleDao.actualizarCantidadFinal(idInventario, idProd, cantFinal); // Metodo inyector relacional BD (Mutating State Update) .
                        }
                    }
                    
                    // DESTRUCTOR LÓGICO ESTADO ORQUESTADOR
                    boolean cerrado = invDao.finalizarInventario(idInventario); // Delegado con retorno de afirmación lógica booleana 
                    
                    if (cerrado) { // Si afirmativo
                        // Depuración o Invalidación forzada in-memory
                        request.getSession().removeAttribute("idInventarioActual"); // Dispara borrador hash map de sesión sobre el Id específico 
                        
                        // CARGADOR ADICIONAL (Pre renderizador)
                        java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario); // Query compleja a List Array Object
                        request.setAttribute("listaDescuadre", detallesFinales); // Inyector Polimorfo
                        request.setAttribute("mensajeExito", "¡Inventario cerrado y guardado correctamente!"); // Binding literal .
                        request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response); // Despacha JSP render passthrough via Request pipeline asíncrona.
                    } else { // Fallo SQL booleano atrapado interno
                        response.sendRedirect("NegocioServlet?error=ErrorGuardandoBD"); // Limpieza control url param flag .
                    }
                } else { // Fallo nulo check 
                    response.sendRedirect("NegocioServlet?error=NoSePudoCerrar"); // Redirect flag error param 
                }
            } catch (Exception e) { // Try catch encapsulador framework 
                e.printStackTrace(); // Suciedad trace CLI 
                response.sendRedirect("NegocioServlet?error=ErrorAlFinalizar"); // Catch fallback redirect url.
            }
            
        } else { // Fallback principal general default no matches Switch IF - ELSE_IF String
            // Fallback no condicionado destructivo ciclo completo 
            response.sendRedirect("NegocioServlet"); // Retorna a Origen inicial Controller Negocio
        }
    }

    /**
     * Sobreescritura puente Post a Get Http Methods.
     * Envoltura pasiva recicladora redirigiendo flujo iterativo para usar un solo método de resolución final .
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Captura base Java Servlets framework JVM
        doGet(request, response); // Invoca a sí misma polimorfizando inyección .
    }
}
