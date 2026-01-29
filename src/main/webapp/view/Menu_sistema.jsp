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

                    <!-- OPCIÓN PARA TODOS: VER MIS BARES -->
                    <a href="../NegocioServlet">
                        <div class="contenedor__aggBar">
                            <div class="fondo_img">
                                <img src="../assets/img/view_bares.png" alt="bares_logo">
                            </div>
                            <p>VER MIS BARES</p>
                        </div>
                    </a>

                    <!-- OPCIONES SOLO SI TIENE PERMISO (Base de Datos) -->
                    <!-- Asumimos que en la tabla PERMISO existe uno llamado 'AGREGAR_NEGOCIO' -->
                    <c:if test="${usuarioLogueado.tienePermiso('AGREGAR_NEGOCIO')}">
                        <a href="registroBar.html">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <img src="../assets/img/agg_bar.png" alt="agg_bar">
                                </div>
                                <p>AGREGAR BAR</p>
                            </div>
                        </a>
                    </c:if>

                    <c:if test="${usuarioLogueado.tienePermiso('GESTIONAR_TRABAJADORES')}">
                        <a href="gestion_trabajadores.html">
                            <div class="contenedor__aggBar">
                                <div class="fondo_img">
                                    <i class="fa-solid fa-users-gear"></i>
                                </div>
                                <p>GESTIÓN TRABAJADORES</p>
                            </div>
                        </a>
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

                    <!-- OPCIÓN PARA TODOS: PRODUCTOS?? (Tal vez restringir si es necesario) -->
                    <!-- Dejamos visible por ahora -->
                    <a href="../ProductoServlet">
                        <div class="contenedor__aggBar">
                            <div class="fondo_img">
                                <i class="fa-solid fa-boxes-stacked"></i>
                            </div>
                            <p>PRODUCTOS DE VENTA</p>
                        </div>
                    </a>


                </section>

            </main>
            <footer>

            </footer>
        </body>

        </html>