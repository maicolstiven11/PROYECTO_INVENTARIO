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

                        <form action="../ProductoServlet" method="POST">
                            <input type="hidden" name="action" value="actualizar">
                            <input type="hidden" name="id_producto" value="${productoEditar.idProducto}">

                            <div class="form-grid">
                                <div class="form-group full-width">
                                    <label for="nombre">Nombre del Producto</label>
                                    <input type="text" id="nombre" name="nombre" value="${productoEditar.nombre}"
                                        required>
                                </div>

                                <div class="form-group">
                                    <label for="marca">Marca</label>
                                    <input type="text" id="marca" name="marca" value="${productoEditar.marca}">
                                </div>

                                <div class="form-group">
                                    <label for="precio">Precio Unitario</label>
                                    <input type="number" id="precio" name="precio" step="0.01"
                                        value="${productoEditar.precioUnitario}" required>
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
                                        value="${productoEditar.cantidadMedida}" placeholder="Ej: 750ml">
                                </div>

                                <div class="form-group">
                                    <label for="fecha_vencimiento">Fecha Vencimiento</label>
                                    <input type="date" id="fecha_vencimiento" name="fecha_vencimiento"
                                        value="<fmt:formatDate value='${productoEditar.fechaVencimiento}' pattern='yyyy-MM-dd'/>">
                                </div>

                                <div class="form-group full-width">
                                    <label for="imagen">Imagen (nombre del archivo)</label>
                                    <input type="text" id="imagen" name="imagen" value="${productoEditar.imagen}">
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
            </body>

            </html>