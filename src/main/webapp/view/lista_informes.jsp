<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Historial de Informes - ${nombreNegocio}</title>
            <link rel="stylesheet" href="../css/lista_bares.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <style>
                .badge {
                    padding: 5px 10px;
                    border-radius: 15px;
                    font-size: 12px;
                    font-weight: bold;
                }

                .badge-activo {
                    background-color: #27ae60;
                    color: white;
                }

                .badge-finalizado {
                    background-color: #7f8c8d;
                    color: white;
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
                <h2>HISTORIAL DE INFORMES</h2>
                <h3 style="text-align: center; color: #666;">Seleccione un periodo para ver estadísticas</h3>

                <section class="lista-bares">
                    <div class="contenido-scroll">
                        <c:forEach var="inv" items="${listaInventarios}">
                            <div class="elemento-bar">
                                <div class="info-bar">
                                    <span class="nombre">Inventario #${inv.idInventario} - ${inv.tipoControl}</span>
                                    <span class="estado">Inicio: ${inv.fechaInicio}</span>
                                    <span class="badge ${inv.estado == 'activo' ? 'badge-activo' : 'badge-finalizado'}">
                                        ${inv.estado}
                                    </span>
                                </div>
                                <div class="acciones">
                                    <a href="../InformeServlet?idInventario=${inv.idInventario}"
                                        class="iniciar-invantario">
                                        <h3>Ver Informe</h3>
                                        <img src="../assets/img/icono_visualizar_bar.png" alt="icono_ver">
                                    </a>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty listaInventarios}">
                            <div class="elemento-bar" style="text-align: center; padding: 40px;">
                                <p>No hay informes registrados para este bar.</p>
                            </div>
                        </c:if>
                    </div>
                </section>

                <a href="../NegocioServlet" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar a Mis Bares
                </a>
            </main>
        </body>

        </html>