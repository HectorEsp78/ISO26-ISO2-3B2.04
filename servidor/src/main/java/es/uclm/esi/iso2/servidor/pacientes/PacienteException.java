package es.uclm.esi.iso2.servidor.pacientes;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Operación de pacientes rechazada por una regla de negocio o una validación. El código y el campo
 * son los que se envían al cliente (DEC-057).
 */
public class PacienteException extends RuntimeException {

    private final CodigoError codigo;
    private final transient CampoPaciente campo;

    public PacienteException(CodigoError codigo) {
        this(codigo, null);
    }

    public PacienteException(CodigoError codigo, CampoPaciente campo) {
        super(campo == null ? codigo.name() : codigo.name() + " (" + campo.nombreProtocolo() + ")");
        this.codigo = codigo;
        this.campo = campo;
    }

    public CodigoError codigo() {
        return codigo;
    }

    /** Campo al que se refiere el error, o {@code null}. */
    public CampoPaciente campo() {
        return campo;
    }
}
