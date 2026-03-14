<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Editar Producto</title>
                <link rel="stylesheet" href="../css/editar_producto_form.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            </head>

            <body>
                <header>
                    <nav class="navbar">
                        <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                    </nav>
                </header>
                <main>
                    <div class="form-container">
                        <h2 class="form-title"><i class="fa-solid fa-pen-to-square"></i> Editar Producto</h2>

                        <form action="../ProductoServlet" method="POST" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="actualizar">
                            <input type="hidden" name="id_producto" value="${productoEditar.idProducto}">

                            <div class="form-grid">
                                <div class="form-group full-width">
                                    <label for="nombre">Nombre del Producto</label>
                                    <input type="text" id="nombre" name="nombre" value="${productoEditar.nombre}"
                                        maxlength="100" required>
                                </div>

                                <div class="form-group">
                                    <label for="marca">Marca</label>
                                    <input type="text" id="marca" name="marca" value="${productoEditar.marca}"
                                        maxlength="50">
                                </div>

                                <div class="form-group">
                                    <label for="precio">Precio Unitario</label>
                                    <input type="number" id="precio" name="precio" step="0.01"
                                        value="${productoEditar.precioUnitario}" min="0" max="999999999" required>
                                </div>

                                <div class="form-group">
                                    <label for="tipo">Tipo de Producto</label>
                                    <select id="tipo" name="tipo" required>
                                        <option value="bebida" ${productoEditar.tipo=='bebida' ? 'selected' : '' }>
                                            Bebidas</option>
                                        <option value="snack" ${productoEditar.tipo=='snack' ? 'selected' : '' }>Snacks
                                        </option>
                                        <option value="dulce" ${productoEditar.tipo=='dulce' ? 'selected' : '' }>Dulces
                                        </option>
                                        <option value="cigarro" ${productoEditar.tipo=='cigarro' ? 'selected' : '' }>
                                            Cigarros</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="cantidad_medida">Cantidad/Medida</label>
                                    <input type="text" id="cantidad_medida" name="cantidad_medida"
                                        value="${productoEditar.cantidadMedida}" placeholder="Ej: 750ml" maxlength="50">
                                </div>

                                <div class="form-group">
                                    <label for="fecha_vencimiento">Fecha Vencimiento</label>
                                    <input type="date" id="fecha_vencimiento" name="fecha_vencimiento"
                                        value="<fmt:formatDate value='${productoEditar.fechaVencimiento}' pattern='yyyy-MM-dd'/>">
                                </div>

                                <div class="form-group full-width">
                                    <label for="imagen">Imagen del producto (Dejar en blanco para mantener la
                                        actual)</label>
                                    <input type="file" id="imagen" name="imagen" accept="image/*">
                                    <div id="preview" class="cuadro-imagen" style="margin-top: 10px;">
                                        <c:if test="${not empty productoEditar.imagen}">
                                            <p style="font-size: 12px; color: #666;">Imagen actual:</p>
                                            <img src="../assets/img/${productoEditar.imagen}"
                                                style="max-width: 150px; border-radius: 8px;" alt="Imagen actual">
                                        </c:if>
                                    </div>
                                </div>

                                <div class="btn-container">
                                    <a href="../ProductoServlet" class="btn btn-cancelar">
                                        <i class="fa-solid fa-xmark"></i> Cancelar
                                    </a>
                                    <button type="submit" class="btn btn-guardar">
                                        <i class="fa-solid fa-save"></i> Guardar
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </main>
                <footer></footer>
                <script src="../js/validaciones.js?v=<%= System.currentTimeMillis() %>"></script>
                <script>
                    document.getElementById('imagen').addEventListener('change', function (e) {
                        var file = e.target.files[0];
                        if (file) {
                            var reader = new FileReader();
                            reader.onload = function (e) {
                                var preview = document.getElementById('preview');
                                preview.innerHTML = '<img src="' + e.target.result + '" style="max-width: 150px; border-radius: 8px;" alt="Nueva Previsualización">';
                            }
                            reader.readAsDataURL(file);
                        }
                    });
                </script>
            </body>

            </html>