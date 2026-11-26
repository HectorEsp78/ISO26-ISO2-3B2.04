package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.ErrorCampo;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.citas.CitaException;
import es.uclm.esi.iso2.servidor.citas.ICitaService;
import es.uclm.esi.iso2.servidor.pacientes.IPacienteService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de CU10 «Registrar realización de prueba» (DEC-074 a DEC-078).
 *
 * <p>Registrar una prueba cambia tres cosas: la prueba (CU10), la cita (CU13) y la solicitud
 * (CU06). Como cada servicio abre sus propias conexiones, no comparten transacción; por eso se
 * guarda primero la prueba y, si falla el cambio de la cita o de la solicitud, se borra la prueba
 * (DEC-076).</p>
 */
public class PruebaService implements IPruebaService, IConsultaPruebas {

    private static final System.Logger LOG = System.getLogger(PruebaService.class.getName());

    private final PruebaDAO dao;
    private final ICitaService citas;
    private final SolicitudesDePrueba solicitudes;
    private final IPacienteService pacientes;
    private final Clock reloj;

    public PruebaService(PruebaDAO dao, ICitaService citas, SolicitudesDePrueba solicitudes,
            IPacienteService pacientes, Clock reloj) {
        this.dao = dao;
        this.citas = citas;
        this.solicitudes = solicitudes;
        this.pacientes = pacientes;
        this.reloj = reloj;
    }

    // ---- Solicitudes pendientes (flujo 6.1) ----

