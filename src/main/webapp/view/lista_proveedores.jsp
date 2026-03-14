<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Lista de Proveedores</title>
            <link rel="stylesheet" href="../css/lista_proveedores.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main>
                <h2>GESTIÓN DE PROVEEDORES</h2>

                <div class="table-container">
                    <table class="data-table">
                        <thead class="data-table__head">
                            <tr class="data-table__row data-table__row--head">
                                <th class="data-table__cell data-table__cell--head">Nombre Proveedor</th>
                                <th class="data-table__cell data-table__cell--head">Contacto</th>
                                <th class="data-table__cell data-table__cell--head">Teléfono</th>
                                <th class="data-table__cell data-table__cell--head">Correo</th>
                                <th class="data-table__cell data-table__cell--head">Acciones</th>
                            </tr>
                        </thead>
                        <tbody class="data-table__body">
                            <!-- Bucle JSTL para listar proveedores -->
                            <c:forEach var="prv" items="${listaProveedores}">
                                <tr class="data-table__row">
                                    <td class="data-table__cell" data-label="Proveedor">${prv.nombreProveedor}</td>
                                    <td class="data-table__cell" data-label="Contacto">${prv.contacto}</td>
                                    <td class="data-table__cell" data-label="Teléfono">${prv.telefono}</td>
                                    <td class="data-table__cell" data-label="Correo">${prv.correo}</td>
                                    <td class="data-table__cell" data-label="Acciones">

                                        <a href="../ProveedorServlet?action=eliminar&id=${prv.idProveedor}"
                                            class="button button--delete"
                                            onclick="return confirm('¿Estás seguro de que deseas eliminar este proveedor?');"
                                            style="background-color: #e74c3c; color: white; margin-left: 5px;">
                                            <i class="fa-solid fa-trash"></i> Eliminar
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty listaProveedores}">
                                <tr>
                                    <td colspan="5" style="text-align: center;">No hay proveedores registrados.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <a href="../view/menu_inventario.jsp" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar
                </a>

                <!-- Botón flotante para agregar (Opcional, si quisieras agregarlo aquí) -->
                <a href="../view/Registro_datos_prv.html" class="btn-floating" title="Agregar Proveedor"
                    style="position:fixed; bottom:20px; right:20px; background:#e74c3c; color:white; width:50px; height:50px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:24px; text-decoration:none; box-shadow: 2px 2px 5px rgba(0,0,0,0.3);">
                    <i class="fa-solid fa-plus"></i>
                </a>

            </main>

            <!-- Modales, Footer, etc -->
            <footer></footer>
        </body>

        </html>