<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cierre de Inventario - Stock Final</title>
            <link rel="stylesheet" href="../css/inv_detalle.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <style>
                .inventario__instruccion--cierre {
                    color: #ff9800;
                    font-weight: bold;
                }

                .inventario__nota--cierre {
                    color: #d32f2f;
                }
            </style>
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main class="inventario">
                <section class="inventario__encabezado">
                    <h1 class="inventario__titulo">Cierre de Inventario (Contabilidad)</h1>
                    <p class="inventario__instruccion inventario__instruccion--cierre">Ingrese la cantidad física final
                        para cuadrar caja</p>

                    <c:if test="${not empty param.error_tiempo}">
                        <div class="alerta"
                            style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; border: 1px solid #f5c6cb;">
                            <i class="fas fa-exclamation-triangle"></i> ${param.error_tiempo}
                        </div>
                    </c:if>
                </section>

                <form action="../InventarioServlet" method="POST">
                    <input type="hidden" name="action" value="finalizar_inventario">

                    <section class="inventario__tabla">
                        <div class="inventario__contenedor-scroll">
                            <table class="inventario__tabla-contenido">
                                <thead class="inventario__cabecera-tabla">
                                    <tr class="inventario__fila inventario__fila--cabecera">
                                        <th class="inventario__celda">Producto</th>
                                        <th class="inventario__celda">Stock Inicial</th>
                                        <th class="inventario__celda">Stock Final Físico</th>
                                    </tr>
                                </thead>
                                <tbody class="inventario__cuerpo-tabla">
                                    <c:forEach var="det" items="${listaDetalles}">
                                        <tr class="inventario__fila">
                                            <td class="inventario__celda">${det.nombreProducto}</td>
                                            <td class="inventario__celda">${det.cantidadInicial}</td>
                                            <td class="inventario__celda">
                                                <input type="hidden" name="id_producto" value="${det.idProducto}">
                                                <input type="number" name="cantidad_final"
                                                    class="inventario__entrada-cantidad" step="0.01"
                                                    placeholder="Cantidad actual" required min="0">
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <div class="contenedor__boton" style="text-align: center; margin-top: 20px;">
                        <p class="inventario__nota inventario__nota--cierre">
                            ¡Atención! Al finalizar, el periodo de inventario se cerrará y no podrá agregar más ventas a
                            este periodo.
                        </p>
                        <button type="submit" class="inventario__boton-finalizar"
                            style="border: none; cursor: pointer; background-color: #ff9800;">
                            <i class="fas fa-lock inventario__icono-finalizar"></i>
                            Finalizar y Cerrar Periodo
                        </button>
                        <br><br>
                        <a href="../view/menu_inventario.jsp" style="text-decoration: none; color: #666;">
                            <i class="fas fa-arrow-left"></i> Volver sin cerrar
                        </a>
                    </div>
                </form>
            </main>
        </body>

        </html>