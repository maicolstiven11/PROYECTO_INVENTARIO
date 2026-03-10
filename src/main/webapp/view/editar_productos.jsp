<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Editar Productos</title>
            <link rel="stylesheet" href="../css/editar_productos.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main>
                <h2>GESTIÓN DE PRODUCTOS</h2>

                <!-- Botón para Registrar Nuevo (SOLO ADMIN) -->
                <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                    <div style="text-align: right; width: 90%; margin: 0 auto; margin-bottom: 20px;">
                        <a href="view/Registro_produc.html"
                            style="background-color: #27ae60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            <i class="fa-solid fa-plus"></i> Registrar nuevo producto
                        </a>
                    </div>
                </c:if>

                <div class="table-container">
                    <table class="data-table">
                        <thead class="data-table__head">
                            <tr class="data-table__row data-table__row--head">
                                <th class="data-table__cell data-table__cell--head">ID</th>
                                <th class="data-table__cell data-table__cell--head">Nombre</th>
                                <th class="data-table__cell data-table__cell--head">Precio</th>
                                <th class="data-table__cell data-table__cell--head">Marca</th>
                                <th class="data-table__cell data-table__cell--head">Tipo</th>
                                <th class="data-table__cell data-table__cell--head">Medida</th>
                                <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                                    <th class="data-table__cell data-table__cell--head">Acciones</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody class="data-table__body">
                            <c:forEach var="p" items="${listaProductos}">
                                <tr class="data-table__row">
                                    <td class="data-table__cell" data-label="ID">${p.idProducto}</td>
                                    <td class="data-table__cell" data-label="Nombre">${p.nombre}</td>
                                    <td class="data-table__cell" data-label="Precio">$ ${p.precioUnitario}</td>
                                    <td class="data-table__cell" data-label="Marca">${p.marca}</td>
                                    <td class="data-table__cell" data-label="Tipo">${p.tipo}</td>
                                    <td class="data-table__cell" data-label="Medida">${p.cantidadMedida}</td>
                                    <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                                        <td class="data-table__cell" data-label="Acciones">
                                            <!-- Botón Editar -->
                                            <a href="../ProductoServlet?action=editar&id=${p.idProducto}"
                                                class="button button--edit" title="Editar">
                                                <i class="fa-solid fa-pen-to-square"></i>
                                            </a>
                                            <!-- Botón Eliminar -->
                                            <a href="../ProductoServlet?action=eliminar&id=${p.idProducto}"
                                                style="color: #e74c3c; margin-left: 10px; font-size: 1.2rem;"
                                                onclick="return confirm('¿Estás seguro de eliminar este producto?');"
                                                title="Eliminar">
                                                <i class="fa-solid fa-trash"></i>
                                            </a>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty listaProductos}">
                                <tr>
                                    <td colspan="7" style="text-align: center;">No hay productos registrados.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <a href="/view/Menu_sistema.jsp" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                </a>

            </main>

            <!-- Modal de Edición (Mantenemos el diseño original, funcionalmente se conectará luego) -->
            <div id="modal-editar" class="modal">
                <div class="modal__content">
                    <a href="#" class="modal__close">&times;</a>
                    <h3 class="modal__title">Editar Producto (Demo)</h3>
                    <form class="modal__form">
                        <div class="form-group">
                            <label class="form-group__label">Nombre del Producto</label>
                            <input class="form-group__input" type="text" value="">
                        </div>
                        <!-- Más campos... -->
                        <div class="modal__actions">
                            <a href="#" class="button button--cancel">Cancelar</a>
                            <a href="#" class="button button--accept">Aceptar</a>
                        </div>
                    </form>
                </div>
            </div>

            <footer>
            </footer>
        </body>

        </html>