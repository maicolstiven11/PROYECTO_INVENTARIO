# Manual Técnico – Sistema de Inventario para Bares
# Contabilidad Sistemática

**Michael Stiven Echeverry Vargas**

Servicio Nacional de Aprendizaje (SENA)

Análisis y Desarrollo de Software

Instructor: Enzy Zulay Angarita Bermúdez

Girón, Santander

18 de marzo de 2026

---

## Contenido

1. Introducción
   - 1.1. Propósito del manual
   - 1.2. Alcance del Software
2. Descripción general del sistema
   - 2.1. Objetivos del sistema
   - 2.2. Requisitos
3. Requisitos técnicos del sistema
   - 3.1. Requisitos de Hardware
   - 3.2. Requisitos de Software
4. Instalación y configuración
   - 4.1. Tipo de aplicación
   - 4.2. Lenguaje o framework usado
   - 4.3. Base de datos utilizada
   - 4.4. Configuración inicial
5. Arquitectura del Software y Base de datos
   - 5.1. Diagrama de casos de uso
   - 5.2. Estructura de tablas
   - 5.3. Scripts de creación y migración
6. Diseño del Software
   - 6.1. Descripción de los Módulos o interfaz del aplicativo
7. Interfaces del Sistema
   - 7.1. Formatos de Entrada/Salida
   - 7.2. Autenticación y Autorización
8. Mensajes de Ayuda y Errores
   - 8.1. Mensajes de ayuda
   - 8.2. Mensajes de error
9. Conclusiones
10. Referencias
11. Anexos
    - 11.1. Glosario de Términos
    - 11.2. Datos de contacto soporte técnico

---

## Lista de figuras

> [!IMPORTANT]
> En este documento se indican las figuras que debes capturar tú mismo desde tu aplicación corriendo en el navegador. En cada lugar donde corresponda una imagen verás la instrucción **[IMAGEN: descripción]** con la explicación de qué screenshot tomar. Donde se necesite una imagen de un sitio web externo (descarga de software), se incluye el enlace directo para que lo abras y captures.

---

## 1. Introducción

### 1.1. Propósito del manual

El propósito de este manual técnico es proporcionar a desarrolladores, administradores y personal de soporte la información necesaria para instalar, configurar, operar y mantener el Sistema de Inventario para Bares (Contabilidad Sistemática).

El documento detalla la arquitectura del sistema, los requisitos técnicos, la estructura de la base de datos, el funcionamiento de cada módulo y las pautas para resolver incidencias comunes.

Su objetivo es servir como guía de referencia durante la implementación y el ciclo de vida del software, facilitando futuras mejoras y asegurando que el sistema pueda ser comprendido y administrado por diferentes equipos técnicos.

### 1.2. Alcance del Software

Contabilidad Sistemática es un sistema web de gestión de inventario diseñado específicamente para bares y establecimientos similares. El sistema permite automatizar los procesos manuales al momento de llevar un control de inventario, contabilidad de ventas, gastos operativos y pedidos a proveedores, de forma que los propietarios y trabajadores tengan una visión clara y detallada sobre el manejo financiero y de productos de su negocio.

Este sistema soporta dos roles: **Administrador** y **Trabajador**, cada uno con vistas y permisos diferentes.

Entre sus funcionalidades principales se encuentran:

- Registro y autenticación de usuarios con roles diferenciados.
- Gestión de múltiples negocios (bares) por usuario.
- Control de inventario con detalle de productos y cantidades.
- Registro de ventas con descuento automático de stock.
- Registro de pedidos a proveedores con incremento automático de stock.
- Control de gastos operativos diarios.
- Gestión de proveedores y productos.
- Generación de informes financieros dinámicos (ventas, gastos, pedidos, ganancia neta).
- Gestión de trabajadores asociados a negocios.
- Panel de administración con perfil de usuario editable.

---

## 2. Descripción general del sistema

### 2.1. Objetivos del sistema

#### 2.1.1. Objetivo general

Crear una solución web que ayude a los propietarios de bares a llevar un control automatizado de su inventario, ventas, gastos y pedidos a proveedores, para que comprendan mejor el flujo financiero de su negocio y puedan tomar decisiones más acertadas.

#### 2.1.2. Objetivos específicos

1. Diseñar un sistema que permita registrar productos, ventas, gastos y pedidos de forma organizada.
2. Implementar un módulo de inventario que actualice automáticamente el stock al vender o realizar pedidos a proveedores.
3. Facilitar la visualización del estado financiero del negocio a través de informes dinámicos.
4. Permitir la gestión de múltiples negocios (bares) desde una misma cuenta de usuario.
5. Implementar un sistema de roles (Administrador y Trabajador) para controlar el acceso a funcionalidades sensibles.
6. Generar reportes de descuadre de inventario para identificar pérdidas o inconsistencias.

### 2.2. Requisitos

#### 2.2.1. Requisitos funcionales

