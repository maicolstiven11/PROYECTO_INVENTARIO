<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Detalle de Venta #${idVenta}</title>
                <!-- Reutilizamos estilos -->
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu_inventario.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <style>
                    .contenedor-detalle {
                        max-width: 800px;
                        margin: 40px auto;
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                    }

                    .detalle-header {
                        text-align: center;
                        border-bottom: 2px solid #ecf0f1;
                        padding-bottom: 20px;
                        margin-bottom: 20px;
                    }

                    .detalle-header h1 {
                        color: #2c3e50;
                        margin: 0;
                    }

                    .tabla-detalle {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    .tabla-detalle th,
                    .tabla-detalle td {
                        padding: 12px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }

                    .tabla-detalle th {
                        background-color: #34495e;
                        color: white;
                    }

                    .total-row {
                        font-weight: bold;
                        font-size: 1.2em;
                        background-color: #f8f9fa;
                    }

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

                    .btn-volver:hover {
                        background-color: #2980b9;
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
                    <div class="contenedor-detalle">
                        <div class="detalle-header">
                            <h1>Compra #${idVenta}</h1>
                            <p>Detalle de productos</p>
                        </div>

                        <table class="tabla-detalle">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Cant.</th>
                                    <th>P. Unitario</th>
                                    <th>Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="granTotal" value="0" />
                                <c:forEach var="d" items="${listaDetalles}">
                                    <tr>
                                        <td>${d.nombreProducto}</td>
                                        <td>${d.cantidad}</td>
                                        <td>$
                                            <fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0" />
                                        </td>
                                        <td>$
                                            <fmt:formatNumber value="${d.subtotal}" pattern="#,##0" />
                                        </td>
                                    </tr>
                                    <c:set var="granTotal" value="${granTotal + d.subtotal}" />
                                </c:forEach>

                                <tr class="total-row">
                                    <td colspan="3" style="text-align: right;">TOTAL:</td>
                                    <td>$
                                        <fmt:formatNumber value="${granTotal}" pattern="#,##0" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div style="text-align: center;">
                            <a href="${pageContext.request.contextPath}/VentaServlet?action=listar" class="btn-volver">
                                <i class="fa-solid fa-arrow-left"></i> Volver al Historial
                            </a>
                        </div>
                    </div>
                </main>
            </body>

            </html>