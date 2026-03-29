<%--=====================================================================
    VISTA JSP: index.jsp - Página de Entrada y Redirección
    
    QUIÉN LA MUESTRA: Es el primer archivo que se ejecuta al acceder a la aplicación
    (http://localhost:8080/PROYECTO_INVENTARIO/)
    
    PROPÓSITO: Redirigir automáticamente al usuario a la página de login
    para evitar errores 404 en rutas relativas
    
    DATOS QUE USA: Ninguno, es una redirección directa
    
    ACCIÓN QUE REALIZA:
    - response.sendRedirect("view/Inicio_sesion.html") → Redirige al login
    
    IMPORTANCIA:
    - Previene errores 404 en rutas relativas (CSS, formularios, enlaces)
    - Establece la URL base correcta desde el inicio
    - Es el punto de entrada único de la aplicación
    =====================================================================--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%-- Redirigir al usuario al inicio de sesión real, actualizando la URL base en su navegador --%>
    <%-- Esto previene errores de "404 Not Found" en rutas relativas (CSS, formularios y enlaces de registro) --%>
        response.sendRedirect("view/Inicio_sesion.html"); %>