    @Override
    public List<SolicitudPendiente> buscarSolicitudesPendientes(String idMedico, String dni) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        String dniFiltro = dniONulo(dni);
        List<SolicitudDePrueba> candidatas = solicitudes.conCitaDelMedico(idMedico.strip(), dniFiltro);
        List<Long> yaRegistradas = dao.solicitudesConPrueba(candidatas.stream().map(SolicitudDePrueba::idSolicitud)
                .toList());
        List<SolicitudPendiente> pendientes = new ArrayList<>();
        for (SolicitudDePrueba s : candidatas) {
            if (yaRegistradas.contains(s.idSolicitud()) || s.idCita() == null) {
                continue;
            }
            Optional<Cita> cita = citaProgramada(s.idCita());
            if (cita.isPresent()) {
                pendientes.add(new SolicitudPendiente(s.idSolicitud(), s.dniPaciente(), nombre(s.dniPaciente()),
                        s.tipoPrueba(), s.prioridad(), s.idCita(), cita.get().inicio()));
            }
        }
        pendientes.sort(Comparator.comparing(SolicitudPendiente::inicioCita)
                .thenComparingLong(SolicitudPendiente::idSolicitud));
        return pendientes;
    }

    // ---- Registro (flujos 6.2 y 6.3) ----

    @Override
    public Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora,
            String observaciones, List<NuevaIncidencia> incidencias) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        LocalDateTime ahora = ahora();
        List<NuevaIncidencia> nuevas = incidencias == null ? List.of() : incidencias;
        comprobar(ValidadorPrueba.validarRealizacion(fechaHora, observaciones, nuevas, ahora));
        SolicitudDePrueba solicitud = comprobarSolicitud(idMedico.strip(), idSolicitud);

        List<Incidencia> incidenciasPrueba = nuevas.stream()
                .map(i -> new Incidencia(0, 0, i.tipo(), ValidadorPrueba.normalizar(i.descripcion()), ahora))
                .toList();
        Prueba prueba = new Prueba(0, idSolicitud, solicitud.dniPaciente(), idMedico.strip(), EstadoPrueba.REALIZADA,
                fechaHora, ValidadorPrueba.normalizar(observaciones), "", ahora, incidenciasPrueba);
        return guardarYActualizar(prueba, solicitud.idCita(), () -> {
            citas.marcarRealizada(solicitud.idCita());
            solicitudes.marcarRealizada(idSolicitud);
        });
    }

    @Override
    public Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        comprobar(ValidadorPrueba.validarMotivo(motivo));
        SolicitudDePrueba solicitud = comprobarSolicitud(idMedico.strip(), idSolicitud);

        String motivoLimpio = ValidadorPrueba.normalizar(motivo);
        Prueba prueba = new Prueba(0, idSolicitud, solicitud.dniPaciente(), idMedico.strip(), EstadoPrueba.CANCELADA,
                null, "", motivoLimpio, ahora(), List.of());
        return guardarYActualizar(prueba, solicitud.idCita(), () -> {
            citas.marcarCancelada(solicitud.idCita(), motivoLimpio);
            solicitudes.marcarCancelada(idSolicitud);
        });
    }

    // ---- Incidencias (flujo 6.5) ----

    @Override
    public Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        comprobar(ValidadorPrueba.validarIncidencia(incidencia));
        Prueba prueba = pruebaDelMedico(idMedico.strip(), idPrueba);
        return dao.guardarIncidencia(new Incidencia(0, prueba.id(), incidencia.tipo(),
                ValidadorPrueba.normalizar(incidencia.descripcion()), ahora()));
    }

    // ---- Consultas (flujos 6.4 y 6.6) ----

    @Override
    public List<ResumenPrueba> buscarPruebas(String idMedico, FiltroPruebas filtro) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        FiltroPruebas f = filtro == null ? FiltroPruebas.SIN_FILTROS : filtro;
        comprobar(ValidadorPrueba.validarFiltro(f));
        return dao.buscar(idMedico.strip(), new FiltroPruebas(dniONulo(f.dniPaciente()), f.idSolicitud(), f.desde(),
                f.hasta(), f.estado()));
    }

    @Override
    public Prueba consultarPrueba(String idMedico, long idPrueba) {
        comprobar(ValidadorPrueba.validarMedico(idMedico));
        return pruebaDelMedico(idMedico.strip(), idPrueba);
    }

    @Override
    public DatosPrueba obtenerPrueba(long idPrueba) {
        Prueba p = dao.buscarPorId(idPrueba)
                .orElseThrow(() -> new PruebaException(CodigoError.PRUEBA_NO_ENCONTRADA));
        return new DatosPrueba(p.id(), p.dniPaciente(), p.idMedico(), p.estado());
    }

    // ---- Auxiliares ----

    private LocalDateTime ahora() {
        return LocalDateTime.now(reloj).truncatedTo(ChronoUnit.SECONDS);
    }

    private static void comprobar(Optional<ErrorCampo> error) {
        error.ifPresent(e -> {
            throw new PruebaException(e.codigo(), e.campo());
        });
    }

    private static String dniONulo(String dni) {
        String limpio = Paciente.normalizarDni(dni);
        return limpio.isEmpty() ? null : limpio;
    }

    private Prueba pruebaDelMedico(String idMedico, long idPrueba) {
        Prueba prueba = dao.buscarPorId(idPrueba)
                .orElseThrow(() -> new PruebaException(CodigoError.PRUEBA_NO_ENCONTRADA));
        if (!prueba.idMedico().equals(idMedico)) {
            throw new PruebaException(CodigoError.PRUEBA_NO_PERMITIDA);
        }
        return prueba;
    }

    /**
     * Comprueba, sin cambiar nada, que la solicitud existe, es de un paciente del médico, no tiene
     * prueba y tiene una cita programada (DEC-076, paso 1).
     */
    private SolicitudDePrueba comprobarSolicitud(String idMedico, long idSolicitud) {
        SolicitudDePrueba solicitud = solicitudes.buscar(idSolicitud)
                .orElseThrow(() -> new PruebaException(CodigoError.SOLICITUD_NO_ENCONTRADA));
        if (!solicitud.idMedico().equals(idMedico)) {
            throw new PruebaException(CodigoError.SOLICITUD_NO_PERMITIDA);
        }
        dao.buscarPorSolicitud(idSolicitud).ifPresent(p -> {
            throw new PruebaException(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(p.id()));
        });
        if (solicitud.idCita() == null || citaProgramada(solicitud.idCita()).isEmpty()) {
            throw new PruebaException(CodigoError.SOLICITUD_SIN_CITA);
        }
        return solicitud;
    }

    private Optional<Cita> citaProgramada(long idCita) {
        try {
            Cita cita = citas.obtenerCita(idCita);
            return cita.estado() == EstadoCita.PROGRAMADA ? Optional.of(cita) : Optional.empty();
        } catch (CitaException e) {
            return Optional.empty();
        }
    }

    private String nombre(String dni) {
        try {
            return pacientes.buscar(dni).nombreCompleto();
        } catch (RuntimeException e) {
            return dni;
        }
    }

    /**
     * Guarda la prueba y ejecuta el cambio de la cita y de la solicitud. Si este falla se borra la
     * prueba y se responde {@code ERROR_INTERNO} (DEC-076, pasos 2 a 4).
     */
    private Prueba guardarYActualizar(Prueba prueba, long idCita, Runnable cambiarEstados) {
        Prueba guardada = dao.guardar(prueba);
        try {
            cambiarEstados.run();
        } catch (RuntimeException e) {
            LOG.log(System.Logger.Level.ERROR, "No se pudo actualizar la cita " + idCita + " o la solicitud "
                    + prueba.idSolicitud() + "; se borra la prueba " + guardada.id()
                    + ". Si la cita ya cambió de estado hay que corregirla a mano.", e);
            dao.borrar(guardada.id());
            throw new PruebaException(CodigoError.ERROR_INTERNO);
        }
        return guardada;
    }
}
