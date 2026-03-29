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
 * Controlador InventarioServlet.
 * 
 * Es el controlador principal y más extenso. Gestiona todo el ciclo de vida
 * de abrir un inventario (mes, semana), contar el stock, cerrarlo y buscar los anteriores.
 */
@WebServlet(name = "InventarioServlet", urlPatterns = {"/InventarioServlet"}) // Expone esta funcionalidad hacia el navegador bajo esta URL
public class InventarioServlet extends HttpServlet { 

    /**
     * El método doGet atiende peticiones según la palabra clave o acción ("action") 
     * que se haya enviado por la URL del navegador.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        String action = request.getParameter("action"); // Leemos la clave de lo que quiere hacer (iniciar, entrar, cargar_detalle...)
        System.out.println("InventarioServlet: action=" + action); 
        
        if ("iniciar".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: INICIAR (CREAR UN NUEVO PERIODO DE INVENTARIO PARA GESTIONARLO)
            // ===============================================================================================
            // 1. Verificamos que sea Administrador, porque un cajero (rol 2) no debería crear el inventario
            com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado"); 
            if (usuario == null || usuario.getIdRol() == 2) { 
                response.sendRedirect("view/Menu_sistema.jsp?error=AccesoDenegado"); // Expulsamos a cajeros intrusos
                return; 
            }

            // 2. Extraer datos del formulario para este nuevo mes
            String idNegocioStr = request.getParameter("idNegocio"); // ID Del local
            String tipoControl = request.getParameter("tipo");       // Semanal o mensual
            String fechaStr = request.getParameter("fecha");         // Desde cuándo rige
            
            System.out.println("InventarioServlet: idNegocio=" + idNegocioStr + ", tipo=" + tipoControl + ", fecha=" + fechaStr); 
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) { 
                try {
                     int idNegocio = Integer.parseInt(idNegocioStr); // Convertir ID a entero
                    java.sql.Date fechaInicio = null; 
                    
                    // Configuramos la variable fecha de SQL 
                    if (fechaStr != null && !fechaStr.isEmpty()) { 
                        fechaInicio = java.sql.Date.valueOf(fechaStr); // Si le puso fecha, aplicamos esa
                    } else {
                        fechaInicio = new java.sql.Date(System.currentTimeMillis()); // Si no, asumimos que arranca hoy mismo
                    }
                    
                    // 3. Crear en la base de datos el objeto abstracto de Cierre
                    InventarioDAO dao = new InventarioDAO(); 
                    int idInventario = dao.iniciarInventario(idNegocio, tipoControl, fechaInicio); // Nos debe devolver el ID Generado del periodo
                    
                    System.out.println("InventarioServlet: idInventario generado=" + idInventario); 
                    
                    if (idInventario > 0) { // Si sí se creó un ticket de inventario bien...
                        // Guardamos ese número y el ID de negocio en el bolsillo del usuario (Sesión)
                        request.getSession().setAttribute("idInventarioActual", idInventario);  
                        request.getSession().setAttribute("idNegocioActual", idNegocio);        
                        
                        // 4. CARGA AUTOMÁTICA O ARRASTRE. Si hay un inventario cerrado del mes pasado, traemos su Stock sobrante a este mes nuevo.
                        com.inventario.model.Inventario invAnterior = dao.obtenerUltimoInventarioCerrado(idNegocio); 
                        boolean stockCargado = false; 
                        
                        if (invAnterior != null) { // Si existió un mes pasado
                            DetalleInventarioDAO detDao = new DetalleInventarioDAO(); 
                            // Traemos todos los productos y cantidades que quedaron al cierre del anterior
                            java.util.List<com.inventario.model.DetalleInventario> detallesAnteriores = detDao.listarDetalles(invAnterior.getIdInventario()); 
                            
                            if (detallesAnteriores != null && !detallesAnteriores.isEmpty()) { 
                                // Bucle o iterador para guardar en nuestra nueva tabla, el stock viejo.
                                for (com.inventario.model.DetalleInventario d : detallesAnteriores) { 
                                    detDao.insertarDetalle(idInventario, d.getIdProducto(), d.getCantidadFinal()); // Inserta producto con stock del cajón pasado como nuestra CantidadInicial
                                }
                                stockCargado = true; // Flag activado
                                System.out.println("InventarioServlet: Stock cargado automáticamente desde inventario anterior ID=" + invAnterior.getIdInventario());
                            }
                        }
                        
                        if (stockCargado) { 
                            // Avanti, stock migrado automáticamente y vamos al menu
                            response.sendRedirect("view/menu_inventario.jsp?msg_exito=" + 
                                java.net.URLEncoder.encode("¡Stock cargado automáticamente del inventario anterior!", "UTF-8"));
                        } else {
                            // Si no hubo inventarios pasados porque es su primero mes trabajando, redirige a que cuente sus productos manualmente por URL "cargar_detalle"
                            response.sendRedirect("InventarioServlet?action=cargar_detalle"); 
                        }
                    } else {
                        System.out.println("InventarioServlet: DAO devolvió -1, algo falló");
                        response.sendRedirect("NegocioServlet?error=FalloInicioInventario"); 
                    }
                } catch (Exception e) { 
                    System.out.println("InventarioServlet ERROR: " + e.getMessage());
                    e.printStackTrace(); 
                    response.sendRedirect("NegocioServlet?error=" + e.getMessage());
                }
            } else { 
                response.sendRedirect("NegocioServlet?error=SinIdNegocio"); 
            }
        } else if ("entrar".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: ENTRAR (VERIFICAR SI YA HAY UNO ABIERTO PARA TRABAJAR EN ÉL)
            // ===============================================================================================
            String idNegocioStr = request.getParameter("idNegocio"); 
            
            if (idNegocioStr != null && !idNegocioStr.isEmpty()) { 
                int idNegocio = Integer.parseInt(idNegocioStr); 
                
                // --- VALIDACIÓN DE SEGURIDAD: NEGOCIO INACTIVO ---
                com.inventario.dao.NegocioDAO negocioDao = new com.inventario.dao.NegocioDAO();
                com.inventario.model.Negocio negocio = negocioDao.obtenerNegocio(idNegocio);
                
                if (negocio != null && "inactivo".equals(negocio.getEstado())) {
                    // Si el negocio está inactivo, no debe poder entrar a gestionar inventarios
                    response.sendRedirect("NegocioServlet?error=NegocioInactivo");
                    return;
                }
                
                InventarioDAO dao = new InventarioDAO(); 
                // Busca rápidamente si el bar tiene un ciclo activo (en estado "A") abierto
                com.inventario.model.Inventario inv = dao.obtenerInventarioActivo(idNegocio);  
                
                if (inv != null) { // Si logra conseguir ese modelo...
                    // Metemos el idInventario vivo en nuestro bolsillo (Sesión) para que Gastos y Ventas sepan a quién afectar
                    request.getSession().setAttribute("idInventarioActual", inv.getIdInventario()); 
                    request.getSession().setAttribute("idNegocioActual", idNegocio);                
                    response.sendRedirect("view/menu_inventario.jsp"); // Va al menu verde
                } else {
                    // Si no había ninguno abierto
                    com.inventario.model.Usuario usuario = (com.inventario.model.Usuario) request.getSession().getAttribute("usuarioLogueado"); 
                    if (usuario != null && usuario.getIdRol() == 2) { // Si el que miraba era Cajero... manda error de que el admin le abra algo.
                        response.sendRedirect("view/Menu_sistema.jsp?error=NoInventarioActivoTrabajador"); 
                    } else { // Si es admin, lo manda al panel de crear uno.
                        response.sendRedirect("NegocioServlet?error=NoInventarioActivo"); 
                    }
                }
            }
        } else if ("cargar_detalle".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: CARGAR CATÁLOGO PARA ANOTAR INVENTARIO MANUAL INICIAL (Primera vez)
            // ===============================================================================================
            ProductoDAO prodDao = new ProductoDAO(); 
            // Extraemos todo el catálogo de galletas y cervezas base para que las vean y cuenten.
            Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual");
            if (idNegocio == null) idNegocio = 0;
            List<Producto> listaProductos = prodDao.listarProductos(idNegocio);
            
            request.setAttribute("listaProductos", listaProductos); // Pegar al request para vista      
            request.getRequestDispatcher("view/inventario_detalle.jsp").forward(request, response); 
            
        } else if ("guardar_stock".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: GUARDAR EL STOCK INICIAL A MANO
            // ===============================================================================================
            try { 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); 
                
                if (idInventario != null) { 
                    // Como el formulario puede enviar 20 galletas a la vez, se recogen ARREGLOS o Vectores (múltiples registros de texto)
                    String[] idProductosStr = request.getParameterValues("id_producto"); 
                    String[] cantidadesStr = request.getParameterValues("cantidad"); 
                    
                    if (idProductosStr != null && cantidadesStr != null) { 
                        DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); 
                        
                        // Iteramos renglón a renglón o producto a producto, anotando uno a uno su Stock en BD
                        for (int i = 0; i < idProductosStr.length; i++) { 
                            int idProd = Integer.parseInt(idProductosStr[i]); 
                            double cant = 0; 
                            if (cantidadesStr[i] != null && !cantidadesStr[i].isEmpty()) { 
                                cant = Double.parseDouble(cantidadesStr[i]); // Convierte ese renglón a número decimal
                            }
                            
                            detalleDao.insertarDetalle(idInventario, idProd, cant); // Empuja el producto y su cantidad contada a la BD
                        }
                    }
                    
                    response.sendRedirect("view/menu_inventario.jsp"); // Devuelve al panel de mando
                } else {
                    response.sendRedirect("NegocioServlet?error=SesionInventarioInvalida"); 
                }
                
            } catch (Exception e) { 
                 e.printStackTrace(); 
                 response.sendRedirect("NegocioServlet?error=ErrorGuardarStock"); 
            }
            
        } else if ("cargar_cierre".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: INGRESAR A LA PESTAÑA PARA CONCLUIR Y CERRAR EL MES/SEMANA (Escribir Sobrantes Cajas)
            // ===============================================================================================
            try { 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); 
                Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual"); 
                if (idInventario != null && idNegocio != null) { 
                    
                    // RESTRICCIÓN DE TIEMPOS DE APERTURA MATEMÁTICO 
                    InventarioDAO invDao = new InventarioDAO(); 
                    com.inventario.model.Inventario invActual = invDao.obtenerInventarioActivo(idNegocio); 
                    
                    if (invActual != null) { 
                        long msActual = System.currentTimeMillis(); // Tiempo actual local
                        long msInicio = invActual.getFechaInicio().getTime(); // Tiempo guardado
                        long diffMs = msActual - msInicio; // Restar diferencia en Milisegundos
                        long diffDias = diffMs / (1000 * 60 * 60 * 24); // Convertir Milisegundos a Días transcurridos
                        
                        String tipo = invActual.getTipoControl(); 
                        boolean puedeCerrar = false; 
                        
                        // Evaluar
                        if ("semanal".equalsIgnoreCase(tipo) && diffDias >= 7) puedeCerrar = true; 
                        else if ("mensual".equalsIgnoreCase(tipo) && diffDias >= 30) puedeCerrar = true; 
                        else if (!"semanal".equalsIgnoreCase(tipo) && !"mensual".equalsIgnoreCase(tipo)) puedeCerrar = true; 

                        if (!puedeCerrar) { // Si falló la ecuación por no ser tiempo (Apenas lleva 3 días de mensual)
                            String msg = "Aún no puede cerrar este periodo (" + tipo + "). Solo han pasado " + diffDias + " días."; 
                            response.sendRedirect("view/menu_inventario.jsp?error_tiempo=" + java.net.URLEncoder.encode(msg, "UTF-8")); // Error
                            return; 
                        }
                    }

                    // Extraer los detalles contables para visualizarlos
                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); 
                    
                    // --- SINCRONIZACIÓN AUTOMÁTICA ---
                    // Asegura que productos nuevos (creados tras iniciar el inventario) aparezcan en la lista
                    detalleDao.sincronizarProductos(idInventario);
                    
                    List<com.inventario.model.DetalleInventario> detalles = detalleDao.listarDetalles(idInventario); 
                    
                    request.setAttribute("listaDetalles", detalles); 
                    request.getRequestDispatcher("view/inventario_cierre.jsp").forward(request, response); // Pintar el formulario para Cerrar
                } else { 
                    response.sendRedirect("NegocioServlet?error=SinInventarioActivo"); 
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
                response.sendRedirect("NegocioServlet?error=ErrorCargandoCierre"); 
            }
            
        } else if ("finalizar_inventario".equals(action)) { 
            // ===============================================================================================
            // ACCIÓN: APRETAR EL BOTÓN ABRUMADOR ("CERRAR Y MIGRAR") CON LAS CANTIDADES SOBRANTES INGRESADAS
            // ===============================================================================================
            try { 
                Integer idInventario = (Integer) request.getSession().getAttribute("idInventarioActual"); 
                if (idInventario != null) { 
                    
                    InventarioDAO invDao = new InventarioDAO(); 
                    Integer idNegocio = (Integer) request.getSession().getAttribute("idNegocioActual"); 
                    com.inventario.model.Inventario invActual = invDao.obtenerInventarioActivo(idNegocio); 
                    
                    // RE-EVALUACIÓN DE TIEMPO (Por seguridad web nuevamente)
                    if (invActual != null) { 
                        long msActual = System.currentTimeMillis(); 
                        long msInicio = invActual.getFechaInicio().getTime(); 
                        long diffMs = msActual - msInicio; 
                        long diffDias = diffMs / (1000 * 60 * 60 * 24); 
                        
                        String tipo = invActual.getTipoControl(); 
                        boolean puedeCerrar = true; 
                        
                        // Limitantes lógicas
                        if ("semanal".equalsIgnoreCase(tipo) && diffDias >= 7) puedeCerrar = true; 
                        else if ("mensual".equalsIgnoreCase(tipo) && diffDias >= 30) puedeCerrar = true; 
                        else if (!"semanal".equalsIgnoreCase(tipo) && !"mensual".equalsIgnoreCase(tipo)) puedeCerrar = true; 

                        if (!puedeCerrar) { 
                            String msg = "No puede cerrar el inventario " + tipo + " aun. Faltan dias (Lleva: " + diffDias + ")"; 
                            response.sendRedirect("InventarioServlet?action=cargar_cierre&error_tiempo=" + java.net.URLEncoder.encode(msg, "UTF-8")); 
                            return;  
                        }
                    }

                    // Tomamos del formulario de la Tabla de sobrantes cada cajita que escribió
                    String[] idProductosStr = request.getParameterValues("id_producto"); 
                    String[] cantidadesFinalesStr = request.getParameterValues("cantidad_final"); 
                    
                    DetalleInventarioDAO detalleDao = new DetalleInventarioDAO(); 
                    if (idProductosStr != null && cantidadesFinalesStr != null) { 
                        // Bucle actualizador
                        for (int i = 0; i < idProductosStr.length; i++) { 
                            int idProd = Integer.parseInt(idProductosStr[i]); 
                            double cantFinal = 0; 
                            if (cantidadesFinalesStr[i] != null && !cantidadesFinalesStr[i].isEmpty()) { 
                                cantFinal = Double.parseDouble(cantidadesFinalesStr[i]); // Recoge lo que sobró físico en estantes
                                // --- VALIDACIÓN DE NEGATIVOS ---
                                if (cantFinal < 0) cantFinal = 0; // Por seguridad, si llega negativo, lo forzamos a 0
                            }
                            // Inyecta o actualiza esta nueva cifra en la tabla general base
                            detalleDao.actualizarCantidadFinal(idInventario, idProd, cantFinal); 
                        }
                    }
                    
                    // PASO FÚNEBRE MATADOR: Acaba el ciclo convirtiéndolo de "A" Activo a Cerrado (False/Finalizado)
                    boolean cerrado = invDao.finalizarInventario(idInventario); 
                    
                    if (cerrado) { 
                        // Le borramos la referencia a "idInventarioActivo" por si trata de facturar o gastar de la nada (Ya quedó cerrado)
                        request.getSession().removeAttribute("idInventarioActual"); 
                        
                        // Ahora le mostramos un pequeño Dashboard visual para que evalúe si faltaron papitas o platica
                        java.util.List<com.inventario.model.DetalleInventario> detallesFinales = detalleDao.listarDetallesConPrecio(idInventario); 
                        request.setAttribute("listaDescuadre", detallesFinales); // Colección de array atada
                        request.setAttribute("mensajeExito", "¡Inventario cerrado y guardado correctamente!"); 
                        request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(request, response); // Pintar reporte descuadres
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
            // Si el Action no coincidió con ninguna palabra esperada.
            response.sendRedirect("NegocioServlet"); 
        }
    }

    /**
     * El método doPost simplemente recicla o envía a doGet. 
     * Es un pequeño truco para que si una web envia esto en formato oculto, también valga en este servlet unificado.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        doGet(request, response); 
    }
}
