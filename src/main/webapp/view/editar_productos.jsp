<%--=====================================================================
    VISTA JSP: editar_productos.jsp - Gestión de Productos del Catálogo
    
    QUIÉN LA MUESTRA: ProductoServlet (GET sin action) → 
    request.getRequestDispatcher("view/editar_productos.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (ProductoServlet):
    - ${listaProductos} → List<Producto>. Viene de: ProductoDAO.listarProductos()
    
    Cada Producto tiene: idProducto, nombre, precioUnitario, marca, tipo, 
                         cantidadMedida, stok_actual (si hay inventario activo)
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.usuarioLogueado.idRol} → Rol del usuario (1=Admin, 2=Trabajador)
    - ${sessionScope.idInventarioActual} → ID del inventario activo (si existe)
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Editar producto: GET → ProductoServlet?action=editar&id=${p.idProducto}
    - Eliminar producto: GET → ProductoServlet?action=eliminar&id=${p.idProducto}
    - Registrar nuevo: Navega a → view/Registro_produc.html (solo admin)
    
    IMPORTANCIA:
    - Vista principal para gestión del catálogo de productos
    - Permite buscar, filtrar y administrar productos
    - Control de acceso por rol para acciones de edición
    =====================================================================--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%-- Librería JSTL Core: Permite usar <c:if>, <c:forEach> para lógica en el JSP --%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
            <title>Editar Productos</title>
            <%-- Hoja de estilos CSS para la vista de edición de productos --%>
            <link rel="stylesheet" href="../css/editar_productos.css">
            <%-- Librería Font Awesome: Iconos (fa-pen-to-square, fa-trash, fa-plus, fa-arrow-left) --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- SweetAlert2: Librería para alertas y confirmaciones modernas --%>
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <%-- Estilos CSS adicionales para el scroll de tabla y búsqueda --%>
            <style>
                <%-- Contenedor con scroll para la tabla de productos --%>
                .table-container {
                    max-height: 500px;
                    overflow-y: auto;
                    border-radius: 5px;
                    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                }

                <%-- Encabezados pegajosos (sticky) para que no se pierdan al hacer scroll --%>
                .data-table__head .data-table__cell--head {
                    position: sticky;
                    top: 0;
                    z-index: 10;
                    background-color: #2c3e50; /* Fondo oscuro estático */
                    color: white;
                }
                
                <%-- Contenedor para la barra de búsqueda --%>
                .search-container {
                    width: 90%;
                    margin: 0 auto 15px auto;
                    display: flex;
                    align-items: center;
                }
                
                <%-- Estilo para el campo de búsqueda --%>
                .search-input {
                    width: 100%;
                    padding: 12px 20px;
                    border: 2px solid #bdc3c7;
                    border-radius: 8px;
                    font-size: 16px;
                    transition: border-color 0.3s;
                }
                <%-- Efecto visual cuando el campo está enfocado --%>
                .search-input:focus {
                    border-color: #3498db;
                    outline: none;
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
                <%-- Título principal de la vista --%>
                <h2>GESTIÓN DE PRODUCTOS</h2>

                <%-- Botón para Registrar Nuevo Producto (SOLO ADMINISTRADORES) --%>
                <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                    <div style="text-align: right; width: 90%; margin: 0 auto; margin-bottom: 15px;">
                        <%-- Enlace al formulario de registro de nuevos productos --%>
                        <a href="view/Registro_produc.html"
                            style="background-color: #27ae60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            <i class="fa-solid fa-plus"></i> Registrar nuevo producto
                        </a>
                    </div>
                </c:if>

                <%-- Barra de búsqueda y filtro para productos --%>
                <div class="search-container">
                    <%-- Campo de texto que filtra la tabla en tiempo real --%>
                    <input type="text" id="searchInput" class="search-input" onkeyup="filtrarTabla()" placeholder="🔍 Buscar producto por nombre, marca, precio o medida...">
                </div>

                <%-- Contenedor con scroll para la tabla de productos --%>
                <div class="table-container">
                    <%-- Tabla principal que muestra el catálogo de productos --%>
                    <table class="data-table">
                        <%-- Encabezado de la tabla con títulos de columnas --%>
                        <thead class="data-table__head">
                            <tr class="data-table__row data-table__row--head">
                                <%-- Columnas fijas del catálogo de productos --%>
                                <th class="data-table__cell data-table__cell--head">ID</th>
                                <th class="data-table__cell data-table__cell--head">Nombre</th>
                                <th class="data-table__cell data-table__cell--head">Precio</th>
                                <th class="data-table__cell data-table__cell--head">Marca</th>
                                <th class="data-table__cell data-table__cell--head">Tipo</th>
                                <th class="data-table__cell data-table__cell--head">Medida</th>

                                <%-- Columna de Stock: Solo visible si hay inventario activo --%>
                                <c:if test="${not empty sessionScope.idInventarioActual}">
                                    <th class="data-table__cell data-table__cell--head">Stok</th>
                                </c:if>
                                <%-- Columna de Acciones: Solo visible para administradores --%>
                                <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                                    <th class="data-table__cell data-table__cell--head">Acciones</th>
                                </c:if>
                            </tr>
                        </thead>
                        <%-- Cuerpo de la tabla con los datos de los productos --%>
                        <tbody class="data-table__body">
                            <%-- Itera sobre la lista de productos que viene del servlet --%>
                            <c:forEach var="p" items="${listaProductos}">
                                <%-- Fila individual para cada producto --%>
                                <tr class="data-table__row">
                                    <%-- Datos básicos del producto --%>
                                    <td class="data-table__cell" data-label="ID">${p.idProducto}</td>
                                    <td class="data-table__cell" data-label="Nombre">${p.nombre}</td>
                                    <td class="data-table__cell" data-label="Precio">$ ${p.precioUnitario}</td>
                                    <td class="data-table__cell" data-label="Marca">${p.marca}</td>
                                    <td class="data-table__cell" data-label="Tipo">${p.tipo}</td>
                                    <td class="data-table__cell" data-label="Medida">${p.cantidadMedida}</td>

                                    <%-- Columna de Stock: Solo muestra si hay sesión de inventario activo --%>
                                    <c:if test="${not empty sessionScope.idInventarioActual}">
                                        <td class="data-table__Cell" data-label="Stok">${p.stok_actual}</td>
                                    </c:if>

                                    <%-- Columna de Acciones: Solo visible para administradores --%>
                                    <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                                        <td class="data-table__cell" data-label="Acciones">
                                            <%-- Botón para editar el producto --%>
                                            <a href="../ProductoServlet?action=editar&id=${p.idProducto}"
                                                class="button button--edit" title="Editar">
                                                <i class="fa-solid fa-pen-to-square"></i>
                                            </a>
                                            <%-- Botón para eliminar el producto con confirmación --%>
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

                            <%-- Mensaje si no hay productos registrados --%>
                            <c:if test="${empty listaProductos}">
                                <tr>
                                    <%-- Ocupa todas las columnas para mostrar mensaje centrado --%>
                                    <td colspan="7" style="text-align: center;">No hay productos registrados.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <%-- Botón para regresar al menú principal --%>
                <a href="/view/Menu_sistema.jsp" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                </a>

            </main>

            <%-- Modal de Edición (Actualmente es demo, funcionalmente se conectará luego) --%>
            <div id="modal-editar" class="modal">
                <div class="modal__content">
                    <%-- Botón para cerrar el modal --%>
                    <a href="#" class="modal__close">&times;</a>
                    <%-- Título del modal --%>
                    <h3 class="modal__title">Editar Producto (Demo)</h3>
                    <%-- Formulario de edición (actualmente no funcional) --%>
                    <form class="modal__form">
                        <div class="form-group">
                            <label class="form-group__label">Nombre del Producto</label>
                            <input class="form-group__input" type="text" value="" maxlength="100">
                        </div>
                        <%-- Aquí irían más campos del formulario --%>
                        <div class="modal__actions">
                            <a href="#" class="button button--cancel">Cancelar</a>
                            <a href="#" class="button button--accept">Aceptar</a>
                        </div>
                    </form>
                </div>
            </div>

            <%-- Pie de página (vacío en este caso) --%>
            <footer>
            </footer>
            <%-- Script para manejo de alertas y funcionalidades interactivas --%>
            <script>
                <%-- Obtener parámetros de la URL para mostrar mensajes --%>
                const urlParams = new URLSearchParams(window.location.search);

                <%-- Mensaje de Error: Producto con datos vinculados no se puede eliminar --%>
                if (urlParams.get('error') === 'producto_con_datos') {
                    Swal.fire({
                        title: 'No se puede eliminar',
                        text: 'Este producto ya tiene historial de ventas o pedidos vinculados. No se puede borrar para no afectar la contabilidad.',
                        icon: 'error',
                        confirmButtonColor: '#3085d6'
                    });
                }

                <%-- Mensaje de Éxito al eliminar producto --%>
                if (urlParams.get('msg') === 'eliminado_exito') {
                    Swal.fire({
                        title: '¡Eliminado!',
                        text: 'El producto ha sido quitado del catálogo correctamente.',
                        icon: 'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }

                <%-- Mensaje de error genérico al eliminar --%>
                if (urlParams.get('error') === 'ErrorEliminar') {
                    Swal.fire('Error', 'No se pudo eliminar el producto del catálogo.', 'error');
                }

                <%-- Función de confirmación para eliminar producto --%>
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
                        <%-- Si el usuario confirma, redirige a la URL de eliminación --%>
                        if (result.isConfirmed) {
                            window.location.href = url;
                        }
                    });
                }
                
                <%-- Función para filtrar la tabla en tiempo real --%>
                function filtrarTabla() {
                    <%-- Obtener el texto de búsqueda y convertir a minúsculas --%>
                    let input = document.getElementById("searchInput");
                    let filter = input.value.toLowerCase();
                    <%-- Obtener la tabla y todas sus filas --%>
                    let table = document.querySelector(".data-table");
                    let tr = table.getElementsByTagName("tr");

                    <%-- Bucle desde 1 para ignorar la fila de los encabezados (<thead>) --%>
                    for (let i = 1; i < tr.length; i++) {
                        let row = tr[i];
                        let tds = row.getElementsByTagName("td");
                        
                        <%-- Omitir la fila de 'No hay productos registrados' (que usa colspan) --%>
                        if (tds.length === 1 && tds[0].hasAttribute("colspan")) continue;

                        let match = false;
                        
                        <%-- Buscar en todas las celdas de esa fila (Nombre, Marca, Precio, etc.) --%>
                        for (let j = 0; j < tds.length; j++) {
                            if (tds[j]) {
                                let txtValue = tds[j].textContent || tds[j].innerText;
                                <%-- Si encuentra coincidencia, marca como encontrada y sale del bucle --%>
                                if (txtValue.toLowerCase().indexOf(filter) > -1) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        
                        <%-- Mostrar u ocultar la fila según si hubo coincidencia --%>
                        if (match) {
                            row.style.display = "";
                        } else {
                            row.style.display = "none";
                        }
                    }
                }
            </script>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>