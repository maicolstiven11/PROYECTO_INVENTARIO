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

                <c:if test="${not empty param.msg}">
                    <div class="alerta-exito">
                        <i class="fa-solid fa-check-circle"></i> Trabajador asignado exitosamente.
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alerta-error">
                        <i class="fa-solid fa-times-circle"></i> Error al asignar trabajador.
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
                                    <form action="../TrabajadorServlet" method="POST" class="form-asignar">
                                        <input type="hidden" name="action" value="asignar">
                                        <input type="hidden" name="id_usuario" value="${trab.idUsuario}">
                                        <select name="id_negocio" required>
                                            <option value="" disabled selected>Elegir bar...</option>
                                            <c:forEach var="neg" items="${listaNegocios}">
                                                <option value="${neg.idNegocio}">${neg.nombre}</option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit" class="btn-asignar">
                                            <i class="fa-solid fa-link"></i> Asignar
                                        </button>
                                    </form>
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
        </body>

        </html>