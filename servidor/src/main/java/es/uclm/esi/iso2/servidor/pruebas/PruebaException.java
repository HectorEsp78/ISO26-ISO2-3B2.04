package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Operación de pruebas rechazada por una regla de negocio o una validación (DEC-075 y DEC-078).
 * El código y el detalle son los que se envían al cliente.
 */
public class PruebaException extends RuntimeException {

    private final CodigoError codigo;
    private final String detalle;

    public PruebaException(CodigoError codigo) {
        this(codigo, null);
    }

    /**
     * @param detalle nombre del campo que falla o, en {@code PRUEBA_YA_EXISTE}, id de la prueba
     *                existente; puede ser {@code null}
     */
    public PruebaException(CodigoError codigo, String detalle) {
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
