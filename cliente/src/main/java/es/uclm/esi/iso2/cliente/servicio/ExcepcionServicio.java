package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Error devuelto por el servicio de pacientes: un error del protocolo (DEC-057) o la falta de
 * conexión con el servidor (FA-08).
 */
public class ExcepcionServicio extends RuntimeException {

    private final CodigoError codigo;
    private final transient CampoPaciente campo;

    public ExcepcionServicio(CodigoError codigo) {
        this(codigo, null, null);
    }

    public ExcepcionServicio(CodigoError codigo, CampoPaciente campo) {
        this(codigo, campo, null);
    }

    public ExcepcionServicio(CodigoError codigo, CampoPaciente campo, Throwable causa) {
        super(campo == null ? codigo.name() : codigo.name() + " (" + campo.nombreProtocolo() + ")", causa);
        this.codigo = codigo;
        this.campo = campo;
    }

    public CodigoError codigo() {
        return codigo;
    }

    /** Campo al que se refiere el error, o {@code null} si no se refiere a ninguno. */
    public CampoPaciente campo() {
        return campo;
    }
}