1. **RF-01 – Registrar Usuario:** Permitir registrar un nuevo usuario ingresando nombre (obligatorio), correo electrónico (obligatorio, único), teléfono (opcional), contraseña (obligatoria, mín. 6 caracteres) y rol (seleccionable: Administrador o Trabajador). Los datos se almacenan en tres tablas: `USUARIO`, `CORREO_USUARIO` y `TELEFONO_USUARIO` mediante una transacción atómica.
2. **RF-02 – Iniciar Sesión:** Permitir al usuario autenticarse ingresando correo electrónico y contraseña. Validar credenciales contra la base de datos.
3. **RF-03 – Gestionar Roles y Permisos:** Diferenciar funcionalidades según el rol del usuario (Administrador o Trabajador).
4. **RF-04 – Registrar Negocio:** Permitir al administrador registrar un nuevo negocio (bar) con nombre, NIT y dirección.
5. **RF-05 – Listar Negocios:** Mostrar todos los negocios asociados al usuario autenticado.
6. **RF-06 – Eliminar Negocio:** Permitir eliminar un negocio y todos sus datos asociados (inventarios, ventas, gastos, pedidos) en cascada.
7. **RF-07 – Iniciar Inventario:** Crear un nuevo inventario activo para un negocio, estableciendo tipo de control y fecha de inicio.
8. **RF-08 – Registrar Producto:** Permitir agregar productos al catálogo general con nombre, marca, precio unitario, tipo, imagen, fecha de vencimiento y cantidad/medida.
9. **RF-09 – Editar Producto:** Permitir modificar los datos de un producto existente.
10. **RF-10 – Agregar Producto al Inventario:** Vincular productos del catálogo al inventario activo con su cantidad inicial.
11. **RF-11 – Registrar Venta:** Permitir registrar ventas seleccionando productos del inventario, cantidades y métodos de pago. El stock se descuenta automáticamente de `inventario_detalle`.
12. **RF-12 – Visualizar Ventas:** Listar todas las ventas realizadas con sus detalles (productos, cantidades, totales).
13. **RF-13 – Registrar Gasto:** Permitir registrar gastos operativos diarios con descripción, cantidad, fecha y subtotal.
14. **RF-14 – Visualizar Gastos:** Listar todos los gastos registrados para el negocio actual.
15. **RF-15 – Registrar Pedido a Proveedor:** Permitir crear pedidos a proveedores indicando producto, cantidad, subtotal, IVA, fechas de pedido y entrega. El stock se incrementa automáticamente en `inventario_detalle`.
16. **RF-16 – Visualizar Pedidos:** Listar todos los pedidos realizados a proveedores.
17. **RF-17 – Registrar Proveedor:** Permitir registrar datos de proveedores (nombre, documento, teléfono, dirección).
18. **RF-18 – Listar Proveedores:** Mostrar todos los proveedores registrados.
19. **RF-19 – Visualizar Informes:** Mostrar un dashboard dinámico con totales de ventas, gastos, pedidos y ganancia neta (ventas − gastos − pedidos).
20. **RF-20 – Gestionar Trabajadores:** Permitir al administrador agregar y gestionar trabajadores asociados al negocio.
21. **RF-21 – Editar Perfil:** Permitir al usuario actualizar sus datos personales y contraseña.
22. **RF-22 – Cerrar Sesión:** Permitir al usuario cerrar sesión e invalidar su sesión activa.

#### 2.2.2. Requisitos no funcionales

1. El sistema debe ser intuitivo y agradable visualmente para el usuario.
2. El sistema debe ser compatible con los navegadores web más utilizados (Chrome, Edge, Opera).
3. El sistema debe estar diseñado de forma modular siguiendo el patrón MVC para facilitar futuras mejoras.
4. El sistema debe validar correctamente los datos ingresados para evitar errores y mantener la integridad de la información.
5. El sistema debe utilizar `PreparedStatement` en todas las consultas SQL para prevenir inyección SQL.
6. Las operaciones críticas (ventas, pedidos, registro de usuario) deben usar transacciones SQL (`commit`/`rollback`).
7. El sistema debe estar desarrollado para operar con una base de datos MySQL 8.

---

## 3. Requisitos técnicos del sistema

### 3.1. Requisitos de Hardware

#### 3.1.1. Requisitos mínimos

| Componente  | Especificación                              |
|-------------|---------------------------------------------|
| Procesador  | Intel Core i3 10ª gen o equivalente         |
| Memoria RAM | 4 GB                                        |
| Disco Duro  | 250 GB                                      |
| Pantalla    | Resolución mínima de 1366×768 (14 pulgadas) |

#### 3.1.2. Requisitos recomendados

| Componente  | Especificación                                             |
|-------------|------------------------------------------------------------|
| Procesador  | AMD Ryzen 3 5300U o equivalente (4 núcleos, 2.6 GHz o superior) |
| Memoria RAM | 8 GB                                                       |
| Disco/SSD   | 500 GB                                                     |
| Pantalla    | Full HD 1920×1080 (16 pulgadas o más)                      |
| Periféricos | Teclado y mouse                                            |

