package com.inventario.model; // Instrucción funcional de enrutamiento o package para vincular esta clase en el ecosistema

/**
 * Modelo de datos: Clase Proveedor (Entidad o POJO - Plain Old Java Object).
 * 
 * Capa de abstracción Orientada a Objetos para la estructura matriz u originadora de la tupla física 'proveedor'.
 * Se yergue como clase referenciable independiente que modela digitalmente perfiles asociados externos
 * catalogándolos o unificándolos bajo identidades que permitirán interacciones transaccionales dependientes conjuntas.
 */
public class Proveedor { // Definición base estructural pública modelada 

    // =====================================================================
    // ATRIBUTOS ENCAPSULADOS PRINCIPALES MATEADOS (Persistencia)
    // Instancian fielmente espacios atómicos idénticos a columnas primarias y simples de datos backend.
    // =====================================================================

    private int idProveedor;         // Llave primaria auto-escalable (PK), primitivo entero de validación unívoca intrínseca inmutable tras instanciarlo permanentemente.
    private String nombreProveedor;  // Literal embebido alfanumérico abstracto que da sustento mnemotécnico semántico legal a la empresa subyacente registrada bajo el identificador.
    private String contacto;         // Instancia cadena interna transitoria simple que detalla nominalizadamente a la rama de recurso humano en comunicación con este ente modelado.
    private String telefono;         // Encapsulado escalar textual primitivo referenciado al código o identificador interconectado a marcación analógica/digital.
    private String correo;           // Componente string asociado para albergar semántica de envío de parámetros telemáticos o digitales tipo email genéricos en texto libre.

    /**
     * CONSTRUCTOR POR DEFECTO
     * Constructor inherente sin verbosidad. Instancia o invoca a una porción reservada volátil temporal vacía
     * que aguardará inyecciones DAO intermitentes u orquestaciones unitarias escalonadas MVC en Setters.
     */
    public Proveedor() { // Generación en vacío basal orientada
    }

    /**
     * CONSTRUCTOR CARGADO PARAMETRIZADO O SOBRECARGA FUERTE
     * Modificador atómico transaccional de construcción POJO, habilita que en el proceso generativo o de nacimiento 
     * en memoria de la Entidad se auto-pongan todas sus variables finales, eficientando procesos for bulk instantiation DAO.
     * 
     * @param idProveedor Puntero primitivo serial base.
     * @param nombreProveedor Carga descriptiva abstracta relacional legal o alias principal.
     * @param contacto Referencial auxiliar para enlace nominal persona a persona de forma String.
     * @param telefono Puntero mnemotécnico string con dígitos directos de conmutación de origen local referencial temporal.
     * @param correo Componente digital electrónico descriptivo inyectado
     */
    public Proveedor(int idProveedor, String nombreProveedor, String contacto, String telefono, String correo) { // Constructor paramétrico explícito completo
        this.idProveedor = idProveedor;           // Traspasa al núcleo interno en bloque
        this.nombreProveedor = nombreProveedor;   // Resuelve la dependencia interna textual literal formal
        this.contacto = contacto;                 // Apuntala al objeto instanciado desde el parámetro foráneo transitorio 
        this.telefono = telefono;                 // Asimilador numérico tipo String.
        this.correo = correo;                     // Seta de email referencial y lo ata de modo atómico al envoltorio.
    }

    // =====================================================================
    // METODOS ACCESORES Y MUTADORES (Getters / Setters)
    // Interface de operación restringida que permite manipular las aristas encapsuladas, limitando fugas de seguridad u omisiones colaterales .
    // =====================================================================

    /** Accesor descriptivo centralizado numérico para la interacción y recolección controlada MVC base. */
    public int getIdProveedor() { // Lectura base del campo PK original
        return idProveedor; // Transige y expulsa primitiva 
    }

    /** Setter de injerto numérico relacional, empleado a modo post-generativo en lectura base generalizada. */
    public void setIdProveedor(int idProveedor) { // Recibe argumento único primitivo localizable 
        this.idProveedor = idProveedor; // Seteador local restringido
    }

    /** Accesor literal semántico o nombre formal para rellenar objetos combinados MVC list o referenciales. */
    public String getNombreProveedor() { // Extracción de la identidad semántica base de la tupla 
        return nombreProveedor; // Válido y directo al exterior en capa de interfaz 
    }

    /** Inyector nominal general de uso transitorio en instanciación por post iterativo o update MVC. */
    public void setNombreProveedor(String nombreProveedor) { // Admite string local descriptivo
        this.nombreProveedor = nombreProveedor; // Guarda la literal dentro del POO
    }

    /** Accesor cadena complementario, enfocado como componente puramente amigable informativo auxiliar. */
    public String getContacto() { // Entrega amarrada literal o vacía 
        return contacto; // Devolución string directa referencial auxiliar 
    }

    /** Receptor cadena limitante o mutador ascriptivo para campos de apoyo. */
    public void setContacto(String contacto) { // Parámetros intermedios string inyectables
        this.contacto = contacto; // Aplica o sobre imprime referencialmente
    }

    /** Lector posicional numérico de formato alfanumérico abstracto asimétrico asociado en BD. */
    public String getTelefono() { // Devuelve instancia puramente nominal temporal.
        return telefono; // Retorno de primitivo envuelto.
    }

    /** Mutador posicional numérico envuelto de string, inyectable desde DAO estático. */
    public void setTelefono(String telefono) { // Adaptador o recibidor 
        this.telefono = telefono; // Amarra en envoltorio referenciable
    }

    /** Exposición pura textual o descriptor estático alfanumérico. */
    public String getCorreo() { // Función de retrolavado a capas de controlador
        return correo; // Retorno en asimetría local.
    }

    /** Mutador para descriptor digital semántico temporalmente asilado en RAM pre-transaccional. */
    public void setCorreo(String correo) { // String portador paramétrico base
        this.correo = correo; // Adaptador estático final.
    }
}
