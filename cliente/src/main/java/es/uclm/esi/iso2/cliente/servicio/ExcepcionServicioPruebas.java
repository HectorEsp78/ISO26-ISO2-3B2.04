package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Error devuelto por el servicio de pruebas (CU10): un error del protocolo (DEC-078) o la falta
 * de conexión con el servidor.
 */
public class ExcepcionServicioPruebas extends RuntimeException {

    private final CodigoError codigo;
    private final String detalle;

    public ExcepcionServicioPruebas(CodigoError codigo) {
        this(codigo, null, null);
    }

    /**
     * @param detalle nombre del campo que falla o, en {@code PRUEBA_YA_EXISTE}, id de la prueba
     *                existente; puede ser {@code null}
     */
    public ExcepcionServicioPruebas(CodigoError codigo, String detalle, Throwable causa) {
        super(detalle == null ? codigo.name() : codigo.name() + " (" + detalle + ")", causa);
        this.codigo = codigo;
        this.detalle = detalle;
    }

    public CodigoError codigo() {
        return codigo;
    }

    public String detalle() {
        return detalle;
    }
}