### 3.2. Requisitos de Software

#### 3.2.1. Requisitos mínimos

| Software              | Versión                     |
|-----------------------|-----------------------------|
| Sistema Operativo     | Windows 10 (64 bits)        |
| JDK (Java)            | Versión 8                   |
| Servidor de Aplicaciones | Apache Tomcat 9          |
| IDE Backend           | Apache NetBeans 13 o VS Code |
| Navegador Web         | Google Chrome, Edge u Opera |
| Gestor de Base de Datos | MySQL Workbench 8.0       |
| Control de Versiones  | Git + GitHub                |

#### 3.2.2. Requisitos recomendados

| Software              | Versión                        |
|-----------------------|--------------------------------|
| Sistema Operativo     | Windows 11 Home (64 bits)      |
| JDK (Java)            | Versión 8 o superior           |
| Servidor de Aplicaciones | Apache Tomcat 9             |
| IDE Backend           | Apache NetBeans 13 o VS Code   |
| Navegador Web         | Google Chrome actualizado      |
| Gestor de Base de Datos | MySQL Workbench 8.0          |
| Control de Versiones  | Git + GitHub                   |
| Herramienta de Build  | Apache Maven 3.9+              |

---

## 4. Instalación y configuración

### 4.1. Tipo de aplicación

Aplicación web monolítica desarrollada en Java con Servlets, JSP y JSTL, ejecutada sobre Apache Tomcat 9. El frontend está integrado dentro del mismo proyecto como archivos HTML, JSP, CSS y JavaScript. No es una SPA; cada vista es una página independiente servida por el servidor.

### 4.2. Lenguaje o framework usado

| Capa     | Tecnología                                      |
|----------|-------------------------------------------------|
| Backend  | Java 8 con Servlets (`javax.servlet-api 4.0.1`) |
| Vistas   | JSP con JSTL 1.2, HTML5, CSS3, JavaScript       |
| Build    | Apache Maven (packaging: WAR)                   |
| Iconos   | Font Awesome 6.5.0 (CDN)                        |

### 4.3. Base de datos utilizada

MySQL 8 a través de MySQL Workbench. La base de datos se llama `proyecto_inventario_bar`. El conector Java utilizado es `mysql-connector-j 8.3.0`.

### 4.4. Configuración inicial

#### 4.4.1. Instalación de backend

**Paso 1: Instalar Java Development Kit (JDK) - Versión 8 o superior**

Ingresar a la página oficial de Oracle Java Downloads y seleccionar el JDK 8, el sistema operativo Windows y descargar el instalador.

> **Enlace:** https://www.oracle.com/java/technologies/downloads/

**[IMAGEN: Figura 1 – Captura la página web de Oracle mostrando las opciones de descarga del JDK]**

**Paso 2: Verificar la instalación de Java**

Abrir la consola de comandos (CMD) y ejecutar:

```
java -version
```

**[IMAGEN: Figura 2 – Captura la consola CMD mostrando el resultado del comando `java -version`]**

**Paso 3: Descargar e instalar Apache NetBeans IDE**

Dirigirse al archivo oficial de Apache NetBeans y descargar el instalador para Windows de la versión 13.

> **Enlace:** https://archive.apache.org/dist/netbeans/netbeans/13/netbeans-13-bin.zip

**[IMAGEN: Figura 3 – Captura la página de descarga de NetBeans IDE]**

**Paso 4: Descargar y descomprimir Apache Tomcat 9**

Ingresar a la página oficial de Apache Tomcat, seleccionar "Tomcat 9" en el menú lateral y descargar el archivo ZIP para Windows (64-bit). Luego descomprimir en el explorador de archivos.

> **Enlace:** https://tomcat.apache.org/download-90.cgi

**[IMAGEN: Figura 4 – Captura la página de descarga de Apache Tomcat 9]**

**Paso 5: Registrar el servidor Tomcat en NetBeans**

1. Abrir NetBeans IDE.
2. Ir a **Tools → Servers**.
3. Hacer clic en **Add Server**.
4. Seleccionar **Apache Tomcat or TomEE**.
5. Indicar la carpeta donde se descomprimió Tomcat.
6. Especificar un usuario y contraseña para la administración.

**[IMAGEN: Figura 5 – Captura la ventana de "Tools → Servers" en NetBeans]**

**[IMAGEN: Figura 6 – Captura la ventana seleccionando "Apache Tomcat" como tipo de servidor]**

**[IMAGEN: Figura 7 – Captura seleccionando la carpeta de Tomcat descomprimida]**

**[IMAGEN: Figura 8 – Captura insertando usuario y contraseña del Tomcat]**

**Paso 6: Descargar e instalar MySQL Workbench**

Ingresar al sitio web oficial de MySQL Community Downloads, seleccionar la versión 8 para Windows y descargar el instalador.

