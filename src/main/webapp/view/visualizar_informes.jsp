<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Visualizar Informes</title>
                <link rel="stylesheet" href="../css/visualizar_informes.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                    </nav>
                </header>
                <main>
                    <h2>INFORMES Y ESTADÍSTICAS</h2>
                    <p class="subtitulo-negocio">Bar: ${sessionScope.nombreNegocioActual}</p>

                    <!-- Dashboard Grid con datos reales -->
                    <div class="dashboard-grid">
                        <!-- Card Ventas -->
                        <div class="card-informe card-ventas">
                            <div class="card-informe__titulo">Ventas Totales</div>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalVentas}" pattern="#,##0" />
                            </div>
                            <i class="fa-solid fa-chart-line card-informe__icono"></i>
                        </div>

                        <!-- Card Gastos -->
                        <div class="card-informe card-gastos">
                            <div class="card-informe__titulo">Gastos Totales</div>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalGastos}" pattern="#,##0" />
                            </div>
                            <i class="fa-solid fa-money-bill-wave card-informe__icono"></i>
                        </div>

                        <!-- Card Pedidos -->
                        <div class="card-informe card-pedidos">
                            <div class="card-informe__titulo">Pedidos a Proveedores</div>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalPedidos}" pattern="#,##0" />
                            </div>
                            <i class="fa-solid fa-truck-fast card-informe__icono"></i>
                        </div>

                        <!-- Card Ganancia -->
                        <div class="card-informe card-ganancia">
                            <div class="card-informe__titulo">Ganancia Neta</div>
                            <div class="card-informe__valor ${gananciaNeta lt 0 ? 'ganancia-negativa' : ''}">
                                <c:choose>
                                    <c:when test="${gananciaNeta >= 0}">
                                        ${'$'}
                                        <fmt:formatNumber value="${gananciaNeta}" pattern="#,##0" />
                                    </c:when>
                                    <c:otherwise>
                                        -${'$'}
                                        <fmt:formatNumber value="${gananciaNeta * -1}" pattern="#,##0" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <i class="fa-solid fa-sack-dollar card-informe__icono"></i>
                        </div>
                    </div>

                    <!-- Sección de Estadísticas Visuales -->
                    <div class="seccion-estadisticas">
                        <h3>Resumen Comparativo</h3>

                        <%-- 1. SOLUCIÓN NUCLEAR: Metemos incluso el "style=" dentro de la variable --%>
                            <%-- Esto engaña al editor para que no intente validar CSS donde no puede --%>
                                <c:set var="attrEstiloVentas"
                                    value='style="width: ${porcentajeVentas != null ? porcentajeVentas : 0}%;"' />
                                <c:set var="attrEstiloGastos"
                                    value='style="width: ${porcentajeGastos != null ? porcentajeGastos : 0}%;"' />
                                <c:set var="attrEstiloPedidos"
                                    value='style="width: ${porcentajePedidos != null ? porcentajePedidos : 0}%;"' />

                                <%-- 2. Ahora el atributo 'style' solo recibe la variable limpia --%>
                                    <div class="barra-progreso-contenedor">
                                        <div class="barra-etiqueta">
                                            <span>Ventas</span>
                                            <span>${porcentajeVentas}${'%'}</span>
                                        </div>
                                        <div class="barra-fondo">
                                            <div class="barra-relleno barra-ventas" ${attrEstiloVentas}></div>
                                        </div>
                                    </div>

                                    <div class="barra-progreso-contenedor">
                                        <div class="barra-etiqueta">
                                            <span>Gastos</span>
                                            <span>${porcentajeGastos}${'%'}</span>
                                        </div>
                                        <div class="barra-fondo">
                                            <div class="barra-relleno barra-gastos" ${attrEstiloGastos}></div>
                                        </div>
                                    </div>

                                    <div class="barra-progreso-contenedor">
                                        <div class="barra-etiqueta">
                                            <span>Pedidos a Proveedores</span>
                                            <span>${porcentajePedidos}${'%'}</span>
                                        </div>
                                        <div class="barra-fondo">
                                            <div class="barra-relleno barra-pedidos" ${attrEstiloPedidos}></div>
                                        </div>
                                    </div>
                    </div>

                    <a href="../InformeServlet" class="btn-regresar" style="background-color: #7f8c8d;">
                        <i class="fa-solid fa-clock-rotate-left"></i> Ver otros periodos
                    </a>
                    <a href="../NegocioServlet" class="btn-regresar">
                        <i class="fa-solid fa-arrow-left"></i> Regresar a Mis Bares
                    </a>
                </main>
                <footer>
                </footer>
            </body>

            </html>