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

                        <!-- VENTAS - Visible para TODOS -->
                        <a href="../VentaServlet?action=listar" class="aside__link">
                            <i class="fa-solid fa-bottle-water aside__icon"></i>
                            Visualizar ventas
                        </a>

                        <!-- GASTOS - Visible para TODOS -->
                        <a href="../GastoServlet?action=listar" class="aside__link">
                            <i class="fa-solid fa-sack-dollar aside__icon"></i>
                            Visualizar gastos
                        </a>

                        <!-- PEDIDOS A PROVEEDORES - Visible para TODOS -->
                        <a href="../PedidoServlet?action=listar" class="aside__link">
                            <i class="fa-solid fa-truck-fast aside__icon"></i>
                            Visualizar pedidos
                        </a>

                        <!-- OPCIONES SOLO PARA ADMINISTRADOR -->
                        <c:if test="${usuarioLogueado.idRol == 1}">
                            <a href="../ProductoServlet" class="aside__link">
                                <i class="fa-solid fa-pen-to-square aside__icon"></i>
                                Editar productos
                            </a>

                            <a href="../ProveedorServlet" class="aside__link">
                                <i class="fa-solid fa-truck-field aside__icon"></i>
                                Editar proveedores
                            </a>

                            <a href="../InformeServlet" class="aside__link">
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

                        <!-- AGREGAR GASTO - Visible para TODOS -->
                        <a href="agregar_gasto.html" class="card">
                            <p class="card__title">Agregar gasto</p>
                            <i class="fa-solid fa-plus card__icon"></i>
                        </a>

                        <!-- AGREGAR VENTA - Visible para TODOS -->
                        <a href="../VentaServlet" class="card">
                            <p class="card__title">Agregar venta</p>
                            <i class="fa-solid fa-plus card__icon"></i>
                        </a>

                        <!-- PEDIDOS PROVEEDOR - Solo ADMINISTRADOR -->
                        <c:if test="${usuarioLogueado.idRol == 1}">
                            <a href="../PedidoServlet?action=nuevo" class="card">
                                <p class="card__title">Agregar pedido de proveedor</p>
                                <i class="fa-solid fa-plus card__icon"></i>
                            </a>

                            <!-- BOTÓN DE CIERRE DE INVENTARIO -->
                            <a href="../InventarioServlet?action=cargar_cierre" class="card card--cierre"
                                style="background-color: #ff9800; color: white;">
                                <p class="card__title">Cerrar Inventario (Semana/Mes)</p>
                                <i class="fa-solid fa-lock card__icon"></i>
                            </a>
                        </c:if>

                    </div>

                </section>

            </main>
            <footer>

            </footer>
        </body>

        </html>