package es.uclm.esi.iso2.comun.dominio;

/**
 * Campos editables de un paciente, en el orden en que viajan por el protocolo (DEC-057).
 * El nombre de protocolo es el que se envía en los errores {@code CAMPO_OBLIGATORIO} y
 * {@code CAMPO_NO_VALIDO}.
 */
public enum CampoPaciente {
    DNI("dni"),
    TARJETA_SANITARIA("tarjetaSanitaria"),
    NOMBRE("nombre"),
    APELLIDOS("apellidos"),
    FECHA_NACIMIENTO("fechaNacimiento"),
    SEXO("sexo"),
    TELEFONO("telefono"),
    CORREO("correo"),
    DIRECCION("direccion");

    private final String nombreProtocolo;

    CampoPaciente(String nombreProtocolo) {
        this.nombreProtocolo = nombreProtocolo;
    }

    public String nombreProtocolo() {
        return nombreProtocolo;
    }

    /**
     * Devuelve el campo cuyo nombre de protocolo es el indicado.
     *
     * @throws IllegalArgumentException si no existe ese campo
     */
    public static CampoPaciente desdeNombreProtocolo(String nombre) {
        for (CampoPaciente campo : values()) {
            if (campo.nombreProtocolo.equals(nombre)) {
                return campo;
            }
        }
        throw new IllegalArgumentException("Campo de paciente desconocido: " + nombre);
    }
}
