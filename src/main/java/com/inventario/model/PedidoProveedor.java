package com.inventario.model; // Empaquetamiento de clase dentro del modelo lógico del proyecto

import java.sql.Date;       // Importación de dependencia utilitaria para la serialización del estado Date
import java.util.List;       // Herramienta base para trabajar con colecciones y objetos anidados

/**
 * Modelo de datos: Clase PedidoProveedor (Entidad o POJO - Plain Old Java Object).
 * 
 * Abstracción estricta en memoria de la tabla física 'pedidos_proveedor'.
 * Se concibe como una superestructura o "Entidad Fuerte" en la semántica del modelo relacional.
 * Actúa como encabezado de una agregación tipo uno-a-muchos (1:N) englobando 
 * colecciones de elementos transaccionales subyacentes (DetallePedido).
 */
public class PedidoProveedor { // Declaración encapsulada para la clase estructuradora pública

    // =====================================================================
    // ATRIBUTOS ENCAPSULADOS DE ENTIDAD (Persistencia y Mapeo Físico)
    // Instancian de manera unívoca o escalar las propiedades del bloque transaccional.
    // =====================================================================

    private int idPedidoBase;     // Clave primaria (PK) tipo primitivo entero auto-asignada, ancla central del objeto encabezado.
    private Date fechaPedido;     // Estado encapsulado Date. Referencia temporal de apertura instanciada por capa superior transaccional.
    private Date fechaEntrega;    // Proyección Date de estimación límite cronológica sujeta a mutabilidad y monitoreo externo.
    private double totalPedido;   // Dimensión decimal computada general que engloba (agregación en cascada) atributos subordinados financieros.
    private double subtotal;      // Fracción decimal financiera cruda estática almacenada independientemente del monto general a nivel clase base.
    private double ivaPedido;     // Elemento flotante fraccionario restrictivo de carga impositiva aplicable explícitamente a este objeto POJO.
    private int idInventario;     // Atributo posicional foráneo (FK) para relacionarse y reportar sobre el objeto Inventario activo padre global.
    private int idProveedor;      // Dependencia estructural de nivel foráneo (FK) fijada contra los registros maestros de los agentes o socios externos de negocio comerciales.

    // =====================================================================
    // ATRIBUTOS ESTRUCTURALES TRANSIENTES (Dependencias Lógicas Extendidas)
    // Complementos a la abstracción puros de modelo y no correspondientes de modo atómico-celular con el esquema SQL local puro.
    // =====================================================================

    private String nombreProveedor; // Componente alfanumérico pre-procesado, poblado transversalmente desde un LEFT o INNER JOIN con la clase proveedor abstracta de apoyo a UI View.
    private List<DetallePedido> detalles; // Colección de inyección de constructos de la capa inferior para completar y recorrer la estructura anidad de objetos en un ambiente de cascada.

    /**
     * CONSTRUCTOR POR DEFECTO
     * Provecha funcional en POO base, permitiendo a interfaces contenedoras la precarga asíncrona u ortogonal
     * del artefacto base con una huella mínima en la Virtual Machine previo a la saturación de data set.
     */
    public PedidoProveedor() { // Formulación y apertura nula genérica
    }

    /**
     * CONSTRUCTOR DE LLENADO MASIVO Y SOBRECARGADO
     * Expone una firma extensa inyectiva de requerimientos absolutos para modelar en sola instrucción atómica relacional sin usar colecciones.
     * 
     * @param idPedidoBase Id transaccional numérico serial de origen BD.
     * @param fechaPedido Mínimo cronológico base.
     * @param fechaEntrega Mínimo cronológico estimado o cierre.
     * @param totalPedido Monto macro del negocio en escala doble.
     * @param subtotal Componente primario atenuado decimal transaccional libre impuestos.
     * @param ivaPedido Impuesto evaluado dependiente aplicable puro.
     * @param idInventario Raíz o ancla a un espacio principal contable estático.
     * @param idProveedor Localizador primario general foráneo del emisor externo matriz.
     */
    public PedidoProveedor(int idPedidoBase, Date fechaPedido, Date fechaEntrega, double totalPedido, double subtotal, double ivaPedido, int idInventario, int idProveedor) { // Firma estructural inicial fuerte
        this.idPedidoBase = idPedidoBase;   // Apropiamiento atómico 
        this.fechaPedido = fechaPedido;     // Paso a variable intrínseca
        this.fechaEntrega = fechaEntrega;   // Asimilación paramétrica externa a local.
        this.totalPedido = totalPedido;     // Absorbe computos matemáticos generados
        this.subtotal = subtotal;           // Restablece estado inicial en RAM
        this.ivaPedido = ivaPedido;         // Restaura inyección local en su scope
        this.idInventario = idInventario;   // Amarra cardinalidad 
        this.idProveedor = idProveedor;     // Define sujeta de emisión del archivo
    }

    // =====================================================================
    // BLOQUE DE COMPORTAMIENTO LÓGICO Y ESTRUCTURAL: GETTERS / SETTERS
    // Subrutinas o Métodos Funcionales orientadas a preservar estado en encapsulamiento y modularidad.
    // =====================================================================

    /** Accesor descriptivo base simple entero (Int/PK) para anclar en cascada */
    public int getIdPedidoBase() { // Extracción nominal en base primitivo
        return idPedidoBase; // Entrega el folio atómico
    }

    /** Inyecta internamente o inicializa variable id de forma unitaria desde DAO u otros. */
    public void setIdPedidoBase(int idPedidoBase) { // Acopla o reconstruye referencial internamente.
        this.idPedidoBase = idPedidoBase; // Seteador local.
    }

