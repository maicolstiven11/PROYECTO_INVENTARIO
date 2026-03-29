<%--=====================================================================
    VISTA JSP: gestion_trabajadores.jsp - Gestión de Trabajadores del Sistema
    
    QUIÉN LA MUESTRA: TrabajadorServlet (GET con action=listar) → 
    request.getRequestDispatcher("view/gestion_trabajadores.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (TrabajadorServlet):
    - ${listaTrabajadores} → List<Usuario>. Viene de: UsuarioDAO.listarTrabajadores()
    - ${listaNegocios} → List<Negocio>. Viene de: NegocioDAO.listarNegociosPorAdmin()
    
    Cada Trabajador tiene: idUsuario, nombre, email, telefono (usado para nombre del bar asignado)
    
    Parámetros URL:
    - ${param.msg} → String. Mensajes de éxito (asignado, desasignado, eliminado, password_reseteada)
    - ${param.error} → String. Mensajes de error (bar_ocupado, sin_inventario, password_corta, etc.)
    
    DATOS QUE USA DE LA SESIÓN:
    - ${sessionScope.usuarioLogueado} → Administrador autenticado
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Asignar bar: POST → TrabajadorServlet?action=asignar
    - Desasignar bar: POST → TrabajadorServlet?action=desasignar
    - Eliminar trabajador: POST → TrabajadorServlet?action=eliminar
    - Resetear contraseña: POST → TrabajadorServlet?action=resetPassword
    - Regresar al menú: GET → Menu_sistema.jsp
    
    IMPORTANCIA:
    - Permite al administrador gestionar los trabajadores del sistema
    - Facilita la asignación de bares a trabajadores
    - Proporciona control de acceso y seguridad de cuentas
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if>, <c:choose>, <c:when>, <c:otherwise> --%>
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
            <title>Gestión de Trabajadores</title>
            <%-- Reutilizamos estilos de lista de bares --%>
            <link rel="stylesheet" href="../css/lista_bares.css">
            <%-- Librería Font Awesome: Iconos varios --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Librería SweetAlert2: Para confirmaciones de acciones --%>
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <%-- Estilos CSS específicos para la gestión de trabajadores --%>
            <style>
                <%-- Estilo para la tabla principal de trabajadores --%>
                .tabla-trabajadores {
                    width: 90%;
                    margin: 20px auto;
                    border-collapse: collapse;
                }

                <%-- Estilo para celdas de la tabla --%>
                .tabla-trabajadores th,
                .tabla-trabajadores td {
                    padding: 12px 15px;
                    text-align: left;
                    border-bottom: 1px solid #ddd;
                }

                <%-- Estilo para encabezados de la tabla --%>
                .tabla-trabajadores th {
                    background-color: #333;
                    color: white;
                }

                <%-- Efecto hover para filas de la tabla --%>
                .tabla-trabajadores tr:hover {
                    background-color: #f5f5f5;
                }

                <%-- Estilo para el formulario de asignar --%>
                .form-asignar {
                    display: flex;
                    gap: 8px;
                    align-items: center;
                }

                <%-- Estilo para los selects del formulario --%>
                .form-asignar select {
                    padding: 6px 10px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                }

                <%-- Estilo para los botones de asignar --%>
                .btn-asignar {
                    padding: 6px 14px;
                    background-color: #333;
                    color: white;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                }

                <%-- Efecto hover para los botones --%>
                .btn-asignar:hover {
                    background-color: #555;
                }

                <%-- Badge para trabajadores asignados --%>
                .badge-asignado {
                    background-color: #27ae60;
                    color: white;
                    padding: 4px 10px;
                    border-radius: 12px;
                    font-size: 12px;
                }

                <%-- Badge para trabajadores sin asignar --%>
                .badge-sin {
                    background-color: #e74c3c;
                    color: white;
                    padding: 4px 10px;
                    border-radius: 12px;
                    font-size: 12px;
                }

                <%-- Estilo para alertas de éxito --%>
                .alerta-exito {
                    background-color: #d4edda;
                    color: #155724;
                    padding: 12px;
                    border-radius: 5px;
                    text-align: center;
                    margin: 10px auto;
                    width: 80%;
                }

                <%-- Estilo para alertas de error --%>
                .alerta-error {
                    background-color: #f8d7da;
                    color: #721c24;
                    padding: 12px;
                    border-radius: 5px;
                    text-align: center;
                    margin: 10px auto;
                    width: 80%;
                }

                <%-- Modal para resetear contraseña --%>
                .modal-overlay {
                    display: none;
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(0, 0, 0, 0.5);
                    z-index: 999;
                    justify-content: center;
                    align-items: center;
                }

                <%-- Clase para activar el modal --%>
                .modal-overlay.activo {
                    display: flex;
                }

                <%-- Caja del modal --%>
                .modal-box {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    width: 380px;
                    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
                    text-align: center;
                }

                <%-- Título del modal --%>
                .modal-box h3 {
                    margin-bottom: 15px;
                    color: #333;
                }

                <%-- Campos de entrada del modal --%>
                .modal-box input {
                    width: 90%;
                    padding: 10px;
                    margin: 8px 0;
                    border: 1px solid #ccc;
                    border-radius: 5px;
                    font-size: 14px;
                }

                <%-- Botón de confirmar del modal --%>
                .modal-box .btn-confirmar {
                    padding: 10px 24px;
                    background-color: #27ae60;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-top: 10px;
                    font-size: 14px;
                }

                <%-- Botón de cancelar del modal --%>
                .modal-box .btn-cancelar {
                    padding: 10px 24px;
                    background-color: #999;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-top: 10px;
                    margin-left: 8px;
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
                <%-- Título principal de la página --%>
                <h2 style="text-align: center; margin-top: 30px;">
                    <%-- Icono de gestión y título --%>
                    <i class="fa-solid fa-users-gear"></i> Gestión de Trabajadores
                </h2>

                <%-- Mensajes de éxito según la operación realizada --%>
                <c:if test="${param.msg == 'asignado'}">
                    <div class="alerta-exito">
                        <%-- Icono de check y mensaje de éxito --%>
                        <i class="fa-solid fa-check-circle"></i> Bar asignado al trabajador exitosamente.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'desasignado'}">
                    <div class="alerta-exito">
                        <%-- Icono de check y mensaje de éxito --%>
                        <i class="fa-solid fa-check-circle"></i> Se le quitó la asignación al trabajador.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'eliminado'}">
                    <div class="alerta-exito" style="background-color: #f8d7da; color: #721c24;">
                        <%-- Icono de papelera y mensaje de eliminación --%>
                        <i class="fa-solid fa-trash-can"></i> Trabajador eliminado del sistema permanentemente.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'password_reseteada'}">
                    <div class="alerta-exito">
                        <%-- Icono de llave y mensaje de éxito --%>
                        <i class="fa-solid fa-key"></i> Contraseña del trabajador reseteada exitosamente.
                    </div>
                </c:if>
                <%-- Mensajes de error según el tipo de error --%>
                <c:if test="${not empty param.error}">
                    <div class="alerta-error">
                        <%-- Icono de error y mensaje --%>
                        <i class="fa-solid fa-times-circle"></i>
                        <%-- Muestra el mensaje específico según el tipo de error --%>
                        <c:choose>
                            <%-- Error de bar ya ocupado --%>
                            <c:when test="${param.error == 'bar_ocupado'}">
                                ERROR: Este bar ya tiene un trabajador asignado. Debes quitarle la asignación al otro
                                trabajador primero.
                            </c:when>
                            <%-- Error de bar sin inventario --%>
                            <c:when test="${param.error == 'sin_inventario'}">
                                ERROR: Este bar NO tiene un inventario activo. No puedes asignar trabajadores a un bar
                                cerrado o sin inventario.
                            </c:when>
                            <%-- Error de contraseña corta --%>
                            <c:when test="${param.error == 'password_corta'}">
                                ERROR: La contraseña debe tener al menos 6 caracteres.
                            </c:when>
                            <%-- Error de contraseñas no coinciden --%>
                            <c:when test="${param.error == 'password_no_coincide'}">
                                ERROR: Las contraseñas no coinciden. Inténtalo de nuevo.
                            </c:when>
                            <%-- Error al resetear contraseña --%>
                            <c:when test="${param.error == 'fallo_reset'}">
                                ERROR: No se pudo resetear la contraseña. Inténtalo de nuevo.
                            </c:when>
                            <%-- Error genérico --%>
                            <c:otherwise>
                                Ocurrió un error en la operación (${param.error}).
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>

                <%-- Tabla principal con la lista de trabajadores --%>
                <table class="tabla-trabajadores">
                    <%-- Encabezado de la tabla --%>
                    <thead>
                        <tr>
                            <%-- Columnas de la tabla --%>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Bar Asignado</th>
                            <th>Asignar / Cambiar</th>
                        </tr>
                    </thead>
                    <%-- Cuerpo de la tabla con los datos --%>
                    <tbody>
                        <%-- Itera sobre la lista de trabajadores --%>
                        <c:forEach var="trab" items="${listaTrabajadores}">
                            <%-- Fila individual para cada trabajador --%>
                            <tr>
                                <%-- ID del trabajador --%>
                                <td>${trab.idUsuario}</td>
                                <%-- Nombre del trabajador con icono --%>
                                <td><i class="fa-solid fa-user"></i> ${trab.nombre}</td>
                                <%-- Email del trabajador --%>
                                <td>${trab.email}</td>
                                <%-- Bar asignado al trabajador --%>
                                <td>
                                    <%-- Muestra si tiene bar asignado o no --%>
                                    <c:choose>
                                        <%-- Si no tiene bar asignado --%>
                                        <c:when test="${trab.telefono == 'Sin asignar'}">
                                            <span class="badge-sin">Sin asignar</span>
                                        </c:when>
                                        <%-- Si tiene bar asignado --%>
                                        <c:otherwise>
                                            <span class="badge-asignado">${trab.telefono}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <%-- Columna de acciones --%>
                                <td>
                                    <%-- Formulario para asignar/cambiar bar --%>
                                    <form action="../TrabajadorServlet" method="POST" class="form-asignar"
                                        style="display:inline-block;">
                                        <%-- Campo oculto con la acción --%>
                                        <input type="hidden" name="action" value="asignar">
                                        <%-- Campo oculto con el ID del usuario --%>
                                        <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                        <%-- Select para elegir el bar --%>
                                        <select name="id_negocio" required>
                                            <option value="" disabled selected>Elegir bar...</option>
                                            <%-- Itera sobre la lista de bares disponibles --%>
                                            <c:forEach var="neg" items="${listaNegocios}">
                                                <option value="${neg.idNegocio}">${neg.nombre}</option>
                                            </c:forEach>
                                        </select>
                                        <%-- Botón para asignar --%>
                                        <button type="submit" class="btn-asignar" title="Asignar/Cambiar Bar">
                                            <%-- Icono de enlace --%>
                                            <i class="fa-solid fa-link"></i>
                                        </button>
                                    </form>

                                    <%-- Botón para desasignar (solo si tiene bar asignado) --%>
                                    <c:if test="${trab.telefono != 'Sin asignar'}">
                                        <form action="../TrabajadorServlet" method="POST"
                                            style="display:inline-block; margin-left: 5px;">
                                            <%-- Campo oculto con la acción de desasignar --%>
                                            <input type="hidden" name="action" value="desasignar">
                                            <%-- Campo oculto con el ID del usuario --%>
                                            <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                            <%-- Botón para quitar asignación --%>
                                            <button type="submit" class="btn-asignar" style="background-color: #e67e22;"
                                                title="Quitar asignación"
                                                onclick="confirmarDesasignar(event, this.form);">
                                                <%-- Icono de desenlace --%>
                                                <i class="fa-solid fa-unlink"></i>
                                            </button>
                                        </form>
                                    </c:if>

                                    <%-- Botón para eliminar trabajador --%>
                                    <form action="../TrabajadorServlet" method="POST"
                                        style="display:inline-block; margin-left: 5px;">
                                        <%-- Campo oculto con la acción de eliminar --%>
                                        <input type="hidden" name="action" value="eliminar">
                                        <%-- Campo oculto con el ID del usuario --%>
                                        <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                        <%-- Botón para eliminar --%>
                                        <button type="submit" class="btn-asignar" style="background-color: #e74c3c;"
                                            title="Eliminar cuenta de trabajador"
                                            onclick="confirmarEliminarTrabajador(event, this.form);">
                                            <%-- Icono de papelera --%>
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>

                                    <%-- Botón para resetear contraseña --%>
                                    <button type="button" class="btn-asignar"
                                        style="background-color: #8e44ad; margin-left: 5px;" title="Resetear contraseña"
                                        onclick="abrirModalReset('${trab.idUsuario}', '${trab.nombre}')">
                                        <%-- Icono de llave --%>
                                        <i class="fa-solid fa-key"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>

                        <%-- Mensaje si no hay trabajadores registrados --%>
                        <c:if test="${empty listaTrabajadores}">
                            <tr>
                                <%-- Mensaje centrado ocupando todas las columnas --%>
                                <td colspan="5" style="text-align: center; padding: 30px; color: #999;">
                                    No hay trabajadores registrados en el sistema.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <%-- Botón para regresar al menú principal --%>
                <div style="text-align: center; margin-top: 20px;">
                    <a href="../view/Menu_sistema.jsp" class="btn-regresar">
                        <%-- Icono de flecha y texto --%>
                        <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                    </a>
                </div>
            </main>

            <%-- Modal para resetear contraseña --%>
            <div class="modal-overlay" id="modalReset">
                <div class="modal-box">
                    <%-- Título del modal --%>
                    <h3><i class="fa-solid fa-key"></i> Resetear Contraseña</h3>
                    <%-- Nombre del trabajador --%>
                    <p id="modalNombreTrab" style="color:#666; margin-bottom:10px;"></p>
                    <%-- Formulario para resetear contraseña --%>
                    <form action="../TrabajadorServlet" method="POST">
                        <%-- Campo oculto con la acción --%>
                        <input type="hidden" name="action" value="resetPassword">
                        <%-- Campo oculto con el ID del usuario --%>
                        <input type="hidden" name="id_usuario" id="modalIdUsuario">
                        <%-- Campo para nueva contraseña --%>
                        <input type="password" name="nueva_password" placeholder="Nueva contraseña" required
                            minlength="6" maxlength="50">
                        <%-- Campo para confirmar contraseña --%>
                        <input type="password" name="confirmar_password" placeholder="Confirmar contraseña" required
                            minlength="6" maxlength="50">
                        <br>
                        <%-- Botón para confirmar --%>
                        <button type="submit" class="btn-confirmar"><i class="fa-solid fa-check"></i> Cambiar</button>
                        <%-- Botón para cancelar --%>
                        <button type="button" class="btn-cancelar" onclick="cerrarModalReset()"><i
                                class="fa-solid fa-xmark"></i> Cancelar</button>
                    </form>
                </div>
            </div>

            <%-- Scripts para la funcionalidad del modal y confirmaciones --%>
            <script>
                // Función para abrir el modal de resetear contraseña
                function abrirModalReset(idUsuario, nombre) {
                    // Establece el ID del usuario en el campo oculto
                    document.getElementById('modalIdUsuario').value = idUsuario;
                    // Establece el nombre del trabajador en el modal
                    document.getElementById('modalNombreTrab').textContent = 'Trabajador: ' + nombre;
                    // Activa el modal
                    document.getElementById('modalReset').classList.add('activo');
                }
                // Función para cerrar el modal
                function cerrarModalReset() {
                    // Desactiva el modal
                    document.getElementById('modalReset').classList.remove('activo');
                }
                // Cerrar modal al hacer clic fuera
                document.getElementById('modalReset').addEventListener('click', function (e) {
                    // Si se hace clic en el overlay, cierra el modal
                    if (e.target === this) cerrarModalReset();
                });
            </script>
            <%-- Script de validaciones externo --%>
            <script src="../js/validaciones.js"></script>
            <%-- Scripts para confirmaciones con SweetAlert2 --%>
            <script>
                // Función para confirmar desasignación
                function confirmarDesasignar(e, form) {
                    // Previene el envío del formulario
                    e.preventDefault();
                    // Muestra alerta de confirmación
                    Swal.fire({
                        title: 'Quitar Asignación',
                        text: '¿Seguro que desea quitarle el bar asignado a este trabajador?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#e67e22',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Sí, desasignar',
                        cancelButtonText: 'Cancelar'
                    }).then((result) => {
                        // Si el usuario confirma, envía el formulario
                        if (result.isConfirmed) {
                            form.submit();
                        }
                    });
                }

                // Función para confirmar eliminación de trabajador
                function confirmarEliminarTrabajador(e, form) {
                    // Previene el envío del formulario
                    e.preventDefault();
                    // Muestra alerta de confirmación con advertencia
                    Swal.fire({
                        title: 'Eliminar Trabajador',
                        text: '¡ADVERTENCIA! ¿Se borrará permanentemente la cuenta de este trabajador?',
                        icon: 'error',
                        showCancelButton: true,
                        confirmButtonColor: '#e74c3c',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Sí, eliminar',
                        cancelButtonText: 'Cancelar'
                    }).then((result) => {
                        // Si el usuario confirma, envía el formulario
                        if (result.isConfirmed) {
                            form.submit();
                        }
                    });
                }
            </script>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>