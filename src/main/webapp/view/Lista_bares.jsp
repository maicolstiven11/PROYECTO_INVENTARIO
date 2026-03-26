<%--=====================================================================
    VISTA JSP: Lista_bares.jsp - Ver Bares del Administrador
    
    QUIÉN LA MUESTRA: NegocioServlet.doGet() → Sin parámetro action →
    request.getRequestDispatcher("view/Lista_bares.jsp").forward(...)
    
    DATOS QUE RECIBE DEL CONTROLADOR (NegocioServlet):
    - ${listaBares} → List<Negocio>. Viene de: NegocioDAO.listarNegocios(idUsuario)
    
    Cada objeto Negocio tiene:
    idNegocio → PK del bar
    nombre → Nombre del bar
    estado → 'activo' o 'inactivo'
    tieneInventarioActivo → boolean calculado
    
    PARÁMETROS DE URL:
    - ${param.status} → 'InventarioCerradoExito', 'inactivado', 'eliminado'
    - ${param.error} → Mensaje de error genérico
    
    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Eliminar/Inactivar bar: GET → NegocioServlet?action=eliminar&id=${bar.idNegocio}
    - Entrar a inventario: GET → InventarioServlet?action=entrar&idNegocio=${bar.idNegocio}
    - Iniciar inventario: Navega a → Inicio_inv.html?idNegocio=${bar.idNegocio}&nombreBar=${bar.nombre}
    - Ver informes: GET → InformeServlet?idNegocio=${bar.idNegocio}
    =====================================================================--%>
    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                <!DOCTYPE html>
                <html lang="es">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Mis Bares</title>
                    <link rel="stylesheet" href="../css/lista_bares.css">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                </head>

                <body>
                    <header>
                        <nav class="navbar">
                            <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
                        </nav>
                    </header>

                    <main>
                        <h2>MIS BARES REGISTRADOS</h2>

                        <%-- MENSAJE DE ÉXITO: Inventario cerrado correctamente --%>
                        <c:if test="${param.status == 'InventarioCerradoExito'}">
                            <div
                                style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 8px; 
                                text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #c3e6cb;">
                                <i class="fa-solid fa-circle-check"></i>
                                ¡Inventario cerrado con éxito! El negocio ahora está inactivo. Puede crear un
                                nuevo inventario cuando lo desee.
                            </div>
                        </c:if>

                        <%-- MENSAJE: Bar inactivado porque tenía datos --%>
                        <c:if test="${param.status == 'inactivado'}">
                            <div
                                style="background-color: #fff3cd; color: #856404; padding: 15px; border-radius: 8px; 
                                text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #ffeeba;">
                                <i class="fa-solid fa-triangle-exclamation"></i>
                                El bar tiene datos vinculados (ventas, gastos, pedidos, etc.) y no se puede eliminar.
                                Se ha cambiado su estado a <strong>inactivo</strong>. Solo podrá ver los informes de este bar.
                            </div>
                        </c:if>

                        <%-- MENSAJE: Bar eliminado exitosamente --%>
                        <c:if test="${param.status == 'eliminado'}">
                            <div
                                style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 8px; 
                                text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #c3e6cb;">
                                <i class="fa-solid fa-circle-check"></i>
                                El bar fue eliminado exitosamente.
                            </div>
                        </c:if>

                        <%-- MENSAJE DE ERROR genérico --%>
                        <c:if test="${not empty param.error}">
                            <div
                                style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; 
                                text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #f5c6cb;">
                                <i class="fa-solid fa-circle-xmark"></i> Error: ${param.error}
                            </div>
                        </c:if>

                        <section class="lista-bares">
                            <div class="contenido-scroll">

                                <%-- BUCLE: Recorre cada bar de la lista --%>
                                <c:forEach var="bar" items="${listaBares}">
                                    <div class="elemento-bar">
                                        <div class="info-bar">
                                            <span class="nombre">${bar.nombre}</span>
                                            <span class="estado">(${bar.estado})</span>
                                        </div>
                                        <div class="acciones">

                                            <%-- =============================================================== --%>
                                            <%-- LÓGICA: Si el bar está INACTIVO, solo mostrar botón de Informes --%>
                                            <%-- =============================================================== --%>
                                            <c:choose>
                                                <c:when test="${bar.estado == 'inactivo'}">
                                                    <%-- BAR INACTIVO: Solo puede ver informes del pasado. No puede entrar a inventario ni borrarlo de nuevo --%>
                                                    <div class="inactivo-badge" style="color: #e74c3c; font-weight: bold; padding: 10px;">
                                                        <i class="fa-solid fa-lock"></i> Negocio Inactivo
                                                    </div>
                                                </c:when>
                                                
                                                <c:when test="${bar.tieneInventarioActivo}">
                                                    <%-- BAR CON INVENTARIO ACTIVO: Puede entrar al inventario --%>
                                                    <a href="../InventarioServlet?action=entrar&idNegocio=${bar.idNegocio}"
                                                        class="iniciar-invantario">
                                                        <h3>Ver Inventario</h3>
                                                        <i class="fa-solid fa-box-open"
                                                            style="font-size: 40px; color: #27ae60;"></i>
                                                    </a>
                                                </c:when>
                                                
                                                <c:otherwise>
                                                    <%-- BAR ACTIVO SIN INVENTARIO: Puede iniciar uno --%>
                                                    <a href="view/Inicio_inv.html?idNegocio=${bar.idNegocio}&nombreBar=${bar.nombre}"
                                                        class="iniciar-invantario">
                                                        <h3>Iniciar-inventario</h3>
                                                        <img src="../assets/img/boton_iniciar_inv.png"
                                                            alt="iniciar-invantario">
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>

                                            <%-- BOTÓN ELIMINAR/INACTIVAR BAR: Solo visible si está ACTIVO --%>
                                            <c:if test="${bar.estado == 'activo'}">
                                                <a href="../NegocioServlet?action=eliminar&id=${bar.idNegocio}"
                                                    class="borrar-bar"
                                                    onclick="confirmarEliminarBar(event, this.href);">
                                                    <h3>borrar-bar</h3>
                                                    <img src="../assets/img/icono_borrar_bar.png"
                                                        alt="borrar-bar">
                                                </a>
                                            </c:if>

                                            <%-- BOTÓN INFORMES (siempre visible para ver historial) --%>
                                            <a href="../InformeServlet?idNegocio=${bar.idNegocio}"
                                                class="visualizar_bar">
                                                <h3>Informes de bar</h3>
                                                <img src="../assets/img/icono_visualizar_bar.png"
                                                    alt="icono_informes">
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>

                                <%-- MENSAJE SI NO HAY BARES REGISTRADOS --%>
                                <c:if test="${empty listaBares}">
                                    <div class="elemento-bar"
                                        style="text-align: center; padding: 40px;">
                                        <c:choose>
                                            <c:when
                                                test="${sessionScope.usuarioLogueado.idRol == 1}">
                                                <p>No tienes bares registrados aún.</p>
                                                <a href="view/registroBar.html"
                                                    style="color: #27ae60; font-weight: bold;">
                                                    <i class="fa-solid fa-plus"></i> Registrar
                                                    mi primer bar
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <p>Aún no tiene ningún negocio asignado.</p>
                                                <p
                                                    style="color: #666; font-size: 14px; margin-top: 10px;">
                                                    Comuníquese con su
                                                    administrador para que le asigne un bar.
                                                </p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>

                            </div>
                        </section>

                        <%-- Botón regresar al menú principal --%>
                        <a href="view/Menu_sistema.jsp" class="btn-regresar">
                            <i class="fa-solid fa-arrow-left"></i> Regresar
                        </a>
                    </main>

                    <footer></footer>
                    <script>
                        function confirmarEliminarBar(e, url) {
                            e.preventDefault();
                            Swal.fire({
                                title: 'Eliminar Bar',
                                text: 'Si el bar tiene datos vinculados (ventas, gastos, pedidos, etc.) NO se eliminará, sino que se inactivará. ¿Desea continuar?',
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonColor: '#e74c3c',
                                cancelButtonColor: '#3085d6',
                                confirmButtonText: 'Sí, continuar',
                                cancelButtonText: 'Cancelar'
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    window.location.href = url;
                                }
                            });
                        }
                    </script>
                </body>

                </html>