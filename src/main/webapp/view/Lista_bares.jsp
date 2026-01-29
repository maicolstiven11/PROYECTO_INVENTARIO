<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Mis Bares</title>
            <link rel="stylesheet" href="../css/lista_bares.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>

            <main>
                <h2>MIS BARES REGISTRADOS</h2>

                <section class="lista-bares">
                    <div class="contenido-scroll">

                        <!-- Bucle JSTL: Por cada bar en la lista -->
                        <c:forEach var="bar" items="${listaBares}">
                            <div class="elemento-bar">
                                <div class="info-bar">
                                    <span class="nombre">${bar.nombre}</span>
                                    <span class="estado">(${bar.estado})</span>
                                </div>
                                <div class="acciones">
                                    <c:choose>
                                        <c:when test="${bar.tieneInventarioActivo}">
                                            <a href="../InventarioServlet?action=entrar&idNegocio=${bar.idNegocio}"
                                                class="iniciar-invantario">
                                                <h3>Ver Inventario</h3>
                                                <i class="fa-solid fa-box-open"
                                                    style="font-size: 40px; color: #27ae60;"></i>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="view/Inicio_inv.html?idNegocio=${bar.idNegocio}&nombreBar=${bar.nombre}"
                                                class="iniciar-invantario">
                                                <h3>Iniciar-inventario</h3>
                                                <img src="../assets/img/boton_iniciar_inv.png" alt="iniciar-invantario">
                                            </a>
                                        </c:otherwise>
                                    </c:choose>

                                    <a href="../NegocioServlet?action=eliminar&id=${bar.idNegocio}" class="borrar-bar"
                                        onclick="return confirm('¿Estás seguro de eliminar este bar?');">
                                        <h3>borrar-bar</h3>
                                        <img src="../assets/img/icono_borrar_bar.png" alt="borrar-bar">
                                    </a>

                                    <a href="view/visualizar_bar.html?idNegocio=${bar.idNegocio}"
                                        class="visualizar_bar">
                                        <h3>Visualizar-bar</h3>
                                        <img src="assets/img/icono_visualizar_bar.png" alt="icono_visualizar_bar">
                                    </a>
                                </div>
                            </div>
                        </c:forEach>

                        <!-- Mensaje si no hay bares -->
                        <c:if test="${empty listaBares}">
                            <div class="elemento-bar" style="text-align: center; padding: 40px;">
                                <p>No tienes bares registrados aún.</p>
                                <a href="view/registroBar.html" style="color: #27ae60; font-weight: bold;">
                                    <i class="fa-solid fa-plus"></i> Registrar mi primer bar
                                </a>
                            </div>
                        </c:if>

                    </div>
                </section>

                <a href="view/Menu_sistema.html" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar
                </a>
            </main>

            <footer></footer>
        </body>

        </html>