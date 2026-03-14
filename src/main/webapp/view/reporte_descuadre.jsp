<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reporte de Descuadre</title>
                <link rel="stylesheet" href="../css/inv_detalle.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <style>
                    .reporte-container {
                        width: 90%;
                        margin: 20px auto;
                    }

                    .msg-exito {
                        background-color: #d4edda;
                        color: #155724;
                        padding: 15px;
                        border-radius: 8px;
                        text-align: center;
                        margin-bottom: 20px;
                        font-weight: bold;
                        border: 1px solid #c3e6cb;
                        font-size: 18px;
                    }

                    .tabla-descuadre {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 15px;
                    }

                    .tabla-descuadre th {
                        background-color: #333;
                        color: white;
                        padding: 12px;
                        text-align: center;
                    }

                    .tabla-descuadre td {
                        padding: 10px 12px;
                        text-align: center;
                        border-bottom: 1px solid #ddd;
                    }

                    .tabla-descuadre tr:hover {
                        background-color: #f5f5f5;
                    }

                    .positivo {
                        color: #27ae60;
                        font-weight: bold;
                    }

                    .negativo {
                        color: #e74c3c;
                        font-weight: bold;
                    }

                    .neutro {
                        color: #666;
                    }

                    .resumen-total {
                        margin-top: 25px;
                        padding: 20px;
                        border-radius: 8px;
                        text-align: center;
                        font-size: 18px;
                    }

                    .resumen-ganancia {
                        background-color: #d4edda;
                        color: #155724;
                        border: 2px solid #27ae60;
                    }

                    .resumen-perdida {
                        background-color: #f8d7da;
                        color: #721c24;
                        border: 2px solid #e74c3c;
                    }

                    .resumen-cuadrado {
                        background-color: #d1ecf1;
                        color: #0c5460;
                        border: 2px solid #17a2b8;
                    }

                    .btn-volver {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 12px 30px;
                        background-color: #333;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                    }

                    .btn-volver:hover {
                        background-color: #555;
                    }
                </style>
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                    </nav>
                </header>

                <main>
                    <div class="reporte-container">
                        <c:if test="${not empty mensajeExito}">
                            <div class="msg-exito">
                                <i class="fa-solid fa-circle-check"></i> ${mensajeExito}
                            </div>
                        </c:if>

                        <h2 style="text-align: center;">
                            <i class="fa-solid fa-scale-balanced"></i> Reporte de Descuadre
                        </h2>
                        <p style="text-align: center; color: #666;">
                            Comparación entre stock inicial y conteo físico final de cada producto
                        </p>

                        <table class="tabla-descuadre">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Stock Inicial</th>
                                    <th>Conteo Final</th>
                                    <th>Diferencia (Uds)</th>
                                    <th>Precio Unit.</th>
                                    <th>Descuadre ($)</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="totalDescuadre" value="0" />
                                <c:forEach var="det" items="${listaDescuadre}">
                                    <%-- Calcular diferencia y descuadre --%>
                                        <c:set var="diferencia" value="${det.cantidadFinal - det.cantidadInicial}" />
                                        <c:set var="descuadreDinero" value="${diferencia * det.precioUnitario}" />
                                        <tr>
                                            <td><strong>${det.nombreProducto}</strong></td>
                                            <td>${det.cantidadInicial}</td>
                                            <td>${det.cantidadFinal}</td>
                                            <td
                                                class="${diferencia > 0 ? 'positivo' : (diferencia < 0 ? 'negativo' : 'neutro')}">
                                                <c:choose>
                                                    <c:when test="${diferencia > 0}">+${diferencia}</c:when>
                                                    <c:when test="${diferencia < 0}">${diferencia}</c:when>
                                                    <c:otherwise>0</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>$
                                                <fmt:formatNumber value="${det.precioUnitario}" pattern="#,##0" />
                                            </td>
                                            <td
                                                class="${descuadreDinero > 0 ? 'positivo' : (descuadreDinero < 0 ? 'negativo' : 'neutro')}">
                                                <c:choose>
                                                    <c:when test="${descuadreDinero > 0}">
                                                        +$
                                                        <fmt:formatNumber value="${descuadreDinero}" pattern="#,##0" />
                                                    </c:when>
                                                    <c:when test="${descuadreDinero < 0}">
                                                        -$
                                                        <fmt:formatNumber value="${descuadreDinero * -1}"
                                                            pattern="#,##0" />
                                                    </c:when>
                                                    <c:otherwise>$ 0</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${diferencia > 0}">
                                                        <span class="positivo"><i class="fa-solid fa-arrow-up"></i>
                                                            Sobrante</span>
                                                    </c:when>
                                                    <c:when test="${diferencia < 0}">
                                                        <span class="negativo"><i class="fa-solid fa-arrow-down"></i>
                                                            Faltante</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="neutro"><i class="fa-solid fa-check"></i>
                                                            Cuadrado</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <%-- Acumular total --%>
                                            <c:set var="totalDescuadre" value="${totalDescuadre + descuadreDinero}" />
                                </c:forEach>
                            </tbody>
                        </table>

                        <%-- Resumen total --%>
                            <c:choose>
                                <c:when test="${totalDescuadre > 0}">
                                    <c:set var="claseResumen" value="resumen-ganancia" />
                                    <c:set var="textoResumen" value="Sobrante total: +$ " />
                                </c:when>
                                <c:when test="${totalDescuadre < 0}">
                                    <c:set var="claseResumen" value="resumen-perdida" />
                                    <c:set var="textoResumen" value="Pérdida total por faltantes: -$ " />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="claseResumen" value="resumen-cuadrado" />
                                    <c:set var="textoResumen" value="¡Inventario cuadrado! No hay descuadre." />
                                </c:otherwise>
                            </c:choose>

                            <div class="resumen-total ${claseResumen}">
                                <i class="fa-solid fa-calculator"></i> <strong>
                                    ${textoResumen}
                                    <c:if test="${totalDescuadre != 0}">
                                        <fmt:formatNumber
                                            value="${totalDescuadre > 0 ? totalDescuadre : -totalDescuadre}"
                                            pattern="#,##0" />
                                    </c:if>
                                </strong>
                            </div>

                            <div style="text-align: center;">
                                <c:choose>
                                    <c:when test="${modoHistorial}">
                                        <a href="../InformeServlet" class="btn-volver"
                                            style="background-color: #7f8c8d;">
                                            <i class="fa-solid fa-list"></i> Volver a Informes
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="../NegocioServlet" class="btn-volver">
                                            <i class="fa-solid fa-arrow-left"></i> Volver a Mis Bares
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                    </div>
                </main>
            </body>

            </html>