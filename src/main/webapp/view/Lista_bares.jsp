<%--=====================================================================VISTA JSP: Lista_bares.jsp - Ver Bares del
    Administrador QUIÉN LA MUESTRA: NegocioServlet.doGet() → Sin parámetro action →
    request.getRequestDispatcher("view/Lista_bares.jsp").forward(...) DATOS QUE RECIBE DEL CONTROLADOR (NegocioServlet):
    - ${listaBares} → List<Negocio>. Viene de: NegocioDAO.listarNegocios(idUsuario)
    Cada objeto Negocio tiene:
    idNegocio → PK del bar. Usado para armar URLs (eliminar, entrar, informes)
    nombre → Nombre del bar. Mostrado en la tarjeta
    estado → 'activo' o 'inactivo'. Mostrado junto al nombre
    tieneInventarioActivo → boolean calculado. Decide si mostrar "Ver Inventario" o "Iniciar Inventario"

    DATOS DE LA URL (parámetros):
    - ${param.status} → 'InventarioCerradoExito': Viene de InventarioServlet al cerrar inventario exitosamente
    - ${param.error} → Mensaje de error genérico

    DATOS DE LA SESIÓN:
    - ${sessionScope.usuarioLogueado.idRol} → Para mostrar "Registrar mi primer bar" solo a admins

    ACCIONES QUE ENVÍA AL CONTROLADOR:
    - Eliminar bar: GET → NegocioServlet?action=eliminar&id=${bar.idNegocio}
    - Entrar a inventario: GET → InventarioServlet?action=entrar&idNegocio=${bar.idNegocio}
    - Iniciar inventario: Navega a → Inicio_inv.html?idNegocio=${bar.idNegocio}&nombreBar=${bar.nombre}
    - Ver informes: GET → InformeServlet?idNegocio=${bar.idNegocio}
    ===================================================================== --%>
    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
        <%-- JSTL Core para lógica condicional y bucles --%>
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
                            <%-- Viene de: InventarioServlet redirige con ?status=InventarioCerradoExito --%>
                                <c:if test="${param.status == 'InventarioCerradoExito'}">
                                    <div
                                        style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 8px; 
                                text-align: center; margin: 10px auto; width: 80%; font-weight: bold; border: 1px solid #c3e6cb;">
                                        <i class="fa-solid fa-circle-check"></i>
                                        ¡Inventario cerrado con éxito! El negocio ahora está inactivo. Puede crear un
                                        nuevo inventario
                                        cuando lo desee.
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

                                            <%--=====================================================================BUCLE:
                                                Recorre cada bar de la lista ${listaBares} viene de: NegocioServlet →
                                                NegocioDAO.listarNegocios(idUsuario) Cada ${bar} es un objeto Negocio
                                                del
                                                Modelo=====================================================================--%>
                                                <c:forEach var="bar" items="${listaBares}">
                                                    <div class="elemento-bar">
                                                        <div class="info-bar">
                                                            <%-- ${bar.nombre} → Modelo: Negocio.getNombre() ← BD:
                                                                columna nombre ← DAO: rs.getString("nombre") --%>
                                                                <span class="nombre">${bar.nombre}</span>
                                                                <%-- ${bar.estado} → Modelo: Negocio.getEstado() ← BD:
                                                                    columna estado ('activo'/'inactivo') --%>
                                                                    <span class="estado">(${bar.estado})</span>
                                                        </div>
                                                        <div class="acciones">
                                                            <%-- BOTÓN CONDICIONAL: Depende de si tiene inventario
                                                                activo o no --%>
                                                                <%-- ${bar.tieneInventarioActivo} → Calculado en
                                                                    NegocioDAO con subconsulta COUNT a tabla INVENTARIO
                                                                    --%>
                                                                    <c:choose>
                                                                        <c:when test="${bar.tieneInventarioActivo}">
                                                                            <%-- Si TIENE inventario activo →
                                                                                Botón "Ver Inventario" --%>
                                                                                <%-- Navega a:
                                                                                    InventarioServlet?action=entrar&idNegocio=X
                                                                                    → menu_inventario.jsp --%>
                                                                                    <a href="../InventarioServlet?action=entrar&idNegocio=${bar.idNegocio}"
                                                                                        class="iniciar-invantario">
                                                                                        <h3>Ver Inventario</h3>
                                                                                        <i class="fa-solid fa-box-open"
                                                                                            style="font-size: 40px; color: #27ae60;"></i>
                                                                                    </a>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <%-- Si NO tiene inventario →
                                                                                Botón "Iniciar Inventario" --%>
                                                                                <%-- Navega a: Inicio_inv.html con los
                                                                                    datos del bar en la URL --%>
                                                                                    <a href="view/Inicio_inv.html?idNegocio=${bar.idNegocio}&nombreBar=${bar.nombre}"
                                                                                        class="iniciar-invantario">
                                                                                        <h3>Iniciar-inventario</h3>
                                                                                        <img src="../assets/img/boton_iniciar_inv.png"
                                                                                            alt="iniciar-invantario">
                                                                                    </a>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                    <%-- BOTÓN ELIMINAR BAR --%>
                                                                        <%-- Navega a:
                                                                            NegocioServlet?action=eliminar&id=X →
                                                                            NegocioDAO.eliminarNegocio() --%>
                                                                            <%-- confirm() pide confirmación antes de
                                                                                eliminar --%>
                                                                                <a href="../NegocioServlet?action=eliminar&id=${bar.idNegocio}"
                                                                                    class="borrar-bar"
                                                                                    onclick="confirmarEliminarBar(event, this.href);">
                                                                                    <h3>borrar-bar</h3>
                                                                                    <img src="../assets/img/icono_borrar_bar.png"
                                                                                        alt="borrar-bar">
                                                                                </a>

                                                                                <%-- BOTÓN INFORMES --%>
                                                                                    <%-- Navega a:
                                                                                        InformeServlet?idNegocio=X →
                                                                                        lista_informes.jsp --%>
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
                                                                <%-- Si es Admin: Mostrar enlace para registrar su
                                                                    primer bar --%>
                                                                    <c:when
                                                                        test="${sessionScope.usuarioLogueado.idRol == 1}">
                                                                        <p>No tienes bares registrados aún.</p>
                                                                        <a href="view/registroBar.html"
                                                                            style="color: #27ae60; font-weight: bold;">
                                                                            <i class="fa-solid fa-plus"></i> Registrar
                                                                            mi primer bar
                                                                        </a>
                                                                    </c:when>
                                                                    <%-- Si es Trabajador: Informar que debe contactar
                                                                        al admin --%>
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
                                text: '¿Estás seguro de eliminar este bar?',
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonColor: '#e74c3c',
                                cancelButtonColor: '#3085d6',
                                confirmButtonText: 'Sí, eliminar',
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