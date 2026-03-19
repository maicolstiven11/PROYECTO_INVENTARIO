package com.inventario.model; // Define el paquete lógico donde reside esta clase de modelo

/**
 * Modelo de datos: Clase Usuario (Entidad o POJO - Plain Old Java Object).
 * 
 * Esta clase es la representación en el paradigma Orientado a Objetos (Java)
 * de la tabla 'usuario' física que existe en la base de datos MySQL.
 * Funciona como un contenedor para transportar datos del usuario entre las distintas
 * capas arquitectónicas (Vista → Controlador → DAO → Base de Datos).
 * Cada atributo encapsulado corresponde directamente a una columna de la tabla.
 */
public class Usuario { // Definición de la clase pública Usuario

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Aplicación del Principio de Encapsulamiento de POO)
    // Estos atributos almacenan el estado del objeto de tipo Usuario.
    // Solo pueden ser accedidos desde el exterior mediante métodos Getters y Setters.
    // =====================================================================

    private int idUsuario;    // Clave primaria (Primary Key). Identificador único del usuario autogenerado en la base de datos.
    private int idRol;        // Clave foránea (Foreign Key). Relaciona al usuario con la clase y tabla Rol (ej: 1 para Admin, 2 para Trabajador).
    private String nombre;    // Atributo de tipo cadena de texto que almacena el nombre completo del usuario.
    private String password;  // Atributo que almacena la contraseña del usuario (idealmente cifrada).
    private String email;     // Atributo que almacena el correo electrónico único del usuario.
    private String telefono;  // Atributo opcional que almacena el número de contacto del usuario.

    /**
     * CONSTRUCTOR POR DEFECTO (Constructor vacío)
     * Es un método especial de inicialización que permite instanciar un objeto de la clase Usuario
     * sin pasar ningún parámetro inicial (usando 'new Usuario()').
     * Los atributos se poblarán posteriormente utilizando los métodos Setters.
     */
    public Usuario() { // Declaración del constructor vacío
    }

    /**
     * CONSTRUCTOR PARAMETRIZADO (Sobrecarga de constructores)
     * Permite instanciar un objeto Usuario inicializando todos sus atributos de una sola vez
     * al momento de su creación.
     * 
     * @param idUsuario Identificador del usuario.
     * @param idRol Identificador del rol.
     * @param nombre Nombre del usuario.
     * @param password Contraseña del usuario.
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     */
    public Usuario(int idUsuario, int idRol, String nombre, String password, String email, String telefono) { // Declaración del constructor con todos los parámetros
        this.idUsuario = idUsuario;  // La palabra reservada 'this' diferencia el atributo de la clase del parámetro del método
        this.idRol = idRol;          // Asigna el parámetro recibido al atributo de la instancia actual
        this.nombre = nombre;        // Asigna el parámetro recibido al atributo de la instancia actual
        this.password = password;    // Asigna el parámetro recibido al atributo de la instancia actual
        this.email = email;          // Asigna el parámetro recibido al atributo de la instancia actual
        this.telefono = telefono;    // Asigna el parámetro recibido al atributo de la instancia actual
    }

    // =====================================================================
    // MÉTODOS ACCESORES Y MUTADORES (Getters y Setters)
    // Permiten la lectura y escritura segura de los atributos privados,
    // garantizando el encapsulamiento y el control sobre el estado del objeto.
    // =====================================================================

    /** Método accesor (Getter) que retorna el identificador único del usuario */
    public int getIdUsuario() { // Retorna un valor de tipo entero
        return idUsuario; // Retorna el valor actual del atributo idUsuario
    }

    /** Método mutador (Setter) que asigna o modifica el identificador del usuario */
    public void setIdUsuario(int idUsuario) { // Recibe un entero y no retorna nada (void)
        this.idUsuario = idUsuario; // Asigna el valor pasado por parámetro al atributo de la clase
    }

    /** Método accesor (Getter) que retorna el identificador lógico del rol asociado a este usuario */
    public int getIdRol() { // Retorna un valor de tipo entero
        return idRol; // Retorna el valor actual del atributo idRol
    }

    /** Método mutador (Setter) que asigna el rol correspondiente a este usuario */
    public void setIdRol(int idRol) { // Recibe un entero y no retorna nada (void)
        this.idRol = idRol; // Asigna el valor del parámetro al atributo interno
    }

    /** Método accesor (Getter) que retorna la cadena de texto con el nombre del usuario */
    public String getNombre() { // Retorna un objeto tipo String
        return nombre; // Retorna el valor actual del atributo nombre
    }

    /** Método mutador (Setter) que asigna el nombre ingresado al objeto actual */
    public void setNombre(String nombre) { // Recibe un String y no retorna nada (void)
        this.nombre = nombre; // Establece el atributo interno de nombre
    }

    /** Método accesor (Getter) que obtiene la contraseña actual en memoria del usuario */
    public String getPassword() { // Retorna un objeto tipo String
        return password; // Retorna el valor actual del atributo password
    }

    /** Método mutador (Setter) que establece o actualiza la contraseña en el objeto */
    public void setPassword(String password) { // Recibe un String y no retorna nada (void)
        this.password = password; // Sobrescribe la contraseña interna del objeto con la nueva proporcionada
    }

    /** Método accesor (Getter) que retorna el correo electrónico del usuario */
    public String getEmail() { // Retorna un objeto tipo String
        return email; // Retorna el valor actual del atributo email
    }

    /** Método mutador (Setter) que asigna el correo electrónico suministrado al atributo interno */
    public void setEmail(String email) { // Recibe un String como parámetro
        this.email = email; // Guarda el nuevo correo en el estado del objeto
    }

    /** Método accesor (Getter) que retorna el número de teléfono como String */
    public String getTelefono() { // Retorna un objeto tipo String
        return telefono; // Retorna el atributo teléfono
    }

    /** Método mutador (Setter) que almacena el número de contacto del usuario */
    public void setTelefono(String telefono) { // Recibe un String y no retorna nada (void)
        this.telefono = telefono; // Asigna el valor del teléfono a la variable privada
    }
    
    /**
     * Método auxiliar (propiedad derivada) que devuelve explícitamente el nombre del rol en formato texto.
     * Esta función evalúa el estado interno del objeto (idRol) y genera una respuesta inteligible.
     * Es útil para integraciones con componentes de Vista (como JSP).
     * 
     * @return Una cadena de texto ("Administrador" o "Trabajador") dependiendo de la lógica interna.
     */
    public String getNombreRol() { // Declaración de método público que retorna un String
        if (this.idRol == 1) { // Condicional if: evalúa si el atributo lógico idRol equivale al entero 1
            return "Administrador";  // Retorna el literal "Administrador" si la condición es verdadera
        } else { // Caso contrario (bloque else)
            return "Trabajador";     // Retorna el literal "Trabajador" al asumir cualquier otro identificador
        }
    }
    
    // =====================================================================
    // ESTRUCTURA DE DATOS COMPLEJA PARA PERMISOS
    // Se utiliza una Colección (java.util.List) para alojar múltiples objetos de tipo String
    // correspondientes a los permisos dinámicos asignados a este objeto Usuario iterando sobre la Base de Datos.
    // =====================================================================
    
    /** 
     * Inicialización del atributo de colección.
     * Crea una instancia de lista vacía en memoria dinámica usando ArrayList (implementación concreta de la interfaz List).
     */
    private java.util.List<String> permisos = new java.util.ArrayList<>(); 

    /** Método accesor (Getter) que retorna la conexión orientada a objetos hacia la lista en memoria de permisos */
    public java.util.List<String> getPermisos() { // Retorna un objeto de tipo List
        return permisos; // Devuelve la referencia al objeto estático en memoria que contiene la lista
    }

    /** Método mutador (Setter) que inyecta en el objeto una lista completa pre-cargada con los permisos recuperados */
    public void setPermisos(java.util.List<String> permisos) { // Recibe como argumento una List genérica de tipo String
        this.permisos = permisos; // Sustituye la colección actual del objeto por la proporcionada mediante el parámetro
    }
    
    /**
     * Método lógico y de utilidad funcional en POO.
     * Itera (recorre) la colección de memoria interna 'permisos' evaluando un caso de uso particular.
     * 
     * @param nombrePermiso Cadena de texto correspondiente al nombre descriptivo a evaluar (ej: "VENTAS").
     * @return true si el objeto interno List contiene una coincidencia, false en caso contrario.
     */
    public boolean tienePermiso(String nombrePermiso) { // Firma del método, retorna un valor primitivo booleano
        if (permisos == null) return false;          // Validación temprana: Si el objeto puntero List es nulo, se previene NullPointerException y se retorna falso.
        for (String p : permisos) {                  // Bucle for-each (iterador sintáctico). Itera objeto por objeto 'p' (tipo String) perteneciente a la colección 'permisos'.
            if (p.equalsIgnoreCase(nombrePermiso)) { // Invocación a método estricto de la clase String: Compara el objeto iterado contra el parámetro omitiendo sensibilidad entre mayúsculas y minúsculas.
                return true;                         // Si hay coincidencia exacta de strings, finaliza la iteración y retorna verdadero.
            }
        }
        return false;                                // Si tras iterar completamente la colección no hubo retorno previo, se devuelve falso confirmando la ausencia de coincidencia.
    }
}
