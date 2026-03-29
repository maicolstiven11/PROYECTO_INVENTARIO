<%--=====================================================================
    VISTA JSP: Bienvenida.jsp - Pantalla de Bienvenida al Sistema
    
    QUIÉN LA MUESTRA: Menu_sistema.jsp → Al hacer clic en "Contabilidad-Sistematica"
    o puede ser una vista intermedia
    
    DATOS QUE USA DE LA SESIÓN:
    - ${usuarioLogueado.nombre} → Nombre del usuario logueado
    
    PROPÓSITO:
    - Dar bienvenida personalizada al usuario
    - Servir como página de transición al registro de bares
    
    ACCIONES QUE ENVÍA:
    - Enlace a "view/registroBar.html" → Para registrar un nuevo bar
    
    IMPORTANCIA:
    - Primera interacción personalizada con el usuario
    - Gateway hacia el registro de bares
    =====================================================================--%>
<!DOCTYPE html>
<%-- Inicio del documento HTML con idioma español --%>
<html lang="es">

<%-- Cabecera del documento con metadatos y recursos --%>
<head>
    <%-- Codificación de caracteres UTF-8 para soporte de caracteres especiales --%>
    <meta charset="UTF-8">
    <%-- Configuración de viewport para diseño responsive --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Título de la página que aparece en el navegador --%>
    <title>PROYECTO INVENTARIO</title>
    <%-- Hoja de estilos CSS para la vista de bienvenida --%>
    <link rel="stylesheet" href="../css/bienvenida.css">
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
        <%-- Mensaje de bienvenida personalizado con el nombre del usuario logueado --%>
        <h1 class="main__bienvenida">Bienvenido, ${usuarioLogueado.nombre}</h1>

        <%-- Contenedor del título principal del sistema --%>
        <div class="contenedor_titulo">
            <%-- Enlace al registro de bares, clickable --%>
            <a class="main__sistema" href="registroBar.html">
                <%-- Nombre del sistema de contabilidad --%>
                Contabilidad-Sistematica
            </a>
        </div>

        <%-- Logo del sistema que se muestra como imagen principal --%>
        <img src="../assets/img/LOGO.png" alt="logo">
    </main>
    <%-- Pie de página (vacío en este caso) --%>
    <footer>
    </footer>
</body>

<%-- Cierre del documento HTML --%>
</html>