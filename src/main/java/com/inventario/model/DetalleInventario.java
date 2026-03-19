package com.inventario.model; // Define el paquete lógico donde reside esta clase de modelo

/**
 * Modelo de datos: Clase DetalleInventario (Entidad o POJO - Plain Old Java Object).
 * 
 * Representa la abstracción orientada a objetos de la tabla física 'inventario_detalle'.
 * Esta entidad actúa como un componente asociativo o tabla puente entre 'Inventario' y 'Producto'.
 * Su propósito es encapsular el estado dinámico del stock disponible para cada producto
 * en un contexto de inventario determinado gestionando su cantidad a lo largo del tiempo.
 */
public class DetalleInventario { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRINCIPALES DE ENTIDAD (Estado persistente)
    // Estos atributos mapean unívocamente a columnas en la base de datos relacional.
    // =====================================================================

    private int idDetalle;         // Atributo primitivo entero para la Clave Primaria (PK). Identificador único irrepetible del registro.
    private int idInventario;      // Atributo entero que funciona como Clave Foránea (FK). Vincula la instancia con un objeto Inventario padre.
    private int idProducto;        // Atributo entero como Clave Foránea (FK). Vincula este detalle con un objeto Producto específico.
    private double cantidadInicial;// Atributo de coma flotante que registra numéricamente el caudal de stock cargado inicialmente en memoria.
    private double cantidadFinal;  // Atributo de coma flotante que almacena estáticamente la valoración del stock al procesar el cierre o auditoría del inventario.

    // =====================================================================
    // ATRIBUTOS AUXILIARES DE PROYECCIÓN (Estado volátil/extendido)
    // No existen como columnas directas en la tabla principal, pero se inyectan 
    // en la instancia tras realizar consultas compuestas (JOINs) en el DAO para uso en la Vista.
    // =====================================================================

    private String nombreProducto; // Atributo tipo String para almacenar temporalmente la resolución textual del idProducto.
    private double precioUnitario; // Atributo numérico temporal que hereda el valor económico del objeto Producto asociado.

    /**
     * CONSTRUCTOR POR DEFECTO
     * Método indispensable para inicializar el objeto en memoria dinámica de manera vacía.
     * Facilita al patrón Data Access Object (DAO) instanciar el objeto e hidratar sus propiedades
     * a posteriori invocando sus métodos mutadores (Setters).
     */
    public DetalleInventario() {} // Invocación sin argumentos

    // =====================================================================
    // MÉTODOS ACCESORES Y MUTADORES (Getters / Setters)
    // Definen el contrato público para alterar los atributos encapsulados de forma controlada.
    // =====================================================================

    /** Accesor: Retorna el identificador principal numérico autogenerado de la clase asociativa */
    public int getIdDetalle() { return idDetalle; }

    /** Mutador: Asigna o sobrescribe el número identificativo asociado a esta instancia de detalle */
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    /** Accesor: Lee el número de clave foránea correspondiente a la entidad Inventario dueña */
    public int getIdInventario() { return idInventario; }

    /** Mutador: Aplica lógicamente la relación asociando este objeto con un ID de inventario válido */
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    /** Accesor: Resuelve qué identificador de material de Producto está amarrado a este detalle */
    public int getIdProducto() { return idProducto; }

    /** Mutador: Enlaza paramétricamente la existencia de este objeto con el identificador de un Producto */
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    /** Accesor: Evalúa y expone mediante retorno la variable de magnitud del balance general de stock disponible */
    public double getCantidadInicial() { return cantidadInicial; }

    /** Mutador: Inserta el volumen o cantidad teórica del artículo en las bóvedas de la clase */
    public void setCantidadInicial(double cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    /** Accesor: Devuelve la cifra documentada del inventario final para métricas y cuadres de caja o stock */
    public double getCantidadFinal() { return cantidadFinal; }

    /** Mutador: Escribe la constante de cierre computada del comportamiento estático del producto tras las ventas */
    public void setCantidadFinal(double cantidadFinal) { this.cantidadFinal = cantidadFinal; }

    // =====================================================================
    // GETTERS Y SETTERS PROYECTADOS (Variables inyectadas desde capa DAO)
    // =====================================================================

    /** Accesor auxiliar: Retorna la cadena de texto con el nombre descriptivo del material interceptado por el JOIN */
    public String getNombreProducto() { return nombreProducto; }

    /** Mutador auxiliar: Carga en memoria la cadena de texto representativa del alias del Producto */
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    /** Accesor auxiliar: Emite el componente de valor unitario transitorio de la abstracción */
    public double getPrecioUnitario() { return precioUnitario; }

    /** Mutador auxiliar: Consigna un costo fijo atómico dentro del envoltorio para propósitos de despliegue aritmético */
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
