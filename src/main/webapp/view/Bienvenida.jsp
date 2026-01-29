<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PROYECTO INVENTARIO</title>
    <link rel="stylesheet" href="../css/bienvenida.css">
</head>

<body>
    <header>
        <nav class="navbar">
            <img class="navbar__logo" src="../assets/img/LOGO.png" alt="logo_sistema">
        </nav>
    </header>
    <main>
        <!-- AQUÍ SE MUESTRA EL NOMBRE DEL USUARIO LOGUEADO -->
        <h1 class="main__bienvenida">Bienvenido, ${usuarioLogueado.nombre}</h1>

        <div class="contenedor_titulo">
            <a class="main__sistema" href="registroBar.html">
                Contabilidad-Sistematica
            </a>
        </div>

        <img src="../assets/img/LOGO.png" alt="logo">
    </main>
    <footer>
    </footer>
</body>

</html>