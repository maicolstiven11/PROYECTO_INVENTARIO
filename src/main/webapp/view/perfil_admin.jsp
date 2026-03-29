<%--=====================================================================
    VISTA JSP: perfil_admin.jsp - Mi Perfil de Administrador
    
    QUIÉN LA MUESTRA: PerfilServlet (GET) → 
    request.getRequestDispatcher("view/perfil_admin.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (PerfilServlet):
    - ${usuarioLogueado} → Usuario. Objeto con datos del usuario de la sesión
    - ${listaCorreos} → List<String>. Correos del usuario (UsuarioDAO.listarCorreos())
    - ${listaTelefonos} → List<String>. Teléfonos del usuario (UsuarioDAO.listarTelefonos())
    - ${numBares} → Integer. Cantidad de bares (sesión, LoginServlet)
    - ${numTrabajadores} → Integer. Cantidad de trabajadores (sesión, LoginServlet)
    
    Parámetros URL:
    - ${param.msg} → String. Mensajes de éxito (CorreoAgregado, TelefonoAgregado)
    - ${param.error} → String. Mensajes de error (CorreoYaExiste, TelefonoYaExiste, etc.)
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.usuarioLogueado} → Usuario autenticado
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Agregar correo: POST → PerfilServlet?action=agregarCorreo
    - Agregar teléfono: POST → PerfilServlet?action=agregarTelefono
    - Eliminar contacto: POST → PerfilServlet?action=eliminarContacto
    - Volver al menú: GET → Menu_sistema.jsp
    
    IMPORTANCIA:
    - Permite al administrador gestionar su información personal
    - Facilita el mantenimiento de datos de contacto
    - Muestra estadísticas del negocio del administrador
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if>, <c:choose> para lógica en el JSP --%>
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
            <title>Mi Perfil</title>
            <%-- Hoja de estilos CSS específica para el perfil --%>
            <link rel="stylesheet" href="../css/perfil_admin.css">
            <%-- Librería Font Awesome: Iconos varios --%>
            <link rel="stylesheet"
                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Estilos CSS adicionales para la sección de contactos --%>
            <style>
                <%-- Estilo para las secciones de contacto --%>
                .seccion-contacto {
                    border-top: 1px solid #eee;
                    padding-top: 15px;
                    margin-bottom: 15px;
                }

                <%-- Estilo para los títulos de las secciones de contacto --%>
                .seccion-contacto h3 {
                    color: #004B76;
                    font-size: 16px;
                    margin-bottom: 10px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                <%-- Estilo para la lista de contactos --%>
                .lista-contactos {
                    list-style: none;
                    padding: 0;
                    margin: 0 0 10px 0;
                }

                <%-- Estilo para los elementos de la lista de contactos --%>
                .lista-contactos li {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    padding: 8px 12px;
                    background-color: #f8f9fa;
                    border-radius: 8px;
                    margin-bottom: 6px;
                    font-size: 14px;
                    color: #2c3e50;
                }

                <%-- Estilo para los iconos en la lista de contactos --%>
                .lista-contactos li i {
                    color: #004B76;
                    font-size: 14px;
                    width: 20px;
                    text-align: center;
                }

                <%-- Badge para indicar el contacto principal --%>
                .badge-principal {
                    background-color: #d1f2eb;
                    color: #16a085;
                    font-size: 11px;
                    padding: 2px 8px;
                    border-radius: 10px;
                    margin-left: auto;
                    font-weight: bold;
                }

                <%-- Estilo para el formulario de agregar contactos --%>
                .form-agregar {
                    display: flex;
                    gap: 8px;
                    margin-top: 8px;
                }

                <%-- Estilo para los campos de entrada del formulario --%>
                .form-agregar input {
                    flex: 1;
                    padding: 8px 12px;
                    border: 1px solid #ddd;
                    border-radius: 8px;
                    font-size: 14px;
                    font-family: inherit;
                    outline: none;
                    transition: border-color 0.3s;
                }

                <%-- Efecto focus para los campos de entrada --%>
                .form-agregar input:focus {
                    border-color: #004B76;
                }

                <%-- Estilo para el botón de agregar --%>
                .btn-agregar {
                    padding: 8px 16px;
                    background-color: #27ae60;
                    color: white;
                    border: none;
                    border-radius: 8px;
                    cursor: pointer;
                    font-weight: bold;
                    font-size: 13px;
                    transition: background-color 0.3s, transform 0.2s;
                }

                <%-- Efecto hover para el botón de agregar --%>
                .btn-agregar:hover {
                    background-color: #219a52;
                    transform: translateY(-1px);
                }

                <%-- Estilo para mensajes de éxito --%>
                .mensaje-exito {
                    background-color: #d4edda;
                    color: #155724;
                    padding: 10px 15px;
                    border-radius: 8px;
                    text-align: center;
                    margin-bottom: 15px;
                    font-weight: bold;
                    border: 1px solid #c3e6cb;
                    font-size: 14px;
                }

                <%-- Estilo para mensajes de error --%>
                .mensaje-error-perfil {
                    background-color: #f8d7da;
                    color: #721c24;
                    padding: 10px 15px;
                    border-radius: 8px;
                    text-align: center;
                    margin-bottom: 15px;
                    font-weight: bold;
                    border: 1px solid #f5c6cb;
                    font-size: 14px;
                }
            </style>
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
            <main>
                <%-- Contenedor principal del perfil --%>
                <div class="contenedor-perfil">
                    <%-- Cabecera del perfil con avatar, nombre y rol --%>
                    <div class="cabecera-perfil">
                        <div class="avatar-perfil">
                            <i class="fa-solid fa-user-tie"></i>
                                        <%-- ${usuarioLogueado.nombreRol} → Modelo: Usuario.getNombreRol()
                                            calcula "Administrador" o "Trabajador" --%>
                                            <span class="rol-perfil">
                                                ${usuarioLogueado.nombreRol}
                                            </span>
                                </div>

                                <%-- MENSAJES DE ÉXITO/ERROR --%>
                                    <c:if test="${param.msg == 'CorreoAgregado'}">
                                        <div class="mensaje-exito">
                                            <i class="fa-solid fa-circle-check"></i> Correo electrónico agregado
                                            correctamente.
                                        </div>
                                    </c:if>
                                    <c:if test="${param.msg == 'TelefonoAgregado'}">
                                        <div class="mensaje-exito">
                                            <i class="fa-solid fa-circle-check"></i> Teléfono agregado correctamente.
                                        </div>
                                    </c:if>
                                    <c:if test="${param.error == 'CorreoYaExiste'}">
                                        <div class="mensaje-error-perfil">
                                            <i class="fa-solid fa-circle-xmark"></i> Ese correo electrónico ya está
                                            registrado.
                                        </div>
                                    </c:if>
                                    <c:if test="${param.error == 'TelefonoYaExiste'}">
                                        <div class="mensaje-error-perfil">
                                            <i class="fa-solid fa-circle-xmark"></i> Ese número de teléfono ya está
                                            registrado.
                                        </div>
                                    </c:if>
                                    <c:if test="${param.error == 'CorreoVacio'}">
                                        <div class="mensaje-error-perfil">
                                            <i class="fa-solid fa-circle-xmark"></i> Debes ingresar un correo
                                            electrónico.
                                        </div>
                                    </c:if>
                                    <c:if test="${param.error == 'TelefonoVacio'}">
                                        <div class="mensaje-error-perfil">
                                            <i class="fa-solid fa-circle-xmark"></i> Debes ingresar un número de
                                            teléfono.
                                        </div>
                                    </c:if>

                                    <%-- Información básica del usuario --%>
                                        <div class="info-perfil">
                                            <div class="item-info">
                                                <span class="etiqueta-info">ID Usuario:</span>
                                                <span class="valor-info">${usuarioLogueado.idUsuario}</span>
                                            </div>
                                        </div>

                                        <%--=====================================================================SECCIÓN:
                                            CORREOS ELECTRÓNICOS Muestra todos los correos del usuario y permite agregar
                                            nuevos ${listaCorreos} viene de: PerfilServlet →
                                            UsuarioDAO.listarCorreos()=====================================================================--%>
                                            <div class="seccion-contacto">
                                                <h3><i class="fa-solid fa-envelope"></i> Correos Electrónicos</h3>

                                                <%-- Lista de correos existentes --%>
                                                    <ul class="lista-contactos">
                                                        <c:forEach var="correo" items="${listaCorreos}"
                                                            varStatus="status">
                                                            <li>
                                                                <i class="fa-solid fa-at"></i>
                                                                ${correo}
                                                                <%-- El primer correo se marca como "Principal" --%>
                                                                    <c:if test="${status.index == 0}">
                                                                        <span class="badge-principal">Principal</span>
                                                                    </c:if>
                                                            </li>
                                                        </c:forEach>
                                                        <%-- Si no tiene correos --%>
                                                            <c:if test="${empty listaCorreos}">
                                                                <li style="color: #999; font-style: italic;">
                                                                    <i class="fa-solid fa-circle-info"></i> Sin correos
                                                                    registrados
                                                                </li>
                                                            </c:if>
                                                    </ul>

                                                    <%-- Formulario para agregar un nuevo correo --%>
                                                        <form class="form-agregar" action="../PerfilServlet"
                                                            method="POST">
                                                            <input type="hidden" name="action" value="agregarCorreo">
                                                            <input type="email" name="nuevoCorreo"
                                                                placeholder="Nuevo correo electrónico" required
                                                                maxlength="150">
                                                            <button type="submit" class="btn-agregar">
                                                                <i class="fa-solid fa-plus"></i> Agregar
                                                            </button>
                                                        </form>
                                            </div>

                                            <%--=====================================================================SECCIÓN:
                                                TELÉFONOS Muestra todos los teléfonos del usuario y permite agregar
                                                nuevos ${listaTelefonos} viene de: PerfilServlet →
                                                UsuarioDAO.listarTelefonos()=====================================================================--%>
                                                <div class="seccion-contacto">
                                                    <h3><i class="fa-solid fa-phone"></i> Teléfonos</h3>

                                                    <%-- Lista de teléfonos existentes --%>
                                                        <ul class="lista-contactos">
                                                            <c:forEach var="telefono" items="${listaTelefonos}"
                                                                varStatus="status">
                                                                <li>
                                                                    <i class="fa-solid fa-mobile-screen"></i>
                                                                    ${telefono}
                                                                    <c:if test="${status.index == 0}">
                                                                        <span class="badge-principal">Principal</span>
                                                                    </c:if>
                                                                </li>
                                                            </c:forEach>
                                                            <c:if test="${empty listaTelefonos}">
                                                                <li style="color: #999; font-style: italic;">
                                                                    <i class="fa-solid fa-circle-info"></i> Sin
                                                                    teléfonos registrados
                                                                </li>
                                                            </c:if>
                                                        </ul>

                                                        <%-- Formulario para agregar un nuevo teléfono --%>
                                                            <form class="form-agregar" action="../PerfilServlet"
                                                                method="POST">
                                                                <input type="hidden" name="action"
                                                                    value="agregarTelefono">
                                                                <input type="tel" name="nuevoTelefono"
                                                                    placeholder="Nuevo número de teléfono" required
                                                                    minlength="7" maxlength="20" pattern="[0-9]+"
                                                                    title="Solo se permiten números"
                                                                    oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                                                                <button type="submit" class="btn-agregar">
                                                                    <i class="fa-solid fa-plus"></i> Agregar
                                                                </button>
                                                            </form>
                                                </div>

                                                <%-- Estadísticas (solo visible para admins) --%>
                                                    <div class="estadisticas-perfil">
                                                        <div class="tarjeta-estadistica">
                                                            <span class="numero-estadistica">${numBares}</span>
                                                            <span class="etiqueta-estadistica">Bares</span>
                                                        </div>
                                                    </div>

                                                    <a href="../view/Menu_sistema.jsp" class="btn-regresar">
                                                        <i class="fa-solid fa-arrow-left"></i> Volver al Menu
                                                    </a>
                        </div>
                    </main>

                    <footer>
                    </footer>

                    <script src="../js/validaciones.js"></script>
                </body>

                </html>