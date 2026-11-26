package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.ErrorValidacion;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de pacientes sin red ni base de datos (DEC-059).
 *
 * <p>Aplica las mismas reglas y devuelve los mismos errores que el servidor (DEC-057), de modo
 * que sirve para las pruebas y para usar la interfaz sin servidor. Los datos se pierden al
 * cerrar el cliente.</p>
 */
public class ServicioPacientesEnMemoria implements ServicioPacientes {

    private final Map<String, Paciente> pacientes = new LinkedHashMap<>();
    private final ValidadorPaciente validador;

    public ServicioPacientesEnMemoria() {
        this(new ValidadorPaciente());
    }

    public ServicioPacientesEnMemoria(ValidadorPaciente validador) {
        this.validador = validador;
    }

    /** Servicio con unos pacientes de ejemplo, para probar la interfaz. */
    public static ServicioPacientesEnMemoria conDatosDeEjemplo() {
        ServicioPacientesEnMemoria servicio = new ServicioPacientesEnMemoria();
        servicio.alta(new Paciente("12345678Z", "CMCM000000000001", "Lucía", "García Martín",
                "1985-04-12", Paciente.MUJER, "600111222", "lucia.garcia@example.com",
                "C/ Mayor 1, Ciudad Real", null));
        servicio.alta(new Paciente("X1234567L", "CMCM000000000002", "Ahmed", "El Idrissi",
                "1990-11-30", Paciente.HOMBRE, "699333444", "", "", null));
        servicio.alta(new Paciente("87654321X", "CMCM000000000003", "Antonio", "López Ruiz",
                "1948-02-01", Paciente.HOMBRE, "926555666", "", "Avda. de la Mancha 5, Toledo", null));
        servicio.baja("87654321X");
        return servicio;
    }

    @Override
    public synchronized Paciente alta(Paciente paciente) {
        Paciente p = paciente.normalizado().conEstado(EstadoPaciente.ACTIVO);
        lanzarSiHayError(validador.primerError(p));
        if (pacientes.containsKey(p.dni())) {
            throw new ExcepcionServicio(CodigoError.DNI_DUPLICADO);
        }
        comprobarTarjetaLibre(p);
        pacientes.put(p.dni(), p);
        return p;
    }

    @Override
    public synchronized Paciente buscar(String dni) {
        return existente(dni);
    }

    @Override
    public synchronized Paciente modificar(Paciente paciente) {
        Paciente p = paciente.normalizado();
        Paciente actual = existente(p.dni());
        if (!actual.activo()) {
            throw new ExcepcionServicio(CodigoError.PACIENTE_DE_BAJA);
        }
        lanzarSiHayError(validador.primerError(p));
        comprobarTarjetaLibre(p);
        Paciente modificado = p.conEstado(EstadoPaciente.ACTIVO);
        pacientes.put(modificado.dni(), modificado);
        return modificado;
    }

    @Override
    public synchronized Paciente baja(String dni) {
        Paciente actual = existente(dni);
        if (!actual.activo()) {
            throw new ExcepcionServicio(CodigoError.PACIENTE_DE_BAJA);
        }
        Paciente deBaja = actual.conEstado(EstadoPaciente.BAJA);
        pacientes.put(deBaja.dni(), deBaja);
        return deBaja;
    }

    @Override
    public synchronized EstadoPaciente comprobar(String dni) {
        return existente(dni).estado();
    }

    /** Pacientes guardados, en orden de alta. */
    public synchronized List<Paciente> todos() {
        return List.copyOf(pacientes.values());
    }

    private Paciente existente(String dni) {
        lanzarSiHayError(validador.validarDni(dni));
        Paciente paciente = pacientes.get(Paciente.normalizarDni(dni));
        if (paciente == null) {
            throw new ExcepcionServicio(CodigoError.PACIENTE_NO_ENCONTRADO);
        }
        return paciente;
    }

    private void comprobarTarjetaLibre(Paciente p) {
        boolean ocupada = pacientes.values().stream()
                .anyMatch(otro -> !otro.dni().equals(p.dni())
                        && otro.tarjetaSanitaria().equals(p.tarjetaSanitaria()));
        if (ocupada) {
            throw new ExcepcionServicio(CodigoError.TARJETA_DUPLICADA);
        }
    }

    private static void lanzarSiHayError(Optional<ErrorValidacion> error) {
        error.ifPresent(e -> {
            throw new ExcepcionServicio(e.codigo(), e.campo());
        });
    }
}
