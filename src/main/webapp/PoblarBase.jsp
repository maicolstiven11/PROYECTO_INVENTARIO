<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="com.inventario.util.Poblador" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Poblado Limpio de BD</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 40px;
                }

                .success {
                    color: green;
                }

                .error {
                    color: red;
                }
            </style>
        </head>

        <body>
            <h2>Reinicio Total de Base de Datos - 10 Registros Estrictos</h2>
            <% String resultado=Poblador.ejecutarPoblado(); out.println(resultado); %>
                <br><br>
                <a href="view/Inicio_sesion.html"
                    style="padding: 10px 15px; background: #333; color: white; text-decoration: none; border-radius: 5px;">Volver
                    al Login</a>
        </body>

        </html>