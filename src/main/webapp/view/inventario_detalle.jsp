<%--=====================================================================
    VISTA JSP: inventario_detalle.jsp - Registro de Stock Inicial
    
    QUIÉN LA MUESTRA: InventarioServlet?action=mostrar_detalle → 
    request.getRequestDispatcher("view/inventario_detalle.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (InventarioServlet):
    - ${listaProductos} → List<Producto>. Viene de: ProductoDAO.listarProductos()
    
    Cada Producto tiene: idProducto, nombre, imagen, precioUnitario
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Guardar stock: POST → InventarioServlet?action=guardar_stock
    Envía: id_producto[] (array), cantidad[] (array) por cada producto
    
    IMPORTANCIA:
    - Es el primer paso para iniciar un nuevo inventario
    - Permite registrar el stock inicial de todos los productos
    - Es fundamental para el control de inventario preciso
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:choose>, <c:when>, <c:otherwise> --%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%-- Inicio del documento HTML con idioma inglés (configuración por defecto) --%>
        <!DOCTYPE html>
        <html lang="en">

        <%-- Cabecera del documento con metadatos y recursos --%>
        <head>
            <%-- Codificación de caracteres UTF-8 para soporte de caracteres especiales --%>
            <meta charset="UTF-8">
            <%-- Configuración de viewport para diseño responsive --%>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <%-- Título de la página que aparece en el navegador --%>
            <title>Inventario Detalle - Stock Inicial</title>
            <%-- Hoja de estilos CSS para el formulario de inventario --%>
            <link rel="stylesheet" href="../css/inv_detalle.css">
            <%-- Librería Font Awesome: Iconos (fa-check) --%>
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
            <main class="inventario">
                <%-- Sección de encabezado del formulario --%>
                <section class="inventario__encabezado">
                    <%-- Título principal del sistema --%>
                    <h1 class="inventario__titulo">Contabilidad-Sistematica</h1>
                    <%-- Instrucciones para el usuario --%>
                    <p class="inventario__instruccion">Registre el stock de productos iniciales</p>
                </section>

                <%-- Formulario que envía los datos de stock al InventarioServlet --%>
                <form action="../InventarioServlet" method="POST">
                    <%-- Campo oculto que indica la acción a ejecutar en el servlet --%>
                    <input type="hidden" name="action" value="guardar_stock">

                    <%-- Sección con la tabla de productos para registrar stock --%>
                    <section class="inventario__tabla">
                        <%-- Contenedor con scroll para la tabla (en caso de muchos productos) --%>
                        <div class="inventario__contenedor-scroll">
                            <%-- Tabla principal que muestra todos los productos del catálogo --%>
                            <table class="inventario__tabla-contenido">
                                <%-- Encabezado de la tabla --%>
                                <thead class="inventario__cabecera-tabla">
                                    <tr class="inventario__fila inventario__fila--cabecera">
                                        <%-- Columnas de la tabla --%>
                                        <th class="inventario__celda">ID</th>
                                        <th class="inventario__celda">Nombre</th>
                                        <th class="inventario__celda">Imagen</th>
                                        <th class="inventario__celda">Precio</th>
                                        <th class="inventario__celda">Cantidad Inicial</th>
                                    </tr>
                                </thead>
                                <%-- Cuerpo de la tabla con los datos de los productos --%>
                                <tbody class="inventario__cuerpo-tabla">
                                    <%-- Itera sobre la lista de productos que viene del servlet --%>
                                    <c:forEach var="p" items="${listaProductos}">
                                        <%-- Fila individual para cada producto --%>
                                        <tr class="inventario__fila">
                                            <%-- ID del producto --%>
                                            <td class="inventario__celda">${p.idProducto}</td>
                                            <%-- Nombre del producto --%>
                                            <td class="inventario__celda">${p.nombre}</td>
                                            <%-- Columna de imagen del producto --%>
                                            <td class="inventario__celda">
                                                <%-- Lógica para mostrar imagen del producto o imagen por defecto --%>
                                                <c:choose>
                                                    <%-- Si el producto tiene imagen asignada --%>
                                                    <c:when test="${not empty p.imagen}">
                                                        <%-- Muestra la imagen específica del producto --%>
                                                        <img src="../assets/img/${p.imagen}" alt="${p.nombre}"
                                                            class="inventario__imagen"
                                                            <%-- Si la imagen no carga, muestra la imagen por defecto --%>
                                                            onerror="this.src='../assets/img/default.png'">
                                                    </c:when>
                                                    <%-- Si el producto no tiene imagen --%>
                                                    <c:otherwise>
                                                        <%-- Muestra la imagen por defecto --%>
                                                        <img src="../assets/img/default.png" alt="Sin Imagen"
                                                            class="inventario__imagen">
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <%-- Precio unitario del producto --%>
                                            <td class="inventario__celda">$${p.precioUnitario}</td>
                                            <%-- Columna para ingresar la cantidad inicial --%>
                                            <td class="inventario__celda">
                                                <%-- Campo oculto para enviar el ID del producto junto con la cantidad --%>
                                                <input type="hidden" name="id_producto" value="${p.idProducto}">
                                                <%-- Campo numérico para ingresar la cantidad inicial del producto --%>
                                                <input type="number" name="cantidad"
                                                    class="inventario__entrada-cantidad" placeholder="0" min="0"
                                                    max="100000">
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <%-- Sección con el botón para finalizar el registro --%>
                    <div class="contenedor__boton" style="text-align: center; margin-top: 20px;">
                        <%-- Nota informativa para el usuario --%>
                        <p class="inventario__nota">(Ingrese la cantidad de producto inicial que tienes a la venta)</p>
                        <%-- Botón que envía el formulario con todos los datos de stock --%>
                        <button type="submit" class="inventario__boton-finalizar"
                            style="border: none; cursor: pointer;">
                            <%-- Icono de check para indicar finalización --%>
                            <i class="fas fa-check inventario__icono-finalizar"></i>
                            Finalizar el registro
                        </button>
                    </div>
                </form>
            </main>

            <%-- Pie de página (vacío en este caso) --%>
            <footer>
            </footer>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>