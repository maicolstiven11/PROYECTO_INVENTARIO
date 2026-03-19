package com.inventario.model; // Instrucción que empaqueta y ubica al código en el subdirectorio del módulo model

/**
 * Modelo de datos: Clase DetallePedido (Entidad o POJO - Plain Old Java Object).
 * 
 * Capa de abstracción Orientada a Objetos para la estructura física de la tabla 'detalle_pedidos'.
 * Modela la cardinalidad "muchos" dentro de un objeto superior "PedidoProveedor", representando 
 * unitariamente un renglón o ítem individual solicitado a nivel de requerimiento de inventario.
 */
public class DetallePedido { // Inicia la declaración de la clase pública encapsulada

    // =====================================================================
    // ATRIBUTOS DE CLASE O ESTADOS PERSISTIDOS
    // Estructuras de datos puras que representan metadatos referenciales de almacenamiento.
    // =====================================================================

    private int idPedidoRegistro;     // Tipo primitivo de dato entero; llave primaria aislada que identifica este ítem en la transacción.
    private int idPedidoBase;         // Tipo primitivo entero usado como vinculación lógica en memoria al modelo agregado PedidoProveedor.
    private int idInvDetalle;         // Parámetro numérico relacional que asocia el ítem de pedido directamente con una variable de stock en DetalleInventario.
    private int cantidadPedida;       // Espacio de memoria de magnitud numérica para alojar la solicitud volumétrica o contable del renglón.
    private double precioUnitarioReal;// Atributo flotante en la memoria estática de la clase que documenta el costo estricto ponderado de la reposición.

    // =====================================================================
    // ATRIBUTOS TRANSIENTES O DEPENDENCIAS VIRTUALES
    // Reservas de memoria adicionales usadas puramente en el runtime del contenedor web.
    // =====================================================================

    private String nombreProducto;    // Ubicación de memoria tipo string para renderizar en vista el sustantivo del producto pedido.
    private double subtotalCalculado; // Variable de instancia de resolución matemática (cantidad * precio) con fines de formateo de datos.

    /**
     * CONSTRUCTOR POR OMISIÓN (Vacío)
     * Requisito de la Especificación de JavaBeans. Permite creación en tiempo de ejecución (Reflexión)
     * y el alojamiento dinámico en pila del objeto sin inicializar per se su espacio de propiedades.
     */
    public DetallePedido() { // Operador invocable 
    }

    /**
     * CONSTRUCTOR DE LLENADO RÁPIDO O SOBRECARGADO
     * Función paramétrica que agiliza la población completa de los datos de negocio en un solo hilo.
     * 
     * @param idPedidoRegistro ID transaccional propio
     * @param idPedidoBase ID general de la cabecera del requerimiento
     * @param idInvDetalle ID abstracto de dependencia del inventario
     * @param cantidadPedida Valor entero de porción requerida
     * @param precioUnitarioReal Valor decimal preciso asociado a monetización
     */
    public DetallePedido(int idPedidoRegistro, int idPedidoBase, int idInvDetalle, int cantidadPedida, double precioUnitarioReal) { // Firma con 5 parámetros tipados
        this.idPedidoRegistro = idPedidoRegistro;       // Uso de puntero explícito "this" para referenciar al campo persistido contra la variable formal
        this.idPedidoBase = idPedidoBase;               // Enlace lógico inter-componentes
        this.idInvDetalle = idInvDetalle;                // Set funcional del nexo al inventario modular
        this.cantidadPedida = cantidadPedida;            // Declaración intrínseca de conteo de elementos
        this.precioUnitarioReal = precioUnitarioReal;    // Ingesta monetaria decimal a nivel de campo asociado
    }

    // =====================================================================
    // METODOS ENCAPSULADORES PROTECTORES Y RESTAURADORES (Getters, Setters)
    // =====================================================================

    /** Accesor simple: retorna numéricamente el serial asignado para este ítem en su registro global */
    public int getIdPedidoRegistro() { // Retorno directo int
        return idPedidoRegistro; // Extracción local
    }

    /** Mutador unitario: ajusta dinámicamente o redefine el número de registro serial */
    public void setIdPedidoRegistro(int idPedidoRegistro) { // Parámetro numérico inyectable
        this.idPedidoRegistro = idPedidoRegistro; // Sobrescritura paramétrica
    }

    /** Accesor relacional: expone externamente a qué objeto matriz (PedidoProveedor) pertenece este detalle hijo */
    public int getIdPedidoBase() { // Retorna foránea del contexto
        return idPedidoBase;
    }

    /** Mutador estructural logic: Fija mediante inyección el puntero (id) referencial a toda la orden completa */
    public void setIdPedidoBase(int idPedidoBase) { // Argumento inyectable para orden general
        this.idPedidoBase = idPedidoBase; // Instancia su dependencia al padre
    }

    /** Accesor secundario relacional: Extrae el elemento atómico que vincula a un lote especifico de inventario (DetalleInventario) */
    public int getIdInvDetalle() { // Devuelve puntero relacional
        return idInvDetalle; // Exposición pura
    }

    /** Mutador dinámico: Asigna numéricamente el alias identificativo del cruce material sobre el inventario */
    public void setIdInvDetalle(int idInvDetalle) { // Toma integrador estructural
        this.idInvDetalle = idInvDetalle; // Acopla objeto en pila
    }

    /** Accesor de métrica física: Extrae iterativamente unidades tangibles requeridas para operaciones sumatorias */
    public int getCantidadPedida() { // Número primitivo devuelto
        return cantidadPedida; // Abstracción encapsulada
    }

    /** Mutador operacional físico: Alteración forzada pre-transaccional o de recuperación DAO para el flujo base numérico solicitado */
    public void setCantidadPedida(int cantidadPedida) { // Puntero al campo abstracto físico
        this.cantidadPedida = cantidadPedida; // Modificación transitoria
    }

    /** Accesor financiero atómico: Retorna el doble de escala flotante relativo a métricas monetarias referenciales de unidad */
    public double getPrecioUnitarioReal() { // Tipo real para moneda
        return precioUnitarioReal; // Salida asimétrica simple
    }

    /** Mutador relacional escalar: Enlaza contablemente a nivel software un coste transaccional derivado final de la vista padre */
    public void setPrecioUnitarioReal(double precioUnitarioReal) { // Asigna precio final evaluado
        this.precioUnitarioReal = precioUnitarioReal; // Absorción en instancia
    }

    /** Accesor derivado nominal: Consumo local de cadena para nombramiento genérico del artículo */
    public String getNombreProducto() { // Resolución visual amigable de puntero dinámico foráneo
        return nombreProducto; // String resuelto relacionalmente
    }

    /** Mutador auxiliar: Consigna variable textual dependiente procesada durante inicializaciones masivas en el controlador */
    public void setNombreProducto(String nombreProducto) { // Adquisición de texto estático descriptivo
        this.nombreProducto = nombreProducto; // Seteo del campo local ampliado
    }

    /** Accesor aritmético resolutivo: Extrae como abstracción independiente de la BD un valor cruzado total del registro base */
    public double getSubtotalCalculado() { // Devuelve estado procesado del ítem
        return subtotalCalculado; // Resultado temporal instanciable
    }

    /** Mutador de presentación matemática transiente: Acepta y registra en encriptado final computado antes de rendir salidas por interfaz Web. */
    public void setSubtotalCalculado(double subtotalCalculado) { // Recibe derivación ya calculada
        this.subtotalCalculado = subtotalCalculado; // Persistencia temporal in memory
    }
}
