<%--=====================================================================
    VISTA JSP: visualizar_ventas.jsp - Historial de Ventas del Inventario
    
    QUIÉN LA MUESTRA: VentaServlet (GET con action=listar) → 
    request.getRequestDispatcher("view/visualizar_ventas.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (VentaServlet):
    - ${listaVentas} → List<Venta>. Viene de: VentaDAO.listarVentasPorInventario()
    
    Cada Venta tiene: idVenta, fechaVenta, totalVenta
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idInventarioActual} → ID del inventario activo
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Ver detalle: GET → VentaServlet?action=ver_detalle&id_venta=X
    - Regresar al menú: GET → menu_inventario.jsp
    
    IMPORTANCIA:
    - Permite revisar todo el historial de ventas del período actual
    - Facilita el análisis de rendimiento comercial
    - Proporciona acceso detallado a cada transacción
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:choose>, <c:when>, <c:otherwise> --%>
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
                <%-- Título de la página que aparece en el navegador --%>
                <title>Visualizar Ventas</title>
                <%-- Reutilizamos estilos del menú de inventario --%>
                <link rel="stylesheet" href="../css/menu_inventario.css">
                <%-- Librería Font Awesome: Iconos (fa-arrow-left, fa-eye) --%>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <%-- Estilos CSS específicos para esta página --%>
                <style>
                    <%-- Estilos básicos para la tabla de reporte (Inline por brevedad) --%>
                    .contenedor-tabla {
                        max-width: 90%;
                        margin: 40px auto;
                        background: white;
                        padding: 20px;
                        border-radius: 10px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    <%-- Tabla principal que muestra las ventas --%>
                    .tabla-ventas {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                    }

                    <%-- Estilo para celdas de la tabla --%>
                    .tabla-ventas th,
                    .tabla-ventas td {
                        padding: 12px 15px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }

                    <%-- Estilo para encabezados de la tabla --%>
                    .tabla-ventas th {
                        background-color: #2c3e50;
                        color: white;
                    }

                    <%-- Efecto hover para filas de la tabla --%>
                    .tabla-ventas tr:hover {
                        background-color: #f1f1f1;
                    }

                    <%-- Botón para regresar al menú --%>
                    .btn-regresar-fix {
                        display: inline-block;
                        margin-bottom: 20px;
                        color: #2c3e50;
                        text-decoration: none;
                        font-weight: bold;
                    }

                    <%-- Estilo para resaltar totales --%>
                    .total-highlight {
                        color: #2ecc71;
                        font-weight: bold;
                    }

                    <%-- Encabezado del reporte --%>
                    .reporte-header {
                        text-align: center;
                        margin-bottom: 30px;
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
                    <%-- Contenedor principal de la tabla --%>
                    <div class="contenedor-tabla">
                        <%-- Botón para volver al menú principal --%>
                        <a href="../view/menu_inventario.jsp" class="btn-regresar-fix">
                            <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                        </a>

                        <%-- Encabezado del reporte --%>
                        <div class="reporte-header">
                            <%-- Título principal del reporte --%>
                            <h1>Historial de Ventas</h1>
                            <%-- Subtítulo descriptivo --%>
                            <p>Registro de todas las ventas realizadas</p>
                        </div>

                        <%-- Tabla que muestra el historial de ventas --%>
                        <table class="tabla-ventas">
                            <%-- Encabezado de la tabla --%>
                            <thead>
                                <tr>
                                    <%-- Columnas de la tabla --%>
                                    <th>ID Venta</th>
                                    <th>Fecha</th>
                                    <th>Total Venta</th>
                                    <th>Acciones</th> <%-- Para futuro ver detalle --%>
                                </tr>
                            </thead>
                            <%-- Cuerpo de la tabla con los datos --%>
                            <tbody>
                                <%-- Lógica para mostrar datos o mensaje vacío --%>
                                <c:choose>
                                    <%-- Si no hay ventas registradas --%>
                                    <c:when test="${empty listaVentas}">
                                        <tr>
                                            <%-- Mensaje centrado ocupando todas las columnas --%>
                                            <td colspan="4" style="text-align: center; padding: 30px;">
                                                No se han registrado ventas aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <%-- Si hay ventas registradas --%>
                                    <c:otherwise>
                                        <%-- Itera sobre la lista de ventas --%>
                                        <c:forEach var="v" items="${listaVentas}">
                                            <%-- Fila individual para cada venta --%>
                                            <tr>
                                                <%-- ID de la venta --%>
                                                <td>#${v.idVenta}</td>
                                                <%-- Fecha de la venta --%>
                                                <td>${v.fechaVenta}</td>
                                                <%-- Total de la venta con formato de moneda --%>
                                                <td class="total-highlight">
                                                    $
                                                    <%-- Formatea el número con separador de miles y decimales --%>
                                                    <fmt:formatNumber value="${v.totalVenta}" pattern="#,##0.00" />
                                                </td>
                                                <%-- Columna de acciones --%>
                                                <td>
                                                    <%-- Botón para ver detalle de la venta --%>
                                                    <a href="../VentaServlet?action=ver_detalle&id_venta=${v.idVenta}"
                                                        style="color:#3498db; text-decoration: none; font-size: 1.2em;"
                                                        title="Ver Productos">
                                                        <%-- Icono de ojo para ver detalles --%>
                                                        <i class="fa-solid fa-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </main>

                <%-- Pie de página (vacío en este caso) --%>
                <footer>
                </footer>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>