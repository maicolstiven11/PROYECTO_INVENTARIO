<%--=====================================================================
    VISTA JSP: agregar_pedido.jsp - Registrar Pedido a Proveedor
    
    QUIÉN LA MUESTRA: PedidoServlet?action=nuevo → 
    request.getRequestDispatcher("view/agregar_pedido.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (PedidoServlet):
    - ${listaProveedores} → List<Proveedor>. Viene de: ProveedorDAO.listarProveedores()
    - ${listaProductos} → List<Producto>. Viene de: ProductoDAO.listarProductos()
    
    Cada Proveedor tiene: idProveedor, nombreProveedor
    Cada Producto tiene: idProducto, nombre, precioUnitario, marca
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Guardar pedido: POST → PedidoServlet?action=guardar
    Envía: id_proveedor, id_producto, fecha_pedido, fecha_entrega, cantidad, 
           subtotal, porcentaje_iva, iva_calculado, total_pedido, precio_unitario
    
    IMPORTANCIA:
    - Permite registrar compras de productos a proveedores
    - Calcula automáticamente IVA y precio unitario real
    - Actualiza el inventario al recibir el pedido
    =====================================================================--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach> para iterar listas --%>
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
            <title>Registrar Pedido a Proveedor</title>
            <%-- Hoja de estilos CSS para el formulario de pedidos --%>
            <link rel="stylesheet" href="css/agg_pedido.css">
            <%-- Librería Font Awesome: Iconos (fa-truck-fast, fa-save, fa-arrow-left) --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Select2 CSS: Librería para hacer los selects buscables y con mejor UX --%>
            <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
        </head>

        <%-- Cuerpo principal del documento --%>
        <body>
            <%-- Cabecera con navegación y logo del sistema --%>
            <header>
                <nav class="navbar">
                    <%-- Logo del sistema que aparece en la barra de navegación --%>
                    <img class="navbar__logo" src="assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>

            <%-- Contenido principal de la página --%>
            <main>
                <%-- Contenedor principal del formulario --%>
                <div class="form-container">
                    <%-- Título del formulario con icono --%>
                    <h2 class="form-title">
                        <i class="fa-solid fa-truck-fast"></i> Registrar Pedido a Proveedor
                    </h2>

                    <%-- Formulario que envía los datos al PedidoServlet --%>
                    <form action="PedidoServlet" method="POST" class="form-grid">
                        <%-- Campo oculto que indica la acción a ejecutar en el servlet --%>
                        <input type="hidden" name="action" value="guardar">

                        <%-- Sección: Selección de Proveedor --%>
                        <div class="form-group" style="padding-bottom: 5px;">
                            <label for="id_proveedor">Proveedor:</label>
                            <%-- Contenedor para el select con estilo Select2 --%>
                            <div class="select-wrapper select2-wrapper">
                                <%-- Select de proveedores cargado dinámicamente desde la base de datos --%>
                                <select name="id_proveedor" id="id_proveedor" required style="width: 100%;">
                                    <%-- Opción por defecto deshabilitada --%>
                                    <option value="" disabled selected>Seleccione un proveedor</option>
                                    <%-- Itera sobre la lista de proveedores que viene del servlet --%>
                                    <c:forEach var="prov" items="${listaProveedores}">
                                        <%-- Cada option tiene el ID como valor y el nombre como texto visible --%>
                                        <option value="${prov.idProveedor}">${prov.nombreProveedor}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <%-- Sección: Selección de Producto --%>
                        <div class="form-group" style="padding-bottom: 5px;">
                            <label for="id_producto">Producto:</label>
                            <%-- Contenedor para el select con estilo Select2 --%>
                            <div class="select-wrapper select2-wrapper">
                                <%-- Select de productos cargado dinámicamente desde la base de datos --%>
                                <select name="id_producto" id="id_producto" required style="width: 100%;">
                                    <%-- Opción por defecto deshabilitada --%>
                                    <option value="" disabled selected>Seleccione un producto</option>
                                    <%-- Itera sobre la lista de productos que viene del servlet --%>
                                    <c:forEach var="item" items="${listaProductos}">
                                        <%-- Muestra nombre, precio unitario y marca para mejor identificación --%>
                                        <option value="${item.idProducto}">${item.nombre} - $${item.precioUnitario}
                                            (${item.marca})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <%-- Sección: Fechas del Pedido --%>
                        <div class="form-group">
                            <label for="fecha_pedido">Fecha de Pedido:</label>
                            <%-- Campo de fecha para registrar cuándo se hace el pedido --%>
                            <input type="date" name="fecha_pedido" id="fecha_pedido" required>
                        </div>

                        <div class="form-group">
                            <label for="fecha_entrega">Fecha de Entrega:</label>
                            <%-- Campo de fecha para registrar cuándo se espera recibir el pedido --%>
                            <input type="date" name="fecha_entrega" id="fecha_entrega" required>
                        </div>

                        <%-- Sección: Datos Económicos del Pedido --%>
                        <div class="form-group">
                            <label for="cantidad">Cantidad (Unidades):</label>
                            <%-- Campo numérico para la cantidad de unidades solicitadas --%>
                            <input type="number" name="cantidad" id="cantidad" min="1" placeholder="Ej: 30" required>
                        </div>

                        <div class="form-group">
                            <label for="subtotal">Subtotal ($):</label>
                            <%-- Campo para el subtotal sin IVA del pedido --%>
                            <input type="number" name="subtotal" id="subtotal" min="0" step="0.01"
                                placeholder="Ej: 100000" required>
                        </div>

                        <%-- Sección: Cálculo de IVA --%>
                        <div class="form-group">
                            <label for="porcentaje_iva">IVA (%):</label>
                            <%-- Select con porcentajes de IVA comunes en Colombia --%>
                            <select id="porcentaje_iva" required style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #ccc; background-color: white;">
                                <option value="0" selected>0% (Sin IVA)</option>
                                <option value="5">5%</option>
                                <option value="8">8% (Impoconsumo)</option>
                                <option value="19">19%</option>
                            </select>
                            <%-- Campo oculto que envía el valor del IVA calculado en pesos al servlet --%>
                            <input type="hidden" name="iva" id="iva_calculado" value="0">
                            <%-- Campo de solo lectura para mostrar al usuario el valor del IVA en pesos --%>
                            <input type="text" id="iva_mostrar" readonly class="input-readonly" style="margin-top: 5px;" placeholder="Valor del IVA ($0.00)">
                        </div>

                        <%-- Sección: Campos Calculados (Solo lectura) --%>
                        <div class="form-group">
                            <label for="total_pedido">Total Pedido ($):</label>
                            <%-- Campo calculado automáticamente: subtotal + IVA --%>
                            <input type="number" name="total_pedido" id="total_pedido" readonly class="input-readonly">
                        </div>

                        <div class="form-group full-width">
                            <label for="precio_unitario">Precio Unitario Real ($):</label>
                            <%-- Campo calculado: (subtotal + IVA) / cantidad --%>
                            <input type="number" name="precio_unitario" id="precio_unitario" readonly
                                class="input-readonly highlight">
                            <%-- Texto de ayuda que explica la fórmula de cálculo --%>
                            <small style="color: grey;">Calculado: (Subtotal + IVA) / Cantidad</small>
                        </div>

                        <%-- Sección: Botones de Acción --%>
                        <div class="btn-container">
                            <%-- Botón para regresar al menú de inventario sin guardar --%>
                            <a href="view/menu_inventario.jsp" class="btn btn-cancelar">
                                <i class="fa-solid fa-arrow-left"></i> Regresar
                            </a>
                            <%-- Botón para enviar el formulario y guardar el pedido --%>
                            <button type="submit" class="btn btn-guardar">
                                <i class="fa-solid fa-save"></i> Registrar Pedido
                            </button>
                        </div>
                    </form>
                </div>
            </main>

            <%-- Pie de página (vacío en este caso) --%>
            <footer></footer>

            <%-- Script para cálculos automáticos y validaciones --%>
            <script>
                <%-- Referencias a los elementos del DOM que se usarán en los cálculos --%>
                const cantidadInput = document.getElementById('cantidad');
                const subtotalInput = document.getElementById('subtotal');
                const porcentajeIvaSelect = document.getElementById('porcentaje_iva');
                const ivaCalculadoInput = document.getElementById('iva_calculado');
                const ivaMostrarInput = document.getElementById('iva_mostrar');
                const totalInput = document.getElementById('total_pedido');
                const precioUnitarioInput = document.getElementById('precio_unitario');

                <%-- Función principal que calcula todos los valores automáticamente --%>
                function calcularValores() {
                    <%-- Obtener valores de los inputs, convirtiendo a número (0 si está vacío) --%>
                    const cantidad = parseFloat(cantidadInput.value) || 0;
                    const subtotal = parseFloat(subtotalInput.value) || 0;
                    const porcentajeIva = parseFloat(porcentajeIvaSelect.value) || 0;

                    <%-- 1. Calcular IVA en pesos: subtotal * (porcentaje / 100) --%>
                    const ivaEnPesos = subtotal * (porcentajeIva / 100);
                    ivaCalculadoInput.value = ivaEnPesos.toFixed(2);
                    ivaMostrarInput.value = "$ " + ivaEnPesos.toFixed(2);

                    <%-- 2. Calcular Total Pedido: subtotal + IVA --%>
                    const total = subtotal + ivaEnPesos;
                    totalInput.value = total.toFixed(2);

                    <%-- 3. Calcular Precio Unitario Real: total / cantidad --%>
                    if (cantidad > 0) {
                        const precioUnitario = total / cantidad;
                        precioUnitarioInput.value = precioUnitario.toFixed(2);
                    } else {
                        precioUnitarioInput.value = "0.00";
                    }
                }

                <%-- Event listeners para ejecutar los cálculos cuando cambian los valores --%>
                cantidadInput.addEventListener('input', calcularValores);
                subtotalInput.addEventListener('input', calcularValores);
                porcentajeIvaSelect.addEventListener('change', calcularValores);
                
                <%-- Configurar restricciones de fecha al cargar la página --%>
                document.addEventListener('DOMContentLoaded', function() {
                    <%-- Obtener el año actual para limitar las fechas --%>
                    const yearActual = new Date().getFullYear();
                    <%-- Limitar fechas al año en curso (1 de enero a 31 de diciembre) --%>
                    const minDate = yearActual + "-01-01";
                    const maxDate = yearActual + "-12-31";
                    
                    <%-- Aplicar restricciones a los campos de fecha --%>
                    const elFechaPedido = document.getElementById('fecha_pedido');
                    const elFechaEntrega = document.getElementById('fecha_entrega');
                    
                    if(elFechaPedido) {
                        elFechaPedido.setAttribute("min", minDate);
                        elFechaPedido.setAttribute("max", maxDate);
                    }
                    if(elFechaEntrega) {
                        elFechaEntrega.setAttribute("min", minDate);
                        elFechaEntrega.setAttribute("max", maxDate);
                    }
                });
            </script>
            <%-- jQuery: Necesario para que Select2 funcione --%>
            <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
            <%-- Select2: Convierte los selects normales en selects con buscador --%>
            <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
            <script>
                <%-- Inicializar Select2 en los selects de proveedores y productos --%>
                $(document).ready(function () {
                    $('#id_proveedor').select2({
                        placeholder: 'Buscar proveedor...',
                        allowClear: true,
                        language: { noResults: function () { return "No se encontraron proveedores activos"; } }
                    });
                    $('#id_producto').select2({
                        placeholder: 'Buscar producto...',
                        allowClear: true,
                        language: { noResults: function () { return "No se encontraron productos"; } }
                    });

                    <%-- Asegurar que las validaciones del formulario funcionen con Select2 --%>
                    $('#id_proveedor').on('change', function () { $(this).valid(); });
                    $('#id_producto').on('change', function () { $(this).valid(); });
                });
            </script>
            <%-- Script de validaciones centralizadas --%>
            <script src="js/validaciones.js"></script>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>