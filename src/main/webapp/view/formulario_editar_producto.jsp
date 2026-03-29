<%--=====================================================================
    VISTA JSP: formulario_editar_producto.jsp - Formulario de Edición de Producto
    
    QUIÉN LA MUESTRA: ProductoServlet (GET con action=editar&id=X) → 
    request.getRequestDispatcher("view/formulario_editar_producto.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (ProductoServlet):
    - ${productoEditar} → Producto. Objeto con datos del producto a editar
    Viene de: ProductoDAO.obtenerProductoPorId()
    
    Cada Producto tiene: idProducto, nombre, precioUnitario, marca, tipo, 
                         cantidadMedida, fechaVencimiento, imagen
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Actualizar producto: POST → ProductoServlet?action=actualizar
    Envía: id_producto, nombre, marca, precio, tipo, cantidad_medida, 
           fecha_vencimiento, imagen (opcional)
    - Cancelar: GET → ProductoServlet (lista de productos)
    
    IMPORTANCIA:
    - Permite modificar información existente de productos
    - Mantiene actualizado el catálogo del negocio
    - Facilita la gestión de precios y fechas de vencimiento
    =====================================================================--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%-- Librería JSTL Core: Permite usar <c:if>, <c:forEach> para lógica en el JSP --%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%-- Librería JSTL Format: Permite usar <fmt:formatDate> para formatear fechas --%>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                <title>Editar Producto</title>
                <%-- Hoja de estilos CSS para el formulario de edición --%>
                <link rel="stylesheet" href="../css/editar_producto_form.css">
                <%-- Librería Font Awesome: Iconos (fa-pen-to-square, fa-xmark, fa-save) --%>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
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
                    <%-- Contenedor principal del formulario --%>
                    <div class="form-container">
                        <%-- Título del formulario con icono --%>
                        <h2 class="form-title"><i class="fa-solid fa-pen-to-square"></i> Editar Producto</h2>

                        <%-- Formulario que envía los datos actualizados al ProductoServlet --%>
                        <form action="../ProductoServlet" method="POST" enctype="multipart/form-data">
                            <%-- Campo oculto que indica la acción de actualizar --%>
                            <input type="hidden" name="action" value="actualizar">
                            <%-- Campo oculto con el ID del producto a editar --%>
                            <input type="hidden" name="id_producto" value="${productoEditar.idProducto}">

                            <%-- Grid de campos del formulario --%>
                            <div class="form-grid">
                                <%-- Campo para el nombre del producto (ancho completo) --%>
                                <div class="form-group full-width">
                                    <label for="nombre">Nombre del Producto</label>
                                    <%-- Campo de texto con el nombre actual del producto --%>
                                    <input type="text" id="nombre" name="nombre" value="${productoEditar.nombre}"
                                        maxlength="100" required>
                                </div>

                                <%-- Campo para la marca del producto --%>
                                <div class="form-group">
                                    <label for="marca">Marca</label>
                                    <%-- Campo de texto con la marca actual --%>
                                    <input type="text" id="marca" name="marca" value="${productoEditar.marca}"
                                        maxlength="50">
                                </div>

                                <%-- Campo para el precio unitario --%>
                                <div class="form-group">
                                    <label for="precio">Precio Unitario</label>
                                    <%-- Campo numérico con el precio actual --%>
                                    <input type="number" id="precio" name="precio" step="0.01"
                                        value="${productoEditar.precioUnitario}" min="0" max="999999999" required>
                                </div>

                                <%-- Campo para el tipo de producto --%>
                                <div class="form-group">
                                    <label for="tipo">Tipo de Producto</label>
                                    <%-- Select con el tipo actual seleccionado --%>
                                    <select id="tipo" name="tipo" required>
                                        <%-- Opción Bebidas seleccionada si es el tipo actual --%>
                                        <option value="bebida" ${productoEditar.tipo=='bebida' ? 'selected' : '' }>
                                            Bebidas</option>
                                        <%-- Opción Snacks seleccionada si es el tipo actual --%>
                                        <option value="snack" ${productoEditar.tipo=='snack' ? 'selected' : '' }>Snacks
                                        </option>
                                        <%-- Opción Dulces seleccionada si es el tipo actual --%>
                                        <option value="dulce" ${productoEditar.tipo=='dulce' ? 'selected' : '' }>Dulces
                                        </option>
                                        <%-- Opción Cigarros seleccionada si es el tipo actual --%>
                                        <option value="cigarro" ${productoEditar.tipo=='cigarro' ? 'selected' : '' }>
                                            Cigarros</option>
                                    </select>
                                </div>

                                <%-- Campo para la cantidad/medida del producto --%>
                                <div class="form-group">
                                    <label for="cantidad_medida">Cantidad/Medida</label>
                                    <%-- Campo de texto con la cantidad/medida actual --%>
                                    <input type="text" id="cantidad_medida" name="cantidad_medida"
                                        value="${productoEditar.cantidadMedida}" placeholder="Ej: 750ml" maxlength="50">
                                </div>

                                <%-- Campo para la fecha de vencimiento --%>
                                <div class="form-group">
                                    <label for="fecha_vencimiento">Fecha Vencimiento</label>
                                    <%-- Campo de fecha con la fecha actual formateada --%>
                                    <input type="date" id="fecha_vencimiento" name="fecha_vencimiento"
                                        value="<fmt:formatDate value='${productoEditar.fechaVencimiento}' pattern='yyyy-MM-dd'/>">
                                </div>

                                <%-- Campo para la imagen del producto (ancho completo) --%>
                                <div class="form-group full-width">
                                    <label for="imagen">Imagen del producto (Dejar en blanco para mantener la
                                        actual)</label>
                                    <%-- Campo de archivo para nueva imagen (opcional) --%>
                                    <input type="file" id="imagen" name="imagen" accept="image/*">
                                    <%-- Contenedor para previsualización de imagen --%>
                                    <div id="preview" class="cuadro-imagen" style="margin-top: 10px;">
                                        <%-- Muestra la imagen actual si existe --%>
                                        <c:if test="${not empty productoEditar.imagen}">
                                            <p style="font-size: 12px; color: #666;">Imagen actual:</p>
                                            <%-- Muestra la imagen actual del producto --%>
                                            <img src="../assets/img/${productoEditar.imagen}"
                                                style="max-width: 150px; border-radius: 8px;" alt="Imagen actual">
                                        </c:if>
                                    </div>
                                </div>

                                <%-- Contenedor de botones de acción --%>
                                <div class="btn-container">
                                    <%-- Botón para cancelar y volver a la lista de productos --%>
                                    <a href="../ProductoServlet" class="btn btn-cancelar">
                                        <i class="fa-solid fa-xmark"></i> Cancelar
                                    </a>
                                    <%-- Botón para guardar los cambios --%>
                                    <button type="submit" class="btn btn-guardar">
                                        <i class="fa-solid fa-save"></i> Guardar
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </main>
                <%-- Pie de página (vacío en este caso) --%>
                <footer></footer>
                <%-- Script de validaciones centralizadas con timestamp para evitar caché --%>
                <script src="../js/validaciones.js?v=<%= System.currentTimeMillis() %>"></script>
                <%-- Script para previsualizar imagen seleccionada --%>
                <script>
                    // Se ejecuta cuando el usuario selecciona una nueva imagen
                    document.getElementById('imagen').addEventListener('change', function (e) {
                        // Obtiene el archivo seleccionado
                        var file = e.target.files[0];
                        // Si se seleccionó un archivo
                        if (file) {
                            // Crea un objeto FileReader para leer el archivo
                            var reader = new FileReader();
                            // Se ejecuta cuando la lectura del archivo se completa
                            reader.onload = function (e) {
                                // Obtiene el contenedor de previsualización
                                var preview = document.getElementById('preview');
                                // Muestra la nueva imagen con estilo específico
                                preview.innerHTML = '<img src="' + e.target.result + '" style="max-width: 150px; border-radius: 8px;" alt="Nueva Previsualización">';
                            }
                            // Inicia la lectura del archivo como URL de datos
                            reader.readAsDataURL(file);
                        }
                    });
                </script>
            </body>

            <%-- Cierre del documento HTML --%>
            </html>