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
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <style>
                /* NUEVO: ESTILO PARA EL SCROLL DE LA TABLA Y ENCABEZADOS ESTÁTICOS */
                .table-container {
                    max-height: 500px;
                    overflow-y: auto;
                    border-radius: 5px;
                    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                }

                .data-table__head .data-table__cell--head {
                    position: sticky;
                    top: 0;
                    z-index: 10;
                    background-color: #2c3e50; /* Fondo oscuro estático */
                    color: white;
                }
                
                .search-container {
                    width: 90%;
                    margin: 0 auto 15px auto;
                    display: flex;
                    align-items: center;
                }
                
                .search-input {
                    width: 100%;
                    padding: 12px 20px;
                    border: 2px solid #bdc3c7;
                    border-radius: 8px;
                    font-size: 16px;
                    transition: border-color 0.3s;
                }
                .search-input:focus {
                    border-color: #3498db;
                    outline: none;
                }
            </style>
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
                    <div style="text-align: right; width: 90%; margin: 0 auto; margin-bottom: 15px;">
                        <a href="view/Registro_produc.html"
                            style="background-color: #27ae60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            <i class="fa-solid fa-plus"></i> Registrar nuevo producto
                        </a>
                    </div>
                </c:if>

                <!-- NUEVO: BARRA DE BÚSQUEDA Y FILTRO -->
                <div class="search-container">
                    <input type="text" id="searchInput" class="search-input" onkeyup="filtrarTabla()" placeholder="🔍 Buscar producto por nombre, marca, precio o medida...">
                </div>

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

                                <%-- SOLO MOSTRAR SI HAY INVENTARIO SELECCIONADO --%>
                                    <c:if test="${not empty sessionScope.idInventarioActual}">
                                        <th class="data-table__cell data-table__cell--head">Stok</th>
                                    </c:if>
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

                                    <%-- SOLO MOSTRAR STOK SI HAY SESIÓN DE INVENTARIO --%>
                                        <c:if test="${not empty sessionScope.idInventarioActual}">
                                            <td class="data-table__Cell" data-label="Stok">${p.stok_actual}</td>
                                        </c:if>

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
                                                    onclick="confirmarEliminarProducto(event, this.href);"
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
                            <input class="form-group__input" type="text" value="" maxlength="100">
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
            <script>
                // --- MANEJO DE ALERTAS POR PARÁMETROS URL ---
                const urlParams = new URLSearchParams(window.location.search);

                // Mensaje de Error (Ej: Producto con datos vinculados)
                if (urlParams.get('error') === 'producto_con_datos') {
                    Swal.fire({
                        title: 'No se puede eliminar',
                        text: 'Este producto ya tiene historial de ventas o pedidos vinculados. No se puede borrar para no afectar la contabilidad.',
                        icon: 'error',
                        confirmButtonColor: '#3085d6'
                    });
                }

                // Mensaje de Éxito al eliminar
                if (urlParams.get('msg') === 'eliminado_exito') {
                    Swal.fire({
                        title: '¡Eliminado!',
                        text: 'El producto ha sido quitado del catálogo correctamente.',
                        icon: 'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }

                // Error genérico
                if (urlParams.get('error') === 'ErrorEliminar') {
                    Swal.fire('Error', 'No se pudo eliminar el producto del catálogo.', 'error');
                }

                // --- FUNCIÓN DE CONFIRMACIÓN ---
                function confirmarEliminarProducto(e, url) {
                    e.preventDefault();
                    Swal.fire({
                        title: '¿Eliminar producto?',
                        text: 'Se quitará del catálogo. Nota: Si ya tiene ventas o pedidos, el sistema no permitirá borrarlo por seguridad.',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#e74c3c',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Sí, intentar eliminar',
                        cancelButtonText: 'Cancelar'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = url;
                        }
                    });
                }
                
                // --- NUEVO: FUNCIÓN PARA FILTRAR EN TIEMPO REAL ---
                function filtrarTabla() {
                    let input = document.getElementById("searchInput");
                    let filter = input.value.toLowerCase();
                    let table = document.querySelector(".data-table");
                    let tr = table.getElementsByTagName("tr");

                    // Bucle desde 1 para ignorar la fila de los encabezados (<thead>)
                    for (let i = 1; i < tr.length; i++) {
                        let row = tr[i];
                        let tds = row.getElementsByTagName("td");
                        
                        // Omitir la fila de 'No hay productos registrados' (que usa colspan)
                        if (tds.length === 1 && tds[0].hasAttribute("colspan")) continue;

                        let match = false;
                        
                        // Buscar en absolutamente TODAS las celdas de esa fila (Nombre, Marca, Precio, etc.)
                        for (let j = 0; j < tds.length; j++) {
                            if (tds[j]) {
                                let txtValue = tds[j].textContent || tds[j].innerText;
                                if (txtValue.toLowerCase().indexOf(filter) > -1) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        
                        // Mostrar u ocultar mediante CSS display
                        if (match) {
                            row.style.display = "";
                        } else {
                            row.style.display = "none";
                        }
                    }
                }
            </script>
        </body>

        </html>