> **Enlace:** https://dev.mysql.com/downloads/workbench/

**[IMAGEN: Figura 9 – Captura la página de descarga de MySQL Workbench]**

**Paso 7: Crear la base de datos**

En MySQL Workbench, desde el usuario Root, ejecutar el script SQL de creación de tablas para generar la base de datos `proyecto_inventario_bar` con toda su estructura.

**[IMAGEN: Figura 10 – Captura MySQL Workbench mostrando la base de datos con sus tablas creadas]**

**Paso 8: Verificar la conexión a la BD desde el backend**

Abrir el archivo `Conexion.java` ubicado en `src/main/java/com/inventario/util/` y verificar que las credenciales coincidan:

```java
private static final String URL = "jdbc:mysql://localhost:3306/proyecto_inventario_bar?serverTimezone=UTC";
private static final String USUARIO = "root";
private static final String PASSWORD = "TU_CONTRASEÑA_AQUI";
```

**[IMAGEN: Figura 11 – Captura el archivo Conexion.java abierto en el IDE mostrando las credenciales]**

**Paso 9: Ejecutar el proyecto**

Hacer clic derecho sobre el proyecto en NetBeans → **Run**. Esto desplegará la aplicación en Tomcat y abrirá el navegador.

**[IMAGEN: Figura 12 – Captura la consola de NetBeans mostrando que Tomcat arrancó exitosamente]**

**[IMAGEN: Figura 13 – Captura la aplicación corriendo en el navegador (página de inicio de sesión)]**

---

## 5. Arquitectura del Software y Base de datos

### 5.1. Patrón de arquitectura: MVC (Modelo-Vista-Controlador)

El sistema sigue el patrón de arquitectura **MVC** (Modelo-Vista-Controlador), separando la lógica de negocio, la presentación y el control del flujo:

| Capa         | Ubicación en el proyecto                              | Descripción                          |
|--------------|-------------------------------------------------------|--------------------------------------|
| **Modelo**   | `src/main/java/com/inventario/model/`                 | Clases Java (POJOs) que representan las tablas de la BD |
| **DAO**      | `src/main/java/com/inventario/dao/`                   | Clases que ejecutan las consultas SQL |
| **Controlador** | `src/main/java/com/inventario/controller/`         | Servlets que reciben peticiones HTTP y coordinan el flujo |
| **Vista**    | `src/main/webapp/view/`                               | Archivos HTML, JSP con JSTL y CSS    |
| **Utilidad** | `src/main/java/com/inventario/util/`                  | Clase de conexión a la BD (`Conexion.java`) |

### 5.1.1. Diagrama de casos de uso

