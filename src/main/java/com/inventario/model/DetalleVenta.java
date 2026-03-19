package com.inventario.model; // Instrucción que declara la ubicación lógica jerárquica de la clase dentro del modelo

/**
 * Modelo de datos: Clase DetalleVenta (Entidad o POJO - Plain Old Java Object).
 * 
 * Interfaz orientada a objetos que encapsula fielmente la tupla física 'detalle_venta'.
 * Funciona como unidad atómica e independiente para manejar conceptualmente un ítem 
 * vendido de forma intrínseca dentro del agregado raíz (Venta), gestionando la relación de objetos.
 */
public class DetalleVenta { // Se genera la firma estructural y la clase principal pública e instanciable

    // =====================================================================
    // ATRIBUTOS ENCAPSULADOS PRINCIPALES MATEADOS (Persistencia)
    // Reservas de variables discretas limitadas a modelar columnas puras subyacentes.
    // =====================================================================

    private int idDetalleVenta;   // Atributo variable primitivo empleado para retener la llave maestra autonumérica del ítem de la transacción.
    private int idVenta;          // Atributo relacional estricto; llave interviniente que enlaza compositivamente este objeto con su Entidad matriz u objeto anfitrión 'Venta'.
    private int idInvDetalle;     // Componente de enlace numérico para aludir transversalmente al objeto padre o bloque físico dentro del área de inventarios (InventarioDetalle).
    private int cantidad;         // Valor de tipo entero asignado a contabilizar explícitamente el volumen despachado a consumidor.

    private double subtotal;      // Parámetro monetario de punto decimal flotante evaluado matemáticamente en proceso de compra.

    // =====================================================================
    // PROPIEDADES TRANSIENTES COMPLEMENTARIAS
    // Estados internos auxiliares asignados y mantenidos puramente en la memoria RAM 
    // del framework contenedor para facilitar transporte y despliegue hacia la capa de presentacion (View).
    // =====================================================================

    private String nombreProducto; // Fragmento de texto encapsulado rellenado de forma tardía a modo alfanumérico para exponer el nombre legible sin doble consulta.
    private int idProducto;        // Auxiliar referencial numérico extraído dinámicamente de contexto para facilitar iteraciones en colecciones antes de persistir.

    /**
     * CONSTRUCTOR POR DEFECTO
     * Método inicializador en sobrecarga nula exigido implícitamente por esquemas como Java Beans
     * permitiendo de forma controlada la instanciación tardía de componentes a priori a su relleno con setters.
     */
    public DetalleVenta() { // Clausula base para memoria dinámica libre sin firmas inyectadas.
    }

    // =====================================================================
    // BLOQUE DE COMPORTAMIENTO BASAL: GETTERS / SETTERS
    // Subrutinas o Métodos Funcionales expuestos orientados únicamente a permitir 
    // y gobernar la alteración de estado de las variables miembro protegiendo el Scope de Clase.
    // =====================================================================

    /** Método accesor: Dispone externamente el acceso restrictivo al número de folio interno transaccional */
    public int getIdDetalleVenta() { // Firma y tipo entero 
        return idDetalleVenta; // Emite el miembro
    }

    /** Método mutador: Reevaluación forzosa del identificador único atómico insertado o recuperado de origen */
    public void setIdDetalleVenta(int idDetalleVenta) { // Aislamiento estructural y asignación posicional obligatoria
        this.idDetalleVenta = idDetalleVenta; // Alteración o fijación interna paramétrica
    }

    /** Acceso relacional: Recupera el alias primitivo entero referencial sobre el objeto mayor del agregado de la compra */
    public int getIdVenta() { // Descubre índice relacional intermedio
        return idVenta; // Evoca miembro de persistencia atada
    }

    /** Mutador estructural interobjetos: Establece y unifica sistémicamente local cual entidad Venta (padre) encubre o contiene a este dependiente */
    public void setIdVenta(int idVenta) { // Interfaz consumible para anclaje a cabeceras maestras
        this.idVenta = idVenta; // Anclaje efectivo
    }

    /** Acceso paramétrico: Dispone ante las capas la ubicación virtual asociada como ítem físico dentro de recintos de almacén local. */
    public int getIdInvDetalle() { // Cede primitivo enlace a la dependencia externa
        return idInvDetalle; // Devuelve la abstracción indexada
    }

    /** Inyección transversal relacional: Acopla a este constructo las métricas de ID asociadas al detalle temporal inventariado */
    public void setIdInvDetalle(int idInvDetalle) { // Seta apuntador foráneo integral en memoria propia temporal
        this.idInvDetalle = idInvDetalle; // Acople primitivo directo
    }

    /** Consulta volumétrica: Evalúa e infiere el escalar abstracto para dimensionar numéricamente componentes de venta despachados. */
    public int getCantidad() { // Firma sin argumentos externa
        return cantidad; // Extraído y presentado
    }

    /** Modificación aritmética local: Asienta lógicamente, puramente como entidad de transporte, la magnitud nominal que rige en esta sola fila. */
    public void setCantidad(int cantidad) { // Recepciona un numérico procesable atómico
        this.cantidad = cantidad; // Ajuste directo dimensional de consumo transitorio 
    }

    /** Accesor aritmético y financiero: Obtiene la fracción derivada como producto cruzado evaluado por cada registro en línea de facturación. */
    public double getSubtotal() { // Emite tipo complejo decimal de alta precisión referida a precios.
        return subtotal; // Retorno primitivo escalado
    }

    /** Seteo paramétrico derivado computacional: Almacena precomputacionalmente valores netos locales de cada bloque singular vendido. */
    public void setSubtotal(double subtotal) { // Recepciona variables de computo flotante
        this.subtotal = subtotal; // Anula o aplica monto base al elemento actual
    }

    // =====================================================================
    // SECCIÓN DE GETTERS Y SETTERS EXTENDIDOS (Mnemotécnicos/Composicionales)
    // =====================================================================

    /** Retorno de alias literario complementario: Extracción amigable nominal visual provisto por colecciones enriquecidas. */
    public String getNombreProducto() { // Firma encapsulada amigable
        return nombreProducto; // Exposición pura de String instanciado temporal
    }

    /** Abastecimiento verbal para capa View: Registra pasivamente identificaciones abstractas asociando el elemento del bloque a cadenas mnemotécnicas directas. */
    public void setNombreProducto(String nombreProducto) { // Adopción y vinculación nominal de tipo texto simple 
        this.nombreProducto = nombreProducto; // Consumo inyectable final
    }

    /** Consulta local referencial primitiva: Resuelve y proporciona métricas de enlace origen sobre las familias base (Producto master) de catálogo. */
    public int getIdProducto() { // Lectura auxiliar de puente
        return idProducto; // Proyección primitiva
    }

    /** Puntero auxiliar interno en rampa de vuelo MVC: Estipula una huella abstracta local inyectada anticipada para facilitar la resolución inter-esquemas (DB). */
    public void setIdProducto(int idProducto) { // Consumo temporal del identificador global
        this.idProducto = idProducto; // Anclaje base relacional transitorio para búsquedas y parseos dinámicos en lista de carrito
    }
}
