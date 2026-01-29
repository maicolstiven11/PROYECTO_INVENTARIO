<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Visualizar Ventas</title>
                <!-- Usamos estilos similares a otras listas, o creamos uno nuevo -->
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu_inventario.css">
                <!-- Si hubiera un CSS específico para tablas, lo importaríamos aquí -->
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <style>
                    /* Estilos básicos para la tabla de reporte (Inline por brevedad, luego se puede mover) */
                    .contenedor-tabla {
                        max-width: 90%;
                        margin: 40px auto;
                        background: white;
                        padding: 20px;
                        border-radius: 10px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .tabla-ventas {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                    }

                    .tabla-ventas th,
                    .tabla-ventas td {
                        padding: 12px 15px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }

                    .tabla-ventas th {
                        background-color: #2c3e50;
                        color: white;
                    }

                    .tabla-ventas tr:hover {
                        background-color: #f1f1f1;
                    }

                    .btn-regresar-fix {
                        display: inline-block;
                        margin-bottom: 20px;
                        color: #2c3e50;
                        text-decoration: none;
                        font-weight: bold;
                    }

                    .total-highlight {
                        color: #2ecc71;
                        font-weight: bold;
                    }

                    .reporte-header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                </style>
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="${pageContext.request.contextPath}/assets/img/LOGO.png"
                            alt="logo_sistema">
                    </nav>
                </header>

                <main>
                    <div class="contenedor-tabla">
                        <a href="${pageContext.request.contextPath}/view/menu_inventario.html" class="btn-regresar-fix">
                            <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                        </a>

                        <div class="reporte-header">
                            <h1>Historial de Ventas</h1>
                            <p>Registro de todas las ventas realizadas</p>
                        </div>

                        <table class="tabla-ventas">
                            <thead>
                                <tr>
                                    <th>ID Venta</th>
                                    <th>Fecha</th>
                                    <th>Total Venta</th>
                                    <th>Acciones</th> <!-- Para futuro ver detalle -->
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty listaVentas}">
                                        <tr>
                                            <td colspan="4" style="text-align: center; padding: 30px;">
                                                No se han registrado ventas aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="v" items="${listaVentas}">
                                            <tr>
                                                <td>#${v.idVenta}</td>
                                                <td>${v.fechaVenta}</td>
                                                <td class="total-highlight">
                                                    $
                                                    <fmt:formatNumber value="${v.totalVenta}" pattern="#,##0.00" />
                                                </td>
                                                <td>
                                                    <!-- Botón Ver Detalle -->
                                                    <a href="${pageContext.request.contextPath}/VentaServlet?action=ver_detalle&id_venta=${v.idVenta}"
                                                        style="color:#3498db; text-decoration: none; font-size: 1.2em;"
                                                        title="Ver Productos">
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

                <footer>
                </footer>
            </body>

            </html>