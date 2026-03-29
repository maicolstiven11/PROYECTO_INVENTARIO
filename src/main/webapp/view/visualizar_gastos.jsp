<%--=====================================================================
    VISTA JSP: visualizar_gastos.jsp - Historial de Gastos del Inventario
    
    QUIÉN LA MUESTRA: GastoServlet (GET con action=listar) → 
    request.getRequestDispatcher("view/visualizar_gastos.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (GastoServlet):
    - ${listaGastos} → List<Gasto>. Viene de: GastoDAO.listarGastosPorInventario()
    
    Cada Gasto tiene: id_gastos, descripcion, cantidad, fecha, subtotal
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idInventarioActual} → ID del inventario activo
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Regresar al menú: GET → menu_inventario.jsp
    
    IMPORTANCIA:
    - Permite revisar todo el historial de gastos del período actual
    - Facilita el análisis de egresos y control de costos
    - Proporciona visibilidad completa de las salidas de dinero
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
                <title>Visualizar Gastos</title>
                <%-- Hoja de estilos CSS específica para visualización de gastos --%>
                <link rel="stylesheet" href="../css/visualizar_gastos.css">
                <%-- Librería Font Awesome: Iconos (fa-arrow-left) --%>
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
                    <%-- Título principal de la página --%>
                    <h2>GASTOS REGISTRADOS</h2>

                    <%-- Contenedor principal de la tabla --%>
                    <div class="table-container">
                        <%-- Tabla que muestra el historial de gastos --%>
                        <table class="data-table">
                            <%-- Encabezado de la tabla --%>
                            <thead class="data-table__head">
                                <%-- Fila de encabezados --%>
                                <tr class="data-table__row data-table__row--head">
                                    <%-- Columnas de la tabla --%>
                                    <th class="data-table__cell data-table__cell--head">ID</th>
                                    <th class="data-table__cell data-table__cell--head">Descripción</th>
                                    <th class="data-table__cell data-table__cell--head">Cantidad</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha</th>
                                    <th class="data-table__cell data-table__cell--head">Subtotal</th>
                                </tr>
                            </thead>
                            <%-- Cuerpo de la tabla con los datos --%>
                            <tbody class="data-table__body">
                                <%-- Iteramos sobre la lista que viene del Servlet --%>
                                <c:choose>
                                    <%-- Si no hay gastos registrados --%>
                                    <c:when test="${empty listaGastos}">
                                        <tr>
                                            <%-- Mensaje centrado ocupando todas las columnas --%>
                                            <td colspan="5" class="mensaje-vacio">
                                                No se han registrado gastos aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <%-- Si hay gastos registrados --%>
                                    <c:otherwise>
                                        <%-- Itera sobre la lista de gastos --%>
                                        <c:forEach var="g" items="${listaGastos}">
                                            <%-- Fila individual para cada gasto --%>
                                            <tr class="data-table__row">
                                                <%-- ID del gasto --%>
                                                <td class="data-table__cell">#${g.id_gastos}</td>
                                                <%-- Descripción del gasto --%>
                                                <td class="data-table__cell">${g.descripcion}</td>
                                                <%-- Cantidad del gasto --%>
                                                <td class="data-table__cell">${g.cantidad}</td>
                                                <%-- Fecha del gasto --%>
                                                <td class="data-table__cell">${g.fecha}</td>
                                                <%-- Subtotal formateado con separador de miles --%>
                                                <td class="data-table__cell subtotal-col">
                                                    $
                                                    <%-- Formatea el número con separador de miles --%>
                                                    <fmt:formatNumber value="${g.subtotal}" pattern="#,##0" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <%-- Botón para regresar al menú principal --%>
                    <a href="../view/menu_inventario.jsp" class="btn-regresar">
                        <%-- Icono de flecha para indicar volver --%>
                        <i class="fa-solid fa-arrow-left"></i> Regresar
                    </a>
                </main>
                <%-- Pie de página (vacío en este caso) --%>
                <footer>
                </footer>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>