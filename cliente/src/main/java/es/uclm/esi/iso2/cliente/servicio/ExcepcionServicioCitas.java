package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Error devuelto por el servicio de citas (CU13): un error del protocolo (DEC-065) o la falta de
 * conexión con el servidor.
 *
 * <p>No se reutiliza {@link ExcepcionServicio} porque esa clase guarda un campo de paciente, y los
 * errores de citas traen otro tipo de detalle: el nombre de un campo de la cita o, en un solape,
 * el número de la cita con la que choca.</p>
 */
public class ExcepcionServicioCitas extends RuntimeException {

    private final CodigoError codigo;
    private final String detalle;

    public ExcepcionServicioCitas(CodigoError codigo) {
        this(codigo, null, null);
    }

    public ExcepcionServicioCitas(CodigoError codigo, String detalle) {
        this(codigo, detalle, null);
    }

    /**
     * @param detalle nombre del campo que falla (por ejemplo {@code inicio}) o, en un solape, id de
     *                la cita con la que choca; puede ser {@code null}
     */
    public ExcepcionServicioCitas(CodigoError codigo, String detalle, Throwable causa) {
        super(detalle == null ? codigo.name() : codigo.name() + " (" + detalle + ")", causa);
        this.codigo = codigo;
        this.detalle = detalle;
    }

    public CodigoError codigo() {
        return codigo;
    }

    /** Detalle del error, o {@code null} si no lo trae. */
    public String detalle() {
        return detalle;
    }
}
