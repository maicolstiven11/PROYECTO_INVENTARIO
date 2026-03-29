<%--=====================================================================
    VISTA JSP: inventario_cierre.jsp - Cierre de Inventario y Cuadre de Caja
    
    QUIÉN LA MUESTRA: InventarioServlet (GET con action=cargar_cierre) → 
    request.getRequestDispatcher("view/inventario_cierre.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (InventarioServlet):
    - ${listaDetalles} → List<DetalleInventario>. Viene de: InventarioDAO.obtenerDetallesInventario()
    
    Cada DetalleInventario tiene: idProducto, nombreProducto, cantidadInicial
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idInventarioActual} → ID del inventario activo a cerrar
    - ${sessionScope.nombreNegocioActual} → Nombre del negocio
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Finalizar inventario: POST → InventarioServlet?action=finalizar_inventario
    Envía: id_producto[], cantidad_final[] (arrays por cada producto)
    
    IMPORTANCIA:
    - Es el paso final del ciclo de contabilidad
    - Permite calcular descuadres y diferencias de inventario
    - Cierra el período y habilita el siguiente ciclo
    - Fundamental para el control y auditoría del negocio
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if> para lógica en el JSP --%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%-- Inicio del documento HTML con idioma español --%>
        <!DOCTYPE html>
        <html lang="es">

        <%-- Cabecera del documento con metadatos y recursos --%>
        <head>
            <%-- Codificación de caracteres UTF-8 para soporte de caracteres especiales --%>
            <meta charset="UTF-8">
            <%-- Configuración de viewport para diseño responsive --%>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <%-- Título de la página que aparece en el navegador --%>
            <title>Cierre de Inventario - Stock Final</title>
            <%-- Reutilizamos estilos del detalle de inventario --%>
            <link rel="stylesheet" href="../css/inv_detalle.css">
            <%-- Librería Font Awesome: Iconos (fa-exclamation-triangle, fa-lock, fa-arrow-left) --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Estilos CSS específicos para el cierre --%>
            <style>
                <%-- Estilo para instrucciones importantes --%>
                .inventario__instruccion--cierre {
                    color: #ff9800;
                    font-weight: bold;
                }

                <%-- Estilo para notas de advertencia --%>
                .inventario__nota--cierre {
                    color: #d32f2f;
                }
            </style>
        </head>

        <%-- Cuerpo principal del documento --%>
        <body>
            <%-- Cabecera con navegación y logo del sistema --%>
            <header>
                <nav class="navbar">
                    <%-- Logo del sistema que aparece en la barra de navegación --%>
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <%-- Contenido principal de la página --%>
            <main class="inventario">
                <%-- Encabezado del formulario de cierre --%>
                <section class="inventario__encabezado">
                    <%-- Título principal del formulario --%>
                    <h1 class="inventario__titulo">Cierre de Inventario (Contabilidad)</h1>
                    <%-- Instrucción principal para el usuario --%>
                    <p class="inventario__instruccion inventario__instruccion--cierre">Ingrese la cantidad física final
                        para cuadrar caja</p>

                    <%-- Mensaje de error si hay problemas con el tiempo --%>
                    <c:if test="${not empty param.error_tiempo}">
                        <div class="alerta"
                            style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; border: 1px solid #f5c6cb;">
                            <%-- Icono de advertencia y mensaje de error --%>
                            <i class="fas fa-exclamation-triangle"></i> ${param.error_tiempo}
                        </div>
                    </c:if>
                </section>

                <%-- Formulario para enviar los datos de cierre --%>
                <form action="../InventarioServlet" method="POST">
                    <%-- Campo oculto que indica la acción de finalizar --%>
                    <input type="hidden" name="action" value="finalizar_inventario">

                    <%-- Sección con la tabla de productos --%>
                    <section class="inventario__tabla">
                        <%-- Contenedor con scroll para tablas largas --%>
                        <div class="inventario__contenedor-scroll">
                            <%-- Tabla principal del formulario --%>
                            <table class="inventario__tabla-contenido">
                                <%-- Encabezado de la tabla --%>
                                <thead class="inventario__cabecera-tabla">
                                    <%-- Fila de encabezados --%>
                                    <tr class="inventario__fila inventario__fila--cabecera">
                                        <%-- Columnas de la tabla --%>
                                        <th class="inventario__celda">Producto</th>
                                        <th class="inventario__celda">Stock Inicial</th>
                                        <th class="inventario__celda">Stock Final Físico</th>
                                    </tr>
                                </thead>
                                <%-- Cuerpo de la tabla con los datos --%>
                                <tbody class="inventario__cuerpo-tabla">
                                    <%-- Itera sobre la lista de detalles del inventario --%>
                                    <c:forEach var="det" items="${listaDetalles}">
                                        <%-- Fila individual para cada producto --%>
                                        <tr class="inventario__fila">
                                            <%-- Nombre del producto --%>
                                            <td class="inventario__celda">${det.nombreProducto}</td>
                                            <%-- Cantidad inicial registrada --%>
                                            <td class="inventario__celda">${det.cantidadInicial}</td>
                                            <%-- Campo para ingresar la cantidad final --%>
                                            <td class="inventario__celda">
                                                <%-- Campo oculto con el ID del producto --%>
                                                <input type="hidden" name="id_producto" value="${det.idProducto}">
                                                <%-- Campo numérico para la cantidad final física --%>
                                                <input type="number" name="cantidad_final"
                                                    class="inventario__entrada-cantidad" step="0.01"
                                                    placeholder="Cantidad actual" required min="0" max="100000">
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <%-- Contenedor con botones de acción --%>
                    <div class="contenedor__boton" style="text-align: center; margin-top: 20px;">
                        <%-- Nota importante sobre el cierre --%>
                        <p class="inventario__nota inventario__nota--cierre">
                            ¡Atención! Al finalizar, el periodo de inventario se cerrará y no podrá agregar más ventas a
                            este periodo.
                        </p>
                        <%-- Botón principal para finalizar y cerrar --%>
                        <button type="submit" class="inventario__boton-finalizar"
                            style="border: none; cursor: pointer; background-color: #ff9800;">
                            <%-- Icono de candado para indicar cierre --%>
                            <i class="fas fa-lock inventario__icono-finalizar"></i>
                            Finalizar y Cerrar Periodo
                        </button>
                        <br><br>
                        <%-- Enlace para volver sin cerrar --%>
                        <a href="../view/menu_inventario.jsp" style="text-decoration: none; color: #666;">
                            <%-- Icono de flecha para indicar volver --%>
                            <i class="fas fa-arrow-left"></i> Volver sin cerrar
                        </a>
                    </div>
                </form>
            </main>
            <%-- Script para validación de entrada de datos --%>
            <script>
                // Evitar que el usuario escriba el signo menos (-) o la letra 'e'
                document.querySelectorAll('.inventario__entrada-cantidad').forEach(input => {
                    // Se ejecuta al presionar una tecla
                    input.addEventListener('keydown', function(e) {
                        // Previene la entrada de caracteres no permitidos
                        if (e.key === '-' || e.key === 'e' || e.key === '+') {
                            e.preventDefault();
                        }
                    });
                    
                    // Asegurar que si pegan un valor negativo, se convierta a 0 al perder el foco
                    input.addEventListener('blur', function() {
                        // Si el valor es negativo, lo convierte a 0
                        if (this.value < 0) {
                            this.value = 0;
                        }
                    });
                });
            </script>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>