package com.inventario.model; // Declaración estricta de ruta lógica para empaquetamiento del artefacto POJO

/**
 * Modelo de datos: Clase Negocio (Entidad o POJO - Plain Old Java Object).
 * 
 * Capa de abstracción Orientada a Objetos para la estructura matriz u originadora de la tabla 'negocio'.
 * Se yergue como clase rectora principal top-level que sirve como pilar fundacional jerárquico. 
 * Sin esta entidad (Negocio), los objetos iterativos descendentes como Inventarios o Usuarios 
 * vinculados no sostienen validez referencial dentro de la arquitectura global.
 */
public class Negocio { // Se instancia la declaración de la clase como abstracción pública accesible.

    // =====================================================================
    // ATRIBUTOS ENCAPSULADOS PRINCIPALES MATEADOS (Persistencia)
    // Conforman los datos base crudos que transitan desde el núcleo al disco persistido.
    // =====================================================================

    private int idNegocio;                   // Llave maestra autogenerada (PK) tipo int que asegura la trazabilidad atómica irrepetible global para esta instancia comercial física.
    private String nombre;                   // Atributo estático textual String donde reposa la identificación referencial nominal visible del modelo a usuarios iterativos.
    private String direccion;                // Objeto interno primitivo de caracteres que provee descripción técnica local abstracta o locación real del contenedor "negocio".
    private String estado;                   // Componente string bandera preformada ('activo', 'inactivo') usado para deshabilitar en RAM un set masivo descendente sin borrados severos (soft logic).
    
    // =====================================================================
    // ATRIBUTOS ESTRUCTURALES TRANSIENTES (No dependientes base a base)
    // Variables bandera implementadas mediante inyección paramétrica extra de DAO para modelado GUI avanzado.
    // =====================================================================
    
    private boolean tieneInventarioActivo;   // Booleano analítico calculado al vuelo in-situ, dictaminando en true o false la presencia dependiente referencial del set inferior activo en momento de render.

    /**
     * CONSTRUCTOR POR DEFECTO
     * Provecha funcional en POO base, permitiendo a interfaces contenedoras la precarga asíncrona u ortogonal
     * del artefacto base con una huella mínima en la Virtual Machine previo a la saturación de data set.
     */
    public Negocio() { // Formulación y apertura nula base genérica 
    }

    /**
     * CONSTRUCTOR RECARGADO EXPLÍCITO O DE LLENADO MASIVO Y SIMULTÁNEO
     * Funcional paramétrica que expone en sola instrucción los requisitos para modelar por completo un objeto pesado
     * garantizándolo óptimo al mapear y procesar cursores extensivos de un ResultSet origen base relacional.
     * 
     * @param idNegocio Valor numérico serial principal
     * @param nombre Sustantivo del objeto negocio
     * @param direccion Señal de puntero espacial físico descriptivo
     * @param estado Variable switch textual orientadora de status integral
     */
    public Negocio(int idNegocio, String nombre, String direccion, String estado) { // Constructor extendido por argumentos discretos
        this.idNegocio = idNegocio;   // Transición de flujo por pointer 'this' fijando valor al espacio memoria del miembro local.
        this.nombre = nombre;         // Toma el string literal de instancia formal a parámetro intrínseco.
        this.direccion = direccion;   // Enlace string análogo a campo propio instanciado.
        this.estado = estado;         // Sobrescritura paramétrica directa de enmascarador en switch flag "estado".
    }

    // =====================================================================
    // BLOQUE DE COMPORTAMIENTO LÓGICO Y ESTRUCTURAL: GETTERS / SETTERS
    // Interface de operación restringida que permite manipular las aristas encapsuladas.
    // =====================================================================

    /** Accesor simple numérico: Retorna de forma segura extrayendo el primitivo identificador llave local. */
    public int getIdNegocio() { // Invocación sin manipulación externa requerida
        return idNegocio; // Tránsito y devolución escalar interna
    }

    /** Mutador unitario: Fuerzas o inicializas tardíamente referencias claves a este bloque desde librerías controladoras relacionales. */
    public void setIdNegocio(int idNegocio) { // Aceptador en cascada descendente atómica
        this.idNegocio = idNegocio; // Traspasa inyector primitivo al campo protegido
    }

    /** Acceso descriptivo base: Retorna a peticionario un encapsulado String conteniendo cadena visual descriptora base del modelo */
    public String getNombre() { // Acceso literal genérico 
        return nombre; // Truncado y retornado string directo
    }

    /** Mutador literal integrador: Impone al objeto transiente o persistido una nueva descripción formal en abstracción léxica */
    public void setNombre(String nombre) { // Parámetro texto puro para asignar
        this.nombre = nombre; // Adopción orientada interna al campo dependiente general
    }

    /** Accesor descriptivo auxiliar: Exposición forzosa para consumos transversos o de perfilamiento del registro donde habita un string locativo. */
    public String getDireccion() { // Retrolimpia y exuda a salida la cadena asociada 
        return direccion; // Entrega estática amarrada
    }

    /** Mutador auxiliar espacial: Inscribe paramétricamente la carga de texto a nivel local antes de asentar rutinas de guardado a nivel backend. */
    public void setDireccion(String direccion) { // String portador paramétrico base
        this.direccion = direccion; // Adaptador estático final
    }

    /** Consulta del status lógico virtual del estado paramétrico (flag activo-apagado): Determina ramificaciones MVC condicionales sin re-consumo generalizado. */
    public String getEstado() { // Entrega orientadora atómica polimórfica (pseudobooleana embebida en String)
        return estado; // Trasmitida a flujos visualizadores y lógicos externos 
    }

    /** Modificador switch maestro: Revierte forzosamente comportamientos o flags de intercepción estática al inyectar lógicas "inactivas" truncando funcionamientos macro */
    public void setEstado(String estado) { // Sustituto léxico condicionador restrictivo 
        this.estado = estado; // Asume y sobre impone a instancia original 
    }

    /** 
     * Funcionalidad lógica de comprobación atípica o extra-transiente a BD (pseudo-relacional proxy).
     * Devuelve una confirmación primitivo-booleana determinista forjada por integraciones foráneas 
     * en DAOs con alcance multi-nivel en vez de ser embebida natural. 
     * @return booleano estricto indicando validación superior.
     */
    public boolean isTieneInventarioActivo() { // Llamado getter convencional mutado para literales boolean de estándar JavaBean ("is")
        return tieneInventarioActivo; // Exporta validación en formato bits estricto.
    }

    /** Seteo de validación condicionada o resolutivo virtual: Procesa y sella in situ respuestas lógicas originadas externamente y validadas transaccionalmente por otros objetos DAO. */
    public void setTieneInventarioActivo(boolean tieneInventarioActivo) { // Variable determinística de inyección
        this.tieneInventarioActivo = tieneInventarioActivo; // Asume variable de decisión precomputada globalmente
    }
}
