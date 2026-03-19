package com.inventario.model; // Define el paquete lógico donde reside esta clase de modelo

import java.sql.Date; // Importa la clase de fecha de Java específica para mapear campos DATE de la base de datos MySQL

/**
 * Modelo de datos: Clase Producto (Entidad o POJO - Plain Old Java Object).
 * 
 * Esta clase es la representación en la Programación Orientada a Objetos 
 * de la tabla física 'producto' alojada en la base de datos relacional.
 * Actúa como una estructura de transporte de datos entre diferentes capas arquitectónicas.
 */
public class Producto { // Definición de la clase pública Producto

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Encapsulamiento de los estados de la entidad)
    // Cada atributo tiene una correlación directa funcional con una columna de la tabla.
    // =====================================================================

    private int idProducto;        // Atributo de tipo primitivo entero, mapea la Clave Primaria (PK) autogenerada del producto.
    private String nombre;         // Atributo de tipo objeto String, almacena la cadena de caracteres del nombre del producto.
    private String marca;          // Atributo de tipo objeto String, representa la marca comercial del producto.
    private double precioUnitario; // Atributo primitivo de doble precisión para representar el valor económico fraccionario (precio).
    private String tipo;           // Atributo de tipo String para almacenar la clasificación o categoría del producto.
    private String imagen;         // Atributo de tipo String que guarda la ruta simbólica o el de referencia del archivo de imagen.
    private Date fechaVencimiento; // Atributo instanciado con la clase Date (java.sql.Date) para representar el vencimiento del producto.
    private String cantidadMedida; // Atributo String empleado para definir descripciones de volumen o peso (p.ej: "750ml").

    /**
     * CONSTRUCTOR POR DEFECTO
     * Método de instanciación fundamental que permite asignar espacio en memoria dinámica 
     * a un objeto Producto en estado vacío, previo a la inyección de sus atributos mediante Setters.
     */
    public Producto() { // Declaración del constructor vacío
    }

    /**
     * CONSTRUCTOR SOBRECARGADO (Parametrizado)
     * Constructor avanzado que facilita inicializar la instancia de Producto con todos
     * sus estados cargados de forma inmediata en una sola instrucción de código.
     * 
     * @param idProducto Identificador principal numérico
     * @param nombre Cadena de texto correspondiente al nombre
     * @param marca Cadena representativa de la marca
     * @param precioUnitario Valor numérico decimal del precio
     * @param tipo Clasificación del artículo
     * @param imagen Cadena de ruta al archivo multimedia
     * @param fechaVencimiento Instancia de Date señalando la validéz del producto
     * @param cantidadMedida Descripción técnica de porción o medida
     */
    public Producto(int idProducto, String nombre, String marca, double precioUnitario, String tipo, String imagen, Date fechaVencimiento, String cantidadMedida) { // Implementación del constructor con firma extensa
        this.idProducto = idProducto;           // Resuelve ambigüedad y asocia el ID recibido al atributo propio de la instancia
        this.nombre = nombre;                   // Asigna la cadena recibida al atributo privado correspondiente
        this.marca = marca;                     // Asocia la marca inyectada por el invocador al miembro de la clase
        this.precioUnitario = precioUnitario;   // Inicializa el estado primitivo del atributo precio de esta instancia
        this.tipo = tipo;                       // Establece el atributo interno 'tipo' con la referencia proporcionada
        this.imagen = imagen;                   // Sobrescribe la referencia a la ruta multimedia en estado interno
        this.fechaVencimiento = fechaVencimiento; // Establece un puntero hacia la instancia de Date recibida en tiempo de ejecución
        this.cantidadMedida = cantidadMedida;   // Inyecta el texto descriptivo de proporción en el objeto actual
    }

    // =====================================================================
    // MÉTODOS ACCESORES (Getters) Y MUTADORES (Setters)
    // Constituyen la interfaz pública única admisible para inspeccionar (get) o alterar (set)
    // el estado de la información contenida en la clase protegida por el paradigma de encapsulamiento.
    // =====================================================================

    /** Accesor: Devuelve el identificador de tipo entero del producto */
    public int getIdProducto() { // Método público de lectura de la propiedad ID
        return idProducto; // Instrucción de retorno del miembro privado
    }

    /** Mutador: Establece un nuevo estado entero en la propiedad ID del producto */
    public void setIdProducto(int idProducto) { // Método público de inyección void (sin retorno)
        this.idProducto = idProducto; // Reemplazo condicional sin validación del identificador
    }

    /** Accesor: Obtiene la cadena de texto con el nombre configurado en instancia */
    public String getNombre() { // Método público de lectura de la propiedad Nombre
        return nombre; // Retorna la referencia al objeto String en memoria
    }

    /** Mutador: Define o reescribe el nombre descriptivo apuntado por el objeto actual */
    public void setNombre(String nombre) { // Método sin retorno, toma un String como inyector
        this.nombre = nombre; // Reemplaza la referencia local actual
    }

    /** Accesor: Retorna el nombre comercial de la marca asociada al objeto instanciado */
    public String getMarca() { // Declaración de método público
        return marca; // Retorna el miembro almacenado marca
    }

    /** Mutador: Reasigna el valor en texto sobre la marca vinculada a este producto */
    public void setMarca(String marca) { // Se provee función que recibe String
        this.marca = marca; // Setea en memoria la referencia
    }

    /** Accesor: Lee el escalar de punto flotante de doble abstracción matemática correspondiente al precio */
    public double getPrecioUnitario() { // Retorno de tipo primitivo de máquina
        return precioUnitario; // Expone exteriormente el valor
    }

    /** Mutador: Inicializa con un valor puramente numérico el estado primitivo del precio */
    public void setPrecioUnitario(double precioUnitario) { // Inyecta double recibido
        this.precioUnitario = precioUnitario; // Modifica registro lógico con el dato formal
    }

    /** Accesor: Consulta la semántica o categorización de la cadena de tipo */
    public String getTipo() { // Función pública con retorno en abstracción String
        return tipo; // Libera contexto sobre el dato encapsulado "tipo"
    }

    /** Mutador: Cambia el criterio de clasificación apuntando la memoria a nueva cadena String asignada */
    public void setTipo(String tipo) { // Toma parámetro lógico desde el llamador
        this.tipo = tipo; // Efectiviza el cambio asignando variable miembro
    }

    /** Accesor: Muestra la ubicación referenciada de recursos iconográficos como String */
    public String getImagen() { // Lectura desde exterior limitando mutabilidad directa
        return imagen; // Extrae atributo local
    }

    /** Mutador: Cambia el puntero local al camino (Path) del archivo de imagen procesado */
    public void setImagen(String imagen) { // Toma texto del path relativo multimedia
        this.imagen = imagen; // Sobrescritura en la asignación orientada
    }

    /** Accesor: Retorna la instancia de un objeto Date complejo contenido dentro del objeto global actual */
    public Date getFechaVencimiento() { // Expone variable tipo Date relacional
        return fechaVencimiento; // Retorna referencia
    }

    /** Mutador: Enlaza una nueva instancia de un objeto Date al registro temporal del producto */
    public void setFechaVencimiento(Date fechaVencimiento) { // Adopta argumento estructurado y no primitivo
        this.fechaVencimiento = fechaVencimiento; // Enlace del puntero en el atributo miembro actual
    }

    /** Accesor: Describe textualmente cualquier métrica dimensionada física (gramaje, volumetría) */
    public String getCantidadMedida() { // Devuelve puntero de Cadena de Caracteres
        return cantidadMedida; // Retorno base
    }

    /** Mutador: Acepta y sustituye texto detallando la magnitud métrica del elemento */
    public void setCantidadMedida(String cantidadMedida) { // Toma literal textual dimensionado
        this.cantidadMedida = cantidadMedida; // Ajuste directo del miembro paramétrico
    }
}
