<%--=====================================================================
    VISTA JSP: visualizar_pedidos.jsp - Historial de Pedidos a Proveedores
    
    QUIÉN LA MUESTRA: PedidoServlet (GET con action=listar) → 
    request.getRequestDispatcher("view/visualizar_pedidos.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (PedidoServlet):
    - ${listaPedidos} → List<Pedido>. Viene de: PedidoDAO.listarPedidosPorInventario()
    
    Cada Pedido tiene: idPedidoBase, nombreProveedor, fechaPedido, fechaEntrega,
                      subtotal, ivaPedido, totalPedido
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idInventarioActual} → ID del inventario activo
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Regresar al menú: GET → menu_inventario.jsp
    
    IMPORTANCIA:
    - Permite revisar todo el historial de compras a proveedores
    - Facilita el análisis de costos de adquisición
    - Proporciona control de fechas de entrega y pagos
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
                <title>Visualizar Pedidos a Proveedores</title>
                <%-- Hoja de estilos CSS específica para visualización de pedidos --%>
                <link rel="stylesheet" href="../css/visualizar_pedidos.css">
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
                    <h2>PEDIDOS A PROVEEDORES</h2>

                    <%-- Contenedor principal de la tabla --%>
                    <div class="table-container">
                        <%-- Tabla que muestra el historial de pedidos --%>
                        <table class="data-table">
                            <%-- Encabezado de la tabla --%>
                            <thead class="data-table__head">
                                <%-- Fila de encabezados --%>
                                <tr class="data-table__row data-table__row--head">
                                    <%-- Columnas de la tabla --%>
                                    <th class="data-table__cell data-table__cell--head">ID</th>
                                    <th class="data-table__cell data-table__cell--head">Proveedor</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha Pedido</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha Entrega</th>
                                    <th class="data-table__cell data-table__cell--head">Subtotal</th>
                                    <th class="data-table__cell data-table__cell--head">IVA</th>
                                    <th class="data-table__cell data-table__cell--head">Total</th>
                                </tr>
                            </thead>
                            <%-- Cuerpo de la tabla con los datos --%>
                            <tbody class="data-table__body">
                                <%-- Iteramos sobre la lista que viene del Servlet --%>
                                <c:choose>
                                    <%-- Si no hay pedidos registrados --%>
                                    <c:when test="${empty listaPedidos}">
                                        <tr>
                                            <%-- Mensaje centrado ocupando todas las columnas --%>
                                            <td colspan="7" class="mensaje-vacio">
                                                No se han registrado pedidos a proveedores aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <%-- Si hay pedidos registrados --%>
                                    <c:otherwise>
                                        <%-- Itera sobre la lista de pedidos --%>
                                        <c:forEach var="p" items="${listaPedidos}">
                                            <%-- Fila individual para cada pedido --%>
                                            <tr class="data-table__row">
                                                <%-- ID del pedido --%>
                                                <td class="data-table__cell" data-label="ID">#${p.idPedidoBase}</td>
                                                <%-- Nombre del proveedor --%>
                                                <td class="data-table__cell" data-label="Proveedor">${p.nombreProveedor}
                                                </td>
                                                <%-- Fecha del pedido --%>
                                                <td class="data-table__cell" data-label="Fecha Pedido">${p.fechaPedido}
                                                </td>
                                                <%-- Fecha de entrega --%>
                                                <td class="data-table__cell" data-label="Fecha Entrega">
                                                    ${p.fechaEntrega}</td>
                                                <%-- Subtotal formateado --%>
                                                <td class="data-table__cell" data-label="Subtotal">
                                                    $
                                                    <%-- Formatea el número con separador de miles --%>
                                                    <fmt:formatNumber value="${p.subtotal}" pattern="#,##0" />
                                                </td>
                                                <%-- IVA del pedido formateado --%>
                                                <td class="data-table__cell" data-label="IVA">
                                                    $
                                                    <%-- Formatea el número con separador de miles --%>
                                                    <fmt:formatNumber value="${p.ivaPedido}" pattern="#,##0" />
                                                </td>
                                                <%-- Total del pedido formateado --%>
                                                <td class="data-table__cell total-col" data-label="Total">
                                                    $
                                                    <%-- Formatea el número con separador de miles --%>
                                                    <fmt:formatNumber value="${p.totalPedido}" pattern="#,##0" />
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