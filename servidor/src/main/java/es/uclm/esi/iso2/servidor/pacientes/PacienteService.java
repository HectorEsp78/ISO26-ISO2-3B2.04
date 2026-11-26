package es.uclm.esi.iso2.servidor.pacientes;

import es.uclm.esi.iso2.comun.dominio.ErrorValidacion;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.util.Optional;

/**
 * Reglas de negocio de CU12 en el servidor (clase de control GestorPacientes del análisis).
 *
 * <p>Valida siempre con {@link ValidadorPaciente} (DEC-055), comprueba los duplicados (RN-01) y
 * que solo se modifiquen o den de baja pacientes activos (CA-07).</p>
 */
public class PacienteService implements IPacienteService {

    private final PacienteDAO dao;
    private final ValidadorPaciente validador;
    private final CancelacionCitas cancelacionCitas;

    public PacienteService(PacienteDAO dao, CancelacionCitas cancelacionCitas) {
        this(dao, new ValidadorPaciente(), cancelacionCitas);
    }

    public PacienteService(PacienteDAO dao, ValidadorPaciente validador, CancelacionCitas cancelacionCitas) {
        this.dao = dao;
        this.validador = validador;
        this.cancelacionCitas = cancelacionCitas;
    }

    @Override
    public Paciente alta(Paciente paciente) {
        Paciente p = paciente.normalizado().conEstado(EstadoPaciente.ACTIVO);
        lanzarSiHayError(validador.primerError(p));
        if (dao.buscar(p.dni()).isPresent()) {
            throw new PacienteException(CodigoError.DNI_DUPLICADO);
        }
        comprobarTarjetaLibre(p);
        dao.insertar(p);
        return p;
    }

    @Override
    public Paciente buscar(String dni) {
        return existente(dni);
    }

    @Override
    public Paciente modificar(Paciente paciente) {
        Paciente p = paciente.normalizado();
        Paciente actual = existente(p.dni());
        if (!actual.activo()) {
            throw new PacienteException(CodigoError.PACIENTE_DE_BAJA);
        }
        lanzarSiHayError(validador.primerError(p));
        comprobarTarjetaLibre(p);
        Paciente modificado = p.conEstado(EstadoPaciente.ACTIVO);
        if (!dao.actualizar(modificado)) {
            throw new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO);
        }
        return modificado;
    }

    /**
     * Primero cancela las citas pendientes y después da de baja al paciente: si falla la
     * cancelación, el paciente sigue activo y se puede repetir la baja.
     */
    @Override
    public Paciente baja(String dni) {
        Paciente actual = existente(dni);
        if (!actual.activo()) {
            throw new PacienteException(CodigoError.PACIENTE_DE_BAJA);
        }
        cancelacionCitas.cancelarCitasPendientes(actual.dni(), CancelacionCitas.MOTIVO_BAJA);
        if (!dao.cambiarEstado(actual.dni(), EstadoPaciente.BAJA)) {
            throw new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO);
        }
        return actual.conEstado(EstadoPaciente.BAJA);
    }

    @Override
    public EstadoPaciente comprobar(String dni) {
        return existente(dni).estado();
    }

    private Paciente existente(String dni) {
        lanzarSiHayError(validador.validarDni(dni));
        return dao.buscar(Paciente.normalizarDni(dni))
                .orElseThrow(() -> new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO));
    }

    private void comprobarTarjetaLibre(Paciente p) {
        if (dao.tarjetaUsadaPorOtro(p.tarjetaSanitaria(), p.dni())) {
            throw new PacienteException(CodigoError.TARJETA_DUPLICADA);
        }
    }

    private static void lanzarSiHayError(Optional<ErrorValidacion> error) {
        error.ifPresent(e -> {
            throw new PacienteException(e.codigo(), e.campo());
        });
    }
}
