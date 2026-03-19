<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Registrar Pedido a Proveedor</title>
            <link rel="stylesheet" href="css/agg_pedido.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>

            <main>
                <div class="form-container">
                    <h2 class="form-title">
                        <i class="fa-solid fa-truck-fast"></i> Registrar Pedido a Proveedor
                    </h2>

                    <form action="PedidoServlet" method="POST" class="form-grid">
                        <input type="hidden" name="action" value="guardar">

                        <!-- Proveedor -->
                        <div class="form-group" style="padding-bottom: 5px;">
                            <label for="id_proveedor">Proveedor:</label>
                            <div class="select-wrapper select2-wrapper">
                                <select name="id_proveedor" id="id_proveedor" required style="width: 100%;">
                                    <option value="" disabled selected>Seleccione un proveedor</option>
                                    <c:forEach var="prov" items="${listaProveedores}">
                                        <option value="${prov.idProveedor}">${prov.nombreProveedor}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Producto (Carga todos los productos de la BD) -->
                        <div class="form-group" style="padding-bottom: 5px;">
                            <label for="id_producto">Producto:</label>
                            <div class="select-wrapper select2-wrapper">
                                <select name="id_producto" id="id_producto" required style="width: 100%;">
                                    <option value="" disabled selected>Seleccione un producto</option>
                                    <c:forEach var="item" items="${listaProductos}">
                                        <option value="${item.idProducto}">${item.nombre} - $${item.precioUnitario}
                                            (${item.marca})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Fechas -->
                        <div class="form-group">
                            <label for="fecha_pedido">Fecha de Pedido:</label>
                            <input type="date" name="fecha_pedido" id="fecha_pedido" required>
                        </div>

                        <div class="form-group">
                            <label for="fecha_entrega">Fecha de Entrega:</label>
                            <input type="date" name="fecha_entrega" id="fecha_entrega" required>
                        </div>

                        <!-- Datos Económicos -->
                        <div class="form-group">
                            <label for="cantidad">Cantidad (Unidades):</label>
                            <input type="number" name="cantidad" id="cantidad" min="1" placeholder="Ej: 30" required>
                        </div>

                        <div class="form-group">
                            <label for="subtotal">Subtotal ($):</label>
                            <input type="number" name="subtotal" id="subtotal" min="0" step="0.01"
                                placeholder="Ej: 100000" required>
                        </div>

                        <div class="form-group">
                            <label for="porcentaje_iva">IVA (%):</label>
                            <select id="porcentaje_iva" required style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #ccc; background-color: white;">
                                <option value="0" selected>0% (Sin IVA)</option>
                                <option value="5">5%</option>
                                <option value="8">8% (Impoconsumo)</option>
                                <option value="19">19%</option>
                            </select>
                            <!-- Input oculto para enviar el valor calculado al servlet -->
                            <input type="hidden" name="iva" id="iva_calculado" value="0">
                            <!-- Mostramos al usuario el valor calculado en pesos -->
                            <input type="text" id="iva_mostrar" readonly class="input-readonly" style="margin-top: 5px;" placeholder="Valor del IVA ($0.00)">
                        </div>

                        <!-- Campos Calculados (Readonly) -->
                        <div class="form-group">
                            <label for="total_pedido">Total Pedido ($):</label>
                            <input type="number" name="total_pedido" id="total_pedido" readonly class="input-readonly">
                        </div>

                        <div class="form-group full-width">
                            <label for="precio_unitario">Precio Unitario Real ($):</label>
                            <input type="number" name="precio_unitario" id="precio_unitario" readonly
                                class="input-readonly highlight">
                            <small style="color: grey;">Calculado: (Subtotal + IVA) / Cantidad</small>
                        </div>

                        <!-- Botones -->
                        <div class="btn-container">
                            <a href="view/menu_inventario.jsp" class="btn btn-cancelar">
                                <i class="fa-solid fa-arrow-left"></i> Regresar
                            </a>
                            <button type="submit" class="btn btn-guardar">
                                <i class="fa-solid fa-save"></i> Registrar Pedido
                            </button>
                        </div>
                    </form>
                </div>
            </main>

            <footer></footer>

            <!-- Script para cálculos automáticos y validaciones -->
            <script>
                const cantidadInput = document.getElementById('cantidad');
                const subtotalInput = document.getElementById('subtotal');
                const porcentajeIvaSelect = document.getElementById('porcentaje_iva');
                const ivaCalculadoInput = document.getElementById('iva_calculado');
                const ivaMostrarInput = document.getElementById('iva_mostrar');
                const totalInput = document.getElementById('total_pedido');
                const precioUnitarioInput = document.getElementById('precio_unitario');

                function calcularValores() {
                    const cantidad = parseFloat(cantidadInput.value) || 0;
                    const subtotal = parseFloat(subtotalInput.value) || 0;
                    const porcentajeIva = parseFloat(porcentajeIvaSelect.value) || 0;

                    // 1. Calcular IVA en pesos
                    const ivaEnPesos = subtotal * (porcentajeIva / 100);
                    ivaCalculadoInput.value = ivaEnPesos.toFixed(2);
                    ivaMostrarInput.value = "$ " + ivaEnPesos.toFixed(2);

                    // 2. Calcular Total Pedido
                    const total = subtotal + ivaEnPesos;
                    totalInput.value = total.toFixed(2);

                    // 3. Calcular Precio Unitario Real
                    if (cantidad > 0) {
                        const precioUnitario = total / cantidad;
                        precioUnitarioInput.value = precioUnitario.toFixed(2);
                    } else {
                        precioUnitarioInput.value = "0.00";
                    }
                }

                // Escuchar eventos para el cálculo
                cantidadInput.addEventListener('input', calcularValores);
                subtotalInput.addEventListener('input', calcularValores);
                porcentajeIvaSelect.addEventListener('change', calcularValores);
                
                // Configurar límites de fecha al cargar la página (solo restringir al año actual)
                document.addEventListener('DOMContentLoaded', function() {
                    const yearActual = new Date().getFullYear();
                    // Limitar entre el primero de enero y el 31 de diciembre del año actual
                    const minDate = yearActual + "-01-01";
                    const maxDate = yearActual + "-12-31";
                    
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
            <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
            <script>
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

                    // Asegurar que validaciones base no choquen con Select2
                    $('#id_proveedor').on('change', function () { $(this).valid(); });
                    $('#id_producto').on('change', function () { $(this).valid(); });
                });
            </script>
            <script src="js/validaciones.js"></script>
        </body>

        </html>