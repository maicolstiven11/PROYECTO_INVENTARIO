package com.inventario.model; // Instrucción funcional de enrutamiento o package para vincular modelo POJO.

import java.sql.Date; // Referencia central base utilitaria relacional tipo SQL en encapsulamientos estéticos temporales.

/**
 * Modelo de datos: Clase Venta (Entidad o POJO - Plain Old Java Object).
 * 
 * Abstracción de encapsulado general en memoria correspondiente a la tabla transaccional origen 'Venta'.
 * Modela un objeto envolvente principal fuerte (cabeza), cuya naturaleza compositiva abarca o enlaza numéricamente
 * y por referenciación foránea una superposición iterativa u orquestación de sub-componentes débiles adjuntos (DetalleVenta) logrando consolidación de operación general o "factura web virtual".
 */
public class Venta { // Declaración matriz genérica

    // =====================================================================
    // ATRIBUTOS PRIVADOS ENCAPSULADOS PRINCIPALES MATEADOS (Persistencia)
    // Conformadores atómicos en analogía estricta de variables en BD asociadas.
    // =====================================================================

    private int idVenta;        // Serialización auto escalada tipo variable primitivo entero, anclaje relacional e indispensable del propio objeto. 
    private int idInventario;   // Objeto primitivo entero interviniente en memoria; llave externa asociativa fuerte para tributar y confinar su validez o sub-entidad a un super-macro activo general en estado Activo en BD.
    private double totalVenta;  // Propiedad decimal envoltura de primitiva numérico en punto flotante atada relacionalmente de forma asimétrica para cálculos acumulativos netos computados localmente como general transaccional.
    private Date fechaVenta;    // Estructura Data de referencia o clase enlazable base que fija inamovible la línea cruzada cronológica que generó su concepción lógica base transiente o de red.

    /**
     * CONSTRUCTOR POR DEFECTO BASE NULA
     * Pre-instancia o modelaje ortogonal nativo y por omisión (sin argumentos forzosos) que ampara, desde el inicio base en JVM estricta o coleccionados iterados masivos DAO como List, a la entidad referenciable.
     */
    public Venta() { // Llama el apuntador inicializado sin carga relacional de atributos para su construcción MVC post.
    }

    // =====================================================================
    // Interface Protegida base y Modular de Rutinas Funcionales: GETTERS Y SETTERS
    // Control exclusivo sobre la arquitectura y la accesibilidad interna, blindando su modelado inicial instanciado ante capas adyacentes lógicas.
    // =====================================================================

    /** Accesor descriptivo base simple entero (Int/PK) para anclar reportes u otros objetos */
    public int getIdVenta() { // Extrae identificador serial atómico relacional.
        return idVenta; // Puntero inyectado y expuesto nativo int.
    }

    /** Inyecta internamente o sobrepone de modo local referenciado la variable transaccional identificatoria maestra general id. */
    public void setIdVenta(int idVenta) { // Aceptador con argumentación requerida in-situ paramétrica 
        this.idVenta = idVenta; // Operación reestructuradora y transitoria estricta relacional cruzada apuntando local in-memory.
    }

    /** Acceso relacional: Recupera el escalar o alias primitivo referencial hacia inventarios maestros temporales u operacionales fijos. */
    public int getIdInventario() { // Evoca local el parámetro para filtrar
        return idInventario; // Retorno en simple inercia sin transformaciones.
    }

    /** Modificador de pertenencia asociativa cruzada: Adiciona relacional jerárquica obligatoria transitoria, indicando el origen padre en lógica global POO antes de insertar persistente base.*/
    public void setIdInventario(int idInventario) { // Apunte base estricto 
        this.idInventario = idInventario; // Acepta la orden y condiciona dependencia funcional externa paramétrica in memory.
    }

    /** Accesor contable sumatorio de base primitiva o asimilador decimal cruzado final de todos los envoltorios en detalles en su matriz y expuesto a GUI view */
    public double getTotalVenta() { // Genera o exporta el resultante puro.
        return totalVenta; // Permeabiliza variable protegida transiente hacia lógica superior o vista en modo float base POO. 
    }

    /** Mutador aritmético resolutivo posicional asimilador final: Asienta lógica y localmente como entidad transitoria sujeta al envoltorio la referencial totalitaria base. */
    public void setTotalVenta(double totalVenta) { // Recibe cálculo doble flotante pre derivado
        this.totalVenta = totalVenta; // Sustituye numérico estáticamente amarrándolo a this y referenciado paramétrico local.
    }

    /** Accesor temporal directo asimétrico y puro para resoluciones SQL. Evoca sello u horizonte atado.*/
    public Date getFechaVenta() { // Llamada con objeto derivado Date como valor output.
        return fechaVenta; // Referencia primitiva instanciada 
    }

    /** Mutador forzado lógico y temporal referenciado sobre base atada Date pre-cazada u orquestada de modo asíncrono temporal base en rutinas. */
    public void setFechaVenta(Date fechaVenta) { // Componente relacional Date recibido nativo
        this.fechaVenta = fechaVenta; // Aplicación estricta al estado original transaccional modificado u originado
    }
}
