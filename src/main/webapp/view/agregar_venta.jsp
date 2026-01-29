<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Registrar Venta</title>
                <!-- Reutilizamos estilos existentes -->
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/agregar_venta.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <!-- Select2 CSS -->
                <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css"
                    rel="stylesheet" />
            </head>

            <body>

                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="${pageContext.request.contextPath}/assets/img/LOGO.png"
                            alt="logo_sistema">
                    </nav>
                </header>
                <main>

                    <div class="venta">
                        <div class="venta__contenedor">

                            <h1 class="venta__titulo">Registrar Venta</h1>

                            <c:if test="${not empty param.error}">
                                <div class="mensaje-error">
                                    <c:choose>
                                        <c:when test="${param.error == 'CarritoVacio'}">El carrito está vacío.</c:when>
                                        <c:when test="${param.error == 'SinInventarioActivo'}">No hay inventario activo
                                            iniciado.</c:when>
                                        <c:otherwise>Ocurrió un error: ${param.error}</c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <!-- FORMULARIO PARA AGREGAR PRODUCTO -->
                            <form class="formulario-venta" action="${pageContext.request.contextPath}/VentaServlet"
                                method="POST">
                                <input type="hidden" name="action" value="agregar">

                                <!-- PRODUCTO -->
                                <div class="formulario-venta__grupo">
                                    <label for="id_producto" class="formulario-venta__label">Producto</label>

                                    <!-- Select Simple con funcionalidad de búsqueda Select2 -->
                                    <select id="id_producto" name="id_producto" class="formulario-venta__input" required
                                        style="width: 100%;">
                                        <option value="">-- Buscar producto --</option>
                                        <c:forEach var="p" items="${listaProductos}">
                                            <option value="${p.idProducto}">
                                                ${p.nombre} - $${p.precioUnitario} (${p.marca})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <!-- CANTIDAD -->
                                <div class="formulario-venta__fila">
                                    <div class="formulario-venta__grupo formulario-venta__grupo--col">
                                        <label for="cantidad" class="formulario-venta__label">Cantidad</label>
                                        <input id="cantidad" name="cantidad" type="number"
                                            class="formulario-venta__input" placeholder="Ej: 1" min="1" value="1"
                                            required>
                                    </div>

                                    <div class="formulario-venta__grupo formulario-venta__grupo--col"
                                        style="display: flex; align-items: flex-end;">
                                        <button type="submit" class="formulario-venta__boton"
                                            style="margin-top: 0; width: 100%;">
                                            <i class="fa-solid fa-plus"></i> Agregar
                                        </button>
                                    </div>
                                </div>
                            </form>

                            <!-- TABLA CARRITO -->
                            <div class="formulario-venta__grupo">
                                <label class="formulario-venta__label">Carrito de Compras</label>
                                <table class="tabla-carrito">
                                    <thead>
                                        <tr>
                                            <th>Producto</th>
                                            <th>Cant.</th>
                                            <th>Precio Unit.</th>
                                            <th>Subtotal</th>
                                            <th>Acción</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty sessionScope.carrito}">
                                                <tr>
                                                    <td colspan="5" style="text-align: center; color: #777;">
                                                        El carrito está vacío.
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="item" items="${sessionScope.carrito}"
                                                    varStatus="status">
                                                    <tr>
                                                        <td>${item.nombreProducto}</td>
                                                        <td>${item.cantidad}</td>
                                                        <td>$
                                                            <fmt:formatNumber value="${item.precioUnitario}"
                                                                pattern="#,##0" />
                                                        </td>
                                                        <td>$
                                                            <fmt:formatNumber value="${item.subtotal}"
                                                                pattern="#,##0" />
                                                        </td>
                                                        <td>
                                                            <a href="${pageContext.request.contextPath}/VentaServlet?action=quitar&index=${status.index}"
                                                                class="btn-eliminar">
                                                                <i class="fa-solid fa-trash"></i>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>

                            <!-- TOTALES Y FINALIZAR -->
                            <div class="formulario-venta__pie">
                                <a href="${pageContext.request.contextPath}/view/menu_inventario.html"
                                    class="btn-regresar">
                                    <i class="fa-solid fa-arrow-left"></i> Regresar
                                </a>

                                <div class="formulario-venta__total">
                                    <span class="formulario-venta__total-texto">TOTAL:</span>
                                    <span class="formulario-venta__total-valor">
                                        $
                                        <fmt:formatNumber value="${totalVenta != null ? totalVenta : 0}"
                                            pattern="#,##0.00" />
                                    </span>
                                </div>

                                <a href="${pageContext.request.contextPath}/VentaServlet?action=finalizar"
                                    class="formulario-venta__boton"
                                    style="text-decoration: none; text-align: center; background-color: #2ecc71;">
                                    Finalizar Venta
                                </a>
                            </div>

                        </div>
                    </div>
                </main>
                <footer>
                </footer>

                <!-- jQuery (Necesario para Select2) -->
                <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
                <!-- Select2 JS -->
                <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

                <script>
                    $(document).ready(function () {
                        // Inicializar Select2 en el select de productos
                        $('#id_producto').select2({
                            placeholder: 'Busca un producto...',
                            allowClear: true,
                            language: {
                                noResults: function () {
                                    return "No se encontraron resultados";
                                }
                            }
                        });
                    });
                </script>
            </body>

            </html>