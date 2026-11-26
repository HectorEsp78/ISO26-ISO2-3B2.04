package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Operación de citas rechazada por una regla de negocio o una validación (DEC-063 y DEC-065).
 * El código y el detalle son los que se envían al cliente.
 */
public class CitaException extends RuntimeException {

    private final CodigoError codigo;
    private final String detalle;

    public CitaException(CodigoError codigo) {
        this(codigo, null);
    }

    /**
     * @param detalle nombre del campo que falla o, en un solape, id de la cita con la que choca;
     *                puede ser {@code null}
     */
    public CitaException(CodigoError codigo, String detalle) {
        super(detalle == null ? codigo.name() : codigo.name() + " (" + detalle + ")");
        this.codigo = codigo;
        this.detalle = detalle;
    }

    public CodigoError codigo() {
        return codigo;
    }

    /** Campo o id al que se refiere el error, o {@code null}. */
    public String detalle() {
        return detalle;
    }
}
