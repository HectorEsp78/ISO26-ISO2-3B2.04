package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicio;
import es.uclm.esi.iso2.cliente.servicio.ServicioPacientes;
import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.ErrorValidacion;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Clase de control del cliente para CU12.
 *
 * <p>Valida los datos antes de enviarlos (para avisar de todos los campos erróneos a la vez y
 * ahorrar peticiones), llama al {@link ServicioPacientes} y traduce el resultado a mensajes. Las
 * pantallas JavaFX solo muestran lo que devuelve esta clase (DEC-059).</p>
 */
public class GestorPacientesCliente {

    private final ServicioPacientes servicio;
    private final ValidadorPaciente validador;

    public GestorPacientesCliente(ServicioPacientes servicio) {
        this(servicio, new ValidadorPaciente());
    }

    public GestorPacientesCliente(ServicioPacientes servicio, ValidadorPaciente validador) {
        this.servicio = servicio;
        this.validador = validador;
    }

    /** Flujo 6.2: busca un paciente por su DNI/NIE. */
    public Resultado buscar(String dni) {
        Optional<ErrorValidacion> error = validador.validarDni(dni);
        if (error.isPresent()) {
            return errorDeValidacion(List.of(error.get()));
        }
        return llamar(() -> servicio.buscar(Paciente.normalizarDni(dni)), p -> null);
    }

    /**
     * Comprueba si existe un paciente con ese DNI/NIE y si está activo, sin mostrar sus datos
     * (DEC-061). Solo es correcto si el paciente existe y está activo; si está de baja o no
     * existe, el resultado es un error con el mensaje correspondiente.
     */
    public Resultado comprobar(String dni) {
        Optional<ErrorValidacion> error = validador.validarDni(dni);
        if (error.isPresent()) {
            return errorDeValidacion(List.of(error.get()));
        }
        String normalizado = Paciente.normalizarDni(dni);
        try {
            EstadoPaciente estado = servicio.comprobar(normalizado);
            if (estado == EstadoPaciente.ACTIVO) {
                return Resultado.correcto(null, "El paciente " + normalizado + " existe y está activo.");
            }
            return Resultado.error("El paciente " + normalizado + " existe, pero está dado de baja.", List.of());
        } catch (ExcepcionServicio e) {
            return resultadoDeError(e);
        }
    }

    /** Flujo 6.1: da de alta un paciente. */
    public Resultado alta(Paciente paciente) {
        Paciente p = paciente.normalizado();
        List<ErrorValidacion> errores = validador.validar(p);
        if (!errores.isEmpty()) {
            return errorDeValidacion(errores);
        }
        return llamar(() -> servicio.alta(p),
                guardado -> "Paciente " + guardado.nombreCompleto() + " dado de alta correctamente.");
    }

    /** Flujo 6.3: guarda los cambios de un paciente. No se puede si está de baja (CA-07). */
    public Resultado modificar(Paciente paciente) {
        if (!puedeModificar(paciente)) {
            return Resultado.error(MensajesError.mensaje(CodigoError.PACIENTE_DE_BAJA, null), List.of());
        }
        Paciente p = paciente.normalizado();
        List<ErrorValidacion> errores = validador.validar(p);
        if (!errores.isEmpty()) {
            return errorDeValidacion(errores);
        }
        return llamar(() -> servicio.modificar(p),
                guardado -> "Datos de " + guardado.nombreCompleto() + " guardados correctamente.");
    }

    /** Flujo 6.4: da de baja a un paciente, después de que el usuario lo haya confirmado. */
    public Resultado baja(Paciente paciente) {
        if (!paciente.activo()) {
            return Resultado.error(MensajesError.mensaje(CodigoError.PACIENTE_DE_BAJA, null), List.of());
        }
        return llamar(() -> servicio.baja(paciente.dni()),
                guardado -> "Paciente " + guardado.nombreCompleto() + " dado de baja.");
    }

    /** Solo se pueden modificar o dar de baja los pacientes activos (CA-07). */
    public boolean puedeModificar(Paciente paciente) {
        return paciente != null && paciente.activo();
    }

    /** Pregunta que se muestra antes de dar de baja (flujo 6.4, paso 3). */
    public String preguntaConfirmacionBaja(Paciente paciente) {
        return "¿Seguro que quieres dar de baja a " + paciente.nombreCompleto() + " (" + paciente.dni()
                + ")? Sus citas pendientes se cancelarán.";
    }

    private Resultado llamar(Supplier<Paciente> operacion, Function<Paciente, String> mensajeExito) {
        try {
            Paciente resultado = operacion.get();
            return Resultado.correcto(resultado, mensajeExito.apply(resultado));
        } catch (ExcepcionServicio e) {
            return resultadoDeError(e);
        }
    }

    private static Resultado resultadoDeError(ExcepcionServicio e) {
        String mensaje = MensajesError.mensaje(e.codigo(), e.campo());
        if (e.codigo() == CodigoError.SIN_CONEXION) {
            return Resultado.sinConexion(mensaje);
        }
        CampoPaciente afectado = MensajesError.campoAfectado(e.codigo(), e.campo());
        return Resultado.error(mensaje, afectado == null ? List.of() : List.of(afectado));
    }

    private static Resultado errorDeValidacion(List<ErrorValidacion> errores) {
        List<CampoPaciente> campos = new ArrayList<>();
        for (ErrorValidacion error : errores) {
            campos.add(error.campo());
        }
        ErrorValidacion primero = errores.get(0);
        String mensaje = MensajesError.mensaje(primero.codigo(), primero.campo());
        if (errores.size() > 1) {
            mensaje += " Revisa también los demás campos marcados.";
        }
        return Resultado.error(mensaje, campos);
    }
}