    /** Emisión abstracta desde envoltura a peticiones externas controladoras usando Date temporal. */
    public Date getFechaPedido() { // Permite manipulación u observaciones condicionales.
        return fechaPedido; // Extrae envoltorio SQL time.
    }

    /** Reconfigura u orienta con variables directas transientes la fecha cronológica paramétrica temporal. */
    public void setFechaPedido(Date fechaPedido) { // Obliga reconstruir contexto por parte del Servlet.
        this.fechaPedido = fechaPedido; // Aplica objeto precreado temporal.
    }

    /** Recibe petición temporal sobre finalizaciones formales emitiendo Date al exterior capa. */
    public Date getFechaEntrega() { // Consulta simple posicional iterada
        return fechaEntrega; // Derivado simple directo a invocador.
    }

    /** Condicionante dinámico interrelacional forzado por el control Servlet o DAO para asentar meta data. */
    public void setFechaEntrega(Date fechaEntrega) { // Parámetro envolvente restrictivo
        this.fechaEntrega = fechaEntrega; // Set base local intrínseca referenciada a this.
    }

    /** Proyecta una suma encapsulada atómica decimal que enmascara los totales base derivados */
    public double getTotalPedido() { // Escapa valores flotantes dobles restrictivos.
        return totalPedido; // Exporta primitivo puro no relacional
    }

    /** Escribe explícitamente el sumatorio abstracto de iteraciones subyacentes referidas internamente en objeto instanciado pre DAO. */
    public void setTotalPedido(double totalPedido) { // Aceptador matemático derivado.
        this.totalPedido = totalPedido; // Ancla la sumatoria instanciando una variable primitiva final libre.
    }

    /** Método funcional: Desvincula del monto maestro general extrayendo sólo los valores primitivos elementales netos calculados numéricos */
    public double getSubtotal() { // Saca abstracción intermedia o paso paramétrico en RAM.
        return subtotal; // Referencia cruda
    }

    /** Inserta y preconfigura de modo estructurado la data en RAM desde formularios parseados sin base impositiva para posterior guardado  */
    public void setSubtotal(double subtotal) { // Componente doble de coma flotante asignable puro.
        this.subtotal = subtotal; // Modifica registro con puntero local cerrado.
    }

    /** Funcionalidad que aísla de interdependencias numéricas y cede solo la cuantificación impositiva al receptor primitivo en la llamada */
    public double getIvaPedido() { // Lectura doble atómica pura 
        return ivaPedido; // Expresa encapsulamiento crudo
    }

    /** Impone con fuerza directa al componente un gravamen en espacio RAM con flotante decimal derivando su uso futuro en cálculos relacionales formales.*/
    public void setIvaPedido(double ivaPedido) { // Absorción explícita de valor de impuesto
        this.ivaPedido = ivaPedido; // Seteo con pointer para aislamiento.
    }

    /** Retrolocaliza y transige devolviendo su identidad anidado, exponiendo FK o folio numérico del recinto que enmarca este objeto Pedido. */
    public int getIdInventario() { // Vinculación cruzada primitiva simple.
        return idInventario; // Revela alias de contenedor físico transaccional 
    }

    /** Fija en su encapsulamiento o coraza referencial, a qué ID maestro en la base referenciadora debe tributar y/o depender el objeto hijo.*/
    public void setIdInventario(int idInventario) { // Relacional transaccional asimétrica
        this.idInventario = idInventario; // Sella el apuntador intermedio en la estructura.
    }

    /** Expide de su protección encapsulada en espacio de RAM, su id origen para que subsistemas filtren en colecciones asociadas (BD u objetos DAO)  */
    public int getIdProveedor() { // Lectura auxiliar foránea 
        return idProveedor; // Reflejo directo de indexado numérico asociado externo.
    }

    /** Fija explícita y asimétricamente un identificador externo para inter-vincular al objeto a sus generadores en BD maestro. */
    public void setIdProveedor(int idProveedor) { // Absorbe puntero int para inyección interna transiente
        this.idProveedor = idProveedor; // Válida y sobre escribe.
    }

    /** Presenta nominalmente como recurso semántico agregado el nombre humano asociado inyectado relacionalmente hacia presentadores UI (Visión MVC). */
    public String getNombreProveedor() { // Exposición no base de DB pero si transiente 
        return nombreProveedor; // Texto de nombre temporal pre consultado 
    }

    /** Mimetiza o injerta el descriptor String estático al registro tras hacer lecturas extendidas (Join relacional manual) a otros POJO/Entidades. */
    public void setNombreProveedor(String nombreProveedor) { // Recibe instanciación semántica extraña 
        this.nombreProveedor = nombreProveedor; // Enlace directo local para iteración gráfica
    }

    /** Extrae como una composición dependiente encapsulada de modelo "todo-parte" (Array/List) sus componentes moleculares en forma de objetos independientes anidados. (Composición POO) */
    public List<DetallePedido> getDetalles() { // Getter de tipo envoltorio colección list de sub-entidades modelo 
        return detalles; // Retorno de objeto múltiple en cascada de nivel secundario POJO .
    }

    /** Recibe internamente, procesado o inyectado desde el DAO como puente MVC, listas dinámicas (Array) en cascada de ítems subordinados acoplados para referenciar internamente toda la relación de base BD directamente a la interface web en una instancia única y consolidada de Pedido genérico general (Colección orientada a objetos fuerte jerarquía estructurada multinivel agregadora general). */
    public void setDetalles(List<DetallePedido> detalles) { // Inserta listado array
        this.detalles = detalles; // Estructura jerárquicamente atada y asimilada.
    }
}
