<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Visualizar Pedidos a Proveedores</title>
                <link rel="stylesheet" href="../css/visualizar_pedidos.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                    </nav>
                </header>
                <main>
                    <h2>PEDIDOS A PROVEEDORES</h2>

                    <div class="table-container">
                        <table class="data-table">
                            <thead class="data-table__head">
                                <tr class="data-table__row data-table__row--head">
                                    <th class="data-table__cell data-table__cell--head">ID</th>
                                    <th class="data-table__cell data-table__cell--head">Proveedor</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha Pedido</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha Entrega</th>
                                    <th class="data-table__cell data-table__cell--head">Subtotal</th>
                                    <th class="data-table__cell data-table__cell--head">IVA</th>
                                    <th class="data-table__cell data-table__cell--head">Total</th>
                                </tr>
                            </thead>
                            <tbody class="data-table__body">
                                <!-- Iteramos sobre la lista que viene del Servlet -->
                                <c:choose>
                                    <c:when test="${empty listaPedidos}">
                                        <tr>
                                            <td colspan="7" class="mensaje-vacio">
                                                No se han registrado pedidos a proveedores aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="p" items="${listaPedidos}">
                                            <tr class="data-table__row">
                                                <td class="data-table__cell" data-label="ID">#${p.idPedidoBase}</td>
                                                <td class="data-table__cell" data-label="Proveedor">${p.nombreProveedor}
                                                </td>
                                                <td class="data-table__cell" data-label="Fecha Pedido">${p.fechaPedido}
                                                </td>
                                                <td class="data-table__cell" data-label="Fecha Entrega">
                                                    ${p.fechaEntrega}</td>
                                                <td class="data-table__cell" data-label="Subtotal">
                                                    $
                                                    <fmt:formatNumber value="${p.subtotal}" pattern="#,##0" />
                                                </td>
                                                <td class="data-table__cell" data-label="IVA">
                                                    $
                                                    <fmt:formatNumber value="${p.ivaPedido}" pattern="#,##0" />
                                                </td>
                                                <td class="data-table__cell total-col" data-label="Total">
                                                    $
                                                    <fmt:formatNumber value="${p.totalPedido}" pattern="#,##0" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <a href="../view/menu_inventario.jsp" class="btn-regresar">
                        <i class="fa-solid fa-arrow-left"></i> Regresar
                    </a>
                </main>
                <footer>
                </footer>
            </body>

            </html>