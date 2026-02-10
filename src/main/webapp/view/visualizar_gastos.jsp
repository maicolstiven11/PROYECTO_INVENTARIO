<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Visualizar Gastos</title>
                <link rel="stylesheet" href="../css/visualizar_gastos.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                    </nav>
                </header>
                <main>
                    <h2>GASTOS REGISTRADOS</h2>

                    <div class="table-container">
                        <table class="data-table">
                            <thead class="data-table__head">
                                <tr class="data-table__row data-table__row--head">
                                    <th class="data-table__cell data-table__cell--head">ID</th>
                                    <th class="data-table__cell data-table__cell--head">Descripción</th>
                                    <th class="data-table__cell data-table__cell--head">Cantidad</th>
                                    <th class="data-table__cell data-table__cell--head">Fecha</th>
                                    <th class="data-table__cell data-table__cell--head">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody class="data-table__body">
                                <!-- Iteramos sobre la lista que viene del Servlet -->
                                <c:choose>
                                    <c:when test="${empty listaGastos}">
                                        <tr>
                                            <td colspan="5" class="mensaje-vacio">
                                                No se han registrado gastos aún.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="g" items="${listaGastos}">
                                            <tr class="data-table__row">
                                                <td class="data-table__cell">#${g.id_gastos}</td>
                                                <td class="data-table__cell">${g.descripcion}</td>
                                                <td class="data-table__cell">${g.cantidad}</td>
                                                <td class="data-table__cell">${g.fecha}</td>
                                                <td class="data-table__cell subtotal-col">
                                                    $
                                                    <fmt:formatNumber value="${g.subtotal}" pattern="#,##0" />
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