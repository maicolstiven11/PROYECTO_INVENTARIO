<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <% // Redirigir al usuario al inicio de sesión real, actualizando la URL base en su navegador // Esto previene
        errores de "404 Not Found" en rutas relativas (CSS, formularios y enlaces de registro)
        response.sendRedirect("view/Inicio_sesion.html"); %>