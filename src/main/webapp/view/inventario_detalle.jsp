<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Inventario Detalle - Stock Inicial</title>
            <link rel="stylesheet" href="../css/inv_detalle.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main class="inventario">
                <section class="inventario__encabezado">
                    <h1 class="inventario__titulo">Contabilidad-Sistematica</h1>
                    <p class="inventario__instruccion">Registre el stock de productos iniciales</p>
                </section>

                <form action="../InventarioServlet" method="POST">
                    <input type="hidden" name="action" value="guardar_stock">



                    <section class="inventario__tabla">
                        <div class="inventario__contenedor-scroll">
                            <table class="inventario__tabla-contenido">
                                <thead class="inventario__cabecera-tabla">
                                    <tr class="inventario__fila inventario__fila--cabecera">
                                        <th class="inventario__celda">ID</th>
                                        <th class="inventario__celda">Nombre</th>
                                        <th class="inventario__celda">Imagen</th>
                                        <th class="inventario__celda">Precio</th>
                                        <th class="inventario__celda">Cantidad Inicial</th>
                                    </tr>
                                </thead>
                                <tbody class="inventario__cuerpo-tabla">
                                    <c:forEach var="p" items="${listaProductos}">
                                        <tr class="inventario__fila">
                                            <td class="inventario__celda">${p.idProducto}</td>
                                            <td class="inventario__celda">${p.nombre}</td>
                                            <td class="inventario__celda">
                                                <c:choose>
                                                    <c:when test="${not empty p.imagen}">
                                                        <img src="../assets/img/${p.imagen}" alt="${p.nombre}"
                                                            class="inventario__imagen"
                                                            onerror="this.src='../assets/img/default.png'">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="../assets/img/default.png" alt="Sin Imagen"
                                                            class="inventario__imagen">
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="inventario__celda">$${p.precioUnitario}</td>
                                            <td class="inventario__celda">
                                                <!-- Inputs ocultos para enviar ID de producto -->
                                                <input type="hidden" name="id_producto" value="${p.idProducto}">
                                                <!-- Input de cantidad -->
                                                <input type="number" name="cantidad"
                                                    class="inventario__entrada-cantidad" placeholder="0" min="0">
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <div class="contenedor__boton" style="text-align: center; margin-top: 20px;">
                        <p class="inventario__nota">(Ingrese la cantidad de producto inicial que tienes a la venta)</p>
                        <button type="submit" class="inventario__boton-finalizar"
                            style="border: none; cursor: pointer;">
                            <i class="fas fa-check inventario__icono-finalizar"></i>
                            Finalizar el registro
                        </button>
                    </div>
                </form>
            </main>

            <footer>
            </footer>
        </body>

        </html>