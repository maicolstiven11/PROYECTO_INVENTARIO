<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Gestión de Trabajadores</title>
            <link rel="stylesheet" href="../css/lista_bares.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <style>
                .tabla-trabajadores {
                    width: 90%;
                    margin: 20px auto;
                    border-collapse: collapse;
                }

                .tabla-trabajadores th,
                .tabla-trabajadores td {
                    padding: 12px 15px;
                    text-align: left;
                    border-bottom: 1px solid #ddd;
                }

                .tabla-trabajadores th {
                    background-color: #333;
                    color: white;
                }

                .tabla-trabajadores tr:hover {
                    background-color: #f5f5f5;
                }

                .form-asignar {
                    display: flex;
                    gap: 8px;
                    align-items: center;
                }

                .form-asignar select {
                    padding: 6px 10px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                }

                .btn-asignar {
                    padding: 6px 14px;
                    background-color: #333;
                    color: white;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                }

                .btn-asignar:hover {
                    background-color: #555;
                }

                .badge-asignado {
                    background-color: #27ae60;
                    color: white;
                    padding: 4px 10px;
                    border-radius: 12px;
                    font-size: 12px;
                }

                .badge-sin {
                    background-color: #e74c3c;
                    color: white;
                    padding: 4px 10px;
                    border-radius: 12px;
                    font-size: 12px;
                }

                .alerta-exito {
                    background-color: #d4edda;
                    color: #155724;
                    padding: 12px;
                    border-radius: 5px;
                    text-align: center;
                    margin: 10px auto;
                    width: 80%;
                }

                .alerta-error {
                    background-color: #f8d7da;
                    color: #721c24;
                    padding: 12px;
                    border-radius: 5px;
                    text-align: center;
                    margin: 10px auto;
                    width: 80%;
                }

                /* Modal para resetear contraseña */
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

                .modal-overlay.activo {
                    display: flex;
                }

                .modal-box {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    width: 380px;
                    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
                    text-align: center;
                }

                .modal-box h3 {
                    margin-bottom: 15px;
                    color: #333;
                }

                .modal-box input {
                    width: 90%;
                    padding: 10px;
                    margin: 8px 0;
                    border: 1px solid #ccc;
                    border-radius: 5px;
                    font-size: 14px;
                }

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

        <body>
            <header>
                <nav class="navbar">
                    <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                </nav>
            </header>

            <main>
                <h2 style="text-align: center; margin-top: 30px;">
                    <i class="fa-solid fa-users-gear"></i> Gestión de Trabajadores
                </h2>

                <c:if test="${param.msg == 'asignado'}">
                    <div class="alerta-exito">
                        <i class="fa-solid fa-check-circle"></i> Bar asignado al trabajador exitosamente.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'desasignado'}">
                    <div class="alerta-exito">
                        <i class="fa-solid fa-check-circle"></i> Se le quitó la asignación al trabajador.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'eliminado'}">
                    <div class="alerta-exito" style="background-color: #f8d7da; color: #721c24;">
                        <i class="fa-solid fa-trash-can"></i> Trabajador eliminado del sistema permanentemente.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'password_reseteada'}">
                    <div class="alerta-exito">
                        <i class="fa-solid fa-key"></i> Contraseña del trabajador reseteada exitosamente.
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alerta-error">
                        <i class="fa-solid fa-times-circle"></i>
                        <c:choose>
                            <c:when test="${param.error == 'bar_ocupado'}">
                                ERROR: Este bar ya tiene un trabajador asignado. Debes quitarle la asignación al otro
                                trabajador primero.
                            </c:when>
                            <c:when test="${param.error == 'sin_inventario'}">
                                ERROR: Este bar NO tiene un inventario activo. No puedes asignar trabajadores a un bar
                                cerrado o sin inventario.
                            </c:when>
                            <c:when test="${param.error == 'password_corta'}">
                                ERROR: La contraseña debe tener al menos 6 caracteres.
                            </c:when>
                            <c:when test="${param.error == 'password_no_coincide'}">
                                ERROR: Las contraseñas no coinciden. Inténtalo de nuevo.
                            </c:when>
                            <c:when test="${param.error == 'fallo_reset'}">
                                ERROR: No se pudo resetear la contraseña. Inténtalo de nuevo.
                            </c:when>
                            <c:otherwise>
                                Ocurrió un error en la operación (${param.error}).
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>

                <table class="tabla-trabajadores">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Bar Asignado</th>
                            <th>Asignar / Cambiar</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="trab" items="${listaTrabajadores}">
                            <tr>
                                <td>${trab.idUsuario}</td>
                                <td><i class="fa-solid fa-user"></i> ${trab.nombre}</td>
                                <td>${trab.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${trab.telefono == 'Sin asignar'}">
                                            <span class="badge-sin">Sin asignar</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-asignado">${trab.telefono}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form action="../TrabajadorServlet" method="POST" class="form-asignar"
                                        style="display:inline-block;">
                                        <input type="hidden" name="action" value="asignar">
                                        <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                        <select name="id_negocio" required>
                                            <option value="" disabled selected>Elegir bar...</option>
                                            <c:forEach var="neg" items="${listaNegocios}">
                                                <option value="${neg.idNegocio}">${neg.nombre}</option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit" class="btn-asignar" title="Asignar/Cambiar Bar">
                                            <i class="fa-solid fa-link"></i>
                                        </button>
                                    </form>

                                    <c:if test="${trab.telefono != 'Sin asignar'}">
                                        <form action="../TrabajadorServlet" method="POST"
                                            style="display:inline-block; margin-left: 5px;">
                                            <input type="hidden" name="action" value="desasignar">
                                            <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                            <button type="submit" class="btn-asignar" style="background-color: #e67e22;"
                                                title="Quitar asignación"
                                                onclick="confirmarDesasignar(event, this.form);">
                                                <i class="fa-solid fa-unlink"></i>
                                            </button>
                                        </form>
                                    </c:if>

                                    <form action="../TrabajadorServlet" method="POST"
                                        style="display:inline-block; margin-left: 5px;">
                                        <input type="hidden" name="action" value="eliminar">
                                        <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                        <button type="submit" class="btn-asignar" style="background-color: #e74c3c;"
                                            title="Eliminar cuenta de trabajador"
                                            onclick="confirmarEliminarTrabajador(event, this.form);">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>

                                    <!-- Botón Resetear Contraseña -->
                                    <button type="button" class="btn-asignar"
                                        style="background-color: #8e44ad; margin-left: 5px;" title="Resetear contraseña"
                                        onclick="abrirModalReset('${trab.idUsuario}', '${trab.nombre}')">
                                        <i class="fa-solid fa-key"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty listaTrabajadores}">
                            <tr>
                                <td colspan="5" style="text-align: center; padding: 30px; color: #999;">
                                    No hay trabajadores registrados en el sistema.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <div style="text-align: center; margin-top: 20px;">
                    <a href="../view/Menu_sistema.jsp" class="btn-regresar">
                        <i class="fa-solid fa-arrow-left"></i> Regresar al Menú
                    </a>
                </div>
            </main>

            <!-- Modal para resetear contraseña -->
            <div class="modal-overlay" id="modalReset">
                <div class="modal-box">
                    <h3><i class="fa-solid fa-key"></i> Resetear Contraseña</h3>
                    <p id="modalNombreTrab" style="color:#666; margin-bottom:10px;"></p>
                    <form action="../TrabajadorServlet" method="POST">
                        <input type="hidden" name="action" value="resetPassword">
                        <input type="hidden" name="id_usuario" id="modalIdUsuario">
                        <input type="password" name="nueva_password" placeholder="Nueva contraseña" required
                            minlength="6" maxlength="50">
                        <input type="password" name="confirmar_password" placeholder="Confirmar contraseña" required
                            minlength="6" maxlength="50">
                        <br>
                        <button type="submit" class="btn-confirmar"><i class="fa-solid fa-check"></i> Cambiar</button>
                        <button type="button" class="btn-cancelar" onclick="cerrarModalReset()"><i
                                class="fa-solid fa-xmark"></i> Cancelar</button>
                    </form>
                </div>
            </div>

            <script>
                function abrirModalReset(idUsuario, nombre) {
                    document.getElementById('modalIdUsuario').value = idUsuario;
                    document.getElementById('modalNombreTrab').textContent = 'Trabajador: ' + nombre;
                    document.getElementById('modalReset').classList.add('activo');
                }
                function cerrarModalReset() {
                    document.getElementById('modalReset').classList.remove('activo');
                }
                // Cerrar modal al hacer clic fuera
                document.getElementById('modalReset').addEventListener('click', function (e) {
                    if (e.target === this) cerrarModalReset();
                });
            </script>
            <script src="../js/validaciones.js"></script>
            <script>
                function confirmarDesasignar(e, form) {
                    e.preventDefault();
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
                        if (result.isConfirmed) {
                            form.submit();
                        }
                    });
                }

                function confirmarEliminarTrabajador(e, form) {
                    e.preventDefault();
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
                        if (result.isConfirmed) {
                            form.submit();
                        }
                    });
                }
            </script>
        </body>

        </html>