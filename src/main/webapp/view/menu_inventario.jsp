<%--=====================================================================
    VISTA JSP: menu_inventario.jsp - Menú Principal de Gestión de Inventario
    
    QUIÉN LA MUESTRA: InventarioServlet?action=entrar → 
    request.getRequestDispatcher("view/menu_inventario.jsp").forward(...)
    
    DATOS QUE USA DE LA SESIÓN:
    - ${usuarioLogueado} → Objeto Usuario. Viene de: LoginServlet (sesión iniciada)
    - ${sessionScope.nombreNegocioActual} → String. Nombre del bar actual
    
    DATOS QUE USA DE PARÁMETROS URL:
    - ${param.error_tiempo} → String. Mensaje de error (ej: tiempo de inventario excedido)
    - ${param.msg_exito} → String. Mensaje de éxito (ej: operación completada)
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Visualizar ventas: GET → VentaServlet?action=listar
    - Visualizar gastos: GET → GastoServlet?action=listar
    - Visualizar pedidos: GET → PedidoServlet?action=listar
    - Editar productos: GET → ProductoServlet (solo admin)
    - Visualizar proveedores: GET → ProveedorServlet (solo admin)
    - Visualizar informes: GET → InformeServlet (solo admin)
    - Agregar venta: GET → VentaServlet (formulario de ventas)
    - Agregar pedido: GET → PedidoServlet?action=nuevo (solo admin)
    - Cerrar inventario: GET → InventarioServlet?action=cargar_cierre (solo admin)
    
    IMPORTANCIA:
    - Es el centro de operaciones del inventario activo
    - Organiza todas las funciones por rol de usuario
    - Proporciona acceso rápido a todas las operaciones del negocio
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:if>, <c:forEach> para lógica en el JSP --%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <title>Menu principal de inventario</title>
            <%-- Hoja de estilos CSS para el menú de inventario --%>
            <link rel="stylesheet" href="../css/menu_inventario.css">
            <%-- Librería Font Awesome: Iconos para menú y acciones --%>
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
            <%-- Contenido principal con layout de sidebar y contenido --%>
            <main>
                <%-- Barra lateral (sidebar) con navegación y perfil --%>
                <aside class="aside">
                    <%-- Título del sistema en el sidebar --%>
                    <h2 class="aside__title">Contabilidad-Sistematica</h2>

                    <%-- Perfil de usuario con enlace a configuración --%>
                    <a href="../PerfilServlet" class="user-profile user-profile--link">
                        <%-- Icono de usuario --%>
                        <i class="fa-solid fa-circle-user user-profile__icon"></i>
                        <%-- Información del usuario logueado --%>
                        <div class="user-profile__info">
                            <%-- Nombre del usuario --%>
                            <p class="user-profile__name">${usuarioLogueado.nombre}</p>
                            <%-- Rol del usuario con estilo de badge --%>
                            <span class="user-profile__role badge-admin">${usuarioLogueado.nombreRol}</span>
                        </div>
                        <%-- Icono de flecha indicando que es un enlace --%>
                        <i class="fa-solid fa-chevron-right user-profile__arrow"></i>
                    </a>

                    <%-- Navegación principal del sidebar --%>
                    <nav class="aside__nav">

                        <%-- Opción de VENTAS - Visible para TODOS los roles --%>
                        <a href="../VentaServlet?action=listar" class="aside__link">
                            <%-- Icono de botella para representar ventas --%>
                            <i class="fa-solid fa-bottle-water aside__icon"></i>
                            Visualizar ventas
                        </a>

                        <%-- Opción de GASTOS - Visible para TODOS los roles --%>
                        <a href="../GastoServlet?action=listar" class="aside__link">
                            <%-- Icono de bolsa de dinero para representar gastos --%>
                            <i class="fa-solid fa-sack-dollar aside__icon"></i>
                            Visualizar gastos
                        </a>

                        <%-- Opción de PEDIDOS A PROVEEDORES - Visible para TODOS los roles --%>
                        <a href="../PedidoServlet?action=listar" class="aside__link">
                            <%-- Icono de camión para representar pedidos --%>
                            <i class="fa-solid fa-truck-fast aside__icon"></i>
                            Visualizar pedidos
                        </a>

                        <%-- OPCIONES SOLO PARA ADMINISTRADOR (idRol == 1) --%>
                        <c:if test="${usuarioLogueado.idRol == 1}">
                            <%-- Opción para editar productos del catálogo --%>
                            <a href="../ProductoServlet" class="aside__link">
                                <%-- Icono de lápiz para editar --%>
                                <i class="fa-solid fa-pen-to-square aside__icon"></i>
                                Editar productos
                            </a>

                            <%-- Opción para gestionar proveedores --%>
                            <a href="../ProveedorServlet" class="aside__link">
                                <%-- Icono de camión con campo para proveedores --%>
                                <i class="fa-solid fa-truck-field aside__icon"></i>
                                Visualizar proveedores
                            </a>

                            <%-- Opción para ver informes y estadísticas --%>
                            <a href="../InformeServlet" class="aside__link">
                                <%-- Icono de factura para informes --%>
                                <i class="fa-solid fa-file-invoice aside__icon"></i>
                                Visualizar informes
                            </a>
                        </c:if>

                        <%-- Divisor visual en el menú --%>
                        <div class="aside__divider"></div>

                        <%-- Botón para salir al menú principal del sistema --%>
                        <a href="Menu_sistema.jsp" class="aside__link btn-salir">
                            <%-- Icono de salida --%>
                            <i class="fa-solid fa-arrow-right-from-bracket aside__icon"></i>
                            Salir al Menú Sistema
                        </a>

                    </nav>
                </aside>

                <%-- Área de contenido principal con tarjetas de acciones --%>
                <section class="content">

                    <%-- Sección de encabezado con mensajes y título --%>
                    <div class="content__inicio">
                        <%-- Mensaje de error (ej: tiempo de inventario excedido) --%>
                        <c:if test="${not empty param.error_tiempo}">
                            <div style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; border: 1px solid #f5c6cb;">
                                <i class="fa-solid fa-triangle-exclamation"></i> <strong>Aviso:</strong> ${param.error_tiempo}
                            </div>
                        </c:if>
                        <%-- Mensaje de éxito (ej: operación completada) --%>
                        <c:if test="${not empty param.msg_exito}">
                            <div style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; border: 1px solid #c3e6cb; font-size: 16px;">
                                <i class="fa-solid fa-circle-check"></i> <strong>${param.msg_exito}</strong>
                            </div>
                        </c:if>
                        <%-- Título principal del contenido --%>
                        <h1 class="content__title">Inventario de</h1>
                        <%-- Subtítulo con el nombre del negocio actual --%>
                        <h2 class="content__subtitle">el bar: ${sessionScope.nombreNegocioActual}</h2>
                        <%-- Línea divisora visual --%>
                        <hr class="content__divider">
                    </div>

                    <%-- Grid de tarjetas con acciones rápidas --%>
                    <div class="cards">

                        <%-- AGREGAR GASTO - Visible para TODOS los roles --%>
                        <a href="agregar_gasto.html" class="card">
                            <%-- Título de la tarjeta --%>
                            <p class="card__title">Agregar gasto</p>
                            <%-- Icono de más para agregar --%>
                            <i class="fa-solid fa-plus card__icon"></i>
                        </a>

                        <%-- AGREGAR VENTA - Visible para TODOS los roles --%>
                        <a href="../VentaServlet" class="card">
                            <%-- Título de la tarjeta --%>
                            <p class="card__title">Agregar venta</p>
                            <%-- Icono de más para agregar --%>
                            <i class="fa-solid fa-plus card__icon"></i>
                        </a>

                        <%-- OPCIONES SOLO PARA ADMINISTRADOR (idRol == 1) --%>
                        <c:if test="${usuarioLogueado.idRol == 1}">
                            <%-- AGREGAR PEDIDO PROVEEDOR - Solo ADMINISTRADOR --%>
                            <a href="../PedidoServlet?action=nuevo" class="card">
                                <%-- Título de la tarjeta --%>
                                <p class="card__title">Agregar pedido de proveedor</p>
                                <%-- Icono de más para agregar --%>
                                <i class="fa-solid fa-plus card__icon"></i>
                            </a>

                            <%-- BOTÓN DE CIERRE DE INVENTARIO - Solo ADMINISTRADOR --%>
                            <a href="../InventarioServlet?action=cargar_cierre" class="card card--cierre"
                                style="background-color: #ff9800; color: white;">
                                <%-- Título de la tarjeta --%>
                                <p class="card__title">Cerrar Inventario (Semana/Mes)</p>
                                <%-- Icono de candado para cierre --%>
                                <i class="fa-solid fa-lock card__icon"></i>
                            </a>
                        </c:if>

                    </div>

                </section>

            </main>
            <%-- Pie de página (vacío en este caso) --%>
            <footer>

            </footer>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>