<%--=====================================================================VISTA JSP: agregar_venta.jsp - Registrar Venta
    QUIÉN LA MUESTRA: VentaServlet.processRequest(action=mostrar) →
    request.getRequestDispatcher("view/agregar_venta.jsp").forward(...) DATOS QUE RECIBE DEL CONTROLADOR (VentaServlet):
    - ${listaProductos} → Lista de objetos Producto. Viene de: ProductoDAO.listarProductos() Cada producto tiene:
    idProducto, nombre, precioUnitario, marca Se usa para llenar el SELECT de productos. - ${totalVenta} → double. Total
    calculado del carrito. Viene de: VentaServlet.calcularTotal(carrito) - ${sessionScope.carrito} → List<DetalleVenta>
    en sesión. Es el carrito de compras.
    Cada item tiene: nombreProducto, cantidad, precioUnitario, subtotal
    - ${sessionScope.error_stock} → String. Mensaje de error si no hay stock. Viene de: VentaServlet.agregarProducto()
    - ${param.error} → String. Parámetro URL de error (ej: CarritoVacio, SinInventarioActivo)

    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Agregar: POST → VentaServlet?action=agregar (input hidden action="agregar")
    Envía: id_producto (del select) y cantidad (del input number)
    - Quitar: GET → VentaServlet?action=quitar&index=${status.index} (enlace en tabla)
    - Finalizar: GET → VentaServlet?action=finalizar (botón "Finalizar Venta")
    - Regresar: Enlace a menu_inventario.jsp
    ===================================================================== --%>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%-- Librería JSTL Core: Permite usar <c:if>, <c:forEach>, <c:choose> para lógica en el JSP --%>
                    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                        <%-- Librería JSTL Format: Permite usar <fmt:formatNumber> para formatear precios --%>
                            <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

                                <!DOCTYPE html>
                                <html lang="es">

                                <head>
                                    <meta charset="UTF-8">
                                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    <title>Registrar Venta</title>
                                    <%-- CSS propio de esta vista --%>
                                        <link rel="stylesheet" href="../css/agregar_venta.css">
                                        <%-- Librería Font Awesome: Iconos (fa-plus, fa-trash, fa-arrow-left) --%>
                                            <link rel="stylesheet"
                                                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                                            <%-- Select2 CSS: Librería para hacer el select de productos buscable --%>
                                                <link
                                                    href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css"
                                                    rel="stylesheet" />
                                </head>

                                <body>

                                    <header>
                                        <nav class="navbar">
                                            <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                                        </nav>
                                    </header>
                                    <main>

                                        <div class="venta">
                                            <div class="venta__contenedor">

                                                <h1 class="venta__titulo">Registrar Venta</h1>

                                                <%-- MENSAJES DE ERROR: Se muestran si el parámetro ?error=viene en la
                                                    URL --%>
                                                    <%-- Ejemplo: VentaServlet redirige a ?error=CarritoVacio si el
                                                        carrito está vacío --%>
                                                        <c:if test="${not empty param.error}">
                                                            <div class="mensaje-error">
                                                                <c:choose>
                                                                    <%-- Viene de: VentaServlet.finalizarVenta() si
                                                                        carrito.isEmpty() --%>
                                                                        <c:when test="${param.error == 'CarritoVacio'}">
                                                                            El carrito está vacío.</c:when>
                                                                        <%-- Viene de: VentaServlet.processRequest() si
                                                                            idInventario==null --%>
                                                                            <c:when
                                                                                test="${param.error == 'SinInventarioActivo'}">
                                                                                No hay inventario activo
                                                                                iniciado.</c:when>
                                                                            <c:otherwise>Ocurrió un error:
                                                                                ${param.error}</c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </c:if>

                                                        <%-- ADVERTENCIA DE STOCK: Se muestra si VentaServlet detectó
                                                            stock insuficiente --%>
                                                            <%-- Viene de:
                                                                session.setAttribute("error_stock", "Stock insuficiente: ..."
                                                                ) en VentaServlet.agregarProducto() --%>
                                                                <c:if test="${not empty sessionScope.error_stock}">
                                                                    <div class="mensaje-error"
                                                                        style="background-color: #fff3cd; color: #856404; border-color: #ffeeba; display: flex; align-items: center; gap: 10px;">
                                                                        <i class="fa-solid fa-triangle-exclamation"></i>
                                                                        <div>
                                                                            <%-- ${sessionScope.error_stock} → Mensaje
                                                                                de la sesión,
                                                                                ej: "Stock insuficiente: Cerveza (Disponible: 5)"
                                                                                --%>
                                                                                <strong>${sessionScope.error_stock}</strong><br>
                                                                                ¿Deseas pedir más a un proveedor?
                                                                                <a href="../PedidoServlet?action=nuevo"
                                                                                    style="color: #0056b3; font-weight: bold; text-decoration: underline;">Registrar
                                                                                    Pedido</a>
                                                                        </div>
                                                                    </div>
                                                                    <%-- Eliminar el mensaje de la sesión después de
                                                                        mostrarlo (para que no aparezca en la siguiente
                                                                        carga) --%>
                                                                        <c:remove var="error_stock" scope="session" />
                                                                </c:if>

                                                                <%--=====================================================================FORMULARIO
                                                                    PARA AGREGAR PRODUCTO AL CARRITO Envía datos a:
                                                                    VentaServlet con action=agregar (método POST)
                                                                    VentaServlet.agregarProducto() procesa estos
                                                                    datos=====================================================================--%>
                                                                    <form class="formulario-venta"
                                                                        action="../VentaServlet" method="POST">
                                                                        <%-- Campo oculto que le dice al Servlet qué
                                                                            acción ejecutar --%>
                                                                            <input type="hidden" name="action"
                                                                                value="agregar">

                                                                            <%-- SELECT DE PRODUCTO: Se llena con
                                                                                ${listaProductos} que viene de
                                                                                ProductoDAO.listarProductos() --%>
                                                                                <div class="formulario-venta__grupo">
                                                                                    <label for="id_producto"
                                                                                        class="formulario-venta__label">Producto</label>

                                                                                    <%-- Este select se vuelve buscable
                                                                                        gracias a la librería Select2
                                                                                        (ver script al final) --%>
                                                                                        <select id="id_producto"
                                                                                            name="id_producto"
                                                                                            class="formulario-venta__input"
                                                                                            required
                                                                                            style="width: 100%;">
                                                                                            <option value="">-- Buscar
                                                                                                producto --</option>
                                                                                            <%-- c:forEach recorre la
                                                                                                lista de productos que
                                                                                                vino del DAO vía Servlet
                                                                                                --%>
                                                                                                <%-- ${p} es un objeto
                                                                                                    Producto del Modelo.
                                                                                                    Java llama a
                                                                                                    p.getIdProducto(),
                                                                                                    p.getNombre(), etc.
                                                                                                    --%>
                                                                                                    <c:forEach var="p"
                                                                                                        items="${listaProductos}">
                                                                                                        <%-- value="${p.idProducto}"
                                                                                                            → Se envía
                                                                                                            al Servlet
                                                                                                            como
                                                                                                            request.getParameter("id_producto")
                                                                                                            --%>
                                                                                                            <%-- El
                                                                                                                texto
                                                                                                                visible
                                                                                                                muestra:
                                                                                                                nombre -
                                                                                                                $precio
                                                                                                                (marca)
                                                                                                                --%>
                                                                                                                <option
                                                                                                                    value="${p.idProducto}">
                                                                                                                    ${p.nombre}
                                                                                                                    -
                                                                                                                    $${p.precioUnitario}
                                                                                                                    (${p.marca})
                                                                                                                </option>
                                                                                                    </c:forEach>
                                                                                        </select>
                                                                                </div>

                                                                                <%-- INPUT DE CANTIDAD: Se envía al
                                                                                    Servlet como
                                                                                    request.getParameter("cantidad")
                                                                                    --%>
                                                                                    <div class="formulario-venta__fila">
                                                                                        <div
                                                                                            class="formulario-venta__grupo formulario-venta__grupo--col">
                                                                                            <label for="cantidad"
                                                                                                class="formulario-venta__label">Cantidad</label>
                                                                                            <input id="cantidad"
                                                                                                name="cantidad"
                                                                                                type="number"
                                                                                                class="formulario-venta__input"
                                                                                                placeholder="Ej: 1"
                                                                                                min="1" max="10000"
                                                                                                value="1" required>
                                                                                        </div>

                                                                                        <%-- BOTÓN AGREGAR: Envía el
                                                                                            formulario (POST) al
                                                                                            VentaServlet --%>
                                                                                            <div class="formulario-venta__grupo formulario-venta__grupo--col"
                                                                                                style="display: flex; align-items: flex-end;">
                                                                                                <button type="submit"
                                                                                                    class="formulario-venta__boton"
                                                                                                    style="margin-top: 0; width: 100%;">
                                                                                                    <i
                                                                                                        class="fa-solid fa-plus"></i>
                                                                                                    Agregar
                                                                                                </button>
                                                                                            </div>
                                                                                    </div>
                                                                    </form>

                                                                    <%--=====================================================================TABLA
                                                                        CARRITO DE COMPRAS Datos vienen de:
                                                                        ${sessionScope.carrito} → List<DetalleVenta> en
                                                                        sesión HTTP
                                                                        Cada item fue agregado por
                                                                        VentaServlet.agregarProducto()
                                                                        Los DetalleVenta del carrito tienen:
                                                                        nombreProducto, cantidad, precioUnitario,
                                                                        subtotal
                                                                        =====================================================================
                                                                        --%>
                                                                        <div class="formulario-venta__grupo">
                                                                            <label
                                                                                class="formulario-venta__label">Carrito
                                                                                de Compras</label>
                                                                            <table class="tabla-carrito">
                                                                                <thead>
                                                                                    <tr>
                                                                                        <th>Producto</th>
                                                                                        <th>Cant.</th>

                                                                                        <th>Subtotal</th>
                                                                                        <th>Acción</th>
                                                                                    </tr>
                                                                                </thead>
                                                                                <tbody>
                                                                                    <%-- Si el carrito está vacío,
                                                                                        mostrar mensaje --%>
                                                                                        <c:choose>
                                                                                            <c:when
                                                                                                test="${empty sessionScope.carrito}">
                                                                                                <tr>
                                                                                                    <td colspan="5"
                                                                                                        style="text-align: center; color: #777;">
                                                                                                        El carrito está
                                                                                                        vacío.
                                                                                                    </td>
                                                                                                </tr>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <%-- Recorrer cada item
                                                                                                    del carrito.
                                                                                                    varStatus="status"
                                                                                                    da el índice
                                                                                                    (status.index) --%>
                                                                                                    <c:forEach
                                                                                                        var="item"
                                                                                                        items="${sessionScope.carrito}"
                                                                                                        varStatus="status">
                                                                                                        <tr>
                                                                                                            <%-- ${item.nombreProducto}
                                                                                                                → Viene
                                                                                                                de:
                                                                                                                DetalleVenta.getNombreProducto()
                                                                                                                ← Puesto
                                                                                                                en
                                                                                                                VentaServlet
                                                                                                                --%>
                                                                                                                <td>${item.nombreProducto}
                                                                                                                </td>
                                                                                                                <%-- ${item.cantidad}
                                                                                                                    →
                                                                                                                    Viene
                                                                                                                    de:
                                                                                                                    DetalleVenta.getCantidad()
                                                                                                                    ←
                                                                                                                    Ingresado
                                                                                                                    por
                                                                                                                    usuario
                                                                                                                    --%>
                                                                                                                    <td>${item.cantidad}
                                                                                                                    </td>
                                                                                                                        <%-- ${item.subtotal}
                                                                                                                            →
                                                                                                                            Calculado
                                                                                                                            como:
                                                                                                                            cantidad
                                                                                                                            ×
                                                                                                                            precioUnitario
                                                                                                                            en
                                                                                                                            VentaServlet
                                                                                                                            --%>
                                                                                                                            <td>$
                                                                                                                                <fmt:formatNumber
                                                                                                                                    value="${item.subtotal}"
                                                                                                                                    pattern="#,##0" />
                                                                                                                            </td>
                                                                                                                            <%-- Botón
                                                                                                                                para
                                                                                                                                quitar:
                                                                                                                                Llama
                                                                                                                                a
                                                                                                                                VentaServlet?action=quitar&index=N
                                                                                                                                --%>
                                                                                                                                <td>
                                                                                                                                    <a href="../VentaServlet?action=quitar&index=${status.index}"
                                                                                                                                        class="btn-eliminar">
                                                                                                                                        <i
                                                                                                                                            class="fa-solid fa-trash"></i>
                                                                                                                                    </a>
                                                                                                                                </td>
                                                                                                        </tr>
                                                                                                    </c:forEach>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                </tbody>
                                                                            </table>
                                                                        </div>

                                                                        <%--=====================================================================PIE:
                                                                            TOTAL +
                                                                            BOTONES=====================================================================--%>
                                                                            <div class="formulario-venta__pie">
                                                                                <%-- Botón regresar al menú del
                                                                                    inventario --%>
                                                                                    <a href="../view/menu_inventario.jsp"
                                                                                        class="btn-regresar">
                                                                                        <i
                                                                                            class="fa-solid fa-arrow-left"></i>
                                                                                        Regresar
                                                                                    </a>

                                                                                    <%-- TOTAL DE LA VENTA: Viene de
                                                                                        request.setAttribute("totalVenta",
                                                                                        ...) en VentaServlet --%>
                                                                                        <div
                                                                                            class="formulario-venta__total">
                                                                                            <span
                                                                                                class="formulario-venta__total-texto">TOTAL:</span>
                                                                                            <span
                                                                                                class="formulario-venta__total-valor">
                                                                                                $
                                                                                                <%-- fmt:formatNumber
                                                                                                    formatea el número
                                                                                                    con 2 decimales y
                                                                                                    separadores de miles
                                                                                                    --%>
                                                                                                    <fmt:formatNumber
                                                                                                        value="${totalVenta != null ? totalVenta : 0}"
                                                                                                        pattern="#,##0.00" />
                                                                                            </span>
                                                                                        </div>

                                                                                        <%-- BOTÓN FINALIZAR VENTA:
                                                                                            Envía GET a
                                                                                            VentaServlet?action=finalizar
                                                                                            --%>
                                                                                            <%-- VentaServlet.finalizarVenta()
                                                                                                →
                                                                                                VentaDAO.registrarVenta()
                                                                                                → INSERT en BD + resta
                                                                                                stock --%>
                                                                                                <a href="../VentaServlet?action=finalizar"
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

                                    <%-- jQuery: Necesario para que Select2 funcione --%>
                                        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
                                        <%-- Select2: Convierte el select normal en un select con buscador --%>
                                            <script
                                                src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

                                            <script>
                                                // Cuando el DOM está listo, inicializar Select2 en el select de productos
                                                $(document).ready(function () {
                                                    $('#id_producto').select2({
                                                        placeholder: 'Busca un producto...',    // Texto por defecto
                                                        allowClear: true,                       // Permite limpiar la selección
                                                        language: {
                                                            noResults: function () {
                                                                return "No se encontraron resultados"; // Mensaje si no hay coincidencias
                                                            }
                                                        }
                                                    });
                                                });
                                            </script>
                                            <%-- Script de validaciones centralizadas (ver: js/validaciones.js) --%>
                                                <script src="../js/validaciones.js"></script>
                                </body>

                                </html>