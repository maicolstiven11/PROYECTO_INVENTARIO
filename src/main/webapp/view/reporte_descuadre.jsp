<%--=====================================================================
    VISTA JSP: reporte_descuadre.jsp - Reporte de Descuadre de Inventario
    
    QUIÉN LA MUESTRA: InventarioServlet (GET con action=mostrar_descuadre) → 
    request.getRequestDispatcher("view/reporte_descuadre.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (InventarioServlet):
    - ${listaDescuadre} → List<Descuadre>. Viene de: InventarioDAO.calcularDescuadre()
    - ${mensajeExito} → String. Mensaje de éxito al cerrar inventario
    - ${modoHistorial} → Boolean. Indica si se accede desde historial
    
    Cada Descuadre tiene: nombreProducto, cantidadInicial, cantidadFinal, precioUnitario
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idInventarioActual} → ID del inventario cerrado
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Volver a informes: GET → InformeServlet (si modoHistorial=true)
    - Volver a bares: GET → NegocioServlet (si modoHistorial=false)
    
    IMPORTANCIA:
    - Es el resultado final del proceso de cierre de inventario
    - Muestra diferencias entre stock teórico y físico
    - Calcula pérdidas o ganancias por descuadre
    - Fundamental para control y auditoría del negocio
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:set>, <c:choose>, <c:when>, <c:otherwise> --%>
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
                <title>Reporte de Descuadre</title>
                <%-- Reutilizamos estilos del detalle de inventario --%>
                <link rel="stylesheet" href="../css/inv_detalle.css">
                <%-- Librería Font Awesome: Iconos (fa-circle-check, fa-scale-balanced, fa-arrow-up, fa-arrow-down, fa-check, fa-calculator, fa-list, fa-arrow-left) --%>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <%-- Estilos CSS específicos para el reporte --%>
                <style>
                    <%-- Contenedor principal del reporte --%>
                    .reporte-container {
                        width: 90%;
                        margin: 20px auto;
                    }

                    <%-- Mensaje de éxito cuando se cierra el inventario --%>
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

                    <%-- Tabla principal del reporte de descuadre --%>
                    .tabla-descuadre {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 15px;
                    }

                    <%-- Estilo para encabezados de la tabla --%>
                    .tabla-descuadre th {
                        background-color: #333;
                        color: white;
                        padding: 12px;
                        text-align: center;
                    }

                    <%-- Estilo para celdas de la tabla --%>
                    .tabla-descuadre td {
                        padding: 10px 12px;
                        text-align: center;
                        border-bottom: 1px solid #ddd;
                    }

                    <%-- Efecto hover para filas de la tabla --%>
                    .tabla-descuadre tr:hover {
                        background-color: #f5f5f5;
                    }

                    <%-- Estilo para valores positivos (sobrantes) --%>
                    .positivo {
                        color: #27ae60;
                        font-weight: bold;
                    }

                    <%-- Estilo para valores negativos (faltantes) --%>
                    .negativo {
                        color: #e74c3c;
                        font-weight: bold;
                    }

                    <%-- Estilo para valores neutros (cuadrado) --%>
                    .neutro {
                        color: #666;
                    }

                    <%-- Contenedor del resumen total --%>
                    .resumen-total {
                        margin-top: 25px;
                        padding: 20px;
                        border-radius: 8px;
                        text-align: center;
                        font-size: 18px;
                    }

                    <%-- Estilo para resumen de ganancia --%>
                    .resumen-ganancia {
                        background-color: #d4edda;
                        color: #155724;
                        border: 2px solid #27ae60;
                    }

                    <%-- Estilo para resumen de pérdida --%>
                    .resumen-perdida {
                        background-color: #f8d7da;
                        color: #721c24;
                        border: 2px solid #e74c3c;
                    }

                    <%-- Estilo para resumen cuadrado --%>
                    .resumen-cuadrado {
                        background-color: #d1ecf1;
                        color: #0c5460;
                        border: 2px solid #17a2b8;
                    }

                    <%-- Botón para volver --%>
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

                    <%-- Efecto hover para el botón --%>
                    .btn-volver:hover {
                        background-color: #555;
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
                    <%-- Contenedor principal del reporte --%>
                    <div class="reporte-container">
                        <%-- Mensaje de éxito si el inventario se cerró correctamente --%>
                        <c:if test="${not empty mensajeExito}">
                            <div class="msg-exito">
                                <%-- Icono de check y mensaje de éxito --%>
                                <i class="fa-solid fa-circle-check"></i> ${mensajeExito}
                            </div>
                        </c:if>

                        <%-- Título principal del reporte --%>
                        <h2 style="text-align: center;">
                            <%-- Icono de balanza y título --%>
                            <i class="fa-solid fa-scale-balanced"></i> Reporte de Descuadre
                        </h2>
                        <%-- Subtítulo descriptivo --%>
                        <p style="text-align: center; color: #666;">
                            Comparación entre stock inicial y conteo físico final de cada producto
                        </p>

                        <%-- Tabla principal del reporte --%>
                        <table class="tabla-descuadre">
                            <%-- Encabezado de la tabla --%>
                            <thead>
                                <tr>
                                    <%-- Columnas de la tabla --%>
                                    <th>Producto</th>
                                    <th>Stock Inicial</th>
                                    <th>Conteo Final</th>
                                    <th>Diferencia (Uds)</th>
                                    <th>Precio Unit.</th>
                                    <th>Descuadre ($)</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <%-- Cuerpo de la tabla con los datos --%>
                            <tbody>
                                <%-- Inicializa variable para calcular el total del descuadre --%>
                                <c:set var="totalDescuadre" value="0" />
                                <%-- Itera sobre la lista de descuadres --%>
                                <c:forEach var="det" items="${listaDescuadre}">
                                    <%-- Calcular diferencia y descuadre --%>
                                    <%-- Calcula la diferencia entre conteo final e inicial --%>
                                        <c:set var="diferencia" value="${det.cantidadFinal - det.cantidadInicial}" />
                                        <%-- Calcula el descuadre en dinero --%>
                                        <c:set var="descuadreDinero" value="${diferencia * det.precioUnitario}" />
                                        <%-- Fila individual para cada producto --%>
                                        <tr>
                                            <%-- Nombre del producto en negrita --%>
                                            <td><strong>${det.nombreProducto}</strong></td>
                                            <%-- Stock inicial --%>
                                            <td>${det.cantidadInicial}</td>
                                            <%-- Conteo final --%>
                                            <td>${det.cantidadFinal}</td>
                                            <%-- Diferencia con color según signo --%>
                                            <td
                                                class="${diferencia > 0 ? 'positivo' : (diferencia < 0 ? 'negativo' : 'neutro')}">
                                                <%-- Muestra la diferencia con signo --%>
                                                <c:choose>
                                                    <%-- Si es positivo, muestra con + --%>
                                                    <c:when test="${diferencia > 0}">+${diferencia}</c:when>
                                                    <%-- Si es negativo, muestra el valor negativo --%>
                                                    <c:when test="${diferencia < 0}">${diferencia}</c:when>
                                                    <%-- Si es cero, muestra 0 --%>
                                                    <c:otherwise>0</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <%-- Precio unitario formateado --%>
                                            <td>$
                                                <fmt:formatNumber value="${det.precioUnitario}" pattern="#,##0" />
                                            </td>
                                            <%-- Descuadre en dinero con color según signo --%>
                                            <td
                                                class="${descuadreDinero > 0 ? 'positivo' : (descuadreDinero < 0 ? 'negativo' : 'neutro')}">
                                                <%-- Muestra el descuadre con formato --%>
                                                <c:choose>
                                                    <%-- Si es positivo, muestra con + --%>
                                                    <c:when test="${descuadreDinero > 0}">
                                                        +$
                                                        <fmt:formatNumber value="${descuadreDinero}" pattern="#,##0" />
                                                    </c:when>
                                                    <%-- Si es negativo, muestra con - y valor absoluto --%>
                                                    <c:when test="${descuadreDinero < 0}">
                                                        -$
                                                        <fmt:formatNumber value="${descuadreDinero * -1}"
                                                            pattern="#,##0" />
                                                    </c:when>
                                                    <%-- Si es cero, muestra $0 --%>
                                                    <c:otherwise>$ 0</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <%-- Estado del descuadre --%>
                                            <td>
                                                <c:choose>
                                                    <%-- Si hay sobrante --%>
                                                    <c:when test="${diferencia > 0}">
                                                        <span class="positivo"><i class="fa-solid fa-arrow-up"></i>
                                                            Sobrante</span>
                                                    </c:when>
                                                    <%-- Si hay faltante --%>
                                                    <c:when test="${diferencia < 0}">
                                                        <span class="negativo"><i class="fa-solid fa-arrow-down"></i>
                                                            Faltante</span>
                                                    </c:when>
                                                    <%-- Si está cuadrado --%>
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
                            <%-- Determina el estilo y texto del resumen según el total --%>
                            <c:choose>
                                <%-- Si hay ganancia por sobrantes --%>
                                <c:when test="${totalDescuadre > 0}">
                                    <c:set var="claseResumen" value="resumen-ganancia" />
                                    <c:set var="textoResumen" value="Sobrante total: +$ " />
                                </c:when>
                                <%-- Si hay pérdida por faltantes --%>
                                <c:when test="${totalDescuadre < 0}">
                                    <c:set var="claseResumen" value="resumen-perdida" />
                                    <c:set var="textoResumen" value="Pérdida total por faltantes: -$ " />
                                </c:when>
                                <%-- Si está cuadrado --%>
                                <c:otherwise>
                                    <c:set var="claseResumen" value="resumen-cuadrado" />
                                    <c:set var="textoResumen" value="¡Inventario cuadrado! No hay descuadre." />
                                </c:otherwise>
                            </c:choose>

                            <%-- Muestra el resumen total con el estilo determinado --%>
                            <div class="resumen-total ${claseResumen}">
                                <%-- Icono de calculadora y texto del resumen --%>
                                <i class="fa-solid fa-calculator"></i> <strong>
                                    ${textoResumen}
                                    <%-- Muestra el valor total si no es cero --%>
                                    <c:if test="${totalDescuadre != 0}">
                                        <fmt:formatNumber
                                            value="${totalDescuadre > 0 ? totalDescuadre : -totalDescuadre}"
                                            pattern="#,##0" />
                                    </c:if>
                                </strong>
                            </div>

                            <%-- Botón de volver según el modo de acceso --%>
                            <div style="text-align: center;">
                                <c:choose>
                                    <%-- Si viene del historial de informes --%>
                                    <c:when test="${modoHistorial}">
                                        <a href="../InformeServlet" class="btn-volver"
                                            style="background-color: #7f8c8d;">
                                            <%-- Icono de lista y texto --%>
                                            <i class="fa-solid fa-list"></i> Volver a Informes
                                        </a>
                                    </c:when>
                                    <%-- Si viene del cierre de inventario --%>
                                    <c:otherwise>
                                        <a href="../NegocioServlet" class="btn-volver">
                                            <%-- Icono de flecha y texto --%>
                                            <i class="fa-solid fa-arrow-left"></i> Volver a Mis Bares
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                    </div>
                </main>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>