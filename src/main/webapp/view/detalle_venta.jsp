<%--=====================================================================
    VISTA JSP: detalle_venta.jsp - Detalle Individual de Venta
    
    QUIÉN LA MUESTRA: VentaServlet (GET con idVenta) → 
    request.getRequestDispatcher("view/detalle_venta.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (VentaServlet):
    - ${idVenta} → Integer. ID de la venta a mostrar
    - ${listaDetalles} → List<DetalleVenta>. Viene de: VentaDAO.obtenerDetallesVenta()
    
    Cada DetalleVenta tiene: nombreProducto, cantidad, subtotal
    
    DATOS QUE USA DE LA SESIÓN:
    - Ninguno específico para esta vista
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Volver al historial: GET → VentaServlet?action=listar
    
    IMPORTANCIA:
    - Permite revisar en detalle cada venta realizada
    - Muestra el desglose de productos y cantidades
    - Facilita el control y auditoría de ventas
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:set> para lógica en el JSP --%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%-- Librería JSTL Format: Permite usar <fmt:formatNumber> para formatear números --%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <%-- Inicio del documento HTML con idioma español --%>
            <!DOCTYPE html>
            <html lang="es">

            <%-- Cabecera del documento con metadatos y recursos --%>
            <head>
                <%-- Codificación de caracteres UTF-8 para soporte de caracteres especiales --%>
                <meta charset="UTF-8">
                <%-- Configuración de viewport para diseño responsive --%>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <%-- Título dinámico con el ID de la venta --%>
                <title>Detalle de Venta #${idVenta}</title>
                <%-- Reutilizamos estilos del menú de inventario --%>
                <link rel="stylesheet" href="../css/menu_inventario.css">
                <%-- Librería Font Awesome: Iconos (fa-arrow-left) --%>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <%-- Estilos CSS específicos para esta página --%>
                <style>
                    <%-- Contenedor principal del detalle --%>
                    .contenedor-detalle {
                        max-width: 800px;
                        margin: 40px auto;
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                    }

                    <%-- Encabezado de la página de detalle --%>
                    .detalle-header {
                        text-align: center;
                        border-bottom: 2px solid #ecf0f1;
                        padding-bottom: 20px;
                        margin-bottom: 20px;
                    }

                    <%-- Estilo para el título principal --%>
                    .detalle-header h1 {
                        color: #2c3e50;
                        margin: 0;
                    }

                    <%-- Tabla que muestra los detalles de la venta --%>
                    .tabla-detalle {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    <%-- Estilo para celdas de la tabla --%>
                    .tabla-detalle th,
                    .tabla-detalle td {
                        padding: 12px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }

                    <%-- Estilo para encabezados de la tabla --%>
                    .tabla-detalle th {
                        background-color: #34495e;
                        color: white;
                    }

                    <%-- Estilo para la fila del total --%>
                    .total-row {
                        font-weight: bold;
                        font-size: 1.2em;
                        background-color: #f8f9fa;
                    }

                    <%-- Botón para volver al historial --%>
                    .btn-volver {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 10px 20px;
                        background-color: #3498db;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        transition: background 0.3s;
                    }

                    <%-- Efecto hover para el botón --%>
                    .btn-volver:hover {
                        background-color: #2980b9;
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
                <main>
                    <%-- Contenedor principal del detalle de venta --%>
                    <div class="contenedor-detalle">
                        <%-- Encabezado con información de la venta --%>
                        <div class="detalle-header">
                            <%-- Título con el número de la venta --%>
                            <h1>Compra #${idVenta}</h1>
                            <%-- Subtítulo descriptivo --%>
                            <p>Detalle de productos</p>
                        </div>

                        <%-- Tabla que muestra los productos de la venta --%>
                        <table class="tabla-detalle">
                            <%-- Encabezado de la tabla --%>
                            <thead>
                                <tr>
                                    <%-- Columnas de la tabla --%>
                                    <th>Producto</th>
                                    <th>Cant.</th>
                                    <th>Subtotal</th>
                                </tr>
                            </thead>
                            <%-- Cuerpo de la tabla con los datos --%>
                            <tbody>
                                <%-- Inicializa variable para calcular el gran total --%>
                                <c:set var="granTotal" value="0" />
                                <%-- Itera sobre la lista de detalles de la venta --%>
                                <c:forEach var="d" items="${listaDetalles}">
                                    <%-- Fila individual para cada detalle --%>
                                    <tr>
                                        <%-- Nombre del producto --%>
                                        <td>${d.nombreProducto}</td>
                                        <%-- Cantidad vendida --%>
                                        <td>${d.cantidad}</td>
                                        <%-- Subtotal formateado con separador de miles --%>
                                        <td>$
                                            <fmt:formatNumber value="${d.subtotal}" pattern="#,##0" />
                                        </td>
                                    </tr>
                                    <%-- Acumula el subtotal en el gran total --%>
                                    <c:set var="granTotal" value="${granTotal + d.subtotal}" />
                                </c:forEach>

                                <%-- Fila especial para mostrar el total --%>
                                <tr class="total-row">
                                    <%-- Celda que combina dos columnas --%>
                                    <td colspan="2" style="text-align: right;">TOTAL:</td>
                                    <%-- Total general formateado --%>
                                    <td>$
                                        <fmt:formatNumber value="${granTotal}" pattern="#,##0" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <%-- Contenedor centrado para el botón de volver --%>
                        <div style="text-align: center;">
                            <%-- Botón para volver al historial de ventas --%>
                            <a href="../VentaServlet?action=listar" class="btn-volver">
                                <%-- Icono de flecha para indicar volver --%>
                                <i class="fa-solid fa-arrow-left"></i> Volver al Historial
                            </a>
                        </div>
                    </div>
                </main>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>