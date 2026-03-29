<%--=====================================================================
    VISTA JSP: visualizar_informes.jsp - Dashboard de Informes y Estadísticas
    
    QUIÉN LA MUESTRA: InformeServlet (GET con idInventario) → 
    request.getRequestDispatcher("view/visualizar_informes.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (InformeServlet):
    - ${totalVentas} → double. Suma total de ventas del período
    - ${totalGastos} → double. Suma total de gastos del período  
    - ${totalPedidos} → double. Suma total de pedidos a proveedores
    - ${gananciaNeta} → double. totalVentas - totalGastos
    - ${porcentajeVentas} → double. Porcentaje de ventas vs total
    - ${porcentajeGastos} → double. Porcentaje de gastos vs total
    - ${porcentajePedidos} → double. Porcentaje de pedidos vs total
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.nombreNegocioActual} → Nombre del bar actual
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Ver otros periodos: GET → InformeServlet (lista de informes)
    - Regresar a bares: GET → NegocioServlet
    
    IMPORTANCIA:
    - Muestra dashboard visual con métricas clave del negocio
    - Permite análisis rápido de rentabilidad y flujo de caja
    - Facilita toma de decisiones basada en datos
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if>, <c:choose> para lógica --%>
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
                <title>Visualizar Informes</title>
                <%-- Hoja de estilos CSS para el dashboard de informes --%>
                <link rel="stylesheet" href="../css/visualizar_informes.css">
                <%-- Librería Font Awesome: Iconos (fa-chart-line, fa-money-bill-wave, fa-truck-fast, fa-sack-dollar, fa-arrow-left, fa-clock-rotate-left) --%>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
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
                    <%-- Título principal de la vista --%>
                    <h2>INFORMES Y ESTADÍSTICAS</h2>
                    <%-- Subtítulo que muestra el nombre del bar actual --%>
                    <p class="subtitulo-negocio">Bar: ${sessionScope.nombreNegocioActual}</p>

                    <%-- Dashboard Grid con tarjetas de métricas principales --%>
                    <div class="dashboard-grid">
                        <%-- Card de Ventas Totales --%>
                        <div class="card-informe card-ventas">
                            <%-- Título de la tarjeta --%>
                            <div class="card-informe__titulo">Ventas Totales</div>
                            <%-- Valor formateado con separadores de miles --%>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalVentas}" pattern="#,##0" />
                            </div>
                            <%-- Icono representativo de ventas --%>
                            <i class="fa-solid fa-chart-line card-informe__icono"></i>
                        </div>

                        <%-- Card de Gastos Totales --%>
                        <div class="card-informe card-gastos">
                            <%-- Título de la tarjeta --%>
                            <div class="card-informe__titulo">Gastos Totales</div>
                            <%-- Valor formateado con separadores de miles --%>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalGastos}" pattern="#,##0" />
                            </div>
                            <%-- Icono representativo de gastos --%>
                            <i class="fa-solid fa-money-bill-wave card-informe__icono"></i>
                        </div>

                        <%-- Card de Pedidos a Proveedores --%>
                        <div class="card-informe card-pedidos">
                            <%-- Título de la tarjeta --%>
                            <div class="card-informe__titulo">Pedidos a Proveedores</div>
                            <%-- Valor formateado con separadores de miles --%>
                            <div class="card-informe__valor">
                                ${'$'}
                                <fmt:formatNumber value="${totalPedidos}" pattern="#,##0" />
                            </div>
                            <%-- Icono representativo de pedidos --%>
                            <i class="fa-solid fa-truck-fast card-informe__icono"></i>
                        </div>

                        <%-- Card de Ganancia Neta --%>
                        <div class="card-informe card-ganancia">
                            <%-- Título de la tarjeta --%>
                            <div class="card-informe__titulo">Ganancia Neta</div>
                            <%-- Valor con clase especial si es negativa --%>
                            <div class="card-informe__valor ${gananciaNeta lt 0 ? 'ganancia-negativa' : ''}">
                                <%-- Lógica para mostrar signo y formato correcto --%>
                                <c:choose>
                                    <%-- Si la ganancia es positiva o cero, mostrar con signo $ --%>
                                    <c:when test="${gananciaNeta >= 0}">
                                        ${'$'}
                                        <fmt:formatNumber value="${gananciaNeta}" pattern="#,##0" />
                                    </c:when>
                                    <%-- Si la ganancia es negativa, mostrar con signo - y valor absoluto --%>
                                    <c:otherwise>
                                        -${'$'}
                                        <fmt:formatNumber value="${gananciaNeta * -1}" pattern="#,##0" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <%-- Icono representativo de ganancia --%>
                            <i class="fa-solid fa-sack-dollar card-informe__icono"></i>
                        </div>
                    </div>

                    <%-- Sección de Estadísticas Visuales con barras de progreso --%>
                    <div class="seccion-estadisticas">
                        <h3>Resumen Comparativo</h3>

                        <%-- Solución técnica para evitar problemas de validación CSS en JSP --%>
                        <%-- Metemos el atributo style completo dentro de variables JSTL --%>
                        <%-- Esto engaña al editor para que no intente validar CSS donde no puede --%>
                        <c:set var="attrEstiloVentas"
                            value='style="width: ${porcentajeVentas != null ? porcentajeVentas : 0}%;"' />
                        <c:set var="attrEstiloGastos"
                            value='style="width: ${porcentajeGastos != null ? porcentajeGastos : 0}%;"' />
                        <c:set var="attrEstiloPedidos"
                            value='style="width: ${porcentajePedidos != null ? porcentajePedidos : 0}%;"' />

                        <%-- Ahora el atributo 'style' solo recibe la variable limpia --%>
                        <%-- Barra de progreso para Ventas --%>
                        <div class="barra-progreso-contenedor">
                            <div class="barra-etiqueta">
                                <span>Ventas</span>
                                <%-- Muestra el porcentaje formateado --%>
                                <span>${porcentajeVentas}${'%'}</span>
                            </div>
                            <div class="barra-fondo">
                                <%-- Barra de relleno dinámica según porcentaje --%>
                                <div class="barra-relleno barra-ventas" ${attrEstiloVentas}></div>
                            </div>
                        </div>

                        <%-- Barra de progreso para Gastos --%>
                        <div class="barra-progreso-contenedor">
                            <div class="barra-etiqueta">
                                <span>Gastos</span>
                                <%-- Muestra el porcentaje formateado --%>
                                <span>${porcentajeGastos}${'%'}</span>
                            </div>
                            <div class="barra-fondo">
                                <%-- Barra de relleno dinámica según porcentaje --%>
                                <div class="barra-relleno barra-gastos" ${attrEstiloGastos}></div>
                            </div>
                        </div>

                        <%-- Barra de progreso para Pedidos --%>
                        <div class="barra-progreso-contenedor">
                            <div class="barra-etiqueta">
                                <span>Pedidos a Proveedores</span>
                                <%-- Muestra el porcentaje formateado --%>
                                <span>${porcentajePedidos}${'%'}</span>
                            </div>
                            <div class="barra-fondo">
                                <%-- Barra de relleno dinámica según porcentaje --%>
                                <div class="barra-relleno barra-pedidos" ${attrEstiloPedidos}></div>
                            </div>
                        </div>
                    </div>

                    <%-- Botones de navegación --%>
                    <%-- Botón para ver otros períodos (lista de informes) --%>
                    <a href="../InformeServlet" class="btn-regresar" style="background-color: #7f8c8d;">
                        <i class="fa-solid fa-clock-rotate-left"></i> Ver otros periodos
                    </a>
                    <%-- Botón para regresar a la lista de bares --%>
                    <a href="../NegocioServlet" class="btn-regresar">
                        <i class="fa-solid fa-arrow-left"></i> Regresar a Mis Bares
                    </a>
                </main>
                <%-- Pie de página (vacío en este caso) --%>
                <footer>
                </footer>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>