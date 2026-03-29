<%--=====================================================================
    VISTA JSP: lista_proveedores.jsp - Gestión de Proveedores del Negocio
    
    QUIÉN LA MUESTRA: ProveedorServlet (GET con action=listar) → 
    request.getRequestDispatcher("view/lista_proveedores.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (ProveedorServlet):
    - ${listaProveedores} → List<Proveedor>. Viene de: ProveedorDAO.listarProveedoresPorNegocio()
    - ${errorEliminar} → String. Mensaje de error si no se puede eliminar (solo para admin)
    
    Cada Proveedor tiene: idProveedor, nombreProveedor, contacto, telefono, correo
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.idNegocioActual} → ID del negocio activo
    - ${sessionScope.usuarioLogueado.idRol} → Para controlar acceso (solo admin)
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Eliminar proveedor: GET → ProveedorServlet?action=eliminar&id=X (solo admin)
    - Agregar proveedor: Navega a → Registro_datos_prv.html
    - Regresar al menú: GET → menu_inventario.jsp
    
    IMPORTANCIA:
    - Permite gestionar la red de proveedores del negocio
    - Facilita el contacto y control de proveedores
    - Fundamental para el proceso de compras y pedidos
    =====================================================================--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if> para lógica en el JSP --%>
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
            <title>Lista de Proveedores</title>
            <%-- Hoja de estilos CSS específica para lista de proveedores --%>
            <link rel="stylesheet" href="../css/lista_proveedores.css">
            <%-- Librería Font Awesome: Iconos (fa-triangle-exclamation, fa-trash, fa-plus, fa-arrow-left) --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Librería SweetAlert2: Para confirmaciones de eliminación --%>
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
                <h2>GESTIÓN DE PROVEEDORES</h2>

                <%-- Mensaje de error si no se puede eliminar un proveedor --%>
                <c:if test="${not empty errorEliminar}">
                    <div style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; border: 1px solid #f5c6cb;">
                        <%-- Icono de advertencia y mensaje de error --%>
                        <i class="fa-solid fa-triangle-exclamation"></i> <strong>${errorEliminar}</strong>
                    </div>
                </c:if>

                <%-- Contenedor principal de la tabla --%>
                <div class="table-container">
                    <%-- Tabla que muestra la lista de proveedores --%>
                    <table class="data-table">
                        <%-- Encabezado de la tabla --%>
                        <thead class="data-table__head">
                            <%-- Fila de encabezados --%>
                            <tr class="data-table__row data-table__row--head">
                                <%-- Columnas de la tabla --%>
                                <th class="data-table__cell data-table__cell--head">Nombre Proveedor</th>
                                <th class="data-table__cell data-table__cell--head">Contacto</th>
                                <th class="data-table__cell data-table__cell--head">Teléfono</th>
                                <th class="data-table__cell data-table__cell--head">Correo</th>
                                <th class="data-table__cell data-table__cell--head">Acciones</th>
                            </tr>
                        </thead>
                        <%-- Cuerpo de la tabla con los datos --%>
                        <tbody class="data-table__body">
                            <%-- Bucle JSTL para listar proveedores --%>
                            <c:forEach var="prv" items="${listaProveedores}">
                                <%-- Fila individual para cada proveedor --%>
                                <tr class="data-table__row">
                                    <%-- Nombre del proveedor --%>
                                    <td class="data-table__cell" data-label="Proveedor">${prv.nombreProveedor}</td>
                                    <%-- Nombre del contacto --%>
                                    <td class="data-table__cell" data-label="Contacto">${prv.contacto}</td>
                                    <%-- Teléfono del proveedor --%>
                                    <td class="data-table__cell" data-label="Teléfono">${prv.telefono}</td>
                                    <%-- Correo electrónico del proveedor --%>
                                    <td class="data-table__cell" data-label="Correo">${prv.correo}</td>
                                    <%-- Columna de acciones --%>
                                    <td class="data-table__cell" data-label="Acciones">

                                        <%-- Botón para eliminar proveedor con confirmación --%>
                                        <a href="../ProveedorServlet?action=eliminar&id=${prv.idProveedor}"
                                            class="button button--delete"
                                            onclick="confirmarEliminacion(event, this.href, '¿Estás seguro de que deseas eliminar este proveedor?');"
                                            style="background-color: #e74c3c; color: white; margin-left: 5px;">
                                            <%-- Icono de papelera --%>
                                            <i class="fa-solid fa-trash"></i> Eliminar
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <%-- Mensaje si no hay proveedores registrados --%>
                            <c:if test="${empty listaProveedores}">
                                <tr>
                                    <%-- Mensaje centrado ocupando todas las columnas --%>
                                    <td colspan="5" style="text-align: center;">No hay proveedores registrados.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <%-- Botón para regresar al menú principal --%>
                <a href="../view/menu_inventario.jsp" class="btn-regresar">
                    <%-- Icono de flecha para indicar volver --%>
                    <i class="fa-solid fa-arrow-left"></i> Regresar
                </a>

                <%-- Botón flotante para agregar nuevo proveedor --%>
                <a href="../view/Registro_datos_prv.html" class="btn-floating" title="Agregar Proveedor"
                    style="position:fixed; bottom:20px; right:20px; background:#e74c3c; color:white; width:50px; height:50px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:24px; text-decoration:none; box-shadow: 2px 2px 5px rgba(0,0,0,0.3);">
                    <%-- Icono de más --%>
                    <i class="fa-solid fa-plus"></i>
                </a>

            </main>

            <%-- Pie de página (vacío en este caso) --%>
            <footer></footer>
            <%-- Script para confirmación de eliminación con SweetAlert2 --%>
            <script>
                // Función para mostrar confirmación antes de eliminar
                function confirmarEliminacion(e, url, mensaje) {
                    // Previene la navegación por defecto
                    e.preventDefault();
                    // Muestra alerta de confirmación
                    Swal.fire({
                        title: 'Confirmación',
                        text: mensaje,
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#e74c3c',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Sí, eliminar',
                        cancelButtonText: 'Cancelar'
                    }).then((result) => {
                        // Si el usuario confirma, navega a la URL de eliminación
                        if (result.isConfirmed) {
                            window.location.href = url;
                        }
                    });
                }
            </script>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>