package com.inventario.model; // Instrucción que empaqueta y ubica al código en el subdirectorio del módulo model

import java.sql.Date; // Importación para manejar la instancia de fecha mapeada al gestor de base de datos MySQL

/**
 * Modelo de datos: Clase Gasto (Entidad o POJO - Plain Old Java Object).
 * 
 * Capa de abstracción Orientada a Objetos para la estructura física de la tabla 'gasto_diario'.
 * Funciona como unidad de transporte temporal que representa estáticamente en memoria 
 * un registro transaccional transitorio (egreso) que se contabiliza como pérdida operativa asociada a un entorno mayor (Inventario).
 */
public class Gasto { // Declaración encapsulada para la clase modelo

    // =====================================================================
    // ATRIBUTOS DE CLASE O ESTADOS PERSISTIDOS
    // Estructuras de propiedades que mimetizan exactamente columnas o tuplas físicas en BD.
    // =====================================================================

    private int id_gastos;       // Llave primaria auto-escalable (PK) que proporciona identidad única indiscutible al objeto gasto.
    private int id_inventario;   // Llave foránea (FK), punto de anclaje relacional que supedita la validez funcional a la existencia de un inventario maestro activo temporal.
    private int cantidad;        // Atributo primitivo entero donde reside el conteo abstracto de ítems unitarios aplicables al egreso.
    private Date fecha;          // Componente envolvente Date referencial orientado a rastrear la dimensión temporal del suceso contable.
    private Double subtotal;     // Abstracción numérica fraccionaria basada en objetos Wrappers que consolida el flujo saliente monetario originado por la sumatoria atómica.
    private String descripcion;  // Atributo de tipo texto descriptivo libre (String) donde se documenta la naturaleza alfanumérica cualitativa que justificó la operación.

    /**
     * CONSTRUCTOR POR DEFECTO (Vacío)
     * Constructor inherente. Método fundamental que provee memoria base sin estado inicial forzoso.
     * Facilita al contenedor crear el objeto en memoria desde los ResultsSets o Formularios 
     * antes de someterlo a iteración poblando sus atributos a nivel unitario mediante getters/setters.
     */
    public Gasto() { // Inicializador dinámico vacío
    }

    // =====================================================================
    // METODOS ENCAPSULADORES PROTECTORES Y RESTAURADORES (Getters, Setters)
    // Conforman la única interfaz que cumple con el principio de ocultación y abstracción para el estado del POJO.
    // =====================================================================

    /** Accesor simple: Exposición controlada en solo lectura lógica del identificador maestro de base de datos */
    public int getId_gastos() { // Firma de lectura directa
        return id_gastos; // Retorno de variable privada
    }

    /** Mutador unitario: Permite al patrón Activo/DAO modificar restrictivamente o inyectar ID en las rutinas de lectura en bulk */
    public void setId_gastos(int id_gastos) { // Setter de inyección atómica
        this.id_gastos = id_gastos; // Afectación referencial usando auto-puntero 'this'
    }

    /** Accesor relacional: Identifica a qué grupo generalizado y efímero de control perimetral (inventario) tributa financieramente este elemento */
    public int getId_inventario() { // Lectura auxiliar foránea
        return id_inventario; // Obtención simple
    }

    /** Mutador estructural inter-objetos: Transfiere a la memoria este objeto la firma numérica (ID) de su clase gobernante para garantizar la atomicidad en un JOIN o INSERT futuro */
    public void setId_inventario(int id_inventario) { // Argumento inyectivo
        this.id_inventario = id_inventario; // Fija o corrige relacionalidad funcional 
    }

    /** Consulta volumétrica: Evalúa la escala atómica ingresada correspondiente al factor de repetición del egreso descripto */
    public int getCantidad() { // Método de extracción primitivo entero
        return cantidad; // Resolución directa al llamador
    }

    /** Modificación aritmética: Procesa y guarda como valor nominal puramente transitorio el input de cantidad antes de persistir la operación en el motor */
    public void setCantidad(int cantidad) { // Método de manipulación paramétrica
        this.cantidad = cantidad; // Sobrescribe con input recibido
    }

    /** Accesor temporal: Genera como respuesta el envoltorio Date que encuadra y posiciona localmente al objeto dentro de loggers de eventos o reportes cronológicos */
    public Date getFecha() { // Firma con respuesta estructurada de objeto relacional
        return fecha; // Devolución de instancia compleja instanciada 
    }

    /** Mutador temporal: Intercepta y ajusta el campo apuntador de fecha vinculando explícitamente al registro la dimensión día generada en controladores */
    public void setFecha(Date fecha) { // Adaptador o recibidor dinámico general de la librería util.java o equivalente sql
        this.fecha = fecha; // Vínculo duro estático post inicialización
    }

    /** Accesor financiero atómico: Obtiene la fracción derivada en envoltorio Objeto Wrapper 'Double' pre calculada para salidas u operaciones sumatorias de agregación local */
    public Double getSubtotal() { // Entrega orientada a objetos usando clase envolvente Float extendida
        return subtotal; // Referencia compartida
    }

    /** Seteo paramétrico computacional: Absorbe internamente valores de moneda generados por la GUI limitados o no en rango primitivo flotante */
    public void setSubtotal(Double subtotal) { // Recepción genérica polimórfica (Wrapper/primitivo)
        this.subtotal = subtotal; // Anula la huella anterior referenciando paramétricamente a la nueva en memoria
    }

    /** Accesor derivado nominal: Consumo y liberación del arreglo textual ingresado manualmente que le da sentido orgánico o cualitativo a la entidad abstracta (¿Para qué se gastó?) */
    public String getDescripcion() { // Firma y tipo String compatible 
        return descripcion; // Flujo inverso de lectura de memoria persistente o temporal
    }

    /** Mutador auxiliar de justificación: Inyecta semántica humana almacenada estáticamente a lo largo de las transacciones seriales Web y MVC antes del commit transaccional  */
    public void setDescripcion(String descripcion) { // Adquisidor paramétrico inyectable simple sin parsing 
        this.descripcion = descripcion; // Persistencia de modificación de carácter contextual
    }
}