**[IMAGEN: Figura 14 – Captura o crea un diagrama de casos de uso del Administrador. Puedes usar draw.io (https://app.diagrams.net/) para crearlo. Debe mostrar: Registrar Negocio, Gestionar Inventario, Registrar Venta, Registrar Gasto, Registrar Pedido, Ver Informes, Gestionar Trabajadores, Gestionar Proveedores, Gestionar Productos, Editar Perfil]**

**[IMAGEN: Figura 15 – Captura o crea un diagrama de casos de uso del Trabajador. Funcionalidades limitadas según los permisos de su rol]**

### 5.2. Estructura de tablas

La base de datos `proyecto_inventario_bar` contiene las siguientes tablas principales:

| Tabla                  | Descripción                                             |
|------------------------|---------------------------------------------------------|
| `permiso`              | Permisos del sistema                                    |
| `rol`                  | Roles de usuario (Administrador, Trabajador)            |
| `rol_permiso`          | Relación muchos-a-muchos entre roles y permisos         |
| `usuario`              | Datos principales del usuario (nombre, password, rol)   |
| `correo_usuario`       | Correos electrónicos asociados a usuarios               |
| `telefono_usuario`     | Teléfonos asociados a usuarios                          |
| `negocio`              | Bares/negocios registrados (nombre, NIT, dirección)     |
| `usuario_negocio`      | Relación usuario-negocio                                |
| `inventario`           | Inventarios activos por negocio                         |
| `inventario_detalle`   | Productos dentro de un inventario con `cantidad_inicial`|
| `producto`             | Catálogo general de productos                           |
| `venta`                | Encabezado de ventas                                    |
| `detalle_venta`        | Productos vendidos en cada venta (usa `id_inv_detalle`) |
| `gasto_diario`         | Gastos operativos                                       |
| `datos_proveedor`      | Datos de proveedores                                    |
| `pedidos_proveedor`    | Encabezado de pedidos a proveedores                     |
| `detalle_pedidos`      | Productos pedidos en cada orden (usa `id_inv_detalle`)  |

**[IMAGEN: Figura 16 – Captura la estructura de tablas en MySQL Workbench. Puedes usar el menú Database → Reverse Engineer para generar un diagrama ER]**

### 5.3. Scripts de creación y migración

| Script               | Descripción                                               |
|----------------------|-----------------------------------------------------------|
| Script de creación   | Crea todas las tablas, roles, permisos y relaciones       |
| `migracion_bd.sql`   | Migra las FK de `detalle_venta` y `detalle_pedidos` para apuntar a `inventario_detalle` |

---

## 6. Diseño del Software

### 6.1. Descripción de los Módulos o interfaz del aplicativo

A continuación se describen los módulos principales del sistema. Se presentan primero los módulos comunes (disponibles para todos los roles) y luego los módulos específicos organizados por función.

---

#### 6.1.1. Módulos comunes

**1. Inicio de Sesión**

Este es el módulo de Login. Se solicita el correo electrónico y la contraseña del usuario. El sistema identifica el rol correspondiente y redirige a la vista adecuada (panel de administración o menú del sistema). Si las credenciales son incorrectas, se muestra un mensaje de error.

- **Archivo:** `view/Inicio_sesion.html`
- **Servlet:** `LoginServlet.java`
- **DAO:** `UsuarioDAO.validarLogin()`

**[IMAGEN: Figura 17 – Captura la vista de Inicio de Sesión desde tu navegador]**

**2. Registro de Usuario**

Formulario de registro para nuevos usuarios. Permite ingresar nombre, correo, teléfono (opcional), contraseña y seleccionar el rol. Los datos se guardan en las tablas `USUARIO`, `CORREO_USUARIO` y `TELEFONO_USUARIO` dentro de una transacción.

- **Archivos:** `view/registroUser.html`, `view/registroUser2.html`
- **Servlet:** `RegistroServlet.java`
- **DAO:** `UsuarioDAO.registrarUsuario()`

**[IMAGEN: Figura 18 – Captura la vista de Registro de Usuario (paso 1)]**

**[IMAGEN: Figura 19 – Captura la vista de Registro de Usuario (paso 2, datos adicionales)]**

**3. Perfil de Administrador**

Permite al usuario visualizar y editar sus datos personales (nombre, correo, teléfono) y cambiar su contraseña.

- **Archivo:** `view/perfil_admin.jsp`
- **Servlet:** `PerfilServlet.java`

**[IMAGEN: Figura 20 – Captura la vista del Perfil del Usuario]**

---

#### 6.1.2. Módulos por rol

##### 6.1.2.1. Administrador

**1. Panel de Administración**

Vista principal tras iniciar sesión como administrador. Muestra opciones de navegación para acceder a los diferentes módulos del sistema.

- **Archivo:** `view/panel_administrador.html`

**[IMAGEN: Figura 21 – Captura el Panel de Administración]**

**2. Gestión de Negocios (Bares)**

Permite listar, registrar y eliminar negocios (bares). Cada negocio tiene nombre, NIT y dirección. Al seleccionar un negocio se accede a su inventario y operaciones.

- **Archivos:** `view/Lista_bares.jsp`, `view/registroBar.html`
- **Servlet:** `NegocioServlet.java`
- **DAO:** `NegocioDAO.java`

**[IMAGEN: Figura 22 – Captura la vista de Lista de Bares mostrando los negocios registrados]**

**[IMAGEN: Figura 23 – Captura el formulario de Registrar Nuevo Bar]**

**[IMAGEN: Figura 24 – Captura la confirmación al eliminar un bar]**

**3. Menú del Sistema**

Vista principal de un negocio seleccionado. Muestra las opciones disponibles: Inventario, Ventas, Gastos, Proveedores, Informes, etc.

- **Archivo:** `view/Menu_sistema.jsp`

**[IMAGEN: Figura 25 – Captura el Menú del Sistema con todas las opciones disponibles]**

**4. Gestión de Trabajadores**

Permite al administrador agregar y gestionar trabajadores asociados al negocio.

- **Archivos:** `view/gestion_trabajadores.jsp`
- **Servlet:** `TrabajadorServlet.java`

**[IMAGEN: Figura 26 – Captura la vista de Gestión de Trabajadores]**

---

##### 6.1.2.2. Módulos de Inventario

**1. Iniciar Inventario**

Permite crear un nuevo inventario para el negocio, definiendo tipo de control (mensual) y fecha de inicio.

- **Archivo:** `view/Inicio_inv.html`
- **Servlet:** `InventarioServlet.java`
- **DAO:** `InventarioDAO.java`

**[IMAGEN: Figura 27 – Captura la vista de Iniciar Inventario]**

**2. Menú del Inventario**

Muestra las opciones del inventario activo: agregar productos, ver detalle, registrar ventas, gastos, pedidos, ver informes.

- **Archivo:** `view/menu_inventario.jsp`

**[IMAGEN: Figura 28 – Captura el Menú del Inventario con el aside de opciones]**

**3. Detalle del Inventario**

Lista los productos actualmente en el inventario con sus cantidades.

- **Archivo:** `view/inventario_detalle.jsp`
- **Servlet:** `InventarioServlet.java`
- **DAO:** `DetalleInventarioDAO.java`

**[IMAGEN: Figura 29 – Captura la vista de Detalle de Inventario mostrando productos con cantidades]**

---

##### 6.1.2.3. Módulo de Productos

**1. Registrar Producto**

Formulario para agregar un nuevo producto al catálogo general: nombre, marca, precio unitario, tipo, imagen, fecha de vencimiento y cantidad/medida.

- **Archivo:** `view/Registro_produc.html`
- **Servlet:** `ProductoServlet.java`
- **DAO:** `ProductoDAO.java`

**[IMAGEN: Figura 30 – Captura el formulario de Registro de Producto]**

**2. Editar Producto**

Permite modificar los datos de un producto existente.

- **Archivos:** `view/editar_productos.jsp`, `view/formulario_editar_producto.jsp`

**[IMAGEN: Figura 31 – Captura la vista de listado de productos para editar]**

**[IMAGEN: Figura 32 – Captura el formulario de edición de un producto]**

---

##### 6.1.2.4. Módulo de Ventas

**1. Registrar Venta**

Permite seleccionar productos del inventario activo, indicar cantidades, establecer método de pago y confirmar la venta. El stock se descuenta automáticamente de `inventario_detalle.cantidad_inicial`.

- **Archivo:** `view/agregar_venta.jsp`
- **Servlet:** `VentaServlet.java`
- **DAO:** `VentaDAO.java`

**[IMAGEN: Figura 33 – Captura la vista de Registro de Venta (carrito de productos)]**

**2. Visualizar Ventas**

Lista todas las ventas realizadas con sus detalles.

- **Archivo:** `view/visualizar_ventas.jsp`
- **Servlet:** `VentaServlet.java`

**[IMAGEN: Figura 34 – Captura la vista de Visualizar Ventas]**

**3. Detalle de Venta**

Muestra los productos comprados, cantidades y totales de una venta específica.

- **Archivo:** `view/detalle_venta.jsp`

**[IMAGEN: Figura 35 – Captura la vista de Detalle de una Venta]**

---

##### 6.1.2.5. Módulo de Gastos

**1. Registrar Gasto**

Formulario para registrar un gasto operativo con descripción, cantidad, fecha y subtotal.

- **Archivo:** `view/agregar_gasto.html`
- **Servlet:** `GastoServlet.java`
- **DAO:** `GastoDao.java`

**[IMAGEN: Figura 36 – Captura el formulario de Agregar Gasto]**

**2. Visualizar Gastos**

Lista todos los gastos registrados para el negocio.

- **Archivo:** `view/visualizar_gastos.jsp`

**[IMAGEN: Figura 37 – Captura la vista de Visualizar Gastos]**

---

##### 6.1.2.6. Módulo de Proveedores y Pedidos

**1. Registrar Proveedor**

Formulario para registrar datos de un proveedor (nombre, documento, teléfono, dirección).

- **Archivo:** `view/Registro_datos_prv.html`
- **Servlet:** `ProveedorServlet.java`
- **DAO:** `ProveedorDAO.java`

**[IMAGEN: Figura 38 – Captura el formulario de Registro de Proveedor]**

**2. Listar Proveedores**

Muestra todos los proveedores registrados.

- **Archivo:** `view/lista_proveedores.jsp`

**[IMAGEN: Figura 39 – Captura la vista de Lista de Proveedores]**

**3. Registrar Pedido a Proveedor**

Permite crear un pedido indicando proveedor, producto del inventario, cantidad, subtotal, IVA, fechas de pedido y entrega. El stock se incrementa automáticamente en `inventario_detalle.cantidad_inicial`.

- **Archivo:** `view/agregar_pedido.jsp`
- **Servlet:** `PedidoServlet.java`
- **DAO:** `PedidoDAO.java`

**[IMAGEN: Figura 40 – Captura el formulario de Registro de Pedido a Proveedor]**

**4. Visualizar Pedidos**

Lista todos los pedidos realizados a proveedores.

- **Archivo:** `view/visualizar_pedidos.jsp`

**[IMAGEN: Figura 41 – Captura la vista de Visualizar Pedidos]**

---

##### 6.1.2.7. Módulo de Informes

**1. Dashboard de Informes**

Muestra un resumen financiero dinámico del negocio: total de ventas, total de gastos, total de pedidos a proveedores, número de ventas realizadas y la ganancia neta (Ventas − Gastos − Pedidos). Incluye barras de progreso comparativas.

- **Archivo:** `view/visualizar_informes.jsp`
- **Servlet:** `InformeServlet.java`
- **DAO:** `InformeDAO.java`

**[IMAGEN: Figura 42 – Captura el Dashboard de Informes mostrando las tarjetas de totales y barras de progreso]**

---

## 7. Interfaces del Sistema

### 7.1. Formatos de Entrada/Salida

#### a) Formato de Entrada

El sistema recibe datos a través de formularios HTML estándar enviados por método **POST** o **GET** al servidor. Los datos viajan como parámetros de solicitud HTTP (`request.getParameter()`).

Ejemplos de parámetros:
- `email` → Correo electrónico del usuario.
- `password` → Contraseña del usuario.
- `id_producto` → ID del producto seleccionado.
- `cantidad` → Cantidad ingresada.
- `subtotal` → Valor monetario.

#### b) Formato de Salida

Las respuestas del servidor son páginas HTML/JSP completas renderizadas dinámicamente con datos de la base de datos usando JSTL (`<c:forEach>`, `<c:if>`, `<fmt:formatNumber>`). También se utilizan redirecciones HTTP (`sendRedirect`) para navegación post-procesamiento.

### 7.2. Autenticación y Autorización

#### a) Autenticación

Se realiza mediante validación de credenciales (correo electrónico y contraseña) contra la base de datos MySQL. El `LoginServlet` recibe las credenciales del formulario, el `UsuarioDAO` consulta la tabla `CORREO_USUARIO` y `USUARIO` para validar. Si son correctas, se almacenan los datos del usuario en la sesión HTTP (`HttpSession`).

#### b) Autorización

El sistema aplica control de acceso según rol de usuario almacenado en sesión:

| Rol             | ID  | Funcionalidades                                                              |
|-----------------|-----|------------------------------------------------------------------------------|
| Administrador   | 1   | Acceso completo: gestión de negocios, inventarios, ventas, gastos, pedidos, trabajadores, informes, perfil |
| Trabajador      | 2   | Acceso limitado según permisos asignados                                     |

Los Servlets verifican el rol almacenado en `session.getAttribute("idRol")` antes de ejecutar operaciones sensibles.

---

## 8. Mensajes de Ayuda y Errores

### 8.1. Mensajes de ayuda

El sistema proporciona mensajes de asistencia contextual para guiar al usuario:

- **Labels y Placeholders:** Cada campo de formulario tiene etiquetas (`<label>`) y textos de ayuda (`placeholder`) que indican qué dato se espera.

**[IMAGEN: Figura 43 – Captura un formulario mostrando las labels y placeholders en los campos]**

- **Mensajes de vistas vacías:** Cuando no hay registros, se muestra un mensaje informativo como "No se han registrado pedidos a proveedores aún" o "No hay productos en el inventario".

**[IMAGEN: Figura 44 – Captura una vista de listado mostrando el mensaje de "sin registros"]**

- **Validaciones en formularios:** Los campos obligatorios tienen el atributo `required`, `minlength` y `maxlength` para prevenir el envío de datos incompletos.

### 8.2. Mensajes de error

El sistema gestiona errores a través de notificaciones visuales:

- **Error de autenticación:** Si las credenciales son incorrectas, se muestra una alerta roja: *"Credenciales incorrectas. El correo o la contraseña no son válidos."*

**[IMAGEN: Figura 45 – Captura el mensaje de error en la vista de Login al ingresar credenciales incorrectas]**

- **Error de restricción FK:** Si se intenta eliminar un registro que tiene datos dependientes, se muestra un mensaje indicando la restricción.

**[IMAGEN: Figura 46 – Captura un mensaje de error al intentar eliminar un registro con dependencias]**

- **Mensajes de éxito:** Tras completar una operación exitosa (registrar venta, registrar gasto, etc.), el sistema muestra una página de confirmación.

**[IMAGEN: Figura 47 – Captura la página de confirmación tras registrar una venta exitosamente]**

**[IMAGEN: Figura 48 – Captura la página de confirmación tras registrar un gasto]**

**[IMAGEN: Figura 49 – Captura la página de confirmación tras registrar un pedido a proveedor]**

---

## 9. Conclusiones

La implementación de Contabilidad Sistemática permite a los propietarios de bares llevar un control más claro, organizado y automatizado de su inventario y finanzas. Al separar los roles de Administrador y Trabajador, se asegura un manejo adecuado de la información y una experiencia adaptada a cada responsabilidad.

El sistema centraliza todo lo relacionado con productos, ventas, gastos y pedidos a proveedores, facilitando que los usuarios comprendan mejor cómo está funcionando su negocio. Gracias a la gestión automática de stock (descuento al vender, incremento al pedir) y a los informes financieros dinámicos, se logra una visión más completa y en tiempo real de la situación del bar.

Además, al estar diseñado como una aplicación web bajo el patrón MVC con tecnologías probadas (Java, Servlets, JSP, MySQL), resulta mantenible, extensible y con posibilidades de crecer en el futuro.

En conclusión, Contabilidad Sistemática se convierte en una herramienta práctica que ayuda a tomar decisiones más acertadas sobre el manejo del inventario y las finanzas del negocio, y que, con el tiempo, puede seguir mejorando para responder a las necesidades de sus usuarios.

---

## 10. Referencias

- Oracle. (s/f). Java SE Development Kit Downloads. Recuperado de https://www.oracle.com/java/technologies/downloads/

- Apache Software Foundation. (s/f). Apache NetBeans IDE. Recuperado de https://netbeans.apache.org/

- Apache Software Foundation. (s/f). Apache Tomcat 9. Recuperado de https://tomcat.apache.org/download-90.cgi

- MySQL. (s/f). MySQL Workbench Downloads. Recuperado de https://dev.mysql.com/downloads/workbench/

- Font Awesome. (s/f). Font Awesome Icons. Recuperado de https://fontawesome.com/

- Oracle. (s/f). Java Servlet API Documentation. Recuperado de https://docs.oracle.com/javaee/7/api/javax/servlet/package-summary.html

- Maven Central Repository. (s/f). MySQL Connector/J. Recuperado de https://mvnrepository.com/artifact/com.mysql/mysql-connector-j

- GitHub. (s/f). Repositorio del Proyecto. Recuperado de https://github.com/

---

## 11. Anexos

### 11.1. Glosario de Términos

| Término            | Definición |
|--------------------|------------|
| **Apache Tomcat**  | Servidor web de código abierto y contenedor de Servlets para Java. Permite ejecutar aplicaciones web Java (JSP y Servlets) en un servidor HTTP. |
| **CSS**            | Cascading Style Sheets (Hojas de estilo en cascada). Lenguaje para aplicar estilos visuales (colores, tipografías, márgenes) a documentos HTML. |
| **DAO**            | Data Access Object. Patrón de diseño que abstrae las operaciones de acceso a base de datos, separando la lógica SQL de la lógica de negocio. |
| **FK (Foreign Key)** | Llave foránea. Columna en una tabla que hace referencia a la llave primaria de otra tabla, estableciendo una relación entre ambas. |
| **HTML**           | HyperText Markup Language. Lenguaje de marcado para definir la estructura y contenido de las páginas web. |
| **Java**           | Lenguaje de programación orientado a objetos utilizado para el desarrollo del backend de esta aplicación. |
| **JavaScript**     | Lenguaje de programación utilizado en el frontend para agregar interactividad a las páginas web (cálculos automáticos, validaciones, etc.). |
| **JDK**            | Java Development Kit. Paquete de herramientas para desarrollar aplicaciones Java (compilador, depurador, etc.). |
| **JSP**            | JavaServer Pages. Tecnología que permite crear páginas web dinámicas usando código Java embebido en HTML. |
| **JSTL**           | JavaServer Pages Standard Tag Library. Conjunto de etiquetas estándar para simplificar el uso de lógica en archivos JSP (`<c:forEach>`, `<c:if>`, `<fmt:formatNumber>`). |
| **Maven**          | Herramienta de gestión de proyectos Java para administrar dependencias, compilar y empaquetar aplicaciones (genera archivos `.war`). |
| **MVC**            | Modelo-Vista-Controlador. Patrón de arquitectura que separa la aplicación en tres capas: Modelo (datos), Vista (interfaz) y Controlador (lógica de flujo). |
| **MySQL**          | Sistema de gestión de bases de datos relacional. Almacena toda la información del sistema en tablas estructuradas. |
| **MySQL Workbench** | Herramienta visual para administrar bases de datos MySQL, ejecutar scripts SQL y diseñar esquemas. |
| **PK (Primary Key)** | Llave primaria. Columna que identifica de forma única cada registro en una tabla de la base de datos. |
| **POJO**           | Plain Old Java Object. Clase Java simple con atributos privados, constructores y getters/setters, sin lógica compleja. |
| **PreparedStatement** | Clase Java que permite ejecutar consultas SQL parametrizadas de forma segura, previniendo inyección SQL. |
| **Script**         | Conjunto de instrucciones SQL escritas en un archivo `.sql` que se ejecutan en el gestor de base de datos para crear tablas o modificar la estructura. |
| **Servlet**        | Clase Java que recibe y responde peticiones HTTP del navegador. Actúa como controlador en la arquitectura MVC. |
| **Transacción SQL** | Conjunto de operaciones SQL que se ejecutan como una unidad atómica. Si una falla, todas se revierten (`rollback`). Si todas tienen éxito, se confirman (`commit`). |
| **WAR**            | Web Application Archive. Formato de empaquetado para aplicaciones web Java que se despliegan en servidores como Tomcat. |

### 11.2. Datos de contacto soporte técnico

En caso de requerir asistencia técnica, el usuario puede comunicarse con el desarrollador del sistema a través de los siguientes canales:

| Dato                   | Valor                                |
|------------------------|--------------------------------------|
| **Nombre**             | Michael Stiven Echeverry Vargas      |
| **Correo electrónico** | *(coloca tu correo aquí)*            |
| **Teléfono / WhatsApp**| *(coloca tu número aquí)*            |
| **Institución**        | SENA – Análisis y Desarrollo de Software |

---

> **Nota final:** Este manual técnico fue elaborado como parte del proyecto formativo del programa Análisis y Desarrollo de Software del SENA, bajo la orientación de la instructora Enzy Zulay Angarita Bermúdez.
