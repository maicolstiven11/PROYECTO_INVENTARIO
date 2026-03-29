<%--=====================================================================
    VISTA JSP: lista_informes.jsp - Historial de Informes de un Bar
    
    QUIÉN LA MUESTRA: InformeServlet (GET con idNegocio) → 
    request.getRequestDispatcher("view/lista_informes.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (InformeServlet):
    - ${listaInventarios} → List<Inventario>. Viene de: InventarioDAO.listarInventariosPorNegocio()
    - ${nombreNegocio} → String. Nombre del bar actual
    
    Cada Inventario tiene: idInventario, tipoControl, fechaInicio, estado
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Ver informe: GET → InformeServlet?idInventario=${inv.idInventario}
    - Ver descuadre: GET → InformeServlet?idInventario=${inv.idInventario}&action=ver_descuadre
    - Regresar: GET → NegocioServlet (lista de bares)
    
    IMPORTANCIA:
    - Permite acceder a informes históricos de un bar específico
    - Muestra inventarios activos y cerrados
    - Facilita el análisis de períodos específicos
    =====================================================================--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%-- Librería JSTL Core: Permite usar <c:forEach>, <c:if> para lógica en el JSP --%>
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
            <%-- Título dinámico que incluye el nombre del negocio --%>
            <title>Historial de Informes - ${nombreNegocio}</title>
            <%-- Hoja de estilos CSS reutilizada de lista_bares --%>
            <link rel="stylesheet" href="../css/lista_bares.css">
            <%-- Librería Font Awesome: Iconos (fa-arrow-left, fa-scale-balanced) --%>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            <%-- Estilos CSS adicionales para las badges de estado --%>
            <style>
                <%-- Estilo base para las badges de estado --%>
                .badge {
                    padding: 5px 10px;
                    border-radius: 15px;
                    font-size: 12px;
                    font-weight: bold;
                }

                <%-- Badge verde para inventarios activos --%>
                .badge-activo {
                    background-color: #27ae60;
                    color: white;
                }

                <%-- Badge gris para inventarios cerrados/inactivos --%>
                .badge-finalizado {
                    background-color: #7f8c8d;
                    color: white;
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
                <%-- Título principal de la vista --%>
                <h2>HISTORIAL DE INFORMES</h2>
                <%-- Subtítulo que explica al usuario qué hacer --%>
                <h3 style="text-align: center; color: #666;">Seleccione un periodo para ver estadísticas</h3>

                <%-- Sección que contiene la lista de inventarios/informes --%>
                <section class="lista-bares">
                    <%-- Contenedor con scroll para la lista de informes --%>
                    <div class="contenido-scroll">
                        <%-- Itera sobre la lista de inventarios que viene del servlet --%>
                        <c:forEach var="inv" items="${listaInventarios}">
                            <%-- Elemento individual para cada inventario/informe --%>
                            <div class="elemento-bar">
                                <%-- Sección de información del inventario --%>
                                <div class="info-bar">
                                    <%-- Nombre del inventario con ID y tipo de control --%>
                                    <span class="nombre">Inventario #${inv.idInventario} - ${inv.tipoControl}</span>
                                    <%-- Fecha de inicio del inventario --%>
                                    <span class="estado">Inicio: ${inv.fechaInicio}</span>
                                    <%-- Badge dinámica según estado (activo/inactivo) --%>
                                    <span class="badge ${inv.estado == 'activo' ? 'badge-activo' : 'badge-finalizado'}">
                                        <%-- Muestra "ACTIVO" o "CERRADO / INACTIVO" según el estado --%>
                                        ${inv.estado == 'activo' ? 'ACTIVO' : 'CERRADO / INACTIVO'}
                                    </span>
                                </div>
                                <%-- Sección de acciones disponibles para el inventario --%>
                                <div class="acciones" style="display: flex; gap: 15px;">
                                    <%-- Botón para ver el informe completo del inventario --%>
                                    <a href="../InformeServlet?idInventario=${inv.idInventario}"
                                        class="iniciar-invantario" style="text-align: center;">
                                        <h3>Ver Informe</h3>
                                        <%-- Icono de visualización --%>
                                        <img src="../assets/img/icono_visualizar_bar.png" alt="icono_ver"
                                            style="margin: 0 auto;">
                                    </a>

                                    <%-- Botón para ver descuadre (solo si el inventario está inactivo/cerrado) --%>
                                    <c:if test="${inv.estado == 'inactivo'}">
                                        <a href="../InformeServlet?idInventario=${inv.idInventario}&action=ver_descuadre"
                                            class="iniciar-invantario"
                                            style="text-align: center; border-left: 2px solid #ddd; padding-left: 15px;">
                                            <h3>Ver Descuadre</h3>
                                            <%-- Icono de balanza para descuadre --%>
                                            <i class="fa-solid fa-scale-balanced"
                                                style="font-size: 32px; color: #ff9800; display: block; margin: 10px auto 0;"></i>
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>

                        <%-- Mensaje si no hay informes registrados --%>
                        <c:if test="${empty listaInventarios}">
                            <div class="elemento-bar" style="text-align: center; padding: 40px;">
                                <p>No hay informes registrados para este bar.</p>
                            </div>
                        </c:if>
                    </div>
                </section>

                <%-- Botón para regresar a la lista de bares --%>
                <a href="../NegocioServlet" class="btn-regresar">
                    <i class="fa-solid fa-arrow-left"></i> Regresar a Mis Bares
                </a>
            </main>
        </body>

        <%-- Cierre del documento HTML --%>
        </html>