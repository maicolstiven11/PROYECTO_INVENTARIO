<%--=====================================================================VISTA JSP: Menu_sistema.jsp - Menú Principal
    del Sistema QUIÉN LA MUESTRA: LoginServlet.doPost() → Después del login exitoso →
    response.sendRedirect("view/Menu_sistema.jsp") DATOS QUE USA DE LA SESIÓN (puestos por LoginServlet): -
    ${usuarioLogueado} → Objeto Usuario. Viene de: session.setAttribute("usuarioLogueado", usuario) Se usa:
    ${usuarioLogueado.idRol} para mostrar opciones según rol (1=Admin, 2=Trabajador) - ${sessionScope.idNegocioActual} →
    Integer. ID del bar asignado al trabajador. Viene de: LoginServlet al buscar en USUARIO_NEGOCIO el bar del
    trabajador Se usa para armar el enlace "IR A MI BAR" DATOS QUE USA DE LA URL (parámetros ?error=...): -
    ${param.error} → String. Posibles valores: 'AccesoDenegado' : Viene de: InventarioServlet si un trabajador intenta
    iniciar inventario 'NoInventarioActivoTrabajador' : Viene de: InventarioServlet si el bar no tiene inventario activo
    CONTROL DE ACCESO POR ROL: - ${usuarioLogueado.idRol==1} → Es Administrador → Ve: VER MIS BARES, AGREGAR BAR,
    GESTIÓN TRABAJADORES, PRODUCTOS - ${usuarioLogueado.idRol==2} → Es Trabajador → Ve: IR A MI BAR
    (o "SIN BAR ASIGNADO" si no tiene) - Ambos roles ven: MI PERFIL y CERRAR
    SESIÓN=====================================================================--%>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%-- Librería JSTL Core: <c:if> para condicionales, <c:choose> para switch --%>
                <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                    <%-- RNF: Control de Acceso - Redirige al login si la sesión expiró --%>
                        <c:if test="${empty sessionScope.usuarioLogueado}">
                            <c:redirect url="Inicio_sesion.html" />
                        </c:if>
                        <!DOCTYPE html>
                        <html lang="es">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Menu sistema</title>
                            <link rel="stylesheet" href="../css/menu_sistema.css">
                            <link rel="stylesheet"
                                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                        </head>

                        <body>
                            <header>
                                <nav class="navbar">
                                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                                </nav>
                            </header>
                            <main>
                                <h1>Contabilidad-Sistematica</h1>

                                <%-- MENSAJE DE ERROR: Acceso denegado (trabajador intentó hacer algo de admin) --%>
                                    <%-- Viene de: InventarioServlet redirige con ?error=AccesoDenegado --%>
                                        <c:if test="${param.error == 'AccesoDenegado'}">
                                            <div
                                                style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #f5c6cb;">
                                                <i class="fa-solid fa-circle-xmark"></i> ACCESO DENEGADO: Los
                                                trabajadores
                                                no tienen permisos
                                                para iniciar inventarios.
                                            </div>
                                        </c:if>

                                        <%-- MENSAJE DE ERROR: Sin inventario activo para trabajador --%>
                                            <%-- Viene de: InventarioServlet si el bar del trabajador no tiene
                                                inventario activo --%>
                                                <c:if test="${param.error == 'NoInventarioActivoTrabajador'}">
                                                    <div
                                                        style="background-color: #fff3cd; color: #856404; padding: 15px; border-radius: 8px; text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #ffeeba;">
                                                        <i class="fa-solid fa-triangle-exclamation"></i> INVENTARIO NO
                                                        ACTIVO. Por favor, comunícate con
                                                        el administrador para que inicie el inventario de este bar.
                                                    </div>
                                                </c:if>

                                                <section class="contenedor__menu">

                                                    <%--=====================================================================OPCIONES
                                                        SOLO PARA ADMINISTRADOR (idRol==1) ${usuarioLogueado.idRol} →
                                                        Java llama a
                                                        usuario.getIdRol()=====================================================================--%>

                                                        <%-- VER MIS BARES: Llama a NegocioServlet (GET) →
                                                            NegocioDAO.listarNegocios() → Lista_bares.jsp --%>
                                                            <c:if test="${usuarioLogueado.idRol == 1}">
                                                                <a href="../NegocioServlet">
                                                                    <div class="contenedor__aggBar">
                                                                        <div class="fondo_img">
                                                                            <img src="../assets/img/view_bares.png"
                                                                                alt="bares_logo">
                                                                        </div>
                                                                        <p>VER MIS BARES</p>
                                                                    </div>
                                                                </a>
                                                            </c:if>

                                                            <%-- AGREGAR BAR: Navega al formulario HTML estático
                                                                registroBar.html --%>
                                                                <c:if test="${usuarioLogueado.idRol == 1}">
                                                                    <a href="registroBar.html">
                                                                        <div class="contenedor__aggBar">
                                                                            <div class="fondo_img">
                                                                                <img src="../assets/img/agg_bar.png"
                                                                                    alt="agg_bar">
                                                                            </div>
                                                                            <p>AGREGAR BAR</p>
                                                                        </div>
                                                                    </a>

                                                                    <%-- GESTIÓN TRABAJADORES: Llama a
                                                                        TrabajadorServlet?action=listar →
                                                                        gestion_trabajadores.jsp --%>
                                                                        <a href="../TrabajadorServlet?action=listar">
                                                                            <div class="contenedor__aggBar">
                                                                                <div class="fondo_img">
                                                                                    <i
                                                                                        class="fa-solid fa-users-gear"></i>
                                                                                </div>
                                                                                <p>GESTIÓN TRABAJADORES</p>
                                                                            </div>
                                                                        </a>
                                                                </c:if>

                                                                <%--=====================================================================OPCIONES
                                                                    SOLO PARA TRABAJADOR
                                                                    (idRol==2)=====================================================================--%>
                                                                    <c:if test="${usuarioLogueado.idRol == 2}">
                                                                        <%-- Si tiene bar asignado (idNegocioActual
                                                                            existe en sesión) --%>
                                                                            <c:if
                                                                                test="${not empty sessionScope.idNegocioActual}">
                                                                                <%-- IR A MI BAR: Llama a
                                                                                    InventarioServlet?action=entrar con
                                                                                    el ID del negocio --%>
                                                                                    <%-- ${sessionScope.idNegocioActual}
                                                                                        → Viene de: LoginServlet al
                                                                                        buscar bar del trabajador --%>
                                                                                        <a
                                                                                            href="../InventarioServlet?action=entrar&idNegocio=${sessionScope.idNegocioActual}">
                                                                                            <div
                                                                                                class="contenedor__aggBar">
                                                                                                <div class="fondo_img">
                                                                                                    <i
                                                                                                        class="fa-solid fa-cash-register"></i>
                                                                                                </div>
                                                                                                <p>IR A MI BAR</p>
                                                                                            </div>
                                                                                        </a>
                                                                            </c:if>
                                                                            <%-- Si NO tiene bar asignado → Mostrar
                                                                                mensaje deshabilitado --%>
                                                                                <c:if
                                                                                    test="${empty sessionScope.idNegocioActual}">
                                                                                    <div class="contenedor__aggBar"
                                                                                        style="opacity: 0.5;">
                                                                                        <div class="fondo_img">
                                                                                            <i
                                                                                                class="fa-solid fa-circle-exclamation"></i>
                                                                                        </div>
                                                                                        <p>SIN BAR ASIGNADO</p>
                                                                                    </div>
                                                                                </c:if>
                                                                    </c:if>

                                                                    <%--=====================================================================OPCIONES
                                                                        PARA TODOS LOS
                                                                        ROLES=====================================================================--%>

                                                                        <%-- MI PERFIL: Ahora apunta al PerfilServlet
                                                                            que carga correos y teléfonos antes de
                                                                            mostrar el JSP --%>
                                                                            <a href="../PerfilServlet">
                                                                                <div class="contenedor__aggBar">
                                                                                    <div class="fondo_img">
                                                                                        <i
                                                                                            class="fa-solid fa-user-tie"></i>
                                                                                    </div>
                                                                                    <p>MI PERFIL</p>
                                                                                </div>
                                                                            </a>

                                                                            <%-- PRODUCTOS DE VENTA: Solo admin. Llama a
                                                                                ProductoServlet (GET) →
                                                                                editar_productos.jsp --%>
                                                                                <c:if
                                                                                    test="${usuarioLogueado.idRol == 1}">
                                                                                    <a href="../ProductoServlet">
                                                                                        <div class="contenedor__aggBar">
                                                                                            <div class="fondo_img">
                                                                                                <i
                                                                                                    class="fa-solid fa-boxes-stacked"></i>
                                                                                            </div>
                                                                                            <p>PRODUCTOS DE VENTA</p>
                                                                                        </div>
                                                                                    </a>
                                                                                </c:if>

                                                </section>

                                                <%-- CERRAR SESIÓN: Llama a LoginServlet?action=logout que invalida la
                                                    sesión --%>
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