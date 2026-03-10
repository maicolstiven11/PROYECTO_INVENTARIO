<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Menu sistema</title>
            <link rel="stylesheet" href="../css/menu_sistema.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main>
                <h1>Contabilidad-Sistematica</h1>
                <section class="contenedor__menu">

                    <!-- OPCIÓN SOLO PARA ADMIN: VER MIS BARES -->
                    <c:if test="${usuarioLogueado.idRol == 1}">
                        <a href="../NegocioServlet">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <img src="../assets/img/view_bares.png" alt="bares_logo">
                                </div>
                                <p>VER MIS BARES</p>
                            </div>
                        </a>
                    </c:if>

                    <!-- OPCIONES SOLO PARA ADMINISTRADOR (Rol 1) -->
                    <c:if test="${usuarioLogueado.idRol == 1}">
                        <a href="registroBar.html">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <img src="../assets/img/agg_bar.png" alt="agg_bar">
                                </div>
                                <p>AGREGAR BAR</p>
                            </div>
                        </a>

                        <a href="../TrabajadorServlet?action=listar">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <i class="fa-solid fa-users-gear"></i>
                                </div>
                                <p>GESTIÓN TRABAJADORES</p>
                            </div>
                        </a>
                    </c:if>

                    <!-- OPCIONES SOLO PARA TRABAJADOR (Rol 2) -->
                    <c:if test="${usuarioLogueado.idRol == 2}">
                        <c:if test="${not empty sessionScope.idNegocioActual}">
                            <a href="../InventarioServlet?action=entrar&idNegocio=${sessionScope.idNegocioActual}">
                                <div class="contenedor__aggBar">
                                    <div class="fondo_img">
                                        <i class="fa-solid fa-cash-register"></i>
                                    </div>
                                    <p>IR A MI BAR</p>
                                </div>
                            </a>
                        </c:if>
                        <c:if test="${empty sessionScope.idNegocioActual}">
                            <div class="contenedor__aggBar" style="opacity: 0.5;">
                                <div class="fondo_img">
                                    <i class="fa-solid fa-circle-exclamation"></i>
                                </div>
                                <p>SIN BAR ASIGNADO</p>
                            </div>
                        </c:if>
                    </c:if>

                    <!-- OPCIÓN PARA TODOS: MI PERFIL -->
                    <a href="perfil_admin.jsp">
                        <div class="contenedor__aggBar">
                            <div class="fondo_img">
                                <i class="fa-solid fa-user-tie"></i>
                            </div>
                            <p>MI PERFIL</p>
                        </div>
                    </a>

                    <!-- OPCIÓN SOLO PARA ADMIN: PRODUCTOS -->
                    <c:if test="${usuarioLogueado.idRol == 1}">
                        <a href="../ProductoServlet">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <i class="fa-solid fa-boxes-stacked"></i>
                                </div>
                                <p>PRODUCTOS DE VENTA</p>
                            </div>
                        </a>
                    </c:if>

                </section>

                <!-- BOTÓN CERRAR SESIÓN -->
                <div style="text-align: center; margin-top: 30px;">
                    <a href="../LoginServlet?action=logout" style="display: inline-block; background-color: #e74c3c; color: white; padding: 12px 30px; 
                              text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 14px;"
                        onclick="return confirm('¿Estás seguro de cerrar sesión?');">
                        <i class="fa-solid fa-right-from-bracket"></i> Cerrar Sesión
                    </a>
                </div>

            </main>
            <footer>

            </footer>
        </body>

        </html>