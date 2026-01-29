<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Menu principal de inventario</title>
            <link rel="stylesheet" href="../css/menu_inventario.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        </head>

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>
            <main>
                <aside class="aside">
                    <h2 class="aside__title">Contabilidad-Sistematica</h2>

                    <!-- Perfil de usuario -->
                    <a href="perfil_admin.jsp" class="user-profile user-profile--link">
                        <i class="fa-solid fa-circle-user user-profile__icon"></i>
                        <div class="user-profile__info">
                            <p class="user-profile__name">${usuarioLogueado.nombre}</p>
                            <span class="user-profile__role badge-admin">${usuarioLogueado.nombreRol}</span>
                        </div>
                        <i class="fa-solid fa-chevron-right user-profile__arrow"></i>
                    </a>

                    <nav class="aside__nav">

                        <!-- VENTAS -->
                        <c:if test="${usuarioLogueado.tienePermiso('VER_HISTORIAL_VENTAS')}">
                            <a href="../VentaServlet?action=listar" class="aside__link">
                                <i class="fa-solid fa-bottle-water aside__icon"></i>
                                Visualizar ventas
                            </a>
                        </c:if>

                        <!-- GASTOS -->
                        <c:if test="${usuarioLogueado.tienePermiso('VER_GASTOS')}">
                            <a href="../GastoServlet?action=listar" class="aside__link">
                                <i class="fa-solid fa-sack-dollar aside__icon"></i>
                                Visualizar gastos
                            </a>
                        </c:if>

                        <!-- EDITAR PRODUCTOS (Solo Admin/Permiso) -->
                        <c:if test="${usuarioLogueado.tienePermiso('EDITAR_PRODUCTO')}">
                            <a href="../ProductoServlet" class="aside__link">
                                <i class="fa-solid fa-pen-to-square aside__icon"></i>
                                Editar productos
                            </a>
                        </c:if>

                        <!-- PROVEEDORES (Solo Admin/Permiso) -->
                        <c:if test="${usuarioLogueado.tienePermiso('GESTIONAR_PROVEEDORES')}">
                            <a href="../ProveedorServlet" class="aside__link">
                                <i class="fa-solid fa-truck-field aside__icon"></i>
                                Editar proveedores
                            </a>
                        </c:if>

                        <!-- INFORMES (Solo Admin/Permiso) -->
                        <c:if test="${usuarioLogueado.tienePermiso('VER_INFORMES')}">
                            <a href="visualizar_informes.html" class="aside__link">
                                <i class="fa-solid fa-file-invoice aside__icon"></i>
                                Visualizar informes
                            </a>
                        </c:if>

                        <div class="aside__divider"></div>

                        <a href="Menu_sistema.jsp" class="aside__link btn-salir">
                            <i class="fa-solid fa-arrow-right-from-bracket aside__icon"></i>
                            Salir al Menú Sistema
                        </a>

                    </nav>
                </aside>

                <section class="content">

                    <div class="content__inicio">
                        <h1 class="content__title">Inventario de</h1>
                        <h2 class="content__subtitle">el bar: ${sessionScope.nombreNegocioActual}</h2>
                        <hr class="content__divider">
                    </div>

                    <div class="cards">

                        <c:if test="${usuarioLogueado.tienePermiso('REGISTRAR_GASTO')}">
                            <a href="agregar_gasto.html" class="card">
                                <p class="card__title">Agregar gasto</p>
                                <i class="fa-solid fa-plus card__icon"></i>
                            </a>
                        </c:if>

                        <c:if test="${usuarioLogueado.tienePermiso('HACER_PEDIDOS_PROVEEDOR')}">
                            <a href="agregar_pedido.html" class="card">
                                <p class="card__title">Agregar pedido de proveedor</p>
                                <i class="fa-solid fa-plus card__icon"></i>
                            </a>
                        </c:if>

                        <c:if test="${usuarioLogueado.tienePermiso('REALIZAR_VENTA')}">
                            <a href="../VentaServlet" class="card">
                                <p class="card__title">Agregar venta</p>
                                <i class="fa-solid fa-plus card__icon"></i>
                            </a>
                        </c:if>

                    </div>

                </section>

            </main>
            <footer>

            </footer>
        </body>

        </